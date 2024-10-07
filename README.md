# Coupons Management System

## Overview
The Coupons Management System is a RESTful API designed for managing discount coupons in an e-commerce platform. It offers full CRUD operations on coupons and supports applying these coupons to shopping carts based on various criteria. The system ensures a smooth, scalable, and maintainable solution while handling real-world constraints like coupon expiration, product-specific discounts, and Buy X Get Y (BxGy) deals.

## Features

### Functionality

1. **Coupon Management**
   - **Create Coupon:** Create new coupons with different types (CART_WISE, PRODUCT_WISE, BxGy) and define associated rules, including thresholds, discounts, and expiration dates.
   -     http://localhost:8080/coupons - POST (For Cart-wise, Product-wise & Buy X Get Y)
   - ![cart-wise](https://github.com/user-attachments/assets/22f29355-81b3-40fc-af32-2b025323ad4f)

   - ![product-wise](https://github.com/user-attachments/assets/2a175b0a-f0da-4dc3-9c54-a06d6a93b516)

   - ![buyXgetY](https://github.com/user-attachments/assets/23781875-e780-4232-ae57-eb910d120f26)

   - **Update Coupon:** Modify existing coupons by changing their type, rules, or expiration date.
     
         http://localhost:8080/coupons/1 - PUT (To Update)
   - **Delete Coupon:** Delete coupons by their unique ID, preventing them from being applied to future carts.
     
          http://localhost:8080/coupons/3 - DELETE (To Delete)
   - **Get All Coupons:** Fetch a list of all available coupons.
     
          http://localhost:8080/coupons - GET
   - ![retrive all](https://github.com/user-attachments/assets/7d83757a-2156-4460-b1bd-3d6e47595a27)

   - **Get Coupon by ID:** Retrieve a coupon by its unique identifier.
     
          http://localhost:8080/coupons/2 - GET

2. **Coupon Application**
   - **Get Applicable Coupons:** Based on the contents of a cart, the system checks which coupons are eligible and returns a list of those that can be applied.
     
          http://localhost:8080/coupons/applicable-coupons - POST
   - ![Applicable-coupon](https://github.com/user-attachments/assets/73ebbeb0-de41-40ac-a219-0e29c83112e0)

   - **Apply Coupon:** Apply an eligible coupon to the cart, adjusting the total price or specific item prices as defined by the coupon's rules.
     
         http://localhost:8080/coupons/apply-coupon/1 - POST 
   - ![ApplyCoupon request](https://github.com/user-attachments/assets/8e7f137b-ae2a-4790-8333-d7c49b5e242e)
   - ![ApplyCoupon response](https://github.com/user-attachments/assets/4f91ff1e-58c4-45b7-afd8-97b52a31c4f3)



3. **Expiration Management**
   - Automatically ignore coupons that are past their expiration date when fetching applicable coupons.

4. **Logging**
   - Integrated logging for monitoring API calls, including coupon creation, application, and error tracking.

5. **Junit**
   - Implement unit tests for your methods using JUnit and Mockito for both the getApplicableCoupons and applyCoupon methods.

## Edge Cases
The following edge cases have been considered and documented to ensure that the system is robust and handles real-world scenarios effectively.

### 1. **Coupon Constraints**

   - **Expired Coupons:**
     - Coupons that have passed their expiration date should not be considered when determining applicable coupons.
     - Coupons applied manually after expiration should result in a rejection response with a proper error message.

   - **Threshold Violations (CART_WISE Coupons):**
     - CART_WISE coupons require a minimum cart total (threshold) to be met. If the total value of the cart is below the defined threshold, the coupon should not be applicable.

   - **Product Availability (PRODUCT_WISE Coupons):**
     - For PRODUCT_WISE coupons, the coupon should only be applied if the specific product exists in the cart and the quantity of the product meets the required amount (if specified).

   - **Buy X Get Y (BxGy Coupons):**
     - All products required for the Buy X part of the coupon should be present in the cart with the specified quantities. If any product is missing or the quantity is insufficient, the coupon should not apply.
     - Get Y products should be correctly added to the cart, and their discount calculated according to the coupon's definition.

### 2. **Null Handling**

   - **Null Cart or Cart Items:**
     - If the cart is null or if the list of cart items is null, an appropriate error should be thrown, preventing the system from processing incomplete data.

   - **Null Coupon Details:**
     - Coupons with missing or incomplete details (e.g., missing discount percentages or product IDs) should be ignored when determining applicable coupons or applying discounts.

### 3. **Multiple Coupons**

   - **Stacking Coupons:**
     - If the system supports multiple coupon applications, ensure a defined behavior (e.g., whether coupons can stack, replace each other, or the highest value coupon is applied).
     - Prevent excessive stacking that might result in negative total prices or overly generous discounts.

   - **Conflicting Coupons:**
     - If multiple coupons apply to the same product or cart, the system should handle potential conflicts (e.g., two PRODUCT_WISE coupons for the same item). Define whether only one coupon applies, or both can stack.

### 4. **Discount Calculation**

   - **Zero or Negative Discounts:**
     - Ensure the system gracefully handles scenarios where a coupon has a 0% or negative discount value. Such coupons should either be ignored or result in no change to the cart total.

   - **Percentage Discounts on Small Items:**
     - Consider edge cases where a percentage discount results in very small fractional amounts. For example, rounding errors when applying a 5% discount on a $0.99 product.

### 5. **Buy X Get Y (BxGy) Coupons**

   - **Buy Products Insufficient:**
     - If the customer does not have enough of the "Buy X" products in the cart, the coupon should not apply.
   
   - **Applying Discounts to Free Items:**
     - Ensure correct handling when applying discounts on "Get Y" products in BxGy deals. It should either provide these products at a 100% discount or apply a defined discount amount.

   - **Mixed Products:**
     - What happens if a customer has a mix of buy products in the cart? For example, they buy 3 apples and 2 oranges but the coupon requires 5 apples to get the discount. These cases should be considered and clearly defined in the logic.

### 6. **Database Integrity and Performance**

   - **Database Indexing:**
     - Coupons should be indexed by relevant fields, such as `expirationDate`, `type`, and `product_id` to improve the efficiency of lookups.
     - Indexing can prevent performance degradation, especially when dealing with large datasets (e.g., thousands of coupons in a busy e-commerce site).

   - **Concurrent Modifications:**
     - Handle concurrent access to the same coupon or cart (e.g., two users trying to apply different coupons simultaneously). Use optimistic locking or other techniques to ensure data integrity.

### 7. **Overuse of Coupons**

   - **Coupon Usage Limits:**
     - Coupons should have configurable usage limits (e.g., max number of redemptions globally or per user).
     - Track how many times a coupon has been used and prevent further use once limits are reached.

   - **Coupon Abuse:**
     - Detect and prevent coupon abuse, such as multiple accounts using the same coupon code or loopholes where coupons apply incorrectly.

### 8. **Edge Performance Issues**

   - **Heavy Cart Loads:**
     - Ensure the system performs well when carts contain many items (e.g., 100+ items) and that coupons can be applied efficiently without timeout errors or heavy database operations.

   - **Large Number of Coupons:**
     - When a large number of coupons (e.g., 1,000+) are in the database, querying for applicable coupons could become expensive. Consider optimizing by reducing query sizes, using pagination, or caching frequently used coupons.

   - **Asynchronous Coupon Checking:**
     - For performance improvements in large systems, consider checking applicable coupons asynchronously or with a caching mechanism.

### 9. **Expired Coupon Detection and Error Messaging**

   - **Invalid Coupons:**
     - Proper handling of invalid coupons should include meaningful error messages when:
     - A coupon does not exist.
     - A coupon is expired.
     - A coupon does not meet cart requirements (e.g., thresholds, required items).
  
   - **Error Logging:**
     - Ensure that errors, especially related to coupon application and business logic, are logged for future diagnostics.

### 10. **Security Considerations**

   - **Coupon Tampering:**
     - Ensure that coupons cannot be tampered with by users when submitted through API requests. Validate all incoming coupon IDs, details, and associated rules on the server side.

### 11. **User-specific Coupons**

   - **Personalized Coupons:**
     - Coupons that are valid only for certain user types (e.g., new customers or premium members).
     - Ensure that only eligible users can apply these personalized coupons.

### 12. **Multiple Cart Sessions**

   - **Cross-session Coupons:**
     - Handle scenarios where a user applies a coupon across multiple active sessions (e.g., from a mobile app and desktop site simultaneously).
  
---

## Limitations and Assumptions

- **Assumption:** The coupon details in the database are correctly formatted and do not contain erroneous data.
- **Assumption:** The system assumes the cart data is always complete and up to date, reflecting the latest product prices and quantities.
- **Limitation:** The current implementation does not handle user-specific coupon limits (e.g., restricting the number of times a user can apply a specific coupon).
- **Limitation:** Multi-coupon stacking is not supported yet, and the system only applies one coupon per transaction at the moment.
- **Limitation:** The performance optimizations for large-scale datasets (e.g., caching, asynchronous processing) are planned for future iterations.
  
---

## Conclusion
The Coupons Management System is designed to be scalable, maintainable, and efficient, handling various real-world constraints like expiration dates, product-specific discounts, and BxGy deals. This README outlines the system's core functionality, along with comprehensive documentation of edge cases to ensure robustness. The limitations and assumptions section provides clarity on the current scope and future enhancement opportunities.
