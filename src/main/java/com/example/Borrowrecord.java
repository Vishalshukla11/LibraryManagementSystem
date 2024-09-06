package com.example;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.ZoneId;

public class Borrowrecord {
    private Book book;
    private Member member;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private boolean isAvailable;

    // Constructor to create a Borrowrecord object
    public Borrowrecord(Book book, Member member, LocalDate borrowDate, LocalDate returnDate) {
        this.book = book;
        this.member = member;
        this.borrowDate = borrowDate != null ? borrowDate : LocalDate.now();
        this.returnDate = returnDate;
    }

    // Constructor to create a Borrowrecord object from ResultSet
    public Borrowrecord(ResultSet resultSet) throws SQLException {
        // Create Book and Member objects from ResultSet
        this.book = new Book(resultSet.getString("book_title"), resultSet.getString("book_author"), null, 0, isAvailable);
        this.member = new Member(resultSet.getString("member_name"), resultSet.getString("member_phone_number"));
        this.borrowDate = resultSet.getDate("borrow_date").toLocalDate();
        this.returnDate = resultSet.getDate("return_date") != null ? resultSet.getDate("return_date").toLocalDate() : null;
    }

    public Borrowrecord() {
        //Deafult constructor
    }

   // Constructor with LocalDate conversion from java.sql.Date
   public Borrowrecord(Book book, java.sql.Date borrowDate, java.sql.Date returnDate) {
    this.book = book;

    // Convert java.sql.Date to java.time.LocalDate
    this.borrowDate = (borrowDate != null) ? borrowDate.toLocalDate() : LocalDate.now();
    this.returnDate = (returnDate != null) ? returnDate.toLocalDate() : null;
}

    // Getters and setters
    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    // Calculate fine based on overdue days
    public long calculateFine() {
        if (returnDate == null) {
            return 0;
        }
        long daysOverdue = LocalDate.now().until(returnDate).getDays() - 30;
        return daysOverdue > 0 ? daysOverdue : 0;
    }

    @Override
    public String toString() {
        return "BorrowRecord [Book=" + book.getBookTitle() + ", Member=" + member.getName() +
               ", BorrowDate=" + borrowDate + ", ReturnDate=" + returnDate + "]";
    }

