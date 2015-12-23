package org.reveno.article.model;

public class Order {

    public final long id;
    public final long accountId;
    public final String symbol;
    public final int size;
    public final long price;

    public Order(long id, long accountId, String symbol, int size, long price) {
        this.id = id;
        this.accountId = accountId;
        this.symbol = symbol;
        this.size = size;
        this.price = price;
    }

    public Order adjust(int size, long price) {
        return new Order(id, accountId, symbol, size, price);
    }

}
