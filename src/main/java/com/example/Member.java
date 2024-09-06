package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Member
{
    private String name;
    private String PhoneNo;
    private List<Borrowrecord> borrowedBooks;

    public Member()
    {

    }

    public Member(String name,String PhoneNo)
    {
        this.name=name;
        this.PhoneNo=PhoneNo;
        this.borrowedBooks=new ArrayList<>();
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhoneNo() {
        return PhoneNo;
    }
    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }

    public List<Borrowrecord> getBorrowedBook()
    {
        return borrowedBooks;
    }

    public void BorrowBook(Borrowrecord record)
    {
        if(borrowedBooks.size() >=5)
        {
            System.out.println("can not borrow more than 5 books");
        }
        else{
            borrowedBooks.add(record);
        }
    }

    public void returnBook(Borrowrecord record)
    {
        borrowedBooks.remove(record);
    }
    @Override
    public String toString() {
        return "Member [Name=" + name + ", Phone Number=" + PhoneNo + "]";
    }
     public void registerMember(String memberName, String phoneNumber) {
        String query = "INSERT INTO members (name, phone_number) VALUES (?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, memberName);
            stmt.setString(2, phoneNumber);
            stmt.executeUpdate();
            System.out.println("Member registered successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeMember(String phoneNumber) {
        String query = "DELETE FROM members WHERE phone_number = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, phoneNumber);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Member removed successfully.");
            } else {
                System.out.println("Member not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Method to get member details by phone number
    public  Optional<Member> viewMemberDetails(String phoneNumber) {
        String query = "SELECT * FROM members WHERE phone_number = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, phoneNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                // Assuming there's a method to fetch borrowed books
                // List<Book> borrowedBooks = fetchBorrowedBooks(phoneNumber);

                // Create a Member object and return it wrapped in Optional
                Member member = new Member(name, phoneNumber);
                return Optional.of(member);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    

}