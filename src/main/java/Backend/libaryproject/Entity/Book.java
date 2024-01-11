package Backend.libaryproject.Entity;
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "book")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// GenerationType.IDENTITY is
    // used to auto increment the value of id
    @Column(name = "id")
private Long id;
    @Column(name = "Author")
private String Author;
    @Column(name = "Title")
private String Title;
    @Column(name = "description")
private String description;
    @Column(name = "copies")
private int copies;
    @Column(name = "copies_available")
private int available_copies;
    @Column(name = "category")
private String category;
    @Column(name = "img")
private String Image;

}
