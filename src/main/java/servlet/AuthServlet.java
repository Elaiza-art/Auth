package servlet;

import accounts.AccountService;
import accounts.UserProfile;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    private final AccountService accountService = AccountService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Если пользователь уже залогинен - перенос на стр файл. менеджера
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/directories");
            return;
        }


        String page = req.getParameter("page");
        if ("register".equals(page)) {
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        if ("register".equals(action)) {
            boolean success = accountService.register(login, password, email);
            if (success) {
                UserProfile user = accountService.authenticate(login, password);
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);
                resp.sendRedirect(req.getContextPath() + "/directories");
            } else {
                req.setAttribute("error", "Ошибка регистрации: логин уже занят или пустые поля.");
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
            }
        } else if ("login".equals(action)) {
            UserProfile user = accountService.authenticate(login, password);
            if (user != null) {
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);
                resp.sendRedirect(req.getContextPath() + "/directories");
            } else {
                req.setAttribute("error", "Неверный логин или пароль.");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
            }
        }
    }
}