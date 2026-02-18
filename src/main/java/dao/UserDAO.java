package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.User;

public class UserDAO {
    // H2コンソールと同じに揃えるのがおすすめ
    private static final String JDBC_URL =  "jdbc:h2:~/StudyLog;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // ログイン認証：NAME と PASSWORD で一致するユーザーを返す
    public User findByNameAndPassword(String name, String password) {
        String sql = "SELECT ID, NAME, ROLE FROM USERS WHERE NAME = ? AND PASSWORD = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("ID");
                    String uname = rs.getString("NAME");
                    String role = rs.getString("ROLE");
                    return new User(id, uname, role);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
