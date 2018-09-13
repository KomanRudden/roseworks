package controllers;

import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.StringTokenizer;
import models.Client;
import models.LineItem;
import models.Order;
import models.Product;
import notifiers.Notifier;
import notifiers.Query;
import play.Logger;
import play.cache.Cache;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.mvc.*;

public class Application extends Controller {

    public Notifier notifier;

    public static void index() {

        Logger.info("Play startup...");

	List<Product> justIn = Product.find("select p from Product p where p.id in ('128', '129', '130', '131')").fetch();

        StringBuilder sb = new StringBuilder("select p from Product p where p.id in ('");
        for (int i = 0; i <= 20; i++) {
            sb.append(getRandomIndex());
            sb.append("','");
        }
        sb.append("')");

        List<Product> latest = Product.find(sb.toString()).fetch();
        List<Product> products = Product.find("select p from Product p order by p.totalSold desc and p.difficulty = '3'").fetch(16);
        render(justIn, latest, products);
    }

    private static int getRandomIndex() {
        Random random = new Random();
        return random.nextInt(125);
    }

    public static void admin() {

        Logger.info("Admin login loading...");

        render();
    }

    public static void login() {

        Logger.info("Logging into Admin Console...");


    }

    public static void catalogue(String productCategory) {

        Logger.info("Retrieving catalogue...");

        List<Product> products = Product.find("byProductCategory", productCategory).fetch();
        render(productCategory, products);
    }

    public static void product(Long id) {

        Logger.info("Retrieving product..." + id);

        Product product = Product.find("byId", id).first();
        List<Product> products = Product.find("productCategory = ? and productId != ?", product.productCategory, product.productId).fetch(4);
        render(product, products);
    }

    public static void aboutUs() {
        render();
    }

    public static void thankYou() {
        render();
    }

    public static void paymentCancelled() {
	render();
    }

    public static void orderThankYou() {
        render();
    }

    public static void queryThankYou() {
	render();
    }

    public static void error() {
	render();
    }

    public static void paymentLink(String payuTotal) {

        int total = Integer.valueOf(payuTotal) / 100;
        render(total, payuTotal);
    }

    public static void checkout() {

        Logger.info("Checking out...");
        List<LineItem> cart = Cache.get(session.getId(), List.class);
        Long totalPrice = calculateTotal(cart);
        render(cart, totalPrice);
    }

    public static void addToCart(Long id, int qty) {

        Product product = Product.findById(id);
        LineItem lineItem = new LineItem(product, 1, product.productPrice);

        Logger.info("Adding product ( " + product.productId + " - " + product.productName + " ) to cart...");

        List<LineItem> cart = Cache.get(session.getId(), List.class);
        if (cart == null) {
            Logger.info("Creating new shopping cart...");
            cart = new ArrayList<LineItem>();
            cart.add(lineItem);

        } else {
            cart.add(lineItem);
        }
        Cache.add(session.getId(), cart);
        shoppingCart();
    }

    public static void shoppingCart() {

        Logger.info("Showing shopping cart...");

        List<LineItem> cart = Cache.get(session.getId(), List.class);
        Long totalPrice = calculateTotal(cart);
        render(cart, totalPrice);
    }

    public static void updateShoppingCart(String quantity) {

        Logger.info("Updating quantities in shopping cart...");

        if (quantity == null) {
            shoppingCart();
        }
        StringTokenizer st = new StringTokenizer(quantity, ", ");
        List<LineItem> cart = Cache.get(session.getId(), List.class);
        if (cart != null) {
            for (LineItem lineItem : cart) {
                try {
                    String qty = st.nextToken();
                    if (Integer.valueOf(qty) == 0) {
                        throw new NumberFormatException();
                    }
                    lineItem.quantity = Integer.valueOf(qty);
                } catch (NumberFormatException nfe) {
                    Logger.error("Only numbers between 1-9 are allowed");
                    Validation.clear();
                    Validation.addError("Cart", "Only numbers between 1-9 are allowed");
                    shoppingCart();
                }
            }
        }
        Cache.set(session.getId(), cart);
        shoppingCart();
    }

    public static Long calculateTotal(List<LineItem> cart) {

        Logger.info("Calculating shopping cart total price...");

        Long totalPrice = 0L;
        if (cart != null) {
            for (LineItem cartItem : cart) {
                totalPrice += (cartItem.product.productPrice * cartItem.quantity);
            }
        }
        return totalPrice;
    }

    public static void delete(LineItem lineItem) {

        Logger.info("Deleting product - " + lineItem.product.productName);

        List<LineItem> cart = Cache.get(session.getId(), List.class);
        cart.remove(lineItem);
        Cache.add(session.getId(), cart);
        shoppingCart();
    }

    public static void contactUs() {

        render();
    }

    public static void continueShopping() {

        Logger.info("Continue shopping...");

        List<LineItem> cart = Cache.get(session.getId(), List.class);
        if (cart == null || cart.isEmpty()) {
            index();
        } else {
            LineItem lastViewedProduct = cart.get(cart.size() - 1);
            String productCategory = lastViewedProduct.product.productCategory;
            catalogue(productCategory);
        }
    }

    public static void sendQuery(Query query) {

        Logger.info("Sending query...");

        Notifier.sendQuery(query);

        queryThankYou();
    }

    public static void submit(@Valid Client client) {

        Logger.info("Placing order...");

        List<LineItem> cart = Cache.get(session.getId(), List.class);
        if (cart == null || cart.isEmpty()) {
            Logger.error("Cart is empty, cannot submit order");
            Validation.clear();
            Validation.addError("Cart", "You cannot place an order with an empty shopping cart");
            Long totalPrice = calculateTotal(cart);
            render("@checkout", cart, client, totalPrice);
        }

        Validation.required("firstName", client);
        if (Validation.hasErrors()) {
            System.out.println(Validation.errors());
            Long totalPrice = calculateTotal(cart);
            render("@checkout", cart, client, totalPrice);            
        }
        
        Order order = saveOrder(client);
        Notifier.mailOrder(order);
        Cache.delete(session.getId());
        orderThankYou();
    }

    public static void payment(Integer orderId) {

        Order order = Order.findById(orderId);
        render(order);
    }

    private static Order saveOrder(Client client) {

        client.save();
        List<LineItem> cart = Cache.get(session.getId(), List.class);
        for (LineItem lineItem : cart) {
            updateProductTotalSoldCount(lineItem);
            lineItem.save();
        }
        Order order = new Order(client, false, cart);
        order.save();
        return order;
    }

    private static void updateProductTotalSoldCount(LineItem lineItem) {

        Product product = Product.find("byProductId", lineItem.product.productId).first();
        if (product.totalSold == null) {
            product.totalSold = 0L;
        }
        product.totalSold += new Long(lineItem.quantity);
        product.save();
    }
}
