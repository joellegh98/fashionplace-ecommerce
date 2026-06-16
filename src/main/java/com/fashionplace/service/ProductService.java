package com.fashionplace.service;

import com.fashionplace.model.Product;
import com.fashionplace.model.User;
import com.fashionplace.repository.ProductRepository;
import com.fashionplace.repository.ReviewRepository;
import com.fashionplace.repository.WishlistItemRepository;
import com.fashionplace.web.CartLineItem;
import com.fashionplace.web.CartSummary;
import com.fashionplace.web.SellForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Business-logic layer for products. Sits between the controllers and the
 * {@link ProductRepository}, so controllers depend on this service rather than on
 * the database access layer directly.
 */
@Service
public class ProductService {

    /** Maximum number of titles returned by the search autocomplete. */
    private static final int MAX_SUGGESTIONS = 10;

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final WishlistItemRepository wishlistItemRepository;

    public ProductService(ProductRepository productRepository,
                          ReviewRepository reviewRepository,
                          WishlistItemRepository wishlistItemRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.wishlistItemRepository = wishlistItemRepository;
    }

    /**
     * Returns every product.
     *
     * @return all products
     */
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * Returns products matching the optional keyword and browse filters.
     *
     * @param keyword   optional title keyword
     * @param category  optional category (exact match)
     * @param condition optional item condition (exact match)
     * @param minPrice  optional minimum price (inclusive)
     * @param maxPrice  optional maximum price (inclusive)
     * @return products matching all non-empty criteria
     */
    public List<Product> browse(String keyword, String category, String condition,
                                BigDecimal minPrice, BigDecimal maxPrice, String sort) {
        List<Product> products = productRepository.filter(
                blankToNull(keyword),
                blankToNull(category),
                blankToNull(condition),
                minPrice,
                maxPrice
        );
        return applySort(products, sort);
    }

    /**
     * Sorts the given products. {@code newest} uses descending id as a stand-in until
     * {@code createdAt} is added to the entity.
     */
    private List<Product> applySort(List<Product> products, String sort) {
        String effectiveSort = (sort == null || sort.isBlank()) ? "newest" : sort;
        return switch (effectiveSort) {
            case "price_asc" -> products.stream()
                    .sorted(Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();
            case "price_desc" -> products.stream()
                    .sorted(Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.reverseOrder())))
                    .toList();
            default -> products.stream()
                    .sorted(Comparator.comparing(Product::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                    .toList();
        };
    }

    /**
     * Returns distinct category values present in the database (for filter dropdowns).
     */
    public List<String> findDistinctCategories() {
        return productRepository.findDistinctCategories();
    }

    /**
     * Returns distinct condition values present in the database (for filter dropdowns).
     */
    public List<String> findDistinctConditions() {
        return productRepository.findDistinctConditions();
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    /**
     * Returns up to {@value #MAX_SUGGESTIONS} product titles matching the keyword, for the
     * Browse search autocomplete. A blank or {@code null} keyword yields no suggestions.
     *
     * @param keyword the partial title typed by the user
     * @return matching product titles (at most {@value #MAX_SUGGESTIONS})
     */
    public List<String> suggestTitles(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return productRepository.findByTitleContainingIgnoreCaseAndDeletedFalse(keyword.trim()).stream()
                .map(Product::getTitle)
                .distinct()
                .limit(MAX_SUGGESTIONS)
                .toList();
    }

    /**
     * Finds a single product by its id.
     *
     * @param id the product id
     * @return the product
     * @throws java.util.NoSuchElementException if no product has the given id
     */
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow();
    }

    /**
     * Looks up an available (non-deleted) product, returning empty when it is missing or has
     * been soft-deleted. Used by cart/checkout, where a deleted product counts as unavailable.
     *
     * @param id the product id
     * @return the product if present and not deleted, otherwise empty
     */
    public Optional<Product> findByIdOptional(Long id) {
        return productRepository.findById(id)
                .filter(product -> !product.isDeleted());
    }

    /**
     * Builds cart line items from a session cart map. Products that were deleted from the
     * catalogue are skipped and their ids are returned in {@link CartSummary#getMissingProductIds()}
     * so the caller can remove them from the session cart.
     *
     * @param cartItems product id → quantity from the session cart
     * @return resolved lines, grand total, and any missing product ids
     */
    public CartSummary summarizeCart(Map<Long, Integer> cartItems) {
        List<CartLineItem> lines = new ArrayList<>();
        List<Long> missing = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Product product = productRepository.findById(entry.getKey()).orElse(null);
            if (product == null || product.isDeleted()) {
                missing.add(entry.getKey());
                continue;
            }
            CartLineItem line = new CartLineItem(
                    product.getId(), product.getTitle(), product.getPrice(), entry.getValue());
            lines.add(line);
            grandTotal = grandTotal.add(line.getLineTotal());
        }
        return new CartSummary(lines, grandTotal, missing);
    }

    /**
     * Creates and persists a new {@code ACTIVE} product listing owned by the given seller.
     *
     * @param form   the submitted listing details
     * @param seller the user listing the product
     * @return the saved product (with its generated id)
     */
    public Product createListing(SellForm form, User seller) {
        Product product = new Product();
        product.setTitle(form.getTitle());
        product.setDescription(form.getDescription());
        product.setPrice(form.getPrice());
        product.setCategory(form.getCategory());
        product.setCondition(form.getCondition());
        product.setQuantity(form.getQuantity() == null ? 1 : form.getQuantity());
        product.setStatus("ACTIVE");
        product.setSeller(seller);
        applyImage(product, form);
        return productRepository.save(product);
    }

    /**
     * Returns all products listed by the given seller, newest first.
     *
     * @param seller the seller whose products to fetch
     * @return that seller's products
     */
    public List<Product> findBySeller(User seller) {
        return productRepository.findBySellerAndDeletedFalseOrderByIdDesc(seller);
    }

