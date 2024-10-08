package com.ecommerce.dao;

import com.ecommerce.entity.Customer;
import com.ecommerce.entity.Product;
//import com.ecommerce.exception.CustomerNotFoundException;

import org.junit.Before;
import org.junit.Test;

//import java.sql.SQLException; 
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class OrderProcessorRepositoryImplTest {
    private OrderProcessorRepositoryImpl orderProcessor;

    @Before
    public void setUp() {
        // Create a new instance of the repository
        orderProcessor = new OrderProcessorRepositoryImpl();
    }

    @Test
    public void testCreateProduct() {
        // Arrange
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(99.99);
        product.setDescription("A product for testing");
        product.setStockQuantity(50);

        // Act
        boolean isCreated = orderProcessor.createProduct(product);

        // Assert
        assertTrue("Product should be created successfully", isCreated);
    }

    @Test
    public void testAddToCart() {
        // Arrange
        Customer customer = new Customer();
        customer.setCustomerId(1); // Assuming customer with ID 1 exists
        Product product = new Product();
        product.setProductId(2); // Assuming product with ID 2 exists
        int quantity = 2;

        // Act
        boolean isAdded = orderProcessor.addToCart(customer.getCustomerId(), product.getProductId(), quantity);

        // Assert
        assertTrue("Product should be added to cart successfully", isAdded);
    }

    @Test
    public void testPlaceOrder() {
        // Arrange
        Customer customer = new Customer();
        customer.setCustomerId(1); // Assuming customer with ID 1 exists
        Product product = new Product();
        product.setProductId(2); // Assuming product with ID 1 exists
        int quantity = 2;

        // Adding product to cart before placing the order
        orderProcessor.addToCart(customer.getCustomerId(), product.getProductId(), quantity);

        // Act
        boolean isOrdered = orderProcessor.placeOrder(customer.getCustomerId(), List.of(Map.of(product, quantity)), "123 Test St, City");

        // Assert
        assertTrue("Order should be placed successfully", isOrdered);
    }
}

    
