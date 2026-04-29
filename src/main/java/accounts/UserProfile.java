package accounts;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String login;
    private final String password;
    private final String email;

    public UserProfile(String login, String password, String email) {
        this.login = login.trim();
        this.password = password.trim();
        this.email = email.trim();
    }

    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }

    public String toFileLine() {
        return login + ":" + password + ":" + email;
    }

    public static UserProfile fromFileLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;

        String[] parts = line.split(":", 3);
        if (parts.length == 3) {
            return new UserProfile(parts[0], parts[1], parts[2]);
        }
        return null;
    }
}