<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Вход</title>
    <style>
        body { font-family: sans-serif; background: #EBECE9; margin: 0; display: flex; justify-content: center; align-items: center; height: 100vh; }
        .card { background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); width: 320px; }
        h2 { text-align: center; margin-top: 0; color: #637C92;; }
        input { width: 100%; padding: 10px; margin: 8px 0 16px; border: 1px solid #A8D1E8; border-radius: 4px; box-sizing: border-box; }
        button { width: 100%; padding: 10px; background: #5C7E6D; color: #fff; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
        button:hover { background: #4A6B58; }
        .error { color: #e74c3c; background: #fadbd8; padding: 8px; border-radius: 4px; margin-bottom: 12px; text-align: center; }
        .link { display: block; text-align: center; margin-top: 15px; color: #3498db; text-decoration: none; }
        .link:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="card">
        <h2> Вход </h2>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>
        <form method="post" action="auth">
            <input type="hidden" name="action" value="login">
            <input type="text" name="login" placeholder="Логин" required>
            <input type="password" name="password" placeholder="Пароль" required>
            <button type="submit">Войти</button>
        </form>
        <a href="auth?page=register" class="link">Нет аккаунта? Зарегистрироваться</a>
    </div>
</body>
</html>