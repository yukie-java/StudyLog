package servlet;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.UserDAO;
import model.User;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RequestDispatcher dispatcher =
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("loginId");   
        String password = request.getParameter("password");


        // 簡易バリデーション
        if (name == null || name.isBlank() || password == null || password.isBlank()) {
            request.setAttribute("error", "ユーザー名とパスワードを入力してください");
            RequestDispatcher dispatcher =
                    request.getRequestDispatcher("/WEB-INF/jsp/login.jsp");
            dispatcher.forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.findByNameAndPassword(name, password);

        if (user == null) {
            request.setAttribute("error", "ユーザー名またはパスワードが違います");
            RequestDispatcher dispatcher =
                    request.getRequestDispatcher("/WEB-INF/jsp/login.jsp");
            dispatcher.forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("loginUser", user);

        String loginType;
        if ("a".equals(user.getName())) {
            loginType = "child";
        } else {
            loginType = "adult"; // y含め、それ以外は全部adult扱い
        }
        session.setAttribute("loginType", loginType);

        response.sendRedirect(request.getContextPath() + "/StudyLogServlet");
    }
}
