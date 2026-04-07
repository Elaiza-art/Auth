package com.example.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

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

    private static final String BASE_DIR = "C:\\Users\\User\\Desktop\\Универ";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathParam = req.getParameter("path");
        String downloadParam = req.getParameter("download");


        if (downloadParam != null && !downloadParam.isEmpty()) {
            handleDownload(downloadParam, resp);
            return;
        }

        File currentDir;

        if(pathParam == null || pathParam.isEmpty()){
            currentDir = new File(BASE_DIR);
        } else {
            try {
                String decoded = java.net.URLDecoder.decode(pathParam, StandardCharsets.UTF_8.toString());
                currentDir = new File(decoded);
            } catch (Exception e) {
                currentDir = new File(pathParam);
            }
            if (!currentDir.isDirectory()) {
                currentDir = new File(BASE_DIR);
            }
        }

        String timeCurrent = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

        List<FileItem> items = listItems(currentDir);
        boolean canGoUp = !currentDir.getAbsolutePath().equals(BASE_DIR);
        String encodedParentPath = encodePathForUrl(currentDir.getParent());

        req.setAttribute("timeCurrent", timeCurrent);
        req.setAttribute("currentPath", currentDir.getAbsolutePath()); //??? почему именно абсолют
        req.setAttribute("items", items);
        req.setAttribute("canGoUp", canGoUp);
        req.setAttribute("encodedParentPath", encodedParentPath);

        req.getRequestDispatcher("mypage.jsp").forward(req, resp);
    }

    private void handleDownload(String encodedPath, HttpServletResponse resp) throws IOException {

        String decodedPath;
        try {
            decodedPath = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неверный формат пути");
            return;
        }

        File file = new File(decodedPath);

        if (!file.isFile() || !file.canRead()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Файл не найден или недоступен");
            return;
        }

        String mimeType = Files.probeContentType(file.toPath());
        resp.setContentType(mimeType != null ? mimeType : "application/octet-stream");
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
