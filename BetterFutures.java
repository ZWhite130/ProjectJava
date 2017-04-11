import java.sql.*;
import java.util.Scanner;
public class BetterFutures {
	
	//Source on UNIXS before compiling and runninga
	
	private static Connection connection;

	public static void main(String[] args) throws SQLException{
		connection = null;
		
		try
		{
			//Attempt to Connect to Database
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			connection = DriverManager.getConnection("jdbc:oracle:thin:@unixs.cis.pitt.edu:1521:dbclass", "ztw9", "3713993");
			
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
		Scanner keyboard = new Scanner(System.in);
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
		System.out.print("\nPlease enter password: ");
		password = keyboard.nextLine();
		
		while(!checkLogin(userName, password, isAdmin))
		{
			System.out.print("\nCould Not Authenticate\n\nPlease enter username: ");
			userName = keyboard.nextLine();
			System.out.print("\nPlease enter password: ");
			password = keyboard.nextLine();
		}
	}

	private static boolean checkLogin(String userName, String password, boolean isAdmin)
	{
		try
		{
			PreparedStatement ps = null;
			
			if(isAdmin)
			{
				ps = connection.prepareStatement("SELECT * FROM ADMINISTRATOR WHERE login = ? AND password = ?");
				ps.setString(1, userName);
				ps.setString(2, password);
			}
			else
			{
					ps = connection.prepareStatement("SELECT * FROM CUSTOMER WHERE login = ? AND password = ?");
					ps.setString(1, userName);
					ps.setString(2, password);
			}
			
			ResultSet rs = ps.executeQuery();
			
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
