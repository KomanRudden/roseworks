package controllers;

import java.util.List;
import models.Order;
import notifiers.Notifier;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.With;

@Check("admin")
@With(Secure.class)
public class Orders extends Controller {
    
    public static void orders() {
                
        render();
    }
    
    public static void pendingOrders() {
                
        List<Order> orders = Order.find("complete = ? and deleted = ?", false, false).fetch();
        render(orders);
    }
    
    public static void complete(long id, String postage) {
        
        Order orderToEdit = null;
        if (id == 0) {
            orderToEdit = Cache.get("order", Order.class);
            orderToEdit = Order.find("byId", orderToEdit.id).first();
        } else {
            orderToEdit = Order.find("byId", id).first();
        }
        
        orderToEdit.complete = true;
        orderToEdit.save();
        
        long post = Long.valueOf(postage);
        Notifier.mailPaymentRequest(orderToEdit, post);
        
        render("@pendingOrders");
    }
    
    public static void delete(long id) {
    
        Order orderToEdit = Order.find("byId", id).first();
        orderToEdit.deleted = true;
        orderToEdit.save();
        
        pendingOrders();
    }
    
    public static void retrieveDetails(long id) {
        
        Order refreshedOrder = Order.find("byId", id).first();
        System.out.println(refreshedOrder.lineItems.size());
        Cache.set("order", refreshedOrder);
        render("@orderDetails", refreshedOrder);
    }
    
    public static void orderHistory() {
        
    }
    
    public static void communicate() {
        
    }
    
    public static void specials() {
        
    }
}
