package com.coupons_management.Service;

import com.coupons_management.Entity.Cart;
import com.coupons_management.Entity.CartItem;
import com.coupons_management.Entity.Coupon;
import com.coupons_management.Enum.CouponType;
import com.coupons_management.Exception.CouponNotFoundException;
import com.coupons_management.Repository.CouponRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private static final Logger logger = LoggerFactory.getLogger(CouponService.class);

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    // Create a new coupon and save it to the database
    public Coupon createCoupon(Coupon coupon) {
        logger.info("Creating a new coupon: {}", coupon);
        return couponRepository.save(coupon);
    }

    // Fetch all coupons from the database
    public List<Coupon> getAllCoupons() {
        logger.info("Fetching all coupons from the database");
        return couponRepository.findAll();
    }

    // Fetch a specific coupon by its ID
    public Optional<Coupon> getCouponById(Long id) {
        logger.info("Fetching coupon with id: {}", id);
        return couponRepository.findById(id);
    }

    // Update an existing coupon by its ID
    public Coupon updateCoupon(Long id, Coupon coupon) {
        logger.info("Updating coupon with id: {}", id);
        Coupon existingCoupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with id " + id));

        // Update the fields of the existing coupon
        existingCoupon.setType(coupon.getType());
        existingCoupon.setDetails(coupon.getDetails());
        logger.info("Coupon updated successfully: {}", existingCoupon);
        return couponRepository.save(existingCoupon);
    }

    // Delete a coupon by its ID
    public void deleteCoupon(Long id) {
        logger.info("Deleting coupon with id: {}", id);
        couponRepository.deleteById(id);
    }

    // Fetch applicable coupons for the given cart based on the coupon type and conditions
    public List<Coupon> getApplicableCoupons(Cart cart) {
        if (cart == null) {
            logger.error("Cart is null. Cannot fetch applicable coupons.");
            throw new IllegalArgumentException("Cart cannot be null");
        }

        List<Coupon> applicableCoupons = new ArrayList<>();
        logger.info("Checking for applicable coupons for the cart");

//        to check the expiration date when determining applicable coupons:
        for (Coupon coupon : couponRepository.findAll()) {
            // Skip expired coupons
            if (coupon.getExpirationDate() != null && coupon.getExpirationDate().isBefore(LocalDateTime.now())) {
                logger.debug("Skipping expired coupon: {}", coupon);
                continue;
            }

            // Ensure the cart has items
            List<CartItem> cartItems = cart.getItems();
            if (cartItems == null || cartItems.isEmpty()) {
                logger.warn("No items found in the cart. No coupons applicable.");
                throw new IllegalArgumentException("Cart items cannot be null or empty");
            }

            // CART_WISE coupon application logic
            if (coupon.getType() == CouponType.CART_WISE) {
                if (coupon.getDetails().containsKey("threshold")) {
                    double threshold = ((Number) coupon.getDetails().get("threshold")).doubleValue();
                    if (cart.getTotalAmount() >= threshold) {
                        applicableCoupons.add(coupon);
                        logger.info("CART_WISE coupon applicable: {}", coupon);
                    }
                }
            }

            // PRODUCT_WISE coupon application logic
            else if (coupon.getType() == CouponType.PRODUCT_WISE) {
                if (coupon.getDetails().containsKey("product_id") && coupon.getDetails().containsKey("discount")) {
                    long productId = ((Number) coupon.getDetails().get("product_id")).longValue();
                    for (CartItem item : cartItems) {
                        if (item.getProduct().getProductId() == productId) {
                            applicableCoupons.add(coupon);
                            logger.info("PRODUCT_WISE coupon applicable for product ID: {}", productId);
                            break;
                        }
                    }
                }
            }

            // BxGy coupon application logic
            else if (coupon.getType() == CouponType.BxGy) {
                if (coupon.getDetails().containsKey("buy_products") && coupon.getDetails().containsKey("get_products")) {
                    List<Map<String, Object>> buyProducts = (List<Map<String, Object>>) coupon.getDetails().get("buy_products");
                    boolean allProductsMatched = true;

                    // Ensure all required products are in the cart
                    for (Map<String, Object> buyProduct : buyProducts) {
                        long buyProductId = ((Number) buyProduct.get("product_id")).longValue();
                        int requiredQuantity = ((Number) buyProduct.get("quantity")).intValue();
                        boolean productMatched = false;

                        for (CartItem item : cartItems) {
                            if (item.getProduct().getProductId() == buyProductId && item.getQuantity() >= requiredQuantity) {
                                productMatched = true;
                                break;
                            }
                        }

                        if (!productMatched) {
                            allProductsMatched = false;
                            break;
                        }
                    }

                    // Apply the coupon if all buy products are matched
                    if (allProductsMatched) {
                        applicableCoupons.add(coupon);
                        logger.info("BxGy coupon applicable: {}", coupon);
                    }
                }
            }
        }

        logger.info("Total applicable coupons: {}", applicableCoupons.size());
        return applicableCoupons;
    }

    // Apply a coupon to a cart and update the cart total or item discounts accordingly
    public Cart applyCoupon(Long id, Cart cart) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            logger.error("Cart is empty or not initialized");
            throw new IllegalArgumentException("Cart is empty or not initialized");
        }

        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));

        Map<String, Object> details = coupon.getDetails();
        logger.info("Applying coupon with id: {} to cart", id);

        // Apply the coupon based on its type
        switch (coupon.getType()) {
            case CART_WISE:
                if (details != null && details.containsKey("discount")) {
                    double discount = ((Number) details.get("discount")).doubleValue();
                    double totalDiscount = cart.getTotalPrice() * (discount / 100);
                    cart.setTotalPrice(cart.getTotalPrice() - totalDiscount);
                    logger.info("Applied CART_WISE discount: {}%", discount);
                }
                break;

            case PRODUCT_WISE:
                if (details != null && details.containsKey("product_id") && details.containsKey("discount")) {
                    long productId = ((Number) details.get("product_id")).longValue();
                    double discount = ((Number) details.get("discount")).doubleValue();

                    for (CartItem item : cart.getItems()) {
                        if (item.getProduct().getProductId() == productId) {
                            double itemDiscount = item.getQuantity() * (discount / 100) * item.getProduct().getPrice();
                            item.setTotalDiscount(itemDiscount);
                            cart.setTotalPrice(cart.getTotalPrice() - itemDiscount);
                            logger.info("Applied PRODUCT_WISE discount for product ID: {}", productId);
                            break;
                        }
                    }
                }
                break;

            case BxGy:
                if (details != null && details.containsKey("get_products") && details.containsKey("buy_products")) {
                    List<Map<String, Object>> buyProducts = (List<Map<String, Object>>) details.get("buy_products");
                    List<Map<String, Object>> getProducts = (List<Map<String, Object>>) details.get("get_products");

                    boolean allProductsMatched = true;

                    // Check if all required buy products are in the cart
                    for (Map<String, Object> buyProduct : buyProducts) {
                        long buyProductId = ((Number) buyProduct.get("product_id")).longValue();
                        int requiredQuantity = ((Number) buyProduct.get("quantity")).intValue();
                        boolean productMatched = false;

                        for (CartItem item : cart.getItems()) {
                            if (item.getProduct() != null && item.getProduct().getProductId() == buyProductId && item.getQuantity() >= requiredQuantity) {
                                productMatched = true;
                                break;
                            }
                        }

                        if (!productMatched) {
                            allProductsMatched = false;
                            break;
                        }
                    }

                    // Apply the coupon if all buy products are matched
                    if (allProductsMatched) {
                        for (Map<String, Object> getProduct : getProducts) {
                            long getProductId = ((Number) getProduct.get("product_id")).longValue();
                            double discount = ((Number) getProduct.get("discount")).doubleValue();

                            for (CartItem item : cart.getItems()) {
                                if (item.getProduct().getProductId() == getProductId) {
                                    double itemDiscount = item.getQuantity() * (discount / 100) * item.getProduct().getPrice();
                                    item.setTotalDiscount(itemDiscount);
                                    cart.setTotalPrice(cart.getTotalPrice() - itemDiscount);
                                    logger.info("Applied BxGy discount for get product ID: {}", getProductId);
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
        }

        logger.info("Coupon applied successfully to the cart");
        return cart;
    }
}