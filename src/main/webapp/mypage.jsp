<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Java Servlet App</title>
    <style>
        body { font-family: sans-serif; margin: 20px; }
        .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .logout { color: #e74c3c; text-decoration: none; font-weight: bold; }
        .logout:hover { text-decoration: underline; }
        .path { background: #f0f0f0; padding: 10px; margin: 10px 0; border-radius: 5px; }
        ul { list-style: none; padding: 0; }
        li { padding: 5px 0; }
        .folder { color: #e67e22; }
        .file { color: #C8A2C8; }
        .btn { display: inline-block; padding: 5px 10px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Файловый менеджер: ${currentUser.login}</h1>
        <a href="${pageContext.request.contextPath}/logout" class="logout">🚪 Выйти</a>
    </div>

    <p><strong>Страница сгенерирована:</strong> ${timeCurrent}</p>

    <div class="path">
        <strong>Путь:</strong> ${currentPath}
    </div>

    <c:if test="${canGoUp}">
        <a href="directories?path=${encodedParentPath}" class="btn">⬆️ На уровень выше</a>
    </c:if>

    <table border="1" cellpadding="8" cellspacing="0" style="width: 100%; margin-top: 15px;">
        <thead>
            <tr>
                <th>Имя</th>
                <th>Тип</th>
                <th class="size">Размер</th>
                <th>Действие</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="item" items="${items}">
                <tr>
                    <td>
                        <c:choose>
                            <c:when test="${item.directory}">
                                📁 <a href="directories?path=${item.encodedPath}">${item.name}</a>
                            </c:when>
                            <c:otherwise>
                                📄 ${item.name}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>${item.directory ? 'Папка' : 'Файл'}</td>
                    <td class="size">${item.sizeFormatted}</td>
                    <td>
                        <c:choose>
                            <c:when test="${item.directory}">
                                <a href="directories?path=${item.encodedPath}">Открыть</a>
                            </c:when>
                            <c:otherwise>
                                <a href="directories?download=${item.encodedPath}">Скачать</a>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>