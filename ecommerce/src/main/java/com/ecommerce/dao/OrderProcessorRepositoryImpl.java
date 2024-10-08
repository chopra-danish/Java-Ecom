package com.ecommerce.dao;
//import com.ecommerce.exception.CustomerNotFoundException;
//import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Customer;
//import com.ecommerce.entity.Order;
import com.ecommerce.entity.Product;
import com.ecommerce.util.DBConnUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderProcessorRepositoryImpl implements OrderProcessorRepository {

    private Connection connection;

    public OrderProcessorRepositoryImpl() {
        this.connection = DBConnUtil.getConnection();
    }

    @Override
    public boolean createProduct(Product product) {
        String query = "INSERT INTO products (name, price, description, stockQuantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, product.getName());
            pstmt.setDouble(2, product.getPrice());
            pstmt.setString(3, product.getDescription());
            pstmt.setInt(4, product.getStockQuantity());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createCustomer(Customer customer) {
        String query = "INSERT INTO customers (name, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPassword());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteProduct(int productId) {
        String query = "DELETE FROM products WHERE product_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCustomer(int customerId) {
        String query = "DELETE FROM customers WHERE customer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addToCart(int customerId, int productId, int quantity) {
        String query = "INSERT INTO cart (customer_id, product_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, quantity);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    

    @Override
    public boolean removeFromCart(int customerId, int productId) {
        String query = "DELETE FROM cart WHERE customer_id = ? AND product_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, productId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Product> getAllFromCart(int customerId) {
        List<Product> productList = new ArrayList<>();
        String query = "SELECT p.product_id, p.name, p.price, p.description, p.stockQuantity " +
                       "FROM cart c JOIN products p ON c.product_id = p.product_id " +
                       "WHERE c.customer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setDescription(rs.getString("description"));
                product.setStockQuantity(rs.getInt("stockQuantity"));
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    @Override
    public boolean placeOrder(int customerId, List<Map<Product, Integer>> productQuantityMap, String shippingAddress) {
        String orderQuery = "INSERT INTO orders (customer_id, total_price, shipping_address) VALUES (?, ?, ?)";
        String orderItemQuery = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?)";
        try {
            connection.setAutoCommit(false); // Start transaction

            // Calculate total price
            double totalPrice = 0;
            for (Map<Product, Integer> entry : productQuantityMap) {
                for (Map.Entry<Product, Integer> item : entry.entrySet()) {
                    totalPrice += item.getKey().getPrice() * item.getValue();
                }
            }

            // Create order
            try (PreparedStatement pstmt = connection.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, customerId);
                pstmt.setDouble(2, totalPrice);
                pstmt.setString(3, shippingAddress);
                pstmt.executeUpdate();

                // Get generated order ID
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);
                    
                    // Create order items
                    for (Map<Product, Integer> entry : productQuantityMap) {
                        for (Map.Entry<Product, Integer> item : entry.entrySet()) {
                            try (PreparedStatement itemPstmt = connection.prepareStatement(orderItemQuery)) {
                                itemPstmt.setInt(1, orderId);
                                itemPstmt.setInt(2, item.getKey().getProductId());
                                itemPstmt.setInt(3, item.getValue());
                                itemPstmt.executeUpdate();
                            }
                        }
                    }
                }
            }

            connection.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback transaction on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true); // Reset auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Map<Product, Integer>> getOrdersByCustomer(int customerId) {
        List<Map<Product, Integer>> orderList = new ArrayList<>();
        String query = "SELECT o.order_id, oi.product_id, oi.quantity " +
                       "FROM orders o JOIN order_items oi ON o.order_id = oi.order_id " +
                       "WHERE o.customer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<Product, Integer> orderItemMap = new HashMap<>();
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                orderItemMap.put(product, rs.getInt("quantity"));
                orderList.add(orderItemMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }
}
