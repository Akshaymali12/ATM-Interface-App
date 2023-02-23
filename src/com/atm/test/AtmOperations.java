package com.atm.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.atm.test.MysqlConnection;

public class AtmOperations 
{
	public static void main(String[] args) throws NumberFormatException, IOException, SQLException, ParseException
	{
		
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("************************* WELCOME TO SBI BANK ATM ****************************");
		
		System.out.println("1 -> User");
		System.out.println("2 -> Admin");
		System.out.println("_________________________________________________________________________________");
		System.out.print("\t\t Enter your choice:"); //escape sequence \t,\n
		int choice=Integer.parseInt(br.readLine());
		if(choice==1)
		{
			System.out.println("______________________________________________________________________________");
			System.out.print("\t\t Please Enter your PIN:");
			String userPin=br.readLine();		
			System.out.println("_______________________________________________________________________________");
			
			Connection conn=MysqlConnection.getConnection();
			PreparedStatement ps=conn.prepareStatement("select * from useraccounts where userPin=?");
			ps.setString(1,userPin);
			ResultSet result=ps.executeQuery();
			String pin=null;
				
				while(result.next())
				{
					pin=result.getString("userPin");
				}
			
			if(userPin.equals(pin))
			{
				System.out.println("You have successfully logged in!!");
				
				boolean login=true;
				do
				{		
				System.out.println("_______________________________________________________________________________");
				System.out.println("1  --->   Deposit");
				System.out.println("2  --->   Withdraw");
				System.out.println("3  --->   Balance Enquiry");
				System.out.println("4  --->   PIN Change");
				System.out.println("5  --->   Transaction History");
				System.out.println("6  --->   Exit / Logout");
				System.out.println("________________________________________________________________________________");				
				System.out.print("\t\t Enter your choice:") ;
				int operationNumber=Integer.parseInt(br.readLine());
				System.out.println("________________________________________________________________________________");
	
				String status=null;
				
				switch(operationNumber)
				{
					case 1: System.out.println("Enter deposit amount:");
							double depositAmount=Double.parseDouble(br.readLine());
							
							if(depositAmount>0)
							{
								conn=MysqlConnection.getConnection();
								ps=conn.prepareStatement("select * from useraccounts where userPin=?");
								ps.setString(1, userPin);
								result=ps.executeQuery();
								
								double balance=0.0;
								long accId=0;
								while(result.next())
								{
									balance=result.getDouble("accBalance");
									accId=result.getLong("accId");
								}
								
								balance=balance+depositAmount;
								
								ps=conn.prepareStatement("update useraccounts set accBalance=? where userPin=?");
								ps.setDouble(1, balance);
								ps.setString(2, userPin);
								
								if(ps.executeUpdate()>0)
								{
									ps=conn.prepareStatement("insert into transactions values(?,?,?,?,?,?)");
									Timestamp timestamp = new Timestamp(System.currentTimeMillis());
									String transactionId="TN"+timestamp.getTime(); //TN3243432432423
									ps.setString(1, transactionId);
									ps.setDouble(2, depositAmount);
									ps.setDate(3, new Date(System.currentTimeMillis()));
									ps.setString(4, "deposit");
									ps.setLong(5,accId);
									ps.setLong(6,accId);
									
									ps.executeUpdate();
			
									
									System.out.println("Balance Updated!!");
									System.out.println("New Balance: "+balance);
								}
								else
								{
									System.out.println("Something went wrong!!");
								}
							}
							System.out.println("Do you want to continue??(Y/N)");
							 status=br.readLine();
							
							if(status.equals("n") || status.equals("N"))
							{
								login=false;
							}
							
							break;
					case 2:	 System.out.println("Enter Withdrawal amount:");
					 double withdrawalAmount=Double.parseDouble(br.readLine());
					 if(withdrawalAmount>0)
					 {
					    conn=MysqlConnection.getConnection();
						ps=conn.prepareStatement("select * from useraccounts where userPin=?");
						ps.setString(1, userPin);
						result=ps.executeQuery();
						
						double balance=0.0;
						long accId=0;
						while(result.next())
						{
							balance=result.getDouble("accBalance");
							accId=result.getLong("accId");
						}
						if(balance>withdrawalAmount)
						{
							balance=balance-withdrawalAmount;
							ps=conn.prepareStatement("update useraccounts set accBalance=? where userPin=?");
							ps.setDouble(1, balance);
							ps.setString(2, userPin);
							
							if(ps.executeUpdate()>0)
							{
								ps=conn.prepareStatement("insert into transactions values(?,?,?,?,?,?)");
								Timestamp timestamp = new Timestamp(System.currentTimeMillis());
								String transactionId="TN"+timestamp.getTime(); //TN3243432432423
								ps.setString(1, transactionId);
								ps.setDouble(2, withdrawalAmount);
								ps.setDate(3, new Date(System.currentTimeMillis()));
								ps.setString(4, "withdraw");
								ps.setLong(5,accId);
								ps.setLong(6,accId);
								
								ps.executeUpdate();
								
								System.out.println("Balance Updated!!");
								System.out.println("New Balance: "+balance);
							}
							else
							{
								System.out.println("Something went wrong!!");
							}
						}
						else
						{
							System.out.println("Insufficient Balance!!");
						}

					 }
					 System.out.println("Do you want to continue??(Y/N)");
					 status=br.readLine();
						
						if(status.equals("n") || status.equals("N"))
						{
							login=false;
						}
					 	
					 	break;
					case 3:	conn=MysqlConnection.getConnection();
							ps=conn.prepareStatement("select accBalance from useraccounts where userPin=?");
							ps.setString(1, userPin);
							result=ps.executeQuery();
							
							double balance=0.0;
							while(result.next())
							{
								balance=result.getDouble("accBalance");
							}
							System.out.println("_______________________________________________________________________________");				
							System.out.println("Current Available Balance:"+balance);
							System.out.println("_______________________________________________________________________________");				

							 System.out.println("Do you want to continue??(Y/N)");
							 status=br.readLine();
								
								if(status.equals("n") || status.equals("N"))
								{
									login=false;
								}
							 	
							 	break;
					case 4: System.out.println("Please enter the existing PIN: ");
							String existingPin=br.readLine();
							
							System.out.println("Set new PIN:");
							String newPin=br.readLine();
							
							System.out.println("Retype new PIN:");
							String retypePin=br.readLine();
							
							
							ps=conn.prepareStatement("select userPin from useraccounts where userPin=?");
							ps.setString(1, userPin);
							
							result=ps.executeQuery();
							String accountPin=null;
							while(result.next())
							{
								accountPin=result.getString("userPin");
							}
							
							if(accountPin.equals(existingPin))
							{
								if(newPin.equals(retypePin))
								{
									ps=conn.prepareStatement("update useraccounts set userPin=? where userPin=?");
									ps.setString(1, newPin);
									ps.setString(2, userPin);
									
									if(ps.executeUpdate()>0)
									{
										System.out.println("______________________________________________________________________________");				
										System.out.println("Passowrd Changed!!");
										System.out.println("_______________________________________________________________________________");				
										
									}
									
									else
									{
										System.out.println("______________________________________________________________________________");				
										System.out.println("Error in password change!!");
										System.out.println("______________________________________________________________________________");				
										
									}
								}
								else
								{
									System.out.println("__________________________________________________________________________________");				
									System.out.println("Set new password and retype password must be same!!");
									System.out.println("__________________________________________________________________________________");				
									
								}
							}
							else
							{
								System.out.println("___________________________________________________________________________________");				
								System.out.println("Please enter correct existing password!!");
								System.out.println("___________________________________________________________________________________");				
								
					
							}
							
							System.out.println("Do you want to continue??(Y/N)");
							status=br.readLine();
							System.out.println("_____________________________________________________________________________");				

							if(status.equals("n") || status.equals("N"))
							{
								login=false;
							}
							break;
					case 5: ps=conn.prepareStatement("select * from useraccounts where userPin=?");
							ps.setString(1, userPin);
							
							result=ps.executeQuery();
							long accId=0;
							while(result.next())
							{
								accId=result.getLong("accId");
							}
							if(accId!=0)
							{
								ps=conn.prepareStatement("select * from transactions where senderAccountId=?");
								ps.setLong(1, accId);
								
								result=ps.executeQuery();
								System.out.println("______________________________________________________________________________");	
								System.out.println("TransactionId \t Amount \t Date \t Type ");
								System.out.println("______________________________________________________________________________");	
								while(result.next())
								{
									System.out.println(result.getString("transactionId")+"\t"+result.getDouble("transactionAmount")+"\t"+result.getDate("transactiondate")+"\t"+result.getString("transactionType"));
								}
								System.out.println("____________________________________________________________________________");	
							}
							System.out.println("Do you want to continue??(Y/N)");
							status=br.readLine();
							System.out.println("______________________________________________________________________________");				

							if(status.equals("n") || status.equals("N"))
							{
								login=false;
							}
							break;
					case 6:  login=false;
							 break;
					
						
							
					default:System.out.println("Wrong Input!!");		
				}	
			}
				while(login);
				System.out.println("______________________________________________________________________________");				
				System.out.println("Bye.");
				System.out.println("Have a nice day!!");
				System.out.println("______________________________________________________________________________");				

			}
			else
			{
				System.out.println("______________________________________________________________________________");				
				System.out.println(" you Entered Wrong PIN !!");
				System.out.println("______________________________________________________________________________");				
			}
			
		}
		else if(choice==2)
		{
		
			System.out.println("__________________________  ENTER LOGIN DETAILS _______________________________");
			
			System.out.print("\t\t Enter your username:");
			String userName=br.readLine();
			System.out.print("\t\t Enter your password:");
			String userPassword=br.readLine();
			System.out.println("________________________________________________________________________________");
			
			Connection conn=MysqlConnection.getConnection();
			PreparedStatement ps=conn.prepareStatement("select * from admin where userName=?");
			ps.setString(1,userName);
			ResultSet result=ps.executeQuery();
			String password=null;
				
				while(result.next())
				{
					password=result.getString("password");
				}
			
			
			if(userPassword.equals(password))
			{
			
				System.out.println("You have successfully logged in!!");
				
				boolean login=true;
			do
			{
			
		
			System.out.println("______________________________  WELCOME " + userName.toUpperCase() + " _________________________________");

			System.out.println("1  --->   Open new Account");
			System.out.println("2  --->   Close account");
			System.out.println("3  --->   View transactions");
			System.out.println("4  --->   Exit / Logout");
			System.out.println("_______________________________________________________________________________");				
			System.out.print("\t\t Enter your choice:"); 
			int operationNumber=Integer.parseInt(br.readLine());
			System.out.println("_______________________________________________________________________________");
			
			
			String status=null;
			switch(operationNumber)
			{
				case 1: System.out.println("Enter Customer full name:");
						String accName=br.readLine();
						
						System.out.println("Enter gender:");
						String gender=br.readLine();
						
						System.out.println("Enter date of birth:(dd/MM/YYYY)");
						String dob=br.readLine();
						
						System.out.println("Enter email:");
						String email=br.readLine();
						
						System.out.println("Enter phone number:");
						long phone=Long.parseLong(br.readLine());
						
						
						System.out.println("Set user PIN");
						String userPin=br.readLine();
						
						System.out.println("Retype user PIN:");
						String reuserPin=br.readLine();
						
						System.out.println("Set account Id:");
						long accId=Long.parseLong(br.readLine());
						
						System.out.println("Account type:");
						String accType=br.readLine();
						
						System.out.println("Enter initial balance:");
						double accBalance=Double.parseDouble(br.readLine());
						
//						System.out.println("Enter IFSC code");
//						String ifsc=br.readLine();
						
						
						ps=conn.prepareStatement("insert into useraccounts values(?,?,?,?,?,?,?,?,?)");
						ps.setLong(1, accId);
						ps.setString(2,accName);
						ps.setString(3, userPin);
						ps.setString(4, accType);
						ps.setString(5, gender);
						
						SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/YYYY");
						java.util.Date utilDate=formatter.parse(dob);
					    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

						ps.setDate(6,sqlDate);
						
						ps.setDouble(7, accBalance);
						ps.setString(8, email);
						ps.setLong(9, phone);
//						ps.setString(11, ifsc);
						
						if(ps.executeUpdate()>0)
						{
							System.out.println("______________________________________________________________________________");				
							System.out.println("New account created successfully!!");
							System.out.println("______________________________________________________________________________");				
						
						}
						else
						{
							System.out.println("______________________________________________________________________________");				
							System.out.println("Problem in account creation!!");
							System.out.println("______________________________________________________________________________ ");				
						
						}
						

						System.out.println("Do you want to continue??(Y/N)");
						status=br.readLine();
						
						if(status.equals("n") || status.equals("N"))
						{
							login=false;
						}
						
						break;
				case 2: System.out.println("Enter account Id:");	
						accId=Long.parseLong(br.readLine());
						
						ps=conn.prepareStatement("delete from useraccounts where accId =?");
						ps.setLong(1, accId);
						
						if(ps.executeUpdate()>0)
						{
							System.out.println("______________________________________________________________________________");				
							System.out.println("Account closed successfully!!");
							System.out.println("______________________________________________________________________________");				
						
						}
						else
						{
							System.out.println("_______________________________________________________________________________");				
							System.out.println("Account id does not exist!!");
							System.out.println("_______________________________________________________________________________");				
						
					
						}
						System.out.println("Do you want to continue??(Y/N)");
						status=br.readLine();
						
						if(status.equals("n") || status.equals("N"))
						{
							login=false;
						}
						
					 break;
				case 3:	ps=conn.prepareStatement("select * from transactions");
						result=ps.executeQuery();
						System.out.println("______________________________________________________________________________");	
						System.out.println("TransactionId \t Amount \t Date \t Type ");
						System.out.println("______________________________________________________________________________");	
						while(result.next())
						{
							System.out.println(result.getString("transactionId")+"\t"+result.getDouble("transactionAmount")+"\t"+result.getDate("transactiondate")+"\t"+result.getString("transactionType"));
						}
						System.out.println("_______________________________________________________________________________");	 
						System.out.println("Do you want to continue??(Y/N)");
						status=br.readLine();
						
						if(status.equals("n") || status.equals("N"))
						{
							login=false;
						}
						break;	
				case 4:	login=false;
				 		break;
	
				default:System.out.println("______________________________________________________________________________");	 
					    System.out.println("Wrong Input!!");	
						System.out.println("______________________________________________________________________________");	 
						System.out.println("Do you want to continue??(Y/N)");
						status=br.readLine();
						
						if(status.equals("n") || status.equals("N"))
						{
							login=false;
						}	
						break;
						
						
			}
			
			}
			while(login);
			System.out.println("_______________________________________________________________________________");				
			System.out.println("Bye.");
			System.out.println("Have a nice day!!");
			System.out.println("________________________________________________________________________________");				

		}
		else if(password==null)
		{
			System.out.println("______________________________________________________________________________");				
			System.out.println("Username does not exist!!");
			System.out.println("______________________________________________________________________________");				

		}
		else
		{
			System.out.println("_______________________________________________________________________________");				
			System.out.println("Wrong password!!");
			System.out.println("_______________________________________________________________________________");				

		}
	}
	
	}
}

