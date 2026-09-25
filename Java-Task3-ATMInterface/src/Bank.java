import java.util.HashMap;
import java.util.Map;

public class Bank {

    private Map<String, Account> accounts;

    public Bank() {
        accounts = new HashMap<>();

        accounts.put(
                "ACC1001",
                new Account("ACC1001", "admin", "1234", 10000.00)
        );

        accounts.put(
                "ACC1002",
                new Account("ACC1002", "user2", "5678", 5000.00)
        );
    }

    public Account authenticate(String userId, String pin) {

        for (Account account : accounts.values()) {

            if (account.getUserId().equals(userId)
                    && account.getPin().equals(pin)) {

                return account;
            }
        }

        return null;
    }

    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }
}