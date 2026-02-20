package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.StudyLogDAO;
import model.StudyLog;
import model.User;

@WebServlet("/StudyLogServlet")
public class StudyLogServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	// ログインチェック
        HttpSession session = request.getSession(false);
        User loginUser = (session == null) ? null : (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }
    	
    	String action = request.getParameter("action");

    	if ("delete".equals(action)) {
    	    int id = Integer.parseInt(request.getParameter("id"));

    	    StudyLogDAO dao = new StudyLogDAO();
    	    dao.delete(id, loginUser.getName());
    	    
    	    
    	    HttpSession s = request.getSession(true);
    	        s.setAttribute("flash", "削除しました");
    	    
    	    
    	    response.sendRedirect(request.getContextPath() + "/StudyLogServlet");
    	    return;
    	}
    	
    	      
    	response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    	response.setHeader("Pragma", "no-cache");
    	response.setDateHeader("Expires", 0);
 
        
        if (session != null) {
            String flash = (String) session.getAttribute("flash");
            if (flash != null) {
                session.removeAttribute("flash"); // ★一回表示したら消す
                request.setAttribute("flash", flash);
            }
        }

        String userId = loginUser.getName(); // ← study_logs.user_id(VARCHAR) なので NAME を使う
        String today = LocalDate.now().toString();

        StudyLogDAO dao = new StudyLogDAO();
        List<StudyLog> list = dao.findByUser(userId);
        int totalToday = dao.sumByDate(userId, today);

        request.setAttribute("logList", list);
        request.setAttribute("totalToday", totalToday);
        request.setAttribute("today", today);
        request.setAttribute("loginUser", loginUser);

        RequestDispatcher dispatcher =
                request.getRequestDispatcher("/WEB-INF/jsp/studylog.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ログインチェック
        HttpSession session = request.getSession(false);
        User loginUser = (session == null) ? null : (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String userId = loginUser.getName();
        String studyDate = request.getParameter("studyDate");
        String subject = request.getParameter("subject");
        String minutesStr = request.getParameter("minutes");
        String memo = request.getParameter("memo");

        // ざっくり最低限のチェック（必要なら強化）
        if (studyDate == null || studyDate.isBlank()
                || subject == null || subject.isBlank()
                || minutesStr == null || minutesStr.isBlank()) {

            // エラー表示したいなら、ここで doGet 相当を呼ぶ or エラーメッセージ付きforward
            response.sendRedirect(request.getContextPath() + "/StudyLogServlet");
            return;
        }

        int minutes;
        try {
            minutes = Integer.parseInt(minutesStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/StudyLogServlet");
            return;
        }

        StudyLog log = new StudyLog(userId, studyDate, subject, minutes, memo);

        StudyLogDAO dao = new StudyLogDAO();
        dao.insert(log);
        
        HttpSession s = request.getSession(true);
        s.setAttribute("flash", "登録しました");

        // PRG
        response.sendRedirect(request.getContextPath() + "/StudyLogServlet");
    }
}
