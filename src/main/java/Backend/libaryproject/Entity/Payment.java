package Backend.libaryproject.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment")
public class Payment {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id")
    private Long id;
    @Column(name = "user_email")
       private String userEmail;
    @Column(name = "amount")
       private double amount;
}
