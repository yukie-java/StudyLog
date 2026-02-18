package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.StudyLog;

public class StudyLogDAO {

	private static final String JDBC_URL = "jdbc:h2:~/StudyLog;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // 一覧（新しい順）
    public List<StudyLog> findByUser(String userId) {
        List<StudyLog> list = new ArrayList<>();

        String sql = "SELECT * FROM study_logs WHERE user_id=? ORDER BY study_date DESC, id DESC";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                StudyLog log = new StudyLog();
                log.setId(rs.getInt("id"));
                log.setUserId(rs.getString("user_id"));
                log.setStudyDate(rs.getString("study_date"));
                log.setSubject(rs.getString("subject"));
                log.setMinutes(rs.getInt("minutes"));
                log.setMemo(rs.getString("memo"));
                list.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // 登録
    public void insert(StudyLog log) {
        String sql = "INSERT INTO study_logs(user_id, study_date, subject, minutes, memo, created_at, updated_at) "
                   + "VALUES(?,?,?,?,?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, log.getUserId());
            ps.setString(2, log.getStudyDate());
            ps.setString(3, log.getSubject());
            ps.setInt(4, log.getMinutes());
            ps.setString(5, log.getMemo());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 今日の合計（1〜3の「3」）
    public int sumByDate(String userId, String studyDate) {
        String sql = "SELECT COALESCE(SUM(minutes), 0) AS total FROM study_logs WHERE user_id=? AND study_date=?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, studyDate);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public boolean delete(int id, String userId) {
        String sql = "DELETE FROM study_logs WHERE id=? AND user_id=?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, userId);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
