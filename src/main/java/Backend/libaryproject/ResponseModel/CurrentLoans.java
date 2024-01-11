package Backend.libaryproject.ResponseModel;

import Backend.libaryproject.Entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentLoans {

    private Book book;
    int daysLeft;

}
