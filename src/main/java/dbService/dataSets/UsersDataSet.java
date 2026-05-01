package dbService.dataSets;

public class UsersDataSet {
    private final String login;
    private final String password;
    private final String email;

    public UsersDataSet(String login, String password, String email) {
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return "UsersDataSet{login='" + login + "', email='" + email + "'}";
    }
}