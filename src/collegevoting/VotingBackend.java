
package collegevoting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VotingBackend {

    // Candidate information
    public static class Candidate {
        public int id;
        public String name;
        public String department;
        public String position;

        public Candidate(int id, String name,
                         String department, String position) {
            this.id = id;
            this.name = name;
            this.department = department;
            this.position = position;
        }

        @Override
        public String toString() {
            return name + " - " + position;
        }
    }

    // Load candidates from database
    public List<Candidate> getCandidates() throws SQLException {
        List<Candidate> candidates = new ArrayList<>();

        String sql = "SELECT candidate_id, full_name, department, position "
                   + "FROM candidates ORDER BY candidate_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Candidate candidate = new Candidate(
                    rs.getInt("candidate_id"),
                    rs.getString("full_name"),
                    rs.getString("department"),
                    rs.getString("position")
                );

                candidates.add(candidate);
            }
        }

        return candidates;
    }

    // Validate student and save vote
    public void castVote(String studentId, int candidateId)
            throws SQLException {

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                // Check student ID
                String studentSql =
                    "SELECT student_id FROM students WHERE student_id = ?";

                try (PreparedStatement ps =
                         con.prepareStatement(studentSql)) {

                    ps.setString(1, studentId.trim());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                "Student ID not found."
                            );
                        }
                    }
                }

                // Check candidate exists
                String candidateSql =
                    "SELECT candidate_id FROM candidates WHERE candidate_id = ?";

                try (PreparedStatement ps =
                         con.prepareStatement(candidateSql)) {

                    ps.setInt(1, candidateId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                "Candidate not found."
                            );
                        }
                    }
                }

                // Prevent a student voting more than once
                String checkVote =
                    "SELECT vote_id FROM votes WHERE student_id = ?";

                try (PreparedStatement ps =
                         con.prepareStatement(checkVote)) {

                    ps.setString(1, studentId.trim());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            throw new SQLException(
                                "You have already voted."
                            );
                        }
                    }
                }

                // Save vote
                String insertSql =
                    "INSERT INTO votes (student_id, candidate_id) "
                    + "VALUES (?, ?)";

                try (PreparedStatement ps =
                         con.prepareStatement(insertSql)) {

                    ps.setString(1, studentId.trim());
                    ps.setInt(2, candidateId);
                    ps.executeUpdate();
                }

                con.commit();

            } catch (SQLException ex) {
                con.rollback();
                throw ex;

            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // Display election results
    public String getResults() throws SQLException {
        StringBuilder results = new StringBuilder();

        String sql =
            "SELECT c.full_name, c.position, COUNT(v.vote_id) AS total_votes "
            + "FROM candidates c "
            + "LEFT JOIN votes v ON c.candidate_id = v.candidate_id "
            + "GROUP BY c.candidate_id, c.full_name, c.position "
            + "ORDER BY total_votes DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                results.append(rs.getString("full_name"))
                       .append(" (")
                       .append(rs.getString("position"))
                       .append("): ")
                       .append(rs.getInt("total_votes"))
                       .append(" votes\n");
            }
        }

        if (results.length() == 0) {
            return "No candidates found.";
        }

        return results.toString();
    }
}
