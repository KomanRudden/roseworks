package notifiers;

import controllers.Application;
import models.Order;
import play.Logger;
import play.mvc.Mailer;
import static play.mvc.Mailer.addBcc;
import static play.mvc.Mailer.addRecipient;
import static play.mvc.Mailer.send;
import static play.mvc.Mailer.setFrom;
import static play.mvc.Mailer.setSubject;

/**
 *
 * @author Koman
 */
public class Notifier extends Mailer {

    public static void mailOrder(Order order) {

        Logger.info("Sending order...");

        setFrom("do_not_reply@roseworks.co.za");
        setSubject("Roseworks Order Confirmation (Order Ref: " +order.id+ ") "+order.client.firstName +" "+order.client.surname);
        addRecipient(order.client.email);
	    addBcc("roseworks.orders@gmail.com");
        addBcc("koman.rudden@gmail.com");

        Long total = Application.calculateTotal(order.lineItems);

        send(order, total);
    }
    
    public static void mailPaymentRequest(Order order, long postage) {
        
        Logger.info("Sending payment request...");
        
        setFrom("do_not_reply@roseworks.co.za");
        
        setSubject("Roseworks - Payment Due (Order Ref: " +order.id+ ") "+order.client.firstName +" "+order.client.surname);
        addRecipient(order.client.email);
        addBcc("roseworks.orders@gmail.com");
        addBcc("koman.rudden@gmail.com");

        Long total = Application.calculateTotal(order.lineItems);
        total += postage;
        Long payuTotal = total*100;
        
        send(order, total, payuTotal);        
    }
    
    public static void sendQuery(Query query) {

        Logger.info("Sending query...");

        setFrom("queries@roseworks.co.za");
        setSubject("Roseworks customer query");
        addRecipient("roseworks.orders@gmail.com");
        addBcc("koman.rudden@gmail.com");

        send(query);
    }    

}
