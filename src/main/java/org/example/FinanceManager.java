package org.example;

import java.util.HashMap;
import java.util.Map;

public class FinanceManager {
    private Map<String, User> users;  // Словарь пользователей по имени пользователя
    private User loggedInUser;        // Текущий вошедший пользователь

    public FinanceManager() {
        users = new HashMap<>();
        loggedInUser = null;
    }

    // Регистрация нового пользователя
    public void register(String username, String password) {
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует.");
        }
        User newUser = new User(username, password);
        users.put(username, newUser);
        System.out.println("Пользователь " + username + " успешно зарегистрирован.");
    }

    // Логин пользователя
    public void login(String username, String password) {
        User user = users.get(username);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден.");
        }
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Неверный пароль.");
        }
        loggedInUser = user;
        System.out.println("Пользователь " + username + " успешно вошел в систему.");
    }

    // Логаут (выход из системы)
    public void logout() {
        if (loggedInUser == null) {
            System.out.println("Вы не вошли в систему.");
            return;
        }
        System.out.println("Вы успешно вышли из системы.");
        loggedInUser = null;
    }

    // Обработка команд
    public void processCommand(String[] command) {
        if (loggedInUser == null) {
            System.out.println("Пожалуйста, войдите в систему.");
            return;
        }

        switch (command[0]) {
            case "add_income":
                if (command.length != 3) {
                    throw new IllegalArgumentException("Команда 'add_income' требует два аргумента: категория и сумма.");
                }
                String incomeCategory = command[1];
                double incomeAmount = Double.parseDouble(command[2]);
                loggedInUser.getWallet().addIncome(incomeCategory, incomeAmount);
                break;

            case "add_expense":
                if (command.length != 3) {
                    throw new IllegalArgumentException("Команда 'add_expense' требует два аргумента: категория и сумма.");
                }
                String expenseCategory = command[1];
                double expenseAmount = Double.parseDouble(command[2]);
                loggedInUser.getWallet().addExpense(expenseCategory, expenseAmount);
                break;

            case "set_budget":
                if (command.length != 3) {
                    throw new IllegalArgumentException("Команда 'set_budget' требует два аргумента: категория и бюджет.");
                }
                String budgetCategory = command[1];
                double budgetAmount = Double.parseDouble(command[2]);
                loggedInUser.getWallet().setBudget(budgetCategory, budgetAmount);
                break;

            case "summary":
                Map<String, Object> summary = loggedInUser.getWallet().getSummary();
                System.out.println("Баланс: " + summary.get("Баланс"));
                System.out.println("Доходы: " + summary.get("Доходы"));
                System.out.println("Расходы: " + summary.get("Расходы"));
                System.out.println("Бюджеты: " + summary.get("Бюджеты"));
                break;

            case "export_summary":
                loggedInUser.getWallet().save();
                break;

            default:
                System.out.println("Неизвестная команда.");
                break;
        }
    }

    // Перевод средств между пользователями
    public void transfer(String fromUsername, String toUsername, double amount, String category) {
        if (loggedInUser == null) {
            throw new IllegalArgumentException("Вы не вошли в систему.");
        }

        // Получаем кошельки пользователей
        User fromUser = users.get(fromUsername);
        User toUser = users.get(toUsername);

        if (fromUser == null) {
            throw new IllegalArgumentException("Пользователь с именем " + fromUsername + " не найден.");
        }
        if (toUser == null) {
            throw new IllegalArgumentException("Пользователь с именем " + toUsername + " не найден.");
        }

        // Проверяем, достаточно ли средств у отправителя
        Wallet fromWallet = fromUser.getWallet();
        if (fromWallet.getBalance() < amount) {
            throw new IllegalArgumentException("Недостаточно средств на балансе отправителя.");
        }

        // Переводим деньги
        fromWallet.addExpense(category, amount);  // Снимаем деньги с отправителя
        toUser.getWallet().addIncome(category, amount);  // Зачисляем деньги получателю

        System.out.printf("Переведено %.2f из категории %s от пользователя %s к пользователю %s.%n",
                amount, category, fromUsername, toUsername);
    }
}
