package servlet;

import java.io.IOException;

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

@WebServlet("/EditStudyLogServlet")
public class EditStudyLogServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 編集画面表示
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User loginUser = (session == null) ? null : (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/StudyLogServlet");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/StudyLogServlet");
            return;
        }

        String userId = loginUser.getName(); // あなたの実装に合わせてる（StudyLogServletと同じ）
        StudyLogDAO dao = new StudyLogDAO();
        StudyLog log = dao.findByIdAndUser(id, userId);

        if (log == null) {
            response.sendRedirect(request.getContextPath() + "/StudyLogServlet");
            return;
        }

        request.setAttribute("log", log);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/edit.jsp");
        dispatcher.forward(request, response);
    }

    // 更新
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User loginUser = (session == null) ? null : (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        String idStr = request.getParameter("id");
        String studyDate = request.getParameter("studyDate");
        String subject = request.getParameter("subject");
        String minutesStr = request.getParameter("minutes");
        String memo = request.getParameter("memo");

        if (idStr == null || idStr.isBlank()
                || studyDate == null || studyDate.isBlank()
                || subject == null || subject.isBlank()
                || minutesStr == null || minutesStr.isBlank()) {

            request.setAttribute("error", "未入力があります");
            // 再表示用に log を作って戻す
            StudyLog back = new StudyLog();
            if (idStr != null && !idStr.isBlank()) {
                try { back.setId(Integer.parseInt(idStr)); } catch (Exception ignore) {}
            }
            back.setStudyDate(studyDate);
            back.setSubject(subject);
            try { back.setMinutes(Integer.parseInt(minutesStr)); } catch (Exception ignore) {}
            back.setMemo(memo);

            request.setAttribute("log", back);
            request.getRequestDispatcher("/WEB-INF/jsp/edit.jsp").forward(request, response);
            return;
        }

        int id;
        int minutes;
        try {
            id = Integer.parseInt(idStr);
            minutes = Integer.parseInt(minutesStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "数値の形式が正しくありません");
            request.getRequestDispatcher("/WEB-INF/jsp/edit.jsp").forward(request, response);
            return;
        }

        String userId = loginUser.getName();

        StudyLog log = new StudyLog();
        log.setId(id);
        log.setUserId(userId);
        log.setStudyDate(studyDate); // 今のあなたの型に合わせて String のまま
        log.setSubject(subject);
        log.setMinutes(minutes);
        log.setMemo(memo);

        StudyLogDAO dao = new StudyLogDAO();
        dao.update(log);

        response.sendRedirect(request.getContextPath() + "/StudyLogServlet");
    }
}