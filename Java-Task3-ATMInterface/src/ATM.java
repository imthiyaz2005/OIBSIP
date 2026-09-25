import java.util.ArrayList;
import java.util.Scanner;

public class ATM {

    private Account currentAccount;
    private Bank bank;
    private ArrayList<Transaction> transactions;
    private Scanner scanner;

    public ATM(Bank bank) {
        this.bank = bank;
        this.transactions = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }

    public void start() {

        System.out.println("===== ATM INTERFACE =====");

        int attempts = 0;

        while (attempts < 3) {

            System.out.print("Enter User ID: ");
            String userId = scanner.nextLine();

            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine();

            currentAccount = bank.authenticate(userId, pin);

            if (currentAccount != null) {
                System.out.println("\nLogin successful!");
                showMenu();
                return;
            }

            attempts++;

            System.out.println("Invalid User ID or PIN.");

            if (attempts < 3) {
                System.out.println("Attempts remaining: " + (3 - attempts));
            }
        }

        System.out.println("Access denied. Maximum login attempts reached.");
    }

    private void showMenu() {

        while (true) {

            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    showTransactionHistory();
                    break;

                case "2":
                    withdraw();
                    break;

                case "3":
                    deposit();
                    break;

                case "4":
                    transfer();
                    break;

                case "5":
                    System.out.println("Thank you for using the ATM. Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void showTransactionHistory() {

        System.out.println("\n===== TRANSACTION HISTORY =====");

        if (transactions.isEmpty()) {
            System.out.println("No transactions available.");
            return;
        }

        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    private void withdraw() {

        System.out.print("Enter withdrawal amount: ");
        double amount = readAmount();

        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }

        if (amount > currentAccount.getBalance()) {
            System.out.println("Insufficient Funds");
            return;
        }

        currentAccount.withdraw(amount);

        transactions.add(
                new Transaction(
                        "Withdrawal",
                        amount,
                        "Cash withdrawn"
                )
        );

        System.out.println("Withdrawal successful.");
        System.out.println("Current Balance: " + currentAccount.getBalance());
    }

    private void deposit() {

        System.out.print("Enter deposit amount: ");
        double amount = readAmount();

        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }

        currentAccount.deposit(amount);

        transactions.add(
                new Transaction(
                        "Deposit",
                        amount,
                        "Cash deposited"
                )
        );

        System.out.println("Deposit successful.");
        System.out.println("Current Balance: " + currentAccount.getBalance());
    }

    private void transfer() {

        System.out.print("Enter recipient account ID: ");
        String recipientId = scanner.nextLine();

        Account recipient = bank.getAccount(recipientId);

        if (recipient == null) {
            System.out.println("Recipient account not found.");
            return;
        }

        if (recipient == currentAccount) {
            System.out.println("Cannot transfer to the same account.");
            return;
        }

        System.out.print("Enter transfer amount: ");
        double amount = readAmount();

        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }

        if (amount > currentAccount.getBalance()) {
            System.out.println("Insufficient Funds");
            return;
        }

        currentAccount.withdraw(amount);
        recipient.deposit(amount);

        transactions.add(
                new Transaction(
                        "Transfer",
                        amount,
                        "Transferred to " + recipientId
                )
        );

        System.out.println("Transfer successful.");
        System.out.println("Current Balance: " + currentAccount.getBalance());
    }

    private double readAmount() {

        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return -1;
        }
    }
}