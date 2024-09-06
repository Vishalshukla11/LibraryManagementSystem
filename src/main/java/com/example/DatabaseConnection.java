package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection
{
  private static final String url="jdbc:mysql://127.0.0.1:3306/library";
  private static final String username="root";
  private static final String password="Pankaj";

  public static Connection getConnection()throws SQLException
  {
    return 
    DriverManager.getConnection(url,username,password);

  }
  public static void closeConnection(Connection connection)
  {
    if(connection!=null)
    {
      try {
        connection.close();
      } catch (Exception e) {
e.printStackTrace();
      }
    }
  }



}