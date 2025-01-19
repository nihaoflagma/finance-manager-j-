package org.example;

import org.json.JSONObject;

public class Transaction {
    private String type; // Тип: доход или расход
    private String category; // Категория (например, еда, транспорт)
    private double amount; // Сумма

    public Transaction(String type, String category, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма транзакции должна быть больше нуля.");
        }
        if (!type.equals("income") && !type.equals("expense")) {
            throw new IllegalArgumentException("Тип транзакции должен быть либо 'income', либо 'expense'.");
        }
        this.type = type;
        this.category = category;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("category", category);
        json.put("amount", amount);
        return json;
    }

    public static Transaction fromJSON(JSONObject json) {
        String type = json.getString("type");
        String category = json.getString("category");
        double amount = json.getDouble("amount");
        return new Transaction(type, category, amount);
    }
}

