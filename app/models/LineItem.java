package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import play.db.jpa.Model;

/**
 *
 * @author Koman
 */
@Entity
public class LineItem extends Model {

    @OneToOne
    public Product product;

    @Column(name="quantity")
    public int quantity;

    @Column(name="unit_price")
    public int unitPrice;

    public LineItem(Product product, Integer quantity, int unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LineItem other = (LineItem) obj;
        if (this.product != other.product && (this.product == null || !this.product.equals(other.product))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

}
