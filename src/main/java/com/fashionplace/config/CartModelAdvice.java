package com.fashionplace.config;

import com.fashionplace.session.CartBean;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Exposes session cart data to every view (e.g. the header cart badge in the layout).
 */
@ControllerAdvice
public class CartModelAdvice {

    @Resource
    private CartBean cartBean;

    /**
     * Total number of units in the session cart (sum of all line quantities).
     */
    @ModelAttribute("cartCount")
    public int cartCount() {
        return cartBean.getTotalItemCount();
    }
}
