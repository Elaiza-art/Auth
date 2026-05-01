package accounts;

import dbService.DBException;
import dbService.DBService;
import dbService.dataSets.UsersDataSet;

public class AccountService {

    private static final AccountService INSTANCE = new AccountService();
    private final DBService dbService;

    private AccountService() {
        this.dbService = DBService.getInstance();
    }

    public static AccountService getInstance() {
        return INSTANCE;
    }

    public boolean register(String login, String password, String email) {
        if (login == null || password == null || email == null ||
                login.isEmpty() || password.isEmpty() || email.isEmpty()) {
            return false;
        }

        try {
            dbService.addUser(login, password, email);
            return true;
        } catch (DBException e) {
            System.err.println("Ошибка регистрации: " + e.getMessage());
            return false;
        }
    }

    public UserProfile authenticate(String login, String password) {
        if (login == null || password == null || login.isEmpty()) {
            return null;
        }

        try {
            UsersDataSet dataSet = dbService.getUserByLogin(login);

            if (dataSet != null && dataSet.getPassword().equals(password)) {
                return new UserProfile(
                        dataSet.getLogin(),
                        dataSet.getPassword(),
                        dataSet.getEmail()
                );
            }
        } catch (DBException e) {
            System.err.println("Ошибка аутентификации: " + e.getMessage());
        }

        return null;
    }
}