    // Method to borrow a book
    public void borrowBook(String isbn, String memberId) {
        String checkBookQuery = "SELECT * FROM book WHERE isbn = ?";
        String checkMemberQuery = "SELECT * FROM members WHERE id = ?";
        String checkBorrowedBooksCountQuery = "SELECT COUNT(*) FROM borrow_records WHERE member_id = ? AND return_date IS NULL";
        String insertBorrowRecordQuery = "INSERT INTO borrow_records (book_isbn, member_id, borrow_date) VALUES (?, ?, ?)";
        String updateBookAvailabilityQuery = "UPDATE book SET is_available = FALSE WHERE isbn = ?";
    
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement checkBookStmt = con.prepareStatement(checkBookQuery);
             PreparedStatement checkMemberStmt = con.prepareStatement(checkMemberQuery);
             PreparedStatement checkCountStmt = con.prepareStatement(checkBorrowedBooksCountQuery);
             PreparedStatement insertStmt = con.prepareStatement(insertBorrowRecordQuery);
             PreparedStatement updateStmt = con.prepareStatement(updateBookAvailabilityQuery)) {
    
            // Check if the book exists and is available
            checkBookStmt.setString(1, isbn);
            ResultSet bookRs = checkBookStmt.executeQuery();
            if (!bookRs.next()) {
                System.out.println("Book not found.");
                return;
            }
    
            boolean isAvailable = bookRs.getBoolean("is_available");
            if (!isAvailable) {
                System.out.println("Book is not available.");
                return;
            }
    
            // Check if the member exists
            checkMemberStmt.setString(1, memberId);
            ResultSet memberRs = checkMemberStmt.executeQuery();
            if (!memberRs.next()) {
                System.out.println("Member not found.");
                return;
            }
    
            // Check how many books the member has borrowed
            checkCountStmt.setString(1, memberId);
            ResultSet countRs = checkCountStmt.executeQuery();
            if (countRs.next()) {
                int borrowedBooksCount = countRs.getInt(1);
                if (borrowedBooksCount >= 5) {
                    System.out.println("You cannot borrow more than 5 books.");
                    return;
                }
            }
    
            // Insert the borrow record
            insertStmt.setString(1, isbn);
            insertStmt.setString(2, memberId);
            insertStmt.setDate(3, Date.valueOf(LocalDate.now()));
            insertStmt.executeUpdate();
    
            // Update the book's availability
            updateStmt.setString(1, isbn);
            updateStmt.executeUpdate();
    
            System.out.println("Book borrowed successfully.");
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    // Method to return a book
    public void returnBook(String isbn, String memberPhoneNumber) {
        String selectBorrowRecordQuery = "SELECT * FROM borrow_records WHERE book_isbn = ? AND member_id = ? AND return_date IS NULL";
        String updateBorrowRecordQuery = "UPDATE borrow_records SET return_date = ? WHERE book_isbn = ? AND member_id= ? AND return_date IS NULL";
        String updateBookQuery = "UPDATE book SET is_available = TRUE WHERE isbn = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement selectBorrowRecordStmt = connection.prepareStatement(selectBorrowRecordQuery);
             PreparedStatement updateBorrowRecordStmt = connection.prepareStatement(updateBorrowRecordQuery);
             PreparedStatement updateBookStmt = connection.prepareStatement(updateBookQuery)) {

            // Check if borrow record exists
            selectBorrowRecordStmt.setString(1, isbn);
            selectBorrowRecordStmt.setString(2, memberPhoneNumber);
            ResultSet rs = selectBorrowRecordStmt.executeQuery();

            if (rs.next()) {
                // Update return date in borrow record
                updateBorrowRecordStmt.setDate(1, Date.valueOf(LocalDate.now()));
                updateBorrowRecordStmt.setString(2, isbn);
                updateBorrowRecordStmt.setString(3, memberPhoneNumber);
                updateBorrowRecordStmt.executeUpdate();

                // Update book availability
                updateBookStmt.setString(1, isbn);
                updateBookStmt.executeUpdate();

                System.out.println("Book returned successfully.");
            } else {
                System.out.println("Borrow record not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
        // Method to get borrowing history for a member
        public List<Borrowrecord> viewBorrowingHistory(String memberId) {
            List<Borrowrecord> history = new ArrayList<>();
        
            //  SQL query with column names from 'book' table
            String query = "SELECT b.isbn, b.title, b.author, br.borrow_date, br.return_date " +
                           "FROM borrow_records br " +
                           "JOIN book b ON br.book_isbn = b.isbn " +
                           "WHERE br.member_id = ?";
        
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
        
                statement.setString(1, memberId);
                ResultSet resultSet = statement.executeQuery();
        
                while (resultSet.next()) {
                    // Fetching correct column names
                    String isbn = resultSet.getString("isbn");
                    String title = resultSet.getString("title"); // Corrected from 'book_title' to 'title'
                    String author = resultSet.getString("author");
        
                    // Create Book object
                    Book book = new Book(isbn, title, author);
        
                    // Fetch borrow and return dates
                    Date borrowDate = resultSet.getDate("borrow_date");
                    Date returnDate = resultSet.getDate("return_date");
        
                    // Create Borrowrecord object and add it to the history list
                    Borrowrecord record = new Borrowrecord(book, borrowDate, returnDate);
                    history.add(record);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        
            return history;
        }
        
   // Method to count the number of currently borrowed books by a member
public int countCurrentBorrowedBooks(int memberId) {
    int count = 0;
    String query = "SELECT COUNT(*) FROM borrow_records WHERE member_id = ? AND return_date IS NULL";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {

        // Set the member ID in the query
        statement.setInt(1, memberId);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            count = resultSet.getInt(1);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return count;
}

}
