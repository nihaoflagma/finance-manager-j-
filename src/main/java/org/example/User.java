package org.example;

public class User {
    private String username;
    private String password;
    private Wallet wallet;

    public User(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Пароль должен содержать хотя бы 6 символов.");
        }

        this.username = username;
        this.password = password;
        this.wallet = new Wallet(username);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Wallet getWallet() {
        return wallet;
    }

    @Override
    public String toString() {
        return "User(username=" + username + ", password=" + password + ")";
    }
}
