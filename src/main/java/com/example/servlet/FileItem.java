package com.example.servlet;

public class FileItem {

    private String name;
    private String path;
    private String encodedPath;
    private boolean directory;
    private long size;  //Размер файла в байтах

    public FileItem() {}

    public FileItem(String name, String path, String encodedPath, boolean directory, long size) {
        this.name = name;
        this.path = path;
        this.encodedPath = encodedPath;
        this.directory = directory;
        this.size = size;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getEncodedPath() { return encodedPath; }
    public void setEncodedPath(String encodedPath) { this.encodedPath = encodedPath; }

    public boolean isDirectory() { return directory; }
    public void setDirectory(boolean directory) { this.directory = directory; }

    public long getSize() { return size; }  // 🆕 Геттер
    public void setSize(long size) { this.size = size; }


    public String getSizeFormatted() {
        if (directory) return "-";
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }
}