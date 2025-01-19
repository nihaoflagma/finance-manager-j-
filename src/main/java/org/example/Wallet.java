package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class Wallet {
    private String username;
    private double balance;
    private List<Transaction> transactions;
    private Map<String, Double> budgets;

    public Wallet(String username) {
        this.username = username;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
        this.budgets = new HashMap<>();
    }

    // Добавляем метод для получения баланса
    public double getBalance() {
        return balance;
    }

    public void addIncome(String category, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма дохода должна быть больше нуля.");
        }
        transactions.add(new Transaction("income", category, amount));
        balance += amount;
        System.out.printf("Добавлен доход %.2f на категорию %s. Баланс: %.2f%n", amount, category, balance);
    }

    public void addExpense(String category, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма расхода должна быть больше нуля.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Недостаточно средств для расхода.");
        }
        transactions.add(new Transaction("expense", category, amount));
        balance -= amount;
        System.out.printf("Добавлен расход %.2f на категорию %s. Баланс: %.2f%n", amount, category, balance);
    }

    public void setBudget(String category, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Бюджет должен быть больше нуля.");
        }
        budgets.put(category, amount);
        System.out.printf("Бюджет для категории %s установлен на %.2f.%n", category, amount);
    }

    public Map<String, Object> getSummary() {
        Map<String, Double> expensesByCategory = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals("expense")) {
                expensesByCategory.merge(transaction.getCategory(), transaction.getAmount(), Double::sum);
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("Баланс", balance);

        double totalIncome = transactions.stream()
                .filter(t -> t.getType().equals("income"))
                .mapToDouble(Transaction::getAmount)
                .sum();
        summary.put("Доходы", totalIncome);

        double totalExpenses = transactions.stream()
                .filter(t -> t.getType().equals("expense"))
                .mapToDouble(Transaction::getAmount)
                .sum();
        summary.put("Расходы", totalExpenses);

        Map<String, Map<String, Double>> budgetSummary = new HashMap<>();
        for (Map.Entry<String, Double> entry : budgets.entrySet()) {
            String category = entry.getKey();
            double budget = entry.getValue();
            double spent = expensesByCategory.getOrDefault(category, 0.0);
            Map<String, Double> categorySummary = new HashMap<>();
            categorySummary.put("Бюджет", budget);
            categorySummary.put("Потрачено", spent);
            categorySummary.put("Остаток", budget - spent);
            budgetSummary.put(category, categorySummary);
        }
        summary.put("Бюджеты", budgetSummary);

        return summary;
    }

    public void save() {
        JSONObject data = new JSONObject();
        data.put("balance", balance);

        JSONArray transactionsArray = new JSONArray();
        for (Transaction transaction : transactions) {
            transactionsArray.put(transaction.toJSON());
        }
        data.put("transactions", transactionsArray);

        JSONObject budgetsJSON = new JSONObject(budgets);
        data.put("budgets", budgetsJSON);

        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (FileWriter file = new FileWriter("data/" + username + "_wallet.json")) {
            file.write(data.toString(4));
            System.out.println("Данные кошелька сохранены.");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении данных кошелька: " + e.getMessage());
        }
    }

    public void load() {
        File file = new File("data/" + username + "_wallet.json");
        if (!file.exists()) {
            System.out.println("Файл кошелька не найден, создается новый кошелек.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            JSONObject data = new JSONObject(content.toString());
            balance = data.getDouble("balance");

            transactions.clear();
            JSONArray transactionsArray = data.getJSONArray("transactions");
            for (int i = 0; i < transactionsArray.length(); i++) {
                transactions.add(Transaction.fromJSON(transactionsArray.getJSONObject(i)));
            }

            budgets.clear();
            JSONObject budgetsJSON = data.getJSONObject("budgets");
            for (String key : budgetsJSON.keySet()) {
                budgets.put(key, budgetsJSON.getDouble(key));
            }
            System.out.println("Данные кошелька успешно загружены.");
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке данных кошелька: " + e.getMessage());
        }
    }
}
