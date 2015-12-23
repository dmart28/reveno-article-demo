package org.reveno.article.commands;

public class CreateAccount {

    public final String currency;
    public final double initialBalance;

    public CreateAccount(String currency, double initialBalance) {
        this.currency = currency;
        this.initialBalance = initialBalance;
    }

    public static class CreateAccountAction {
        public final CreateAccount info;
        public final long id;

        public CreateAccountAction(CreateAccount info, long id) {
            this.info = info;
            this.id = id;
        }
    }

}
