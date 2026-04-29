package accounts;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountService {
    private static final AccountService INSTANCE = new AccountService();

    private final Map<String, UserProfile> users = new ConcurrentHashMap<>();

    // Файл для сохранения данных между рестартами
    private static final String USERS_FILE = "users.txt";

    private AccountService() {
        loadUsersFromFile();
    }

    public static AccountService getInstance() {
        return INSTANCE;
    }

    private void loadUsersFromFile() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                UserProfile user = UserProfile.fromFileLine(line);
                if (user != null) {
                    users.put(user.getLogin(), user);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки пользователей: " + e.getMessage());
        }
    }

    private void saveUserToFile(UserProfile user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            writer.write(user.toFileLine());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Ошибка сохранения пользователя: " + e.getMessage());
        }
    }

    public boolean register(String login, String password, String email) {
        if (login == null || password == null || email == null ||
                login.isEmpty() || password.isEmpty()) {
            return false;
        }
        if (users.containsKey(login)) {
            return false; // Логин уже занят
        }

        UserProfile newUser = new UserProfile(login, password, email);
        users.put(login, newUser);
        saveUserToFile(newUser);
        return true;
    }

    public UserProfile authenticate(String login, String password) {
        if (login == null || password == null) return null;
        UserProfile user = users.get(login.trim());
        if (user != null && user.getPassword().equals(password.trim())) {
            return user;
        }
        return null;
    }
}