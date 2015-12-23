package org.reveno.article.commands;

public class MakeOrder {

    public final long accountId;
    public final String symbol;
    public final int size;
    public final double price;

    public MakeOrder(long accountId, String symbol, int size, double price) {
        this.accountId = accountId;
        this.symbol = symbol;
        this.size = size;
        this.price = price;
    }

    public static class MakeOrderAction {
        public final long id;
        public final long price;
        public final MakeOrder command;

        public MakeOrderAction(long id, long price, MakeOrder command) {
            this.id = id;
            this.price = price;
            this.command = command;
        }
    }

}
