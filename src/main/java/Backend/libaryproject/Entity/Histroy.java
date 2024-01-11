package Backend.libaryproject.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "History")
@Data
@NoArgsConstructor
public class Histroy {

    //Had to override the all args constructors because the id was not being generated
    //Same with the checkout class
    public Histroy( String userEmail, String checkoutDate, String returnedDate,
                    String title, String author, String description, String img) {
        this.userEmail = userEmail;
        this.checkoutDate = checkoutDate;
        this.returnedDate = returnedDate;
        this.title = title;
        this.author = author;
        this.description = description;
        this.img = img;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_email")
    private String userEmail;

    @Column(name="checkout_date")
    private String checkoutDate;

    @Column(name="returned_date")
    private String returnedDate;

    @Column(name="title")
    private String title;

    @Column(name="author")
    private String author;

    @Column(name="description")
    private String description;

    @Column(name="img")
    private String img;

}
