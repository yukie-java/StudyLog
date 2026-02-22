package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    
    
    
    //DB接続を取得するメソッド
    private Connection getConnection()
    throws SQLException{
    	return 
    DriverManager.getConnection(JDBC_URL,DB_USER,DB_PASS);
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
    
    public List<StudyLog> findByCondition(String userId, String from, String to, String subject) {

        List<StudyLog> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT * FROM study_logs WHERE user_id=?"
        );

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (from != null && !from.isEmpty()) {
            sql.append(" AND study_date >= ?");
            params.add(from);
        }

        if (to != null && !to.isEmpty()) {
            sql.append(" AND study_date <= ?");
            params.add(to);
        }

        if (subject != null && !subject.isEmpty()) {
            sql.append(" AND subject LIKE ?");
            params.add("%" + subject + "%");
        }

        sql.append(" ORDER BY study_date DESC");

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    
    public List<String> findSubjectsByUser(String userId, String subjectType) {

        List<String> list = new ArrayList<>();

        String sql =
          "SELECT DISTINCT subject FROM study_logs " +
          "WHERE user_id=? AND subject_type=? " +
          "ORDER BY subject";

        try(Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, subjectType);

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                list.add(rs.getString("subject"));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
    
 // 科目別集計（child/adult別）
    public Map<String, Map<String, Integer>> sumBySubject(String userId) {

        Map<String, Map<String, Integer>> result = new LinkedHashMap<>();
        result.put("child", new LinkedHashMap<>());
        result.put("adult", new LinkedHashMap<>());

        String sql =
            "SELECT subject_type, subject, SUM(minutes) AS total " +
            "FROM study_logs " +
            "WHERE user_id=? " +
            "GROUP BY subject_type, subject " +
            "ORDER BY subject_type, total DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("subject_type"); // child / adult
                    String subject = rs.getString("subject");
                    int total = rs.getInt("total");

                    if (!result.containsKey(type)) {
                        result.put(type, new LinkedHashMap<>());
                    }
                    result.get(type).put(subject, total);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // 登録
    public void insert(StudyLog log) {
        String sql = "INSERT INTO study_logs(user_id, study_date, subject, subject_type, minutes, memo, created_at, updated_at) "
                   + "VALUES(?,?,?,?,?,?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

        	ps.setString(1, log.getUserId());
        	ps.setString(2, log.getStudyDate());
        	ps.setString(3, log.getSubject());
        	ps.setString(4, log.getSubjectType());   // ★追加
        	ps.setInt(5, log.getMinutes());
        	ps.setString(6, log.getMemo());

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
    public StudyLog findByIdAndUser(int id, String userId) {
        String sql = "SELECT id, user_id, study_date, subject, minutes, memo " +
                     "FROM study_logs WHERE id=? AND user_id=?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StudyLog log = new StudyLog();
                    log.setId(rs.getInt("id"));
                    log.setUserId(rs.getString("user_id"));
                    log.setStudyDate(rs.getString("study_date"));
                    log.setSubject(rs.getString("subject"));
                    log.setMinutes(rs.getInt("minutes"));
                    log.setMemo(rs.getString("memo"));
                    return log;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(StudyLog log) {
        String sql = "UPDATE study_logs SET study_date=?, subject=?, minutes=?, memo=? " +
                     "WHERE id=? AND user_id=?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, log.getStudyDate());
            ps.setString(2, log.getSubject());
            ps.setInt(3, log.getMinutes());
            ps.setString(4, log.getMemo());
            ps.setInt(5, log.getId());
            ps.setString(6, log.getUserId());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public List<String> findSubjectsByUser(String userId) {

        List<String> list = new ArrayList<>();

        String sql =
            "SELECT DISTINCT subject FROM study_logs WHERE user_id=? ORDER BY subject";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("subject"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
