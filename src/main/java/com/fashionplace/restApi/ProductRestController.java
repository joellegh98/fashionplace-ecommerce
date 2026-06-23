package com.fashionplace.restApi;

import com.fashionplace.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * JSON and binary API endpoints for product data used by the UI without a full page reload.
 */
@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Returns product titles matching the given keyword for Browse search autocomplete.
     *
     * @param q the partial title typed by the user
     * @return matching titles as a JSON array of strings
     */
    @GetMapping("/suggest")
    public List<String> suggest(@RequestParam(required = false, defaultValue = "") String q) {
        return productService.suggestTitles(q);
    }

    /**
     * Streams a product's uploaded image bytes from the database.
     *
     * @param id the product id
     * @return the image bytes with the stored content type, or 404 if none
     */
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> productImage(@PathVariable Long id) {
        byte[] data = productService.getImageData(id);
        if (data == null || data.length == 0) {
            return ResponseEntity.notFound().build();
        }
        String contentType = productService.getImageContentType(id);
        MediaType mediaType = contentType != null
                ? MediaType.parseMediaType(contentType)
                : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok().contentType(mediaType).body(data);
    }
}
