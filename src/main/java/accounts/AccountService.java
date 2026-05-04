package accounts;

import dbService.HibernateService;
import dbService.dataSets.UsersDataSet;

public class AccountService {
    private static final AccountService INSTANCE = new AccountService();
    private final HibernateService hibernateService;

    private AccountService() {
        this.hibernateService = HibernateService.getInstance();
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
            hibernateService.addUser(login, password, email);
            return true;
        } catch (Exception e) {
            System.err.println("️Ошибка регистрации: " + e.getMessage());
            return false;
        }
    }

    public UserProfile authenticate(String login, String password) {
        if (login == null || password == null || login.isEmpty()) return null;

        try {
            UsersDataSet dataSet = hibernateService.getUserByLogin(login);
            if (dataSet != null && dataSet.getPassword().equals(password)) {
                return new UserProfile(dataSet.getLogin(), dataSet.getPassword(), dataSet.getEmail());
            }
        } catch (Exception e) {
            System.err.println("Ошибка аутентификации: " + e.getMessage());
        }
        return null;
    }
}