import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class LedgerApp {
    static final String FILE_PATH = "transactions.csv";
    static final String REPORT_FILE_PATH = "report.csv";
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("****************************************");
        System.out.println("Welcome to your Account Ledger Application!");
        System.out.println("****************************************");
        while (true) {
            System.out.println("\n==** Home Screen **==");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment");
            System.out.println("L) View Ledger");
            System.out.println("B) View Balance"); // Added balance option
            System.out.println("X) Exit");
            System.out.println("********************");
            System.out.println("Enter your choice: ");
            System.out.println("********************");
            String choice = scanner.nextLine().toUpperCase();

            switch (choice) {
                case "D": addTransaction(true); break;
                case "P": addTransaction(false); break;
                case "L": showLedger(); break;
                case "B": showBalance(); break; // Added case for balance
                case "X": System.out.println("Goodbye!"); return;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    static void addTransaction(boolean isDeposit) {
        System.out.print("Description: ");
        String description = scanner.nextLine();

        System.out.print("Vendor: ");
        String vendor = scanner.nextLine();

        System.out.print("Amount: ");
        String amountStr = scanner.nextLine().replaceAll("[^0-9.-]", "");

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }

        if (!isDeposit) amount = -amount;

        Transaction transaction = new Transaction(LocalDate.now(), LocalTime.now(), description, vendor, amount);
        appendTransaction(transaction);
        System.out.println("Transaction recorded!");
    }

    static void showLedger() {
        List<Transaction> transactions = readTransactions();
        transactions.sort(Comparator.comparing(Transaction::getDate).reversed());

        while (true) {
            System.out.println("\n== Ledger Menu ==");
            System.out.println("A) All Transactions");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.print("Select option: ");
            String choice = scanner.nextLine().toUpperCase();

            switch (choice) {
                case "A": printTransactions(transactions); break;
                case "D": printTransactions(filterDeposits(transactions)); break;
                case "P": printTransactions(filterPayments(transactions)); break;
                case "R": showReports(transactions); break;
                case "H": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    static void showReports(List<Transaction> transactions) {
        while (true) {
            System.out.println("\n== Reports ==");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    filterByDate(transactions, LocalDate.now().withDayOfMonth(1), LocalDate.now());
                    break;
                case "2":
                    LocalDate lastMonthStart = LocalDate.now().minusMonths(1).withDayOfMonth(1);
                    LocalDate lastMonthEnd = lastMonthStart.withDayOfMonth(lastMonthStart.lengthOfMonth());
                    filterByDate(transactions, lastMonthStart, lastMonthEnd);
                    break;
                case "3":
                    filterByDate(transactions, LocalDate.now().withDayOfYear(1), LocalDate.now());
                    break;
                case "4":
                    LocalDate prevYearStart = LocalDate.now().minusYears(1).withDayOfYear(1);
                    LocalDate prevYearEnd = prevYearStart.withDayOfYear(prevYearStart.lengthOfYear());
                    filterByDate(transactions, prevYearStart, prevYearEnd);
                    break;
                case "5":
                    System.out.print("Enter vendor name: ");
                    String vendor = scanner.nextLine().toLowerCase();
                    List<Transaction> vendorMatches = transactions.stream()
                            .filter(t -> t.getVendor().toLowerCase().contains(vendor))
                            .collect(Collectors.toList());
                    printTransactions(vendorMatches);
                    break;
                case "6":
                    customSearch(transactions);
                    break;
                case "0": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    static void customSearch(List<Transaction> transactions) {
        System.out.println("Leave blank to skip a field.");

        System.out.print("Start Date (yyyy-mm-dd): ");
        String startStr = scanner.nextLine();

        System.out.print("End Date (yyyy-mm-dd): ");
        String endStr = scanner.nextLine();

        System.out.print("Description: ");
        String description = scanner.nextLine().toLowerCase();

        System.out.print("Vendor: ");
        String vendor = scanner.nextLine().toLowerCase();

        System.out.print("Amount: ");
        String amountStr = scanner.nextLine();

        List<Transaction> filtered = transactions.stream().filter(t -> {
            boolean match = true;

            if (!startStr.isEmpty()) match &= !t.getDate().isBefore(LocalDate.parse(startStr));
            if (!endStr.isEmpty()) match &= !t.getDate().isAfter(LocalDate.parse(endStr));
            if (!description.isEmpty()) match &= t.getDescription().toLowerCase().contains(description);
            if (!vendor.isEmpty()) match &= t.getVendor().toLowerCase().contains(vendor);
            if (!amountStr.isEmpty()) {
                try {
                    double amt = Double.parseDouble(amountStr);
                    match &= t.getAmount() == amt;
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            return match;
        }).collect(Collectors.toList());

        printTransactions(filtered);
        exportToReport(filtered);

        //delete this
    }

    static void exportToReport(List<Transaction> transactions) {
        try (PrintWriter writer = new PrintWriter(REPORT_FILE_PATH)) {
            for (Transaction t : transactions) {
                writer.println(t.toCSV());
            }
            System.out.println("Report exported to " + REPORT_FILE_PATH);
        } catch (IOException e) {
            System.out.println("Failed to export report.");
        }
    }

    static void filterByDate(List<Transaction> transactions, LocalDate start, LocalDate end) {
        List<Transaction> filtered = transactions.stream()
                .filter(t -> !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                .collect(Collectors.toList());
        printTransactions(filtered);
        exportToReport(filtered);
    }

    static void printTransactions(List<Transaction> list) {
        if (list.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (Transaction t : list) {
                System.out.println(t);
            }
        }
    }

    static List<Transaction> filterDeposits(List<Transaction> list) {
        return list.stream().filter(t -> t.getAmount() > 0).collect(Collectors.toList());
    }

    static List<Transaction> filterPayments(List<Transaction> list) {
        return list.stream().filter(t -> t.getAmount() < 0).collect(Collectors.toList());
    }

    static List<Transaction> readTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    LocalDate date = LocalDate.parse(parts[0]);
                    LocalTime time = LocalTime.parse(parts[1]);
                    String desc = parts[2];
                    String vendor = parts[3];
                    double amount = Double.parseDouble(parts[4]);
                    transactions.add(new Transaction(date, time, desc, vendor, amount));
                }
            }
        } catch (IOException e) {
            System.out.println("No existing file, starting fresh.");
        }
        return transactions;
    }

    static void appendTransaction(Transaction t) {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            out.println(t.toCSV());
        } catch (IOException e) {
            System.out.println("Error saving transaction.");
        }
    }

    // === New Methods Below ===

    static void showBalance() {
        double balance = calculateBalance();
        System.out.println("===================================");
        System.out.printf("Current Account Balance: $%.2f%n", balance);
        System.out.println("===================================");
    }

    static double calculateBalance() {
        return readTransactions().stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}
