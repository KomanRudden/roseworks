package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author Koman
 */
@Entity
public class Product extends Model {

    @Column(name="product_id")
    public String productId;

    @Column(name="product_name")
    public String productName;

    @Column(name="product_description")
    public String productDescription;

    @Column(name="product_price")
    public int productPrice;

    @Column(name="product_difficulty")
    public int productDifficulty;

    @Column(name="product_creator")
    public String productCreator;

    @Column(name="product_image_url")
    public String productImageUrl;

    @Column(name="product_category")
    public String productCategory;

    @Column(name="product_size")
    public String productSize;

    @Column(name="total_sold")
    public Long totalSold;
    
    public int quantity;

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Product other = (Product) obj;
        if ((this.productId == null) ? (other.productId != null) : !this.productId.equals(other.productId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        int hash = 7;
        hash = 97 * hash + (this.productId != null ? this.productId.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return productName;
    }
    
}
