package com.ecommerce.main;

import com.ecommerce.dao.OrderProcessorRepositoryImpl;
import com.ecommerce.entity.Customer;
import com.ecommerce.entity.Product;
//import com.ecommerce.exception.CustomerNotFoundException;
//import com.ecommerce.exception.ProductNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainModule {

    private static final Scanner scanner = new Scanner(System.in);
    private static final OrderProcessorRepositoryImpl orderProcessor = new OrderProcessorRepositoryImpl();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("Welcome to E-commerce Application");
            System.out.println("1. Register Customer");
            System.out.println("2. Create Product");
            System.out.println("3. Delete Product");
            System.out.println("4. Add to Cart");
            System.out.println("5. View Cart");
            System.out.println("6. Place Order");
            System.out.println("7. View Customer Order");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerCustomer();
                    break;
                case 2:
                    createProduct();
                    break;
                case 3:
                    deleteProduct();
                    break;
                case 4:
                    addToCart();
                    break;
                case 5:
                    viewCart();
                    break;
                case 6:
                    placeOrder();
                    break;
                case 7:
                    viewCustomerOrders();
                    break;
                case 8:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please choose again.");
            }
        }
        scanner.close();
    }

    private static void registerCustomer() {
        System.out.print("Enter Customer Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Customer Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        Customer customer = new Customer(name, email, password);
        boolean success = orderProcessor.createCustomer(customer);

        if (success) {
            System.out.println("Customer registered successfully!");
        } else {
            System.out.println("Failed to register customer.");
        }
    }

    private static void createProduct() {
        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Product Price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter Product Description: ");
        String description = scanner.nextLine();
        System.out.print("Enter Stock Quantity: ");
        int stockQuantity = scanner.nextInt();

        Product product = new Product(name, price, description, stockQuantity);
        boolean success = orderProcessor.createProduct(product);

        if (success) {
            System.out.println("Product created successfully!");
        } else {
            System.out.println("Failed to create product.");
        }
    }

    private static void deleteProduct() {
        System.out.print("Enter Product ID to delete: ");
        int productId = scanner.nextInt();

        boolean success = orderProcessor.deleteProduct(productId);
        if (success) {
            System.out.println("Product deleted successfully!");
        } else {
            System.out.println("Failed to delete product.");
        }
    }
    /*  private static void deleteProduct() {
        System.out.print("Enter Product ID to delete: ");
        int productId = scanner.nextInt();

        try {
            boolean success = orderProcessor.deleteProduct(productId);
            if (success) {
                System.out.println("Product deleted successfully!");
            } else {
                System.out.println("Failed to delete product.");
            }
        } catch (ProductNotFoundException e) {
            System.out.println("Product not found: " + e.getMessage());
        }
    }*/

    private static void addToCart() {
        System.out.print("Enter Customer ID: ");
        int customerId = scanner.nextInt();
        System.out.print("Enter Product ID: ");
        int productId = scanner.nextInt();
        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();

        boolean success = orderProcessor.addToCart(customerId, productId, quantity);
        if (success) {
            System.out.println("Product added to cart successfully!");
        } else {
            System.out.println("Failed to add product to cart.");
        }
    }

    private static void viewCart() {
        System.out.print("Enter Customer ID: ");
        int customerId = scanner.nextInt();

        List<Product> productsInCart = orderProcessor.getAllFromCart(customerId);
        System.out.println("Products in Cart:");
        for (Product product : productsInCart) {
            System.out.println(product);
        }
    }

    private static void placeOrder() {
        System.out.print("Enter Customer ID: ");
        int customerId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter Shipping Address: ");
        String shippingAddress = scanner.nextLine();

        List<Map<Product, Integer>> orderItems = new ArrayList<>();
        // Example logic to populate orderItems; in a real app, you may need to retrieve from the cart

        boolean success = orderProcessor.placeOrder(customerId, orderItems, shippingAddress);
        if (success) {
            System.out.println("Order placed successfully!");
        } else {
            System.out.println("Failed to place order.");
        }
    }

    private static void viewCustomerOrders() {
        System.out.print("Enter Customer ID: ");
        int customerId = scanner.nextInt();

        List<Map<Product, Integer>> orders = orderProcessor.getOrdersByCustomer(customerId);
        System.out.println("Customer Orders:");
        for (Map<Product, Integer> order : orders) {
            System.out.println(order);
        }
    }
}
