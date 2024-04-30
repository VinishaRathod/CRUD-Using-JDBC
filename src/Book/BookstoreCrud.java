package Book;
import java.sql.*;
import java.util.Scanner;

public class BookstoreCrud {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bookstore";
    private static final String USER = "root";
    private static final String PASSWORD = "Vinisha@0987";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection connection = null;

        try {
            // Establish a connection to the database
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Create the books table if it doesn't exist
            createBooksTable(connection);

            // Perform CRUD operations
            while (true) {
                System.out.println("Enter an option (create, read, update, delete, exit):");
                String option = scanner.nextLine().trim().toLowerCase();

                switch (option) {
                    case "create":
                        createBook(scanner, connection);
                        break;
                    case "read":
                        readBooks(connection);
                        break;
                    case "update":
                        updateBook(scanner, connection);
                        break;
                    case "delete":
                        deleteBook(scanner, connection);
                        break;
                    case "exit":
                        System.out.println("Exiting application.");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error.");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void createBooksTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS books (id INT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(255), author VARCHAR(255), price DOUBLE, qty INT)";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private static void createBook(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Enter book details (title, author, price, qty):");
        String title = scanner.nextLine();
        String author = scanner.nextLine();
        double price = scanner.nextDouble();
        int qty = scanner.nextInt();
        scanner.nextLine(); // Consume the newline left-over

        String sql = "INSERT INTO books (title, author, price, quantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setDouble(3, price);
            statement.setInt(4, qty);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new book was inserted successfully!");
            }
        }
    }

    private static void readBooks(Connection connection) throws SQLException {
        String sql = "SELECT * FROM books";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                double price = resultSet.getDouble("price");
                int qty = resultSet.getInt("quantity");
                System.out.println(String.format("ID: %d, Title: %s, Author: %s, Price: %.2f, Quantity: %d", id, title, author, price, qty));
            }
        }
    }

    private static void updateBook(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Enter the ID of the book to update:");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume the newline left-over
        System.out.println("Enter new book details (title, author, price, qty):");
        String title = scanner.nextLine();
        String author = scanner.nextLine();
        double price = scanner.nextDouble();
        int qty = scanner.nextInt();
        scanner.nextLine(); // Consume the newline left-over

        String sql = "UPDATE books SET title = ?, author = ?, price = ?, quantity = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setDouble(3, price);
            statement.setInt(4, qty);
            statement.setInt(5, id);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Book updated successfully!");
            } else {
                System.out.println("No book found with the specified ID.");
            }
        }
    }

    private static void deleteBook(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Enter the ID of the book to delete:");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume the newline left-over

        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Book deleted successfully!");
            } else {
                System.out.println("No book found with the specified ID.");
            }
        }
    }
}
