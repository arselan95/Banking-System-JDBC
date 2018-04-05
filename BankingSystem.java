import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.sql.*;

import java.util.Scanner;

/**
 * Manage connection to database and perform SQL statements.
 */
public class BankingSystem {
	// Connection properties
	private static String driver;
	private static String url;
	private static String username;
	private static String password;
	
	// JDBC Objects
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;
	
	/**
	 * Initialize database connection given properties file.
	 * @param filename name of properties file
	 */
	public static void init(String filename) {
		try {	
			Properties props = new Properties();						// Create a new Properties object
			FileInputStream input = new FileInputStream(filename);	// Create a new FileInputStream object using our filename parameter
			props.load(input);										// Load the file contents into the Properties object
			driver = props.getProperty("jdbc.driver");				// Load the driver
			url = props.getProperty("jdbc.url");						// Load the url
			username = props.getProperty("jdbc.username");			// Load the username
			password = props.getProperty("jdbc.password");			// Load the password
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test database connection.
	 */
	public static void testConnection() {
		System.out.println(":: TEST - CONNECTING TO DATABASE");
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			con.close();
			System.out.println(":: TEST - SUCCESSFULLY CONNECTED TO DATABASE");
			} catch (Exception e) {
				System.out.println(":: TEST - FAILED CONNECTED TO DATABASE");
				e.printStackTrace();
			}
	  }

	/**
	 * Create a new customer.
	 * @param name customer name
	 * @param gender customer gender
	 * @param age customer age
	 * @param pin customer pin
	 */
	public static void newCustomer(String name, String gender, String age, String pin) 
	{
		System.out.println(":: CREATE NEW CUSTOMER - RUNNING");
		if(name.length() > 15)
		{
			System.out.println("Your name must be 15 characters or less, customer not created");
			return;
		}
		if(!gender.equals("M") && !gender.equals("m") && !gender.equals("F") && !gender.equals("f"))
		{
			System.out.println("Gender must be 'M' or 'F', customer not created");
			return;
		}
		try
		{
			if(Integer.parseInt(age) < 0)
			{
				System.out.println("Age must be >= 0");
				System.out.println(":: CREATE NEW CUSTOMER - FAILURE");
				System.out.println();
				return;
			}
			if(Integer.parseInt(pin) < 0)
			{
				System.out.println("PIN cannot be a negative number");
				System.out.println(":: CREATE NEW CUSTOMER - FAILURE");
				System.out.println();
				return;
			}
		}
		catch(Exception e)
		{
			System.out.println("Age and Pin must be integer values >= 0");
			System.out.println(":: CREATE NEW CUSTOMER - FAILURE");
			System.out.println();
			return;
		}	
		try
		{	
			con = DriverManager.getConnection(url, username, password);
			String query = "INSERT INTO P1.Customer(NAME,GENDER,AGE,PIN) "
				     + "VALUES(?, ?, ?, ?)";
			PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, name);
			ps.setString(2, gender.toUpperCase());
			ps.setInt(3, Integer.parseInt(age));
			ps.setInt(4, Integer.parseInt(pin));
			ps.executeUpdate();
			ResultSet res = ps.getGeneratedKeys();
			if(res.next())
			{
				java.math.BigDecimal thing = res.getBigDecimal(1);
				System.out.println("Customer created, your ID is " + thing);
			}
			con.close(); 	
			System.out.println(":: CREATE NEW CUSTOMER - SUCCESS");
			System.out.println();
		}
		catch(SQLException ex)
		{
			System.out.println("Error inserting into database");
			System.out.println(":: CREATE NEW CUSTOMER - FAILURE");
			System.out.println();
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("Error parsing fields");
			System.out.println(":: CREATE NEW CUSTOMER - FAILURE");
			System.out.println();
		}
		finally
		{
			return;
		}
	}

