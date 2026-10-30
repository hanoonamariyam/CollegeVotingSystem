
package collegevoting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class CandidateViewer {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/college_voting";
        String username = System.getenv("DB_USER");
String password = System.getenv("DB_PASSWORD");
        String query = "SELECT candidate_id, full_name, department, position "
                     + "FROM candidates";

        try (
            Connection con = DriverManager.getConnection(
                url, username, password
            );
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query)
        ) {
            System.out.println("===== COLLEGE VOTING CANDIDATES =====");

            while (rs.next()) {
                System.out.println(
                    "ID: " + rs.getInt("candidate_id")
                    + " | Name: " + rs.getString("full_name")
                    + " | Department: " + rs.getString("department")
                    + " | Position: " + rs.getString("position")
                );
            }

        } catch (SQLException e) {
            System.out.println("Could not load candidates.");
            e.printStackTrace();
        }
    }
}
