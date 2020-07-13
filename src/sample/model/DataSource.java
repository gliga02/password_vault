package sample.model;

import javafx.scene.control.Alert;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataSource {
    private static DataSource instance = new DataSource();

    public static DataSource getInstance() {
        return instance;
    }

    private DataSource() {
    }

    public static final String DB_NAME = "Bazica";
    public static final String CONNECTION_STRING = "jdbc:sqlserver://localhost:52323;databaseName=" + DB_NAME + ";integratedSecurity=true";

    public static final String TABLE_USER = "UserTable";
    public static final String USER_COLUMN_ID = "USER_ID";
    public static final String USER_COLUMN_NAME = "NAME";
    public static final String USER_COLUMN_USERNAME = "USERNAME";
    public static final String USER_COLUMN_PASSWORD = "PASSWORD";

    public static final String TABLE_ACCOUNT = "AccountTable";
    public static final String ACCOUNT_COLUMN_ID = "ACCOUNT_ID";
    public static final String ACCOUNT_COLUMN_SERVICE = "SERVICE";
    public static final String ACCOUNT_COLUMN_EMAIL = "EMAIL";
    public static final String ACCOUNT_COLUMN_USERNAME = "USERNAME";
    public static final String ACCOUNT_COLUMN_PASSWORD = "PASSWORD";

    public static final String INSERT_USERS = "INSERT INTO " + TABLE_USER + " VALUES (?, ?, ?)";
    public static final String LOGIN_QUERY = "SELECT * FROM " + TABLE_USER + " WHERE " + USER_COLUMN_USERNAME + " = ? AND "
            + USER_COLUMN_PASSWORD + " = ?";
    public static final String QUERY_ACCOUNTS_USER = "SELECT * FROM " + TABLE_ACCOUNT + " WHERE " + TABLE_ACCOUNT + ".USER_ID = ?";
    public static final String QUERY_ID_FOR_USER = "SELECT " + USER_COLUMN_ID + " FROM " + TABLE_USER + " WHERE " +
            USER_COLUMN_USERNAME + "= ? AND " + USER_COLUMN_PASSWORD + " = ?";
    public static final String DELETE_ACCOUNT = "DELETE FROM " + TABLE_ACCOUNT + " WHERE " + ACCOUNT_COLUMN_ID + " = ?";
    public static final String DELETE_USER_ACCOUNT = "DELETE FROM " + TABLE_USER + " WHERE " + USER_COLUMN_USERNAME + " = ? AND " + USER_COLUMN_PASSWORD + " = ?";
    public static final String COUNT_ACCOUNTS = "SELECT COUNT(*) AS count FROM " + TABLE_ACCOUNT + " JOIN " + TABLE_USER + " ON " +
            TABLE_ACCOUNT + "." + "USER_ID" + " = " + TABLE_USER + "." + "USER_ID" + " WHERE " + TABLE_USER + "." + USER_COLUMN_USERNAME + " = ? AND " +
            TABLE_USER + "." + USER_COLUMN_PASSWORD + " = ?";
    public static final String QUERY_USER_ID = "SELECT " + USER_COLUMN_ID + " FROM " + TABLE_USER + " WHERE " + USER_COLUMN_USERNAME + " = ?";
    public static final String INSERT_ACCOUNTS = "INSERT INTO " + TABLE_ACCOUNT + " VALUES (?, ?, ?, ?, ?)";
    public static final String QUERY_NAME_FOR_USERNAME = "SELECT " + USER_COLUMN_NAME + " FROM " + TABLE_USER + " WHERE " +
            USER_COLUMN_USERNAME + " = ? AND " + USER_COLUMN_PASSWORD + " = ?";
    public static final String QUERY_USERNAME = "SELECT " + USER_COLUMN_USERNAME + " FROM " + TABLE_USER;


    private Connection connection;
    private PreparedStatement insertUsers;
    private PreparedStatement loginQuery;
    private PreparedStatement queryAccountsUser;
    private PreparedStatement queryIdForUser;
    private PreparedStatement deleteAccount;
    private PreparedStatement deleteUserAccount;
    private PreparedStatement countAccounts;
    private PreparedStatement queryUserIdStatement;
    private PreparedStatement insertAccounts;
    private PreparedStatement queryNameForUsername;
    private PreparedStatement queryUsername;

    public boolean open() {
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING);
            insertUsers = connection.prepareStatement(INSERT_USERS, Statement.RETURN_GENERATED_KEYS);
            loginQuery = connection.prepareStatement(LOGIN_QUERY);
            queryAccountsUser = connection.prepareStatement(QUERY_ACCOUNTS_USER);
            queryIdForUser = connection.prepareStatement(QUERY_ID_FOR_USER);
            deleteAccount = connection.prepareStatement(DELETE_ACCOUNT);
            deleteUserAccount = connection.prepareStatement(DELETE_USER_ACCOUNT);
            countAccounts = connection.prepareStatement(COUNT_ACCOUNTS);
            queryUserIdStatement = connection.prepareStatement(QUERY_USER_ID);
            insertAccounts = connection.prepareStatement(INSERT_ACCOUNTS);
            queryNameForUsername = connection.prepareStatement(QUERY_NAME_FOR_USERNAME);
            queryUsername = connection.prepareStatement(QUERY_USERNAME);
            System.out.println("Successfully opened db");
            return true;
        } catch (SQLException e) {
            System.out.println("Error, database isn't opened");
            return false;
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }

            if (insertUsers != null) {
                insertUsers.close();
            }

            if (loginQuery != null) {
                loginQuery.close();
            }

            if (queryAccountsUser != null) {
                queryAccountsUser.close();
            }

            if (queryIdForUser != null) {
                queryIdForUser.close();
            }

            if (deleteAccount != null) {
                deleteAccount.close();
            }

            if (deleteUserAccount != null) {
                deleteUserAccount.close();
            }

            if (countAccounts != null) {
                countAccounts.close();
            }

            if (queryUserIdStatement != null) {
                queryUserIdStatement.close();
            }

            if (insertAccounts != null) {
                insertAccounts.close();
            }

            if (queryNameForUsername != null) {
                queryNameForUsername.close();
            }

            if (queryUsername != null) {
                queryUsername.close();
            }

        } catch (SQLException e) {
            System.out.println("Error, database isn't closed");
        }
    }

    public String queryName(String username, String password) {
        try {
            queryNameForUsername.setString(1, username);
            queryNameForUsername.setString(2, password);

            ResultSet results = queryNameForUsername.executeQuery();
            if (results.next()) {
                return results.getString(1);
            } else {
                return "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    public int insertUsers(String name, String username, String password) throws SQLException {
        queryUserIdStatement.setString(1, username);
        ResultSet results = queryUserIdStatement.executeQuery();
        if (results.next()) {
            return results.getInt(1);
        } else {
            insertUsers.setString(1, name);
            insertUsers.setString(2, username);
            insertUsers.setString(3, password);
            int affectedRows = insertUsers.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert user");
            }

            ResultSet generatedKeys = insertUsers.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Couldn't get id for user");
            }
        }
    }


    public boolean loginQuery(String username, String password) throws SQLException {
        loginQuery.setString(1, username);
        loginQuery.setString(2, password);
        ResultSet results = loginQuery.executeQuery();
        int count = 0;

        while (results.next()) {
            count++;
        }

        if (count == 1) {
            System.out.println("Logged in successfully");
            return true;
        } else {
            System.out.println("Username or password are not correct");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Username or password are not correct");
            alert.show();
            return false;
        }
    }


    public List<Account> queryAccountsUser(int userId) throws SQLException {
        queryAccountsUser.setInt(1, userId);
        ResultSet results = queryAccountsUser.executeQuery();
        List<Account> accounts = new ArrayList<>();
        while (results.next()) {
            int accountId = results.getInt(ACCOUNT_COLUMN_ID);
            String service = results.getString(ACCOUNT_COLUMN_SERVICE);
            String email = results.getString(ACCOUNT_COLUMN_EMAIL);
            String username = results.getString(ACCOUNT_COLUMN_USERNAME);
            String password = results.getString(ACCOUNT_COLUMN_PASSWORD);
            Account account = new Account();
            account.setId(accountId);
            account.setEmail(email);
            account.setPassword(password);
            account.setUsername(username);
            account.setService(service);
            accounts.add(account);
        }

        return accounts;
    }

    public int queryUserId(String username, String password) {
        try {
            queryIdForUser.setString(1, username);
            queryIdForUser.setString(2, password);
            int userId = 0;
            ResultSet results = queryIdForUser.executeQuery();
            while (results.next()) {
                userId = results.getInt(USER_COLUMN_ID);
            }

            return userId;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public boolean deleteAccount(int accountId) {
        try {
            deleteAccount.setInt(1, accountId);
            int affectedRecords = deleteAccount.executeUpdate();
            return affectedRecords == 1;
        } catch (SQLException e) {
            System.out.println("Deleting failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUserAccount(String username, String password) throws SQLException {
        deleteUserAccount.setString(1, username);
        deleteUserAccount.setString(2, password);
        int affectedRecords = deleteUserAccount.executeUpdate();
        return affectedRecords == 1;
    }

    public int countAccountsForUser(String username, String password) {
        try {
            countAccounts.setString(1, username);
            countAccounts.setString(2, password);

            ResultSet results = countAccounts.executeQuery();
            results.next();
            int count = results.getInt("count");

            return count;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void insertAccounts(User user, String service, String email, String username, String password) {
        try {
            connection.setAutoCommit(false);

            int userId = insertUsers(user.getName(), user.getUsername(), user.getPassword());
            insertAccounts.setString(1, service);
            insertAccounts.setString(2, email);
            insertAccounts.setString(3, username);
            insertAccounts.setString(4, password);
            insertAccounts.setInt(5, userId);
            int affectedRows = insertAccounts.executeUpdate();
            if (affectedRows == 1) {
                connection.commit();
            } else {
                throw new SQLException("Failed to insert account");
            }
        } catch (Exception e) {
            System.out.println("Exception while inserting account: " + e.getMessage());
            try {
                System.out.println("Performing rollback");
                connection.rollback();
            } catch (SQLException e2) {
                System.out.println("Failed to perform rollback: " + e2.getMessage());
            }
        } finally {
            try {
                System.out.println("Resetting commit to default");
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Couldn't reset auto commit: " + e.getMessage());
            }
        }
    }

    public List<String> queryUsernames() {
        try {
            ResultSet results =  queryUsername.executeQuery();
            List<String> usernames = new ArrayList<>();
            while (results.next()) {
                usernames.add(results.getString(1));
            }

            return usernames;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