	/**
	 * Open a new account.
	 * @param id customer id
	 * @param type type of account
	 * @param amount initial deposit amount
	 */
	public static void openAccount(String id, String type, String amount) 
	{
		System.out.println(":: OPEN ACCOUNT - RUNNING");
		try
		{
			if(Integer.parseInt(id) < 0)
			{
				System.out.println("Invalid account ID, cannot be negative");
				System.out.println(":: OPEN ACCOUNT - FAILURE");
				System.out.println();
				return;
			}
			if(!type.equals("C") && !type.equals("c") && !type.equals("S") && !type.equals("s"))
			{
				System.out.println("Invalid account type, must be 'C' or 'S'");
				System.out.println(":: OPEN ACCOUNT - FAILURE");
				System.out.println();
				return;
			}
			if(Integer.parseInt(amount) < 0)
			{
				System.out.println("You cannot start with a negative balance");
				System.out.println(":: OPEN ACCOUNT - FAILURE");
				System.out.println();
				return;
			}		
		}
		catch(Exception e)
		{
			System.out.println("Error reading ID or deposit amount, must enter integer > 0");
			System.out.println(":: OPEN ACCOUNT - FAILURE");
			System.out.println();
			return; 
		}

		try
		{
			con = DriverManager.getConnection(url, username, password);
			String query = "SELECT * FROM P1.Customer WHERE ID = ?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, Integer.parseInt(id));
			ResultSet res = ps.executeQuery();
			if(!res.next())
			{
				System.out.println("Customer ID does not exist");
				System.out.println(":: OPEN ACCOUNT - FAILURE");
				System.out.println();
				return;
			}
			query = "INSERT INTO P1.Account(ID, BALANCE, TYPE, STATUS) "
				+ "VALUES(?, ?, ?, ?)";
			ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, Integer.parseInt(id));
			ps.setInt(2, Integer.parseInt(amount));
			ps.setString(3, type);
			ps.setString(4, "A");
			ps.executeUpdate();
			res = ps.getGeneratedKeys();
			if(res.next())
			{
				java.math.BigDecimal thing = res.getBigDecimal(1);
				System.out.println("Account created, the account number is " + thing);
			} 
			System.out.println(":: OPEN ACCOUNT - SUCCESS");
			System.out.println();
			con.close();
			return;	
		}
		catch(Exception ex)
		{
			System.out.println("Error inserting into database");
			System.out.println(":: OPEN ACCOUNT - FAILURE");
			System.out.println();
			return;
		}
	}

	/**
	 * Close an account.
	 * @param accNum account number
	 */
	public static void closeAccount(String accNum) 
	{	
		System.out.println(":: CLOSE ACCOUNT - RUNNING");
		try
		{	con = DriverManager.getConnection(url, username, password);
			String query = "SELECT * FROM P1.Account WHERE NUMBER = ? AND STATUS = ?";
			PreparedStatement ps = con.prepareStatement(query);			
			ps.setInt(1, Integer.parseInt(accNum));
			ps.setString(2, "A");
			ResultSet res = ps.executeQuery();
			if(!res.next())
			{
				System.out.println("Could not close account " + accNum + " because no open account with that number exists");
				System.out.println(":: CLOSE ACCOUNT - FAILURE");
				System.out.println();
				return;
			}
			
			query = "UPDATE P1.Account SET BALANCE = ?, STATUS = ? WHERE NUMBER = ?";
			ps = con.prepareStatement(query);
			ps.setInt(1, 0);
			ps.setString(2, "I");
			ps.setInt(3, Integer.parseInt(accNum));
			ps.executeUpdate(); 
			System.out.println(":: CLOSE ACCOUNT - SUCCESS");
			System.out.println();
			con.close();
			return;
		}
		catch(SQLException ex)
		{
			System.out.println("Error updating Account, account not closed");
			System.out.println(":: CLOSE ACCOUNT - FAILURE");
			System.out.println();
			return;
		}
		catch(Exception e)
		{	
			System.out.println("Error parsing account number, use integers only, account not closed");
			System.out.println(":: CLOSE ACCOUNT - FAILURE");
			System.out.println();
			return;
		}
	}

	/**
	 * Deposit into an account.
	 * @param accNum account number
	 * @param amount deposit amount
	 */
	public static void deposit(String accNum, String amount) 
	{
		System.out.println(":: DEPOSIT - RUNNING");	
		int accountNumber = -1;
		int depositAmount = -1;
		try
		{
			accountNumber = Integer.parseInt(accNum);
			depositAmount = Integer.parseInt(amount);
			if(accountNumber < 0 || depositAmount < 0)
			{
				System.out.println("Could not complete deposit because you entered a negative number for account or amount");
				System.out.println(":: DEPOSIT - FAILURE");
				System.out.println();
				return;
			}
		}
		catch(Exception e)
		{
			System.out.println("Could not complete deposit because you entered a non-integer value for account or amount");
			System.out.println(":: DEPOSIT - FAILURE");
			System.out.println();
			return;
		}
		try
		{
			con = DriverManager.getConnection(url, username, password);
			String query = "SELECT * FROM P1.Account WHERE NUMBER = ? AND STATUS = ?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, accountNumber);
			ps.setString(2, "A");
			ResultSet res = ps.executeQuery();
			if(!res.next())
			{
				System.out.println("Could not complete deposit, there are no active accounts with number " + accountNumber);
				System.out.println(":: DEPOSIT - FAILURE");
				System.out.println();
				con.close();
				return;
			}	 
		}
		catch(Exception e)
		{
			System.out.println("Error querying database, transaction aborted");
			System.out.println(":: DEPOSIT - FAILURE");
			System.out.println();
			return;
		}
		try
		{	
			con = DriverManager.getConnection(url, username, password);
			String query = "UPDATE P1.Account SET BALANCE = BALANCE + ? WHERE NUMBER = ?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, depositAmount);
			ps.setInt(2, accountNumber);
			ps.executeUpdate();
			con.close();
			System.out.println(":: DEPOSIT - SUCCESS");
			System.out.println();
			return;
		}
		catch(Exception e)
		{
			System.out.println("Error updating database, transaction aborted");  //This should not happen
			System.out.println(":: DEPOSIT - FAILURE");
			System.out.println();
			return;
		}
	}

	/**
	 * Withdraw from an account.
	 * @param accNum account number
	 * @param amount withdraw amount
	 */
	public static void withdraw(String accNum, String amount) 
	{	
		System.out.println(":: WITHDRAW - RUNNING");
		int accountNumber = -1;
		int withdrawalAmount = -1;
		int balance = -1;
		try
		{
			accountNumber = Integer.parseInt(accNum);
			withdrawalAmount = Integer.parseInt(amount);
			if(accountNumber < 0 || withdrawalAmount < 0)
			{
				System.out.println("Could not complete withdrawal because you entered a negative number for account or amount");
				System.out.println(":: WITHDRAW - FAILURE");
				System.out.println();
				return;
			}
		}
		catch(Exception e)
		{
			System.out.println("Could not complete withdrawal because you entered a non-integer value for account or amount");
			System.out.println(":: WITHDRAW - FAILURE");
			System.out.println();
			return;
		}
		try
		{
			con = DriverManager.getConnection(url, username, password);
			String query = "SELECT * FROM P1.Account WHERE NUMBER = ? AND STATUS = ?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, accountNumber);
			ps.setString(2, "A");
			ResultSet res = ps.executeQuery();
			if(!res.next())
			{
				System.out.println("Could not complete withdrawal, there are no active accounts with number " + accountNumber);
				System.out.println(":: WITHDRAW - FAILURE");
				System.out.println();
				con.close();
				return;
			}
			else  //There is such an account, check the balance
			{
				balance = res.getInt(3);
				if(withdrawalAmount > balance)
				{
					System.out.println("Could not complete withdrawal, you may not withdraw more than your current balance");
					System.out.println(":: WITHDRAW - FAILURE");
					System.out.println();
					con.close();
					return;
				}
			}	 
		}
		catch(Exception e)
		{
			System.out.println("Error querying database, transaction aborted");
			System.out.println(":: WITHDRAW - FAILURE");
			System.out.println();
			return;
		}
		try
		{	
			con = DriverManager.getConnection(url, username, password);
			String query = "UPDATE P1.Account SET BALANCE = BALANCE - ? WHERE NUMBER = ?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, withdrawalAmount);
			ps.setInt(2, accountNumber);
			ps.executeUpdate();
			con.close();
			System.out.println(":: WITHDRAW - SUCCESS");
			System.out.println();
			return;
		}
		catch(Exception e)
		{
			System.out.println("Error updating database, transaction aborted");  //This should not happen
			System.out.println(":: WITHDRAW - FAILURE");
			System.out.println();
			return;
		}
	}

	/**
	 * Transfer amount from source account to destination account. 
	 * @param srcAccNum source account number
	 * @param destAccNum destination account number
	 * @param amount transfer amount
	 */
	public static void transfer(String srcAccNum, String destAccNum, String amount) 
	{
		System.out.println(":: TRANSFER - RUNNING");
		int srcAcc = -1;
		int destAcc = -1;
		int amt = -1;
		int balance= -1;
		
		try
		{
			srcAcc = Integer.parseInt(srcAccNum);
			destAcc = Integer.parseInt(destAccNum);
			amt= Integer.parseInt(amount);
			if(srcAcc < 0 || destAcc < 0 || amt < 0)
			{
				System.out.println("Could not complete transfer because you entered a negative number for account or amount");
				System.out.println(":: TRANSFER - FAILURE");
				System.out.println();
				return;
			}
		}
		catch(Exception e)
		{
			System.out.println("Could not complete transfer because you entered a non-integer value for account or amount");
			System.out.println(":: TRANSFER - FAILURE");
			System.out.println();
			return;
		}
		try
		{
			con = DriverManager.getConnection(url, username, password);
			String query = "SELECT * FROM P1.Account WHERE NUMBER = ? AND STATUS = ?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, destAcc);
			ps.setString(2, "A");
			ResultSet res = ps.executeQuery();
			if(!res.next())
			{
				System.out.println("Could not complete transfer, there are no active accounts with number " + destAcc);
				System.out.println(":: TRANSFER - FAILURE");
				System.out.println();
				con.close();
				return;
			}
			else  //There is a dest acct, check for src
			{
				query = "SELECT * FROM P1.Account WHERE NUMBER = ? AND STATUS = ?";
			 	ps = con.prepareStatement(query);
				ps.setInt(1,srcAcc);
				ps.setString(2,"A");
				res = ps.executeQuery();
				if(!res.next())
				{
					System.out.println("Could not complete transfer, there are no active accounts with number " + srcAcc);
					System.out.println(":: TRANSFER - FAILURE");
					System.out.println();
					con.close();
					return;
				}
				else	//there is a src acct too, check the balance
				{
					balance = res.getInt(3);
					if(amt > balance)
					{
						System.out.println("Could not complete transfer, you may not transfer more than your current balance");
						System.out.println(":: TRANSFER - FAILURE");
						System.out.println();
						con.close();
						return;
					}	
			 		query = "UPDATE P1.ACCOUNT SET BALANCE= BALANCE - ? WHERE NUMBER = ?";
			 		ps = con.prepareStatement(query);
					ps.setInt(1, amt);
					ps.setInt(2, srcAcc);
					ps.executeUpdate();
			
					query = "UPDATE P1.ACCOUNT SET BALANCE = BALANCE + ? WHERE NUMBER = ?";
				 	ps = con.prepareStatement(query);
					ps.setInt(1,amt);
					ps.setInt(2,destAcc);
					ps.executeUpdate();
					con.close();
					System.out.println(":: TRANSFER - SUCCESS");
					System.out.println();
				}	
			}	 
		}
		catch(Exception e)
		{
			System.out.println("Database communication error, transaction aborted");
			System.out.println(":: TRANSFER - FAILURE");
			System.out.println();
			e.printStackTrace();
			return;
		}
	}
	
	public static void printSummary(ResultSet res)
	{
		System.out.println("NUMBER      BALANCE");
		System.out.println("----------- -----------");
		String number = "";
		String balance = "";
		int nlen = 0;
		int blen = 0;
		int total = 0;
		try
		{
			while(res.next())
			{
				number = res.getString(1);
				balance = res.getString(3);
				total += res.getInt(3);
				System.out.print(String.format("%1$" + 11  + "s", number));
				System.out.println(String.format("%1$" + 12 + "s", balance));
			}
			System.out.println("TOTAL");
			System.out.println("-----------");
			String tot = Integer.toString(total);
			System.out.println(String.format("%1$" + 11  + "s", tot));
			System.out.println(":: ACCOUNT SUMMARY - SUCCESS");
			System.out.println();
		}
		catch(SQLException e)
		{
			System.out.println("Error retrieving account summary from database");
			System.out.println(":: ACCOUNT SUMMARY - FAILURE");
			System.out.println();
		}
		
	}

	/**
	 * Display account summary.
	 * @param accNum account number
	 */
	public static void accountSummary(String accNum) 
	{	
		System.out.println(":: ACCOUNT SUMMARY - RUNNING");
		int ac = -1;
		try
		{
			ac = Integer.parseInt(accNum);
		}
		catch(Exception e)
		{
			System.out.println("Error, you did not supply an integer for account number");
			System.out.println(":: ACCOUNT SUMMARY - FAILURE");
			System.out.println();
			return;
		}
		try{
			con = DriverManager.getConnection(url, username, password);
			String query = "SELECT * FROM P1.Account WHERE ID  = ? AND STATUS = ?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, ac);
			ps.setString(2, "A");
			ResultSet res = ps.executeQuery();
			printSummary(res);
			con.close();
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("SQL Error while querying database");
			System.out.println(":: ACCOUNT SUMMARY - FAILURE");
			System.out.println();
			return;
		}
	}

	/**
	 * Display Report A - Customer Information with Total Balance in Decreasing Order.
	 */
	public static void reportA() 
	{
		System.out.println(":: REPORT A - RUNNING");
		try
		{
			con = DriverManager.getConnection(url, username, password);
			String query = "SELECT * FROM P1.AccountTotals ORDER BY Total DESC";
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet res = ps.executeQuery();
			
			System.out.println("ID          NAME            GENDER AGE         PIN         TOTAL");
			System.out.println("----------- --------------- ------ ----------- ----------- -----------");
			
			while(res.next())
			{
				String id = res.getString(1);
				String name = res.getString(2);
				String gender = res.getString(3);
				String age = res.getString(4);
				String pin = res.getString(5);
				String total = res.getString(6);
				System.out.print(String.format("%1$" + 11 + "s", id));
				System.out.print(String.format("%1$" + (name.length() + 1) + "s", name));
				System.out.print(String.format("%1$" + (17 - name.length()) + "s", gender));
				System.out.print(String.format("%1$" + 17 + "s", age));
				System.out.print(String.format("%1$" + 12 + "s", pin));
				System.out.println(String.format("%1$" + 12 + "s", total)); 
			}
			System.out.println(":: REPORT A - SUCCESS");
			System.out.println();
			con.close();
			return;
		}
		catch(Exception e)
		{
			System.out.println("Error querying database");
			System.out.println(":: REPORT A - FAILURE");
			System.out.println();
		}
	}

	/**
	 * Display Report B - Customer Information with Total Balance in Decreasing Order.
	 * @param min minimum age
	 * @param max maximum age
	 */
	public static void reportB(String min, String max) 
	{	
		int lo = 0;
		int hi = 0;
		System.out.println(":: REPORT B - RUNNING");
		try
		{
			lo = Integer.parseInt(min);
			hi = Integer.parseInt(max);
		}
		catch(Exception e)
		{
			System.out.println("You did not enter integers for min and max age");	
			System.out.println(":: REPORT B - FAILURE");
			System.out.println();
		}
		try
		{
			con = DriverManager.getConnection(url, username, password);
			String query = "SELECT AVG(P1.AccountTotals.Total) AS AVERAGE FROM P1.AccountTotals WHERE AGE >= ? AND AGE <= ?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, lo);
			ps.setInt(2, hi);
			ResultSet res = ps.executeQuery();
	 		if(!res.next())
			{
				System.out.println("There are no open accounts for that age range");
			}
			else
			{
				System.out.println("AVERAGE");
				System.out.println("-----------");
				String average = res.getString(1);
				System.out.println(String.format("%1$" + 11 + "s", average));
				System.out.println(":: REPORT B - SUCCESS");
				System.out.println();
				con.close();	
			}	
		}
		catch(Exception e)
		{
			System.out.println("Error querying database");
			System.out.println(":: REPORT B - FAILURE");
			System.out.println();
		}	
	}
	
	public static void MainMenu()
	{
		Scanner s = new Scanner(System.in);
		int choice = -1;
		
		while(choice != 3)
		{
			System.out.println();
			System.out.println("WELCOME TO THE BANK OF FRANK ATM!");
			System.out.println("MAIN MENU");
			System.out.println("1.  New Customer");
			System.out.println("2.  Customer Login");
			System.out.println("3.  Exit");
			try
			{
				choice = s.nextInt();
				if(choice < 1 || choice > 3)
				{
					System.out.println("Invalid choice, try again");
					s.nextLine();
				}
				else if(choice == 1) // New Customer
				{      
					s.nextLine();
					System.out.println("Enter your name:");
					String name = s.nextLine();
					System.out.println("Enter your gender 'M' or 'F'");
					String gender = s.nextLine();
					System.out.println("Enter your age:");
					String age = s.nextLine();
					System.out.println("Enter a PIN:");
					String pin = s.nextLine();
					newCustomer(name, gender, age, pin);
				}
				else if(choice == 2)
				{
					s.nextLine();
					System.out.println("Enter your ID:");
					String id = s.nextLine();
					System.out.println("Enter your PIN:");
					String pin = s.nextLine();
					login(id, pin);		
				}
				else
				{
					s.close();
					return;
				}	
			}
			catch(Exception e)
			{
				System.out.println("Invalid choice, try again");
				s.nextLine();
			}
		}
	}
	
	public static void login(String id, String pin)
	{
		int customerID = -1;
		int pinNumber = -1;
		try
		{
			customerID = Integer.parseInt(id);
			pinNumber = Integer.parseInt(pin);
		}
		catch(Exception e)
		{
			System.out.println("Error cannot login, you did not enter an integer for ID and PIN");
			return;
		}
		if(customerID == 0 && pinNumber == 0)
		{
			administratorMenu();
		}
		else
		{
			try
			{
				con = DriverManager.getConnection(url, username, password);
				String query = "SELECT * FROM P1.CUSTOMER WHERE ID = ? AND PIN = ?";
				PreparedStatement ps = con.prepareStatement(query);
				ps.setInt(1, Integer.parseInt(id));
				ps.setInt(2, Integer.parseInt(pin));
				ResultSet res = ps.executeQuery();
				if(!res.next())
				{
					con.close();
					System.out.println("Incorrect ID and PIN");
					return;
				}
				else
				{
					con.close();
					customerMenu(id, pin);
				}			
			}		
			catch(SQLException ex)
			{
				System.out.println("SQL Error");
				return;
			}
			catch(Exception e)
			{	
				System.out.println("Error reading ID or Pin");
				return;
			}
		}
	}	
	
	public static void administratorMenu()
	{	
		Scanner s = new Scanner(System.in);
		int choice = -1;
		while(choice != 4)
		{
			System.out.println();
			System.out.println("ADMINISTRATOR MENU");
			System.out.println("1.  Account Summary For A Customer");
			System.out.println("2.  Report A :: Customer Information with Total Balance in Decreasing Order");
			System.out.println("3.  Report B :: Find the Average Total Balance Between Age Groups");
			System.out.println("4.  Exit");	
			try
			{
				choice = s.nextInt();
				s.nextLine();
				if(choice < 1 || choice > 4)
				{
					System.out.println("That is not a valid choice, try again");
				}
				else if(choice == 4)
				{
					return;
				}
				else if(choice == 1)
				{
					System.out.println("Enter the account ID to get summary of");
					String str = s.nextLine();
					accountSummary(str);
						
				}
				else if(choice == 2)
				{
					reportA();
				}
				else
				{
					System.out.println("Enter the minimum age to include in the report");
					String min = s.nextLine();
					System.out.println("Enter the maximum age to include in the report");
					String max = s.nextLine();
					reportB(min, max);
					return;
				}
				
			}
			catch(Exception e)
			{	
				s.nextLine();
				System.out.println("That is not a valid choice, enter an integer 1-4");
			}
		}
	}

	public static void customerMenu(String id, String pin)
	{
		Scanner s = new Scanner(System.in);
		int choice = -1;
		while(choice != 7)
		{
			System.out.println();
			System.out.println("Welcome Back! - CUSTOMER MAIN MENU");
			System.out.println("1.  Open Account");
			System.out.println("2.  Close Account");
			System.out.println("3.  Deposit");
			System.out.println("4.  Withdraw");
			System.out.println("5.  Transfer");
			System.out.println("6.  Account Summary");
			System.out.println("7.  Exit");
			try
			{
				choice = s.nextInt();
				s.nextLine();
				if(choice < 1 || choice >  7)
				{
					System.out.println("That's not a valid choice");
				}
				else if(choice == 1)
				{
					System.out.println("Enter the ID of the person to own this account");
					String newacctid = s.nextLine();
					System.out.println("Enter the account type, 'C' for checking, 'S' for savings");
					String type = s.nextLine();
					System.out.println("How much do you want to initially deposit?");
					String balance = s.nextLine();
					openAccount(newacctid, type, balance);
				}
				else if(choice == 2)
				{
					System.out.println("Enter the account number of the account to close");
					String accNum = s.nextLine();
					try
					{
						int acc = Integer.parseInt(accNum);
						if(acc < 0)
						{
							System.out.println("Invalid account number, account not closed");
							customerMenu(id, pin);
						}
						con = DriverManager.getConnection(url, username, password);
						String query = "SELECT * FROM P1.Account WHERE NUMBER = ? AND ID = ? AND STATUS = ?";
						PreparedStatement ps = con.prepareStatement(query); 
						ps.setInt(1, acc);
						ps.setInt(2, Integer.parseInt(id));
						ps.setString(3, "A");
						ResultSet res = ps.executeQuery();
						if(!res.next())
						{	
							con.close();
							System.out.println("You do not have an open account with that account number, could not close account");		
						}
						else
						{
							con.close();
							closeAccount(accNum);
						}							
					}
					catch(SQLException ex)
					{
						System.out.println("SQL Error while querying table, account not closed");
					}
					catch(Exception e)
					{
						System.out.println("Could not read account number, enter an integer only, account not closed");
					}
				}
				else if(choice == 3)
				{
					System.out.println("Enter the account number to deposit into");
					String accNum = s.nextLine();
					System.out.println("Enter the amount to deposit");
					String depAmt = s.nextLine();
					deposit(accNum, depAmt); 
				}
				else if(choice == 4)
				{
					System.out.println("Enter the number of the account to withdraw from");
					String accNum = s.nextLine();
					System.out.println("Enter the amount to withdraw");
					String withdrawal = s.nextLine();
					int a = -1;
					try
					{
						a = Integer.parseInt(accNum);
						try
						{
							con = DriverManager.getConnection(url, username, password);
							String query = "SELECT * FROM P1.Account WHERE NUMBER = ? AND ID = ? AND STATUS = ?";
							PreparedStatement ps = con.prepareStatement(query);
							ps.setInt(1, a);
							ps.setInt(2, Integer.parseInt(id));
							ps.setString(3, "A");
							ResultSet res = ps.executeQuery();
							if(!res.next())
							{	
								con.close();
								System.out.println("You don't have an active account with that number, cannot withdraw");
							}
							else
							{
								con.close();
								withdraw(accNum, withdrawal); 
							}
						}
						catch(SQLException ex)
						{
							System.out.println("Error querying database, withdrawal aborted");
						}
						catch(Exception e)
						{
							System.out.println("Could not parse ID because it was not an integer");
						}	
					}
					catch(Exception e)
					{
						System.out.println("Could not make withdrawal because the account number you entered is not an integer");
					}	
					
				}
				else if(choice == 5)
				{
					System.out.println("Enter the account number to transfer from");
					String source = s.nextLine();
					System.out.println("Enter the account number to transfer funds to");
					String dest = s.nextLine();
					System.out.println("Enter the amount to transfer");
					String amt = s.nextLine();
					int acc = -1;
					try
					{
						acc = Integer.parseInt(source);
						try	
						{
							con = DriverManager.getConnection(url, username, password);
							String query = "SELECT * FROM P1.Account WHERE NUMBER = ? AND ID = ? AND STATUS = ?";
							PreparedStatement ps = con.prepareStatement(query);
							ps.setInt(1, acc);
							ps.setInt(2, Integer.parseInt(id));
							ps.setString(3, "A");
							ResultSet res = ps.executeQuery();
							if(!res.next())
							{	
								con.close();
								System.out.println("You don't have an active account with that number, cannot withdraw");
							}
							else
					        	{
								con.close();
								transfer(source, dest, amt); 
						       	}
						}
						catch(SQLException ex)
						{
							System.out.println("Error querying database, withdrawal aborted");
						}
						catch(Exception e)
						{
							System.out.println("Could not parse ID because it was not an integer");
						}	
					}
					catch(Exception e)
					{
						System.out.println("Error no tranfer: did not enter integer for account number");
					}		
				}
				else if(choice == 6)
				{
					accountSummary(id);
				}
				else
				{	
					return;
				}
			}
			catch(Exception e)
			{
				System.out.println("You must enter an integer 1-7");
				s.nextLine();
			}
		}
	}

}
