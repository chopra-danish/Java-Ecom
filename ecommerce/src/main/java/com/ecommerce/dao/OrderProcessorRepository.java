package com.ecommerce.dao;

//import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Customer;
//import com.ecommerce.entity.Order;
import com.ecommerce.entity.Product;
//import com.ecommerce.exception.CustomerNotFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface OrderProcessorRepository {
    
    boolean createProduct(Product product);

    boolean createCustomer(Customer customer);

    boolean deleteProduct(int productId);

    boolean deleteCustomer(int customerId);

    boolean addToCart(int customerId, int productId, int quantity) throws SQLException;

    boolean removeFromCart(int customerId, int productId);

    List<Product> getAllFromCart(int customerId);

    boolean placeOrder(int customerId, List<Map<Product, Integer>> productQuantityMap, String shippingAddress);

    List<Map<Product, Integer>> getOrdersByCustomer(int customerId);
}
