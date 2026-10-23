
package collegevoting;

import java.sql.*;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        try (Connection con = DBConnection.getConnection()) {

            while (true) {
                System.out.println("\n===== COLLEGE VOTING SYSTEM =====");
                System.out.println("1. View Candidates");
                System.out.println("2. Cast Vote");
                System.out.println("3. View Results");
                System.out.println("4. Admin Login");
                System.out.println("5. Exit");
                System.out.print("Choose: ");

                int choice = readInt();

                switch (choice) {
                    case 1:
                        viewCandidates(con);
                        break;
                    case 2:
                        castVote(con);
                        break;
                    case 3:
                        viewResults(con);
                        break;
                    case 4:
                        adminMenu(con);
                        break;
                    case 5:
                        System.out.println("Thank you!");
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }

    // ================= CANDIDATES =================

    static void viewCandidates(Connection con) throws SQLException {

        String sql = "SELECT candidate_id, full_name, position, department "
                   + "FROM candidates";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== CANDIDATES =====");

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println(
                    "ID: " + rs.getInt("candidate_id")
                    + " | Name: " + rs.getString("full_name")
                    + " | Position: " + rs.getString("position")
                    + " | Department: " + rs.getString("department")
                );
            }

            if (!found) {
                System.out.println("No candidates found.");
            }
        }
    }

    // ================= VOTING =================

    static void castVote(Connection con) throws SQLException {

        viewCandidates(con);

        System.out.print("\nEnter Student ID: ");
        String studentId = sc.nextLine().trim();

        String checkStudent =
            "SELECT student_id FROM students WHERE student_id = ?";

        try (PreparedStatement ps = con.prepareStatement(checkStudent)) {
            ps.setString(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Student ID not found!");
                    return;
                }
            }
        }

        System.out.print("Enter Candidate ID: ");
        int candidateId = readInt();

        String sql =
            "INSERT INTO votes (student_id, candidate_id) VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setInt(2, candidateId);
            ps.executeUpdate();

            System.out.println("Vote submitted successfully!");

        } catch (SQLException e) {
            System.out.println(
                "Vote failed. You may have already voted, "
                + "or the candidate ID is invalid.");
            System.out.println("Details: " + e.getMessage());
        }
    }

    // ================= RESULTS =================

    static void viewResults(Connection con) throws SQLException {

        String sql =
            "SELECT c.full_name, c.position, "
            + "COUNT(v.vote_id) AS total_votes "
            + "FROM candidates c "
            + "LEFT JOIN votes v "
            + "ON c.candidate_id = v.candidate_id "
            + "GROUP BY c.candidate_id, c.full_name, c.position "
            + "ORDER BY c.position, total_votes DESC";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== VOTING RESULTS =====");

            while (rs.next()) {
                System.out.println(
                    rs.getString("position") + " - "
                    + rs.getString("full_name") + ": "
                    + rs.getInt("total_votes") + " votes");
            }
        }
    }

    // ================= ADMIN =================

    static void adminMenu(Connection con) throws SQLException {

        System.out.print("Admin username: ");
        String username = sc.nextLine().trim();

        System.out.print("Admin password: ");
        String password = sc.nextLine();

        // Demo credentials
        if (!username.equals("admin")
                || !password.equals("admin123")) {
            System.out.println("Invalid admin login!");
            return;
        }

        System.out.println("Admin login successful!");

        while (true) {
            System.out.println("\n===== ADMIN PANEL =====");
            System.out.println("1. Add New Student");
            System.out.println("2. View Students");
            System.out.println("3. Add New Candidate");
            System.out.println("4. View Candidates");
            System.out.println("5. View Results");
            System.out.println("6. Logout");
            System.out.print("Choose: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    addStudent(con);
                    break;
                case 2:
                    viewStudents(con);
                    break;
                case 3:
                    addCandidate(con);
                    break;
                case 4:
                    viewCandidates(con);
                    break;
                case 5:
                    viewResults(con);
                    break;
                case 6:
                    System.out.println("Admin logged out.");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ================= ADD STUDENT =================

    static void addStudent(Connection con) throws SQLException {

        System.out.print("Student ID: ");
        String id = sc.nextLine().trim();

        System.out.print("Student Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Department: ");
        String department = sc.nextLine().trim();

        if (id.isEmpty() || name.isEmpty() || department.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        String sql =
            "INSERT INTO students "
            + "(student_id, full_name, department, password_hash) "
            + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, department);

            // Temporary placeholder, not a secure password hash.
            ps.setString(4, "TEMP_" + id);

            ps.executeUpdate();
            System.out.println("Student added successfully!");

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Student ID already exists!");
            } else {
                System.out.println("Could not add student: "
                        + e.getMessage());
            }
        }
    }

    // ================= VIEW STUDENTS =================

    static void viewStudents(Connection con) throws SQLException {

        String sql =
            "SELECT student_id, full_name, department FROM students";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== REGISTERED STUDENTS =====");

            while (rs.next()) {
                System.out.println(
                    rs.getString("student_id") + " | "
                    + rs.getString("full_name") + " | "
                    + rs.getString("department"));
            }
        }
    }

    // ================= ADD CANDIDATE =================

    static void addCandidate(Connection con) throws SQLException {

        System.out.print("Candidate Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Election Position: ");
        String position = sc.nextLine().trim();

        System.out.print("Department: ");
        String department = sc.nextLine().trim();

        if (name.isEmpty() || position.isEmpty()
                || department.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        String sql =
            "INSERT INTO candidates (full_name, position, department) "
            + "VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, position);
            ps.setString(3, department);

            ps.executeUpdate();
            System.out.println("Candidate added successfully!");

        } catch (SQLException e) {
            System.out.println("Could not add candidate: "
                    + e.getMessage());
        }
    }
}
