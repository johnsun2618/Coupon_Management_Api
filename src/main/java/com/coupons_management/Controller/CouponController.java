package com.coupons_management.Controller;

import com.coupons_management.Entity.Cart;
import com.coupons_management.Entity.Coupon;
import com.coupons_management.Exception.CouponNotFoundException;
import com.coupons_management.Service.CouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;
    private static final Logger logger = LoggerFactory.getLogger(CouponController.class);

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        logger.info("Creating a new coupon: {}", coupon);
        Coupon newCoupon = couponService.createCoupon(coupon);
        return ResponseEntity.ok(newCoupon);
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        logger.info("Fetching all coupons");
        List<Coupon> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable Long id) {
        logger.info("Fetching coupon with id: {}", id);
        Coupon coupon = couponService.getCouponById(id)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with id " + id));
        return ResponseEntity.ok(coupon);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        logger.info("Updating coupon with id: {}", id);
        Coupon updatedCoupon = couponService.updateCoupon(id, coupon);
        return ResponseEntity.ok(updatedCoupon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        logger.info("Deleting coupon with id: {}", id);
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint to fetch applicable coupons for a given cart
    @PostMapping("/applicable-coupons")
    public ResponseEntity<List<Coupon>> getApplicableCoupons(@RequestBody Cart cart) {
        logger.info("Fetching applicable coupons for cart: {}", cart);
        List<Coupon> applicableCoupons = couponService.getApplicableCoupons(cart);
        return ResponseEntity.ok(applicableCoupons);
    }

    // Endpoint to apply coupon
    @PostMapping("/apply-coupon/{id}")
    public ResponseEntity<Cart> applyCoupon(@PathVariable Long id, @RequestBody Cart cart) {
        logger.info("Applying coupon with id: {} to cart: {}", id, cart);
        Cart updatedCart = couponService.applyCoupon(id, cart);
        return ResponseEntity.ok(updatedCart);
    }
}