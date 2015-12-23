package org.reveno.article.view;

public class OrderView {

    public final double price;
    public final int size;
    public final String symbol;
    public final TradeAccountView account;

    public OrderView(double price, int size, String symbol, TradeAccountView account) {
        this.price = price;
        this.size = size;
        this.account = account;
        this.symbol = symbol;
    }

}