    /**
     * Updates an existing product's editable fields and persists it. The listing status is
     * recomputed from the quantity ({@code ACTIVE} when in stock, {@code SOLD} when zero).
     * The image is only changed when the edit supplies a new file or URL (see
     * {@link #applyImageOnUpdate}).
     *
     * @param id   the product to update
     * @param form the submitted edit details
     * @return the saved product
     * @throws NoSuchElementException if no product has the given id
     */
    public Product updateListing(Long id, SellForm form) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setTitle(form.getTitle());
        product.setDescription(form.getDescription());
        product.setPrice(form.getPrice());
        product.setCategory(form.getCategory());
        product.setCondition(form.getCondition());
        int quantity = form.getQuantity() == null ? 0 : form.getQuantity();
        product.setQuantity(quantity);
        product.setStatus(quantity > 0 ? "ACTIVE" : "SOLD");
        applyImageOnUpdate(product, form);
        return productRepository.save(product);
    }

    /**
     * Soft-deletes a product: marks it {@code deleted}, zeroes out any remaining stock, and
     * marks it {@code SOLD}. The product is kept in the database so past orders can still show
     * the purchased item, but it disappears from Browse, search, recommendations, My Products,
     * and the cart. Reviews are removed and the product is taken off every wishlist, since it
     * is no longer a live listing. Order line items are intentionally preserved for history.
     *
     * @param id the product to delete
     * @throws NoSuchElementException if no product has the given id
     */
    @Transactional
    public void deleteListing(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        wishlistItemRepository.deleteByProduct(product);
        reviewRepository.deleteByProduct(product);
        product.setDeleted(true);
        product.setQuantity(0);
        product.setStatus("SOLD");
        productRepository.save(product);
    }

    /**
     * Builds a {@link SellForm} pre-filled with an existing product's editable fields, so the
     * edit page can render the current values. The image source defaults to the kind the
     * product currently uses.
     *
     * @param product the product to copy values from
     * @return a form populated for editing
     */
    public SellForm toForm(Product product) {
        SellForm form = new SellForm();
        form.setTitle(product.getTitle());
        form.setDescription(product.getDescription());
        form.setPrice(product.getPrice());
        form.setCategory(product.getCategory());
        form.setCondition(product.getCondition());
        form.setQuantity(product.getQuantity());
        form.setImageSource(product.isHasUploadedImage() ? "upload" : "url");
        form.setImageUrl(product.getImageUrl());
        return form;
    }

    /**
     * Attaches an image to the product from whichever source the user chose: an uploaded
     * file or an external URL. Does nothing when the chosen source has no value (images
     * are optional). An uploaded file and a URL are mutually exclusive.
     *
     * @param product the product to attach the image to
     * @param form    the submitted form holding the image choice
     * @throws IllegalStateException if the uploaded file cannot be read
     */
    private void applyImage(Product product, SellForm form) {
        boolean useUrl = "url".equals(form.getImageSource());
        if (useUrl) {
            String url = form.getImageUrl();
            if (url != null && !url.isBlank()) {
                product.setImageUrl(url.trim());
            }
            return;
        }

        MultipartFile image = form.getImage();
        if (image == null || image.isEmpty()) {
            return;
        }
        try {
            product.setImageData(image.getBytes());
            product.setImageContentType(image.getContentType());
        } catch (IOException e) {
            throw new IllegalStateException("Could not read uploaded image", e);
        }
    }

    /**
     * Applies an image change during an edit. Unlike {@link #applyImage}, when a new source
     * value is supplied it clears the other source so the switch takes effect (an uploaded
     * image and a URL are mutually exclusive). When no new value is supplied the product
     * keeps its current image.
     *
     * @param product the product being edited
     * @param form    the submitted edit form
     * @throws IllegalStateException if the uploaded file cannot be read
     */
    private void applyImageOnUpdate(Product product, SellForm form) {
        if ("url".equals(form.getImageSource())) {
            String url = form.getImageUrl();
            if (url != null && !url.isBlank()) {
                product.setImageUrl(url.trim());
                product.setImageData(null);
                product.setImageContentType(null);
            }
            return;
        }

        MultipartFile image = form.getImage();
        if (image != null && !image.isEmpty()) {
            try {
                product.setImageData(image.getBytes());
                product.setImageContentType(image.getContentType());
                product.setImageUrl(null);
            } catch (IOException e) {
                throw new IllegalStateException("Could not read uploaded image", e);
            }
        }
    }

    /**
     * Returns the raw image bytes stored for a product, or {@code null} if it has no
     * uploaded image (e.g. it uses an external {@code imageUrl} or has no image at all).
     *
     * @param id the product id
     * @return the image bytes, or {@code null}
     * @throws NoSuchElementException if no product has the given id
     */
    public byte[] getImageData(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        return product.getImageData();
    }

    /**
     * Returns the stored image content type for a product, or {@code null} if none.
     *
     * @param id the product id
     * @return the MIME type, or {@code null}
     * @throws NoSuchElementException if no product has the given id
     */
    public String getImageContentType(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        return product.getImageContentType();
    }

    /**
     * Reduces a product's stock by the purchased amount and persists it. When the remaining
     * quantity reaches zero the product is marked {@code SOLD}. Quantity never goes negative.
     *
     * @param product  the product to update
     * @param quantity the number of units sold
     * @return the saved product
     */
    public Product reduceStock(Product product, int quantity) {
        int remaining = Math.max(product.getQuantity() - quantity, 0);
        product.setQuantity(remaining);
        if (remaining == 0) {
            product.setStatus("SOLD");
        }
        return productRepository.save(product);
    }
}
