package com.atm.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class MysqlConnection 
{
	
	static Connection conn=null;
	public static Connection getConnection()
	{
		
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_interface_db", "root", "root");			
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}

		return conn;
	}
	
	public static void closeConnection()
	{
		if(conn!=null)
		{
			try 
			{
				conn.close();
			} 
			catch (SQLException e) {

				System.out.println("Something went wrong!!");
			}
		}
	}
}
