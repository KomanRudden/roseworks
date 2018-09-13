package models;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import play.db.jpa.Model;

/**
 *
 * @author Koman
 */
@Entity
public class Order extends Model {

    @Column(name="order_id")
    public long orderId;

    @Column(name = "complete")
    public boolean complete;
    
    @Column(name = "deleted")
    public boolean deleted;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    public Client client;

    @OneToMany(fetch = FetchType.EAGER)
    public List<LineItem> lineItems;

    public Order(Client client, boolean complete, List<LineItem> lineItems) {
        this.client = client;
        this.lineItems = lineItems;
        this.complete = complete;
    }

    public long calculateOrderTotal() {
        
        long total = 0L;
        for (LineItem lineItem : lineItems) {
            total += lineItem.quantity * lineItem.unitPrice;
        }
        return total;
    }
    
    @Override
    public String toString() {
        return "Order Id=" + orderId + ", Client=" + client + ", LineItems=" + lineItems;
    }   
    
}
