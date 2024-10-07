package com.coupons_management;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.coupons_management.Entity.Cart;
import com.coupons_management.Entity.Coupon;
import com.coupons_management.Enum.CouponType;
import com.coupons_management.Repository.CouponRepository;
import com.coupons_management.Service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetApplicableCoupons_NoCoupons() {
        Cart cart = new Cart();
        cart.setTotalPrice(100.0);
        cart.setItems(Collections.emptyList());

        when(couponRepository.findAll()).thenReturn(Collections.emptyList());

        List<Coupon> result = couponService.getApplicableCoupons(cart);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetApplicableCoupons_ExpiredCoupon() {
        Cart cart = new Cart();
        cart.setTotalPrice(100.0);
        Coupon expiredCoupon = new Coupon();
        expiredCoupon.setExpirationDate(LocalDateTime.now().minusDays(1));

        when(couponRepository.findAll()).thenReturn(Arrays.asList(expiredCoupon));

        List<Coupon> result = couponService.getApplicableCoupons(cart);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testApplyCoupon_ValidCoupon() {
        Cart cart = new Cart();
        cart.setTotalPrice(100.0);
        Coupon validCoupon = new Coupon();
        validCoupon.setType(CouponType.CART_WISE);
        validCoupon.setDetails(Collections.singletonMap("discount", 10));
        validCoupon.setExpirationDate(LocalDateTime.now().plusDays(1));

        when(couponRepository.findById(1L)).thenReturn(Optional.of(validCoupon));

        Cart updatedCart = couponService.applyCoupon(1L, cart);
        assertEquals(90.0, updatedCart.getTotalPrice());
        // Additional assertions based on how discounts are applied
    }
}