package com.fashionplace.config;

import com.fashionplace.service.WishlistService;
import com.fashionplace.session.InterestBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * After login, saves a product the guest tried to wishlist and sends them to {@code /wishlist}.
 */
@Component
public class WishlistAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public static final String PENDING_WISHLIST_PRODUCT_ID = "pendingWishlistProductId";

    private final WishlistService wishlistService;
    private final ObjectProvider<InterestBean> interestBeanProvider;

    public WishlistAuthenticationSuccessHandler(WishlistService wishlistService,
                                                ObjectProvider<InterestBean> interestBeanProvider) {
        this.wishlistService = wishlistService;
        this.interestBeanProvider = interestBeanProvider;
        setDefaultTargetUrl("/");
        setAlwaysUseDefaultTargetUrl(false);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        Long pendingProductId = session != null
                ? (Long) session.getAttribute(PENDING_WISHLIST_PRODUCT_ID)
                : null;
        if (pendingProductId != null) {
            session.removeAttribute(PENDING_WISHLIST_PRODUCT_ID);
            wishlistService.toggleForCurrentUser(pendingProductId);
            interestBeanProvider.getObject().record(pendingProductId);
            getRedirectStrategy().sendRedirect(request, response, "/wishlist");
            return;
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
