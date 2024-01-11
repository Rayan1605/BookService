package Backend.libaryproject.Service;

import Backend.libaryproject.Entity.Book;
import Backend.libaryproject.Entity.Checkout;
import Backend.libaryproject.Entity.Histroy;
import Backend.libaryproject.Entity.Payment;
import Backend.libaryproject.Repository.BookRepository;
import Backend.libaryproject.Repository.CheckOutRepository;
import Backend.libaryproject.Repository.HistoryRepository;
import Backend.libaryproject.ResponseModel.CurrentLoans;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@AllArgsConstructor
public class BookService {

    private BookRepository bookRepository;
    //Using lombok to generate constructor for dependency injection
    private CheckOutRepository checkout;
    private final CheckOutRepository checkOutRepository;
    private final HistoryRepository historyRepository;
    private final Backend.libaryproject.Interface.Payment paymentRepository;
    public Book checkoutBook(String userEmail, Long bookId) throws Exception {
//This method will check out a book by a user,
//It will check if the user has already checked out the book
//If the user has already checked out the book, it will throw an exception
//If the user has not checked out the book, it will check out the book and return the book

        //First, we need to find a specific book by its id
        //When you call the database you need to return an optional

        Optional<Book> book = bookRepository.findById(bookId);
//If the book is not found, we will throw an exception

        Checkout validateUser = checkout.findByUserEmailAndBookId(userEmail, bookId);
//Making sure validateUser is null because if not null, then the user has already checked out the book
        int validate = Validate(validateUser, book);
        switch (validate) {
            case 1 -> throw new Exception("You have already checked out this book");
            case 2 -> throw new Exception("Book not found");
            case 3 -> throw new Exception("Book is not available");
        }
       //Checking if a user has any outstanding payments or overdue books before
        // allowing a new checkout. Here is what it's doing:
        //
        //Find all current checked-out books for the user using their email.
        //Loop through each checked-out book.
        //Parse the return date and today's date into Date objects.
        //Calculate the difference between return date and today.
        //If the difference is negative, the book is overdue. Set flag BooksNeedToBeReturned.
        //Lookup any existing Payment for the user by email.
        //If Payment amount > 0, they have an outstanding balance.
        //Or if BooksNeedToBeReturned is true, they have overdue books.
        //If either is true, throw Exception to deny checkout.
        //If no Payment is found, create a new Payment for the user with $0 balance.
        //Save the new Payment.
        //So in summary:
        //
        //It checks for overdue books
        //Checks for outstanding payment balance
        //Throws exception if either exists to deny checkout
        //Creates new $0 Payment if none existed
     List<Checkout> currentBooksCheckedOut = checkOutRepository.findByUserEmail(userEmail);
        SimpleDateFormat start = new SimpleDateFormat("yyyy-MM-dd");
    boolean BooksNeedToBeReturned = false;
    for (Checkout bookCheckedOut : currentBooksCheckedOut) {
        Date d1 = start.parse(bookCheckedOut.getReturn_date());
        Date d2 = start.parse(LocalDate.now().toString());
        TimeUnit time = TimeUnit.DAYS;
        double difference = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
        if (difference < 0) {
            BooksNeedToBeReturned = true;
            break;
        }
    }

        Payment payment = paymentRepository.findByUserEmail(userEmail);

    if((payment != null && payment.getAmount() > 0)|| (payment != null && BooksNeedToBeReturned)){
        throw new Exception("You have outstanding payments");
    }
    if (payment==null){
     Payment newPayment = new Payment();
        newPayment.setAmount(0);
        newPayment.setUserEmail(userEmail);
        paymentRepository.save(newPayment);

    }

        book.get().setAvailable_copies(book.get().getAvailable_copies() - 1);
        bookRepository.save(book.get());
//If the book is found, we will check out the book
        Checkout checkout = new Checkout(
                userEmail,
                LocalDate.now().toString(),
                LocalDate.now().plusDays(7).toString(),
                book.get().getId()

        );
        checkOutRepository.save(checkout);
        return book.get();

    }

    private int Validate(Checkout validateUser, Optional<Book> book) {
        if (validateUser != null) return 1;
        if (book.isEmpty()) return 2;
        if (book.get().getAvailable_copies() == 0) return 3;
        return 0;
    }

