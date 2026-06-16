package com.fashionplace.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * Form backing object for the Sell page: captures the details of a new product
 * listing before it is persisted.
 */
public class SellForm {

    @NotBlank(message = "Please enter a title.")
    private String title;

    @NotBlank(message = "Please enter a description.")
    private String description;

    @NotNull(message = "Please enter a price.")
    @DecimalMin(value = "0.01", message = "Price must be at least $0.01.")
    private BigDecimal price;

    @NotBlank(message = "Please choose a category.")
    private String category;

    @NotBlank(message = "Please choose a condition.")
    private String condition;

    @NotNull(message = "Please enter a quantity.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer quantity = 1;

    /** Which image source the user chose: {@code "upload"} (default) or {@code "url"}. */
    private String imageSource = "upload";

    /** Optional uploaded image file for the listing (used when {@code imageSource == "upload"}). */
    private MultipartFile image;

    /** Optional external image URL for the listing (used when {@code imageSource == "url"}). */
    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
