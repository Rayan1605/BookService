package Backend.libaryproject.Controller;

import Backend.libaryproject.Entity.Book;
import Backend.libaryproject.Entity.Checkout;
import Backend.libaryproject.Repository.BookRepository;
import Backend.libaryproject.ResponseModel.CurrentLoans;
import Backend.libaryproject.Service.BookService;
import Backend.libaryproject.Utils.ExtractJwt;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "https://localhost:3000")//This is to allow the React app
// to access the api
@RestController
@RequestMapping("/api/books")
@AllArgsConstructor
public class BookController {
    private final BookService bookService;
    private final BookRepository bookRepository;

    @GetMapping("/secure/currentloans/count") // the secure mean only a user with a
    // role of user can access this
    ////The secure we set up in the okta in the utils folder
    public int currentLoansCount(@RequestHeader(value = "Authorization") String token) {
        //We are extracting the token from the header and passing it to the method
        //we are expecting something in the request header that has a key of Authorization
        // it's validating with okta automatically, and then we are passing the token to the method
        String userEmail = ExtractJwt.extractJwtExtraction(token, "sub");
        //Above is To get the user's email
        return bookService.currentLoansCount(userEmail);
    }

    @PutMapping("/secure/checkout") // the secure mean only a user with a
    // role of user can access this
    //Put is referring to updating the book
    //The secure we set up in the okta in the utils folder
    public Book checkoutBook(@RequestParam Long bookId,
                             @RequestHeader(value = "Authorization") String token) throws Exception {

        String userEmail = ExtractJwt.extractJwtExtraction(token, "sub");
        //Above is To get the user's email
        return bookService.checkoutBook(userEmail, bookId);
    }

    @GetMapping("/secure/ischeckoutedout/byuser")
    public boolean checkoutBookByUser(@RequestParam Long bookId,
                                      @RequestHeader(value = "Authorization") String token) {
        ////The secure we set up in the okta in the utils folder
//Remember this is to check if the user has already checked out the book
        String userEmail = ExtractJwt.extractJwtExtraction(token, "sub");
        //Above is To get the user's email
        return bookService.checkoutBookByUser(userEmail, bookId);

    }

    @GetMapping("/secure/currentloans")
    public List<CurrentLoans> currentLoans(@RequestHeader(value = "Authorization") String token) throws Exception {
        String userEmail = CheckJwt(token);
        return bookService.getCurrentLoans(userEmail);

    }

    private String CheckJwt(String token) throws Exception {
        String userEmail = ExtractJwt.extractJwtExtraction(token, "\"sub\"");
        if (userEmail == null) {
            throw new Exception("You are not logged in");
        }
        return userEmail;
    }
    @PutMapping("/secure/return")
    public void returnbook(@RequestHeader(value = "Authorization") String token,
                           @RequestParam Long bookId) throws Exception {

        String userEmail = CheckJwt(token); //extracting from Jwt
        bookService.returnBook(userEmail, bookId);//then returning the book
    }
    @PutMapping("/secure/renew/loan")
    public void renewLoan(@RequestHeader(value = "Authorization") String token,
                           @RequestParam Long bookId) throws Exception {
        //request param is to get the book id from the url
        String userEmail = CheckJwt(token); //extracting from Jwt
        bookService.renewBook(userEmail, bookId);//then returning the book
    }

    @PostMapping("/save")
    void save(@RequestBody Backend.libaryproject.Entity.Book book){
       bookRepository.save(book);
    }

    @GetMapping("/FindID")
    Backend.libaryproject.Entity.Book findById(Long id){
        return bookRepository.findById(id).orElse(null);
    }
    @PostMapping("/Delete")
    void delete(@RequestBody Backend.libaryproject.Entity.Book book){
        bookRepository.delete(book);
    }



}

