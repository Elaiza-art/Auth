package servlet;

import accounts.UserProfile;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/directories")
public class MainServlet extends HttpServlet {

    private static final String ROOT_DIR = "E:\\Students\\filemanager\\";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth");
            return;
        }

        UserProfile currentUser = (UserProfile) session.getAttribute("user");
        String userHomePath = ROOT_DIR + currentUser.getLogin();
        File userHome = new File(userHomePath);
        if (!userHome.exists()) userHome.mkdirs();

        String pathParam = req.getParameter("path");
        String downloadParam = req.getParameter("download");

        File currentDir;
        if (pathParam == null || pathParam.isEmpty()) {
            currentDir = userHome;
        } else {
            String decoded = URLDecoder.decode(pathParam, StandardCharsets.UTF_8.toString());
            currentDir = new File(decoded);
        }

        // Защита от выхода за пределы домашней папки
        String canonicalDir;
        try {
            canonicalDir = currentDir.getCanonicalPath();
        } catch (IOException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный путь");
            return;
        }

        if (!canonicalDir.startsWith(userHome.getCanonicalPath())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Доступ за пределы вашей папки запрещён");
            return;
        }

        if (downloadParam != null && !downloadParam.isEmpty()) {
            handleDownload(downloadParam, resp, userHome.getCanonicalPath());
            return;
        }

        if (!currentDir.isDirectory()) {
            currentDir = userHome;
        }


        String timeCurrent = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        List<FileItem> items = listItems(currentDir);
        boolean canGoUp = !currentDir.getAbsolutePath().equals(userHome.getAbsolutePath());
        String encodedParentPath = canGoUp ? encodePathForUrl(currentDir.getParent()) : "";

        req.setAttribute("timeCurrent", timeCurrent);
        req.setAttribute("currentPath", currentDir.getAbsolutePath());
        req.setAttribute("items", items);
        req.setAttribute("canGoUp", canGoUp);
        req.setAttribute("encodedParentPath", encodedParentPath);
        req.setAttribute("currentUser", currentUser);

        req.getRequestDispatcher("mypage.jsp").forward(req, resp);
    }

    private void handleDownload(String encodedPath, HttpServletResponse resp, String userHomeCanonical) throws IOException {
        String decodedPath = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.toString());
        File file = new File(decodedPath);

        try {
            String canonicalFile = file.getCanonicalPath();
            if (!canonicalFile.startsWith(userHomeCanonical)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Доступ запрещён");
                return;
            }
        } catch (IOException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный путь");
            return;
        }

        if (!file.isFile() || !file.canRead()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Файл не найден или недоступен");
            return;
        }

        String mimeType = Files.probeContentType(file.toPath());
        resp.setHeader("Content-Disposition", "attachment; filename=\"" +
                URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.toString()) + "\"");
        resp.setContentLengthLong(file.length());

        try (var input = Files.newInputStream(file.toPath());
             var output = resp.getOutputStream()) {
            input.transferTo(output);
        }
    }

    private List<FileItem> listItems(File dir){
        List<FileItem> result = new ArrayList<>();
        File[] files = dir.listFiles();
        if(files != null){
            for(File file : files){
                String encodedPath = encodePathForUrl(file.getAbsolutePath());
                result.add(new FileItem(
                        file.getName(),
                        file.getAbsolutePath(),
                        encodedPath,
                        file.isDirectory(),
                        file.length()));
            }
        }
        return result;
    }

    public static String encodePathForUrl(String path) {
        if (path == null || path.isEmpty()) return "";
        try {
            String normalized = path.replace('\\', '/');
            return URLEncoder.encode(normalized, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return path;
        }
    }
}