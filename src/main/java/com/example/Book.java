package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Book {
    private String ISBN;
    private String BookTitle;
    private String Author;
    private int publication_year;
    private boolean is_Availabe;
    

    public Book(String ISBN,String BookTitle, String Author,int publication_year, boolean is_avilabe) {
        this.ISBN = ISBN;
        this.BookTitle = BookTitle;
        this.Author = Author;
        this.publication_year =publication_year;
        this.is_Availabe = is_avilabe;

    }

    public Book() {
        
    }

    public Book(String isbn, String title, String author) {
        this.ISBN=isbn;
        this.BookTitle=title;
        this.Author=author;

        
    }

    public int getPublication_year() {
        return publication_year;
    }

    public void setPublication_year(int publication_year) {
        this.publication_year = publication_year;
    }

    public String getBookTitle() {
        return BookTitle;
    }

    public void setBookTitle(String bookTitle) {
        BookTitle = bookTitle;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String iSBN) {
        ISBN = iSBN;
    }

    public void setAvailable(boolean isAvailabe) {
        this.is_Availabe = isAvailabe;
    }

    public boolean isAvailabe() {
        return is_Availabe;
    }

   

    @Override
    public String toString() {
        return "Book [Title=" + BookTitle + ", Author=" + Author + ", ISBN=" + ISBN + ", Available=" + is_Availabe
                + "]";
    }

     public void addNewBook(String isbn, String title, String author, int year, boolean isAvailable) {
        String query = "INSERT INTO book (isbn, title, author, publication_year, is_available) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, isbn);
            stmt.setString(2, title);
            stmt.setString(3, author);
            stmt.setInt(4, year);
            stmt.setBoolean(5, isAvailable);
            stmt.executeUpdate();
            System.out.println("Book added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public void removeBook(String isbn) {
        String deleteBookQuery = "DELETE FROM book WHERE isbn = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(deleteBookQuery)) {
            stmt.setString(1, isbn);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Book removed successfully.");
            } else {
                System.out.println("Book not found in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public void searchBooks(String keyword) {
        String query = "SELECT * FROM book WHERE isbn = ?";
        List<Book> resultBooks = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, keyword);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("publication_year"),
                    rs.getBoolean("is_available")
                );
                resultBooks.add(book);
            }
    
            // Print book details or message if no books found
            if (resultBooks.isEmpty()) {
                System.out.println("Book is not present in the database.");
            } else {
                for (Book book : resultBooks) {
                    System.out.println("ISBN: " + book.getISBN());
                    System.out.println("Title: " + book.getBookTitle());
                    System.out.println("Author: " + book.getAuthor());
                    System.out.println("Publication Year: " + book.getPublication_year());
                    System.out.println("Available: " + (book.isAvailabe() ? "Yes" : "No"));
                    System.out.println(); // Add an empty line between books for readability
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Optional<Book> viewBookDetails(String isbn) {
        String query = "SELECT * FROM book WHERE isbn = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Book book = new Book(
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getInt("publication_year"),
                    rs.getBoolean("is_available")
                );
                return Optional.of(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


}