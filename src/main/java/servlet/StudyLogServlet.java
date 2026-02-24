package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

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

	    // loginType（セッションから）
	    String loginType = (String) session.getAttribute("loginType");
	    request.setAttribute("loginType", loginType);

	    // 削除
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

	    // キャッシュ無効
	    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
	    response.setHeader("Pragma", "no-cache");
	    response.setDateHeader("Expires", 0);

	    // フラッシュ
	    String flash = (String) session.getAttribute("flash");
	    if (flash != null) {
	        session.removeAttribute("flash");
	        request.setAttribute("flash", flash);
	    }

	    String userId = loginUser.getName();
	    String today = LocalDate.now().toString();

	    StudyLogDAO dao = new StudyLogDAO();

	    // ★履歴候補：今は「全subject」でOK（adult入力の候補用途）
	    List<String> subjects = dao.findSubjectsByUser(userId);
	    request.setAttribute("subjects", subjects);

	 // ===== 表示中のsubjectTypeを先に決める =====
	    String subjectType = request.getParameter("subjectType");

	    // childログインは固定
	    if ("child".equals(loginType)) {
	        subjectType = "child";
	    } else {
	        if (subjectType == null || subjectType.isBlank()) {
	            subjectType = "adult";
	        }
	    }
	    request.setAttribute("viewSubjectType", subjectType);

	    // ===== 検索 =====
	    String from = request.getParameter("from");
	    String to = request.getParameter("to");
	    String subject = request.getParameter("subject");

	    List<StudyLog> list;
	    if ((from != null && !from.isEmpty())
	     || (to != null && !to.isEmpty())
	     || (subject != null && !subject.isEmpty())) {

	        // ★検索でもtypeで絞る
	        list = dao.findByConditionAndType(userId, subjectType, from, to, subject);

	        request.setAttribute("from", from);
	        request.setAttribute("to", to);
	        request.setAttribute("subject", subject);
	    } else {
	        // ★通常一覧もtypeで絞る
	        list = dao.findByUserAndType(userId, subjectType);
	    }

	    int totalToday = dao.sumByDate(userId, today);

	    String thisMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

	    int weekTotal  = dao.sumWeek(userId, subjectType, today);
	    int monthTotal = dao.sumMonth(userId, subjectType, thisMonth);
	    int grandTotal = dao.sumTotal(userId, subjectType);

	    request.setAttribute("weekTotal", weekTotal);
	    request.setAttribute("monthTotal", monthTotal);
	    request.setAttribute("grandTotal", grandTotal);

	    Map<String, Map<String,Integer>> subjectTotals = dao.sumBySubject(userId);
	    request.setAttribute("subjectTotals", subjectTotals);

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
        String subjectType = request.getParameter("subjectType");
        
     // ★保険：subjectTypeが来ない時は loginType で補完（NULL防止）
        String loginType = (String) session.getAttribute("loginType");

        // childログインなら強制child（adultへ切替させない）
        if ("child".equals(loginType)) {
            subjectType = "child";
        }

        // adultログインで subjectType が空なら adult にする
        if (subjectType == null || subjectType.isBlank()) {
            subjectType = "adult";
        }

        
        if (studyDate == null || studyDate.isBlank()
                || subject == null || subject.isBlank()
                || minutesStr == null || minutesStr.isBlank()) {

            
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

        StudyLog log = new StudyLog(userId, studyDate, subjectType, subject, minutes, memo);
        StudyLogDAO dao = new StudyLogDAO();
        dao.insert(log);
        
        HttpSession s = request.getSession(true);
        s.setAttribute("flash", "登録しました");

        // PRG
        response.sendRedirect(request.getContextPath() + "/StudyLogServlet");
    }
}