    //This is to check if the user has already checked out the book if it did then we will return true
    // and print the already checked out in our React app
    public boolean checkoutBookByUser(String userEmail, Long bookId) {
        Checkout validateCheckout = checkOutRepository.findByUserEmailAndBookId(userEmail, bookId);
        return validateCheckout != null;
    }

    public int currentLoansCount(String userEmail) {
        return checkOutRepository.findByUserEmail(userEmail).size();
    }

    public List<CurrentLoans> getCurrentLoans(String userEmail) throws Exception {
        List<Checkout> checkoutList = checkOutRepository.findByUserEmail(userEmail);
        List<CurrentLoans> currentLoans = new ArrayList<>();
        List<Book> books = getBooks(checkoutList);
        //Going to check if the book is overdue and how longs it is
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        for(Book book: books){
            // Each book in our checkoutList we are going to look for the book in our book list,
            // and once we find it, then we can end the loop and check to see if the book is overdue
            Optional<Checkout> checkout = checkoutList.stream().
                    filter(x-> x.getBookId().equals(book.getId())).findFirst();


               if(checkout.isPresent()){
                   Date return_Date = formatter.parse(checkout.get().getReturn_date());
                     Date today = formatter.parse(LocalDate.now().toString());
                   TimeUnit timeUnit = TimeUnit.DAYS;
                     long diff = timeUnit.convert(return_Date.getTime() - today.getTime(),
                             TimeUnit.MILLISECONDS);
                       currentLoans.add(new CurrentLoans(book, (int)diff));

               }
        }
        return currentLoans;
    }

    public List<Book> getBooks(List<Checkout> checkoutList) {
        //So here we are going to get all the books that the user has checked out,
        //However, we can only get the book ids



        List<Long> bookIds = new ArrayList<>();
// We are going to get all the book ids that the user has checked out
        for (Checkout checkout : checkoutList) {
            bookIds.add(checkout.getBookId());
        }
        return bookRepository.findBooksByBookIds(bookIds);

    }
//Implementing the return book method
    public void returnBook (String userEmail, Long BookId) throws Exception {
        Optional<Book> book = bookRepository.findById(BookId);

        Checkout checkout = checkOutRepository.findByUserEmailAndBookId(userEmail, BookId);

        if(checkout == null || book.isEmpty()) throw new Exception("You have not checked out this book");

        //So one more book is available, and then we save it and delete it from the checkout
        book.get().setAvailable_copies(book.get().getAvailable_copies() + 1);
        bookRepository.save(book.get());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Date d1 = formatter.parse(checkout.getReturn_date());
        Date d2 = formatter.parse(LocalDate.now().toString());

        TimeUnit time = TimeUnit.DAYS;

        double difference = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
        if (difference < 0){
            Payment payment = paymentRepository.findByUserEmail(userEmail);

            payment.setAmount(payment.getAmount() + Math.abs(difference));
            paymentRepository.save(payment);

            payment.setAmount(payment.getAmount() + Math.abs(difference));
            paymentRepository.save(payment);
        }
        checkOutRepository.delete(checkout);
//This is to save it in our history when we return the book
        //And we are using our constructor to save it in our history
        //so we are creating a new History object and saving it in our history repository
        //Using the constructor
        Histroy histroy = new Histroy(
                  userEmail,
                checkout.getCheckout_date()
                ,LocalDate.now().toString(),
                book.get().getTitle(),
                book.get().getAuthor(),
                book.get().getDescription(),
                book.get().getImage());
        historyRepository.save(histroy);

    }
    //Implementing the renewed book method
    //Basically getting the checkout and changing the thing to the current date
    // and return date to + 7 days
    public void renewBook(String userEmail, Long BookId) throws Exception {
        Checkout checkout = checkOutRepository.findByUserEmailAndBookId(userEmail, BookId);
        if(checkout == null) throw new Exception("You have not checked out this book");
        checkout.setReturn_date(LocalDate.now().plusDays(7).toString());
        checkout.setCheckout_date(LocalDate.now().toString());
        checkOutRepository.save(checkout);
    }

}























