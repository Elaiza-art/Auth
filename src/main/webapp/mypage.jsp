<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Java Servlet App</title>
        <style>
            body { font-family: sans-serif; margin: 20px; }
            .path { background: #f0f0f0; padding: 10px; margin: 10px 0; border-radius: 5px; }
            ul { list-style: none; padding: 0; }
            li { padding: 5px 0; }
            .folder { color: #e67e22; }
            .file { color: #27ae60; }
        </style>
</head>
<body>
    <h1>📁 File Browser</h1>
    <p><strong>Страница сгенерирована:</strong> ${timeCurrent}</p>

    <div class="path">
        <strong>📍 Путь:</strong> ${currentPath}
    </div>

    <c:if test="${canGoUp}">
        <a href="directories?path=${encodedParentPath}" class="btn">⬆️ На уровень выше</a>
    </c:if>

    <table>
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
                                <span>📁</span> <a href="directories?path=${item.encodedPath}">${item.name}</a>
                            </c:when>
                            <c:otherwise>
                                <span>📄</span> ${item.name}
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
                                <a href="directories?download=${item.encodedPath}" class="dl-btn">⬇️ Скачать</a>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>