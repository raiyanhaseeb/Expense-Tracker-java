package com.expensetracker;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExpenseTracker {
    private List<Transaction> transactions = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public void addTransaction() {
        System.out.print("Enter type (INCOME/EXPENSE): ");
        String type = scanner.next().toUpperCase();
        scanner.nextLine(); 

        System.out.print("Enter category (e.g., Salary, Rent): ");
        String category = scanner.nextLine();

        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();

        System.out.print("Enter date (yyyy-MM-dd): ");
        String dateInput = scanner.next();
        LocalDate date = LocalDate.parse(dateInput);

        transactions.add(new Transaction(type, category, amount, date));
        System.out.println("Transaction added.\n");
    }

    public void viewMonthlySummary(int year, int month) {
        double totalIncome = 0, totalExpense = 0;
        Map<String, Double> incomeMap = new HashMap<>();
        Map<String, Double> expenseMap = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.getDate().getYear() == year && t.getDate().getMonthValue() == month) {
                if (t.getType().equalsIgnoreCase("INCOME")) {
                    totalIncome += t.getAmount();
                    incomeMap.merge(t.getCategory(), t.getAmount(), Double::sum);
                } else if (t.getType().equalsIgnoreCase("EXPENSE")) {
                    totalExpense += t.getAmount();
                    expenseMap.merge(t.getCategory(), t.getAmount(), Double::sum);
                }
            }
        }

        System.out.println("\n===== Monthly Summary for " + year + "-" + String.format("%02d", month) + " =====");
        System.out.println("Total Income   : ₹" + totalIncome);
        System.out.println("Total Expenses : ₹" + totalExpense);
        System.out.println("Net Savings    : ₹" + (totalIncome - totalExpense));
        
        System.out.println("\n--- Income Breakdown ---");
        if (incomeMap.isEmpty()) {
            System.out.println("No income transactions found.");
        } else {
            incomeMap.forEach((category, amount) ->
                System.out.println("• " + category + " : ₹" + amount));
        }

        System.out.println("\n--- Expense Breakdown ---");
        if (expenseMap.isEmpty()) {
            System.out.println("No expense transactions found.");
        } else {
            expenseMap.forEach((category, amount) ->
                System.out.println("• " + category + " : ₹" + amount));
        }

        System.out.println("=========================================\n");
    }


    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Transaction t : transactions) {
                writer.write(t.toString());
                writer.newLine();
            }
            System.out.println("Data saved to " + filename + "\n");
        } catch (IOException e) {
            System.out.println("Error saving to file.");
        }
    }

    public void loadFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            transactions.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String type = parts[0];
                String category = parts[1];
                double amount = Double.parseDouble(parts[2]);
                LocalDate date = LocalDate.parse(parts[3]);
                transactions.add(new Transaction(type, category, amount, date));
            }
            System.out.println("Data loaded from " + filename + "\n");
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }

    public void run() {
        while (true) {
            System.out.println("1. Add Transaction");
            System.out.println("2. View Monthly Summary");
            System.out.println("3. Load from File");
            System.out.println("4. Save to File");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> addTransaction();
                case 2 -> {
                    System.out.print("Enter year: ");
                    int year = scanner.nextInt();
                    System.out.print("Enter month (1-12): ");
                    int month = scanner.nextInt();
                    viewMonthlySummary(year, month);
                }
                case 3 -> {
                    System.out.print("Enter filename to load: ");
                    scanner.nextLine();
                    String loadFile = scanner.nextLine();
                    loadFromFile(loadFile);
                }
                case 4 -> {
                    System.out.print("Enter filename to save: ");
                    scanner.nextLine();
                    String saveFile = scanner.nextLine();
                    saveToFile(saveFile);
                }
                case 5 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice.\n");
            }
        }
    }
}

