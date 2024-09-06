package com.example;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the Library Management System");
         Book book= new Book();
         Member member = new Member();
         Borrowrecord borrowrecord = new Borrowrecord();
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Enter your choice:");
            System.out.println("1. Add new book");
            System.out.println("2. Remove book");
            System.out.println("3. Search for a book");
            System.out.println("4. Register new member");
            System.out.println("5. Remove member");
            System.out.println("6. View member details");
            System.out.println("7. Borrow a book");
            System.out.println("8. Return a book");
            System.out.println("9. View member's borrowing history");
            System.out.println("10. Exit");

            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("Enter book title:");
                    String title = sc.nextLine();
                    System.out.println("Enter book author:");
                    String author = sc.nextLine();
                    System.out.println("Enter publication year:");
                    int year = sc.nextInt();
                    sc.nextLine(); // Consume newline
                    System.out.println("Enter book ISBN:");
                    String isbn = sc.nextLine();
                    System.out.println("Enter if the book is available (true/false):");
                    boolean isAvailable = sc.nextBoolean();
                    sc.nextLine(); // Consume newline
                book.addNewBook(isbn, title, author, year, isAvailable);
                    break;
                case 2:
                    System.out.println("Enter book ISBN to remove:");
                    String isbnToRemove = sc.nextLine();
                    book.removeBook(isbnToRemove);
                    break;
                case 3:
                    System.out.println("Enter keyword to search for a book:");
                    String keyword = sc.nextLine();
                    book.searchBooks(keyword);
                    break;
                case 4:
                    System.out.println("Enter member name:");
                    String memberName = sc.nextLine();
                    System.out.println("Enter member phone number:");
                    String phoneNumber = sc.nextLine();
                    member.registerMember(memberName, phoneNumber);
                    System.out.println("Member registered successfully.");
                    break;
                case 5:
                    System.out.println("Enter member phone number to remove:");
                    String phoneNumberToRemove = sc.nextLine();
                    member.removeMember(phoneNumberToRemove);
                    System.out.println("Member removed successfully.");
                    break;
                case 6:
                    System.out.println("Enter phone number to get member details:");
                    String phoneNumberForDetails = sc.nextLine();
                    Optional<Member> memberopt = member.viewMemberDetails(phoneNumberForDetails);
                    if (memberopt.isPresent()) {
                        System.out.println("Member Name: " + memberopt.get().getName());
                        System.out.println("Member Phone Number: " + memberopt.get().getPhoneNo());
                        System.out.println("Borrowed Books: " + memberopt.get().getBorrowedBook());
                    } else {
                        System.out.println("Member not found.");
                    }
                    break;
                case 7:
                    System.out.println("Enter member id to borrow a book:");
                    String borrowerPhoneNumber = sc.nextLine();
                    System.out.println("Enter book ISBN to borrow:");
                    String bookIsbn = sc.nextLine();
                    borrowrecord.borrowBook(bookIsbn, borrowerPhoneNumber);
                    break;
                case 8:
                    System.out.println("Enter member id to return a book:");
                    String returnerPhoneNumber = sc.nextLine();
                    System.out.println("Enter book ISBN to return:");
                    String bookIsbnToReturn = sc.nextLine();
                    borrowrecord.returnBook(bookIsbnToReturn, returnerPhoneNumber);
                    break;
                case 9:
                    System.out.println("Enter member id to view borrowing history:");
                    String historymemberid = sc.nextLine();
                    List<Borrowrecord> memberForHistory = borrowrecord.viewBorrowingHistory(historymemberid);
                    if (memberForHistory.isEmpty()) {
                        System.out.println("No member found with Member id " + historymemberid);
                        break;
                    }
                    List<Borrowrecord> borrowHistory = borrowrecord.viewBorrowingHistory(historymemberid);
                    System.out.println("Borrowing History:");
                    for (Borrowrecord record : borrowHistory) {
                        System.out.println("Borrowed: " + record.getBook().getISBN() +  " title is  " + record.getBook().getBookTitle() +" on "
                                + record.getBorrowDate() + " (Returned: "
                                + (record.getReturnDate() != null ? record.getReturnDate() : "not yet returned")
                                + ")");
                    }
                    break;
               
                case 10:
                    running = false;
                    System.out.println("Exiting the Library Management System.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
        sc.close();
    }
}
