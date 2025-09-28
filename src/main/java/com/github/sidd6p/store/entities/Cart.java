package com.github.sidd6p.store.entities;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity // This tells EntityManager "this is a database entity"
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Database automatically sets the date_created with DEFAULT(curdate())
    // so we prevent JPA from trying to insert/update this field
    @Column(name = "date_created", insertable = false, updatable = false)
    private LocalDate dateCreated;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    // Business logic methods - Information Expert principle

    /**
     * Checks if the cart contains a product with the given ID
     */
    public boolean hasProduct(Integer productId) {
        return cartItems.stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));
    }

    /**
     * Finds a cart item by product ID
     */
    public Optional<CartItem> findCartItemByProductId(Integer productId) {
        return cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }

    /**
     * Adds a new product to the cart
     * @throws IllegalStateException if product already exists in cart
     */
    public CartItem addProduct(Product product) {
        if (hasProduct(product.getId())) {
            throw new IllegalStateException("Product already exists in cart");
        }

        CartItem cartItem = new CartItem();
        cartItem.setQuantity(1);
        cartItem.setProduct(product);
        cartItem.setCart(this);
        cartItems.add(cartItem);

        return cartItem;
    }

    /**
     * Updates the quantity of a product in the cart
     * @return true if the product was found and updated, false otherwise
     */
    public boolean updateProductQuantity(Integer productId, Integer newQuantity) {
        Optional<CartItem> cartItem = findCartItemByProductId(productId);
        if (cartItem.isPresent()) {
            cartItem.get().setQuantity(newQuantity);
            return true;
        }
        return false;
    }

    /**
     * Checks if the cart is empty
     */
    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    /**
     * Gets the total number of items in the cart
     */
    public int getTotalItems() {
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
