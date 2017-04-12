import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;
public class BetterFutures {
	
	//Source on UNIXS before compiling and runninga
	
	private static Connection connection;
	private static Scanner keyboard;
	
	private static PreparedStatement ps;
	private static ResultSet rs;

	public static void main(String[] args) throws SQLException{
		connection = null;
		keyboard = null;
		ps = null;
		rs = null;
		
		try
		{
			//Attempt to Connect to Database
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			//MUST BE CLASS3
			connection = DriverManager.getConnection("jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass", "rlb97", "3938666");
			
			//Set connection isolation level & commit preference
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			System.out.println("Error connecting to database");
			System.exit(1);
		}
		
		//High-Level Menu Select Admin or Customer 
		boolean isAdmin = false;
		
		System.out.println("Welcome to the BetterFutures database program! Login as:\n");
		System.out.println("1) Admin\n2) Customer\n");
		
		String input = null;
		System.out.print("Please enter an option: ");
		keyboard = new Scanner(System.in); //CLOSE KEYBOARD
		input = keyboard.nextLine();
		while(!input.equals("1") && !input.equals("2"))
		{
			System.out.println("Please enter an option");
			input = keyboard.nextLine();
		}
		
		if(input.equals("1"))
		{
			isAdmin = true;
		}
		
		String userName = null;
		String password = null;
		
		System.out.print("\nPlease enter username: ");
		userName = keyboard.nextLine();
		System.out.print("Please enter password: ");
		password = keyboard.nextLine();
		
		while(!checkLogin(userName, password, isAdmin))
		{
			System.out.print("\nCould Not Authenticate\n\nPlease enter username: ");
			userName = keyboard.nextLine();
			System.out.print("Please enter password: ");
			password = keyboard.nextLine();
		}
		
		if(isAdmin)
		{
			adminMenu();
		}
		else
		{
			customerMenu();
		}
		
		connection.close();
	}
	
	private static void printRS(ResultSet rs)
	{
		try
		{
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			while (rs.next()) {
				for(int i = 1; i <= columnsNumber; i++)
					System.out.print(rs.getString(i) + " ");
				System.out.println();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private static void adminMenu()
	{
		System.out.println("\n1) New Customer Registration");
		System.out.println("2) Update Share Quotas For Day");
		System.out.println("3) Add Mutual Fund");
		System.out.println("4) Update Time and Date");
		System.out.println("5) Statistics");
		
		String input = null;
		input = keyboard.nextLine();
		
		while(Integer.parseInt(input) < 1 || Integer.parseInt(input) > 5)
		{
			System.out.print("Please enter an option: ");
			input = keyboard.nextLine();
		}
	}
	
	private static void customerMenu()
	{
		System.out.println("\n1) Browse Mutual Funds");
		System.out.println("2) Search Mutual Funds");
		System.out.println("3) Invest");
		System.out.println("4) Sell Shares");
		System.out.println("5) Buy Shares");
		System.out.println("6) Conditional Invest");
		System.out.println("7) Change Allocation Preference");
		System.out.println("8) Customer Portfolio\n");
		System.out.print("Please enter an option: ");
		
		String input = null;
		input = keyboard.nextLine();
		
		while(Integer.parseInt(input) < 1 || Integer.parseInt(input) > 8)
		{
			System.out.print("Please enter an option: ");
			input = keyboard.nextLine();
		}
		
		if(input.equals("1"))
		{
			browseMutualFunds();
		}
	}
	
	private static void browseMutualFunds()
	{
		System.out.println("\n1) List all by price");
		System.out.println("2) List all alphabetically");
		System.out.println("3) List all in category by price");
		System.out.println("4) List all in category alphabetically\n");
		System.out.print("Please enter an option: ");
		
		String input = null;
		input = keyboard.nextLine();
		
		while(Integer.parseInt(input) < 1 || Integer.parseInt(input) > 4)
		{
			System.out.print("Please enter an option: ");
			input = keyboard.nextLine();
		}
		
		if(input.equals("1"))
		{
			System.out.print("Please enter a date (in format YYYY-MM-DD): ");
			String date = keyboard.nextLine();
			
			String query = "SELECT * FROM MUTUALFUND M JOIN CLOSINGPRICE C ON M.symbol = C.symbol WHERE C.p_date = ? ORDER BY C.price DESC";
			try {
				ps = connection.prepareStatement(query);
				ps.setDate(1, java.sql.Date.valueOf(date));
				
				rs = ps.executeQuery();
				
				printRS(rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static boolean checkLogin(String userName, String password, boolean isAdmin)
	{
		try
		{
			if(isAdmin)
			{
				ps = connection.prepareStatement("SELECT * FROM ADMINISTRATOR");
			}
			else
			{
					ps = connection.prepareStatement("SELECT * FROM CUSTOMER");
			}
			
			rs = ps.executeQuery();
	
			while(rs.next())
			{
				if(rs.getString(1).equals(userName) && rs.getString(5).equals(password))
				{
					return true;
				}
			}
			
			ps.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		return false;
	}
}
