package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Создание объекта для менеджера финансов
        FinanceManager manager = new FinanceManager();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Добро пожаловать в систему управления финансами!");

        while (true) {
            try {
                // Ввод команды от пользователя
                System.out.print("Введите команду: ");
                String input = scanner.nextLine();
                String[] command = input.split(" ");

                if (command.length == 0) {
                    continue;
                }

                switch (command[0]) {
                    case "register":
                        if (command.length != 3) {
                            throw new IllegalArgumentException("Команда 'register' требует два аргумента: имя пользователя и пароль.");
                        }
                        manager.register(command[1], command[2]);
                        break;

                    case "login":
                        if (command.length != 3) {
                            throw new IllegalArgumentException("Команда 'login' требует два аргумента: имя пользователя и пароль.");
                        }
                        manager.login(command[1], command[2]);
                        break;

                    case "logout":
                        manager.logout();
                        break;

                    case "exit":
                        if (manager != null) {
                            manager.logout();
                        }
                        System.out.println("Приложение завершено.");
                        scanner.close();
                        return;

                    case "add_income":
                    case "add_expense":
                    case "set_budget":
                    case "summary":
                    case "export_summary":
                        manager.processCommand(command);
                        break;

                    case "transfer":
                        if (command.length != 5) {
                            throw new IllegalArgumentException("Команда 'transfer' требует четырех аргументов: от кого, кому, сумма и категория.");
                        }
                        String fromUsername = command[1];
                        String toUsername = command[2];
                        double amountToTransfer = Double.parseDouble(command[3]);
                        String transferCategory = command[4];
                        manager.transfer(fromUsername, toUsername, amountToTransfer, transferCategory);
                        break;

                    default:
                        System.out.println("Неизвестная команда.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }
}
