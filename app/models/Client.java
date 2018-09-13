package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 *
 * @author Koman
 */
@Entity
public class Client extends Model {

    @Column(name="client_id")
    public String clientId;

    @Column(name="username")
    public String username;

    @Column(name="password")
    public String password;

    @Column(name="firstName")
    @Required(message="First name is required")
    public String firstName;

    @Column(name="surname")
    @Required(message="Surname is required")
    public String surname;

    @Column(name="street_address_line1")
    @Required(message="Address is required")
    public String streetAddressLine1;

    @Column(name="street_address_line2")
    public String streetAddressLine2;

    @Column(name="city")
    @Required(message="City is required")
    public String city;

    @Column(name="province")
    @Required(message="Province is required")
    public String province;

    @Column(name="street_post_code")
    @Required(message="Post code is required")
    public String streetPostCode;

    @Column(name="country")
    @Required(message="Country is required")
    public String country;

    @Column(name="telephone")
    @Required(message="Telephone is required")
    public String telephone;

    @Column(name="fax")
    public Integer fax;

    @Column(name="cell")
    public int cell;

    @Email
    @Column(name="email")
    @Required(message="E-mail is required")
    public String email;

    @Column(name="deliveryType")
    @Required(message="Delivery type is required")
    public String deliveryType;

    public Client(String firstName, String surname, String streetAddressLine1, String streetAddressLine2, String city, String streetPostCode, 
                  String country, String telephone, String email, String deliveryType) {
        
        this.firstName = firstName;
        this.surname = surname;
        this.streetAddressLine1 = streetAddressLine1;
        this.streetAddressLine2 = streetAddressLine2;
        this.city = city;
        this.streetPostCode = streetPostCode;
        this.country = country;
        this.telephone = telephone;
        this.email = email;
        this.deliveryType = deliveryType;
    }

    @Override
    public String toString() {
        return firstName + " " + surname;
    }

}
