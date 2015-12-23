package org.reveno.article.view;

import java.util.Set;

public class TradeAccountView {
    public final double balance;
    public final Set<OrderView> orders;

    public TradeAccountView(double balance, Set<OrderView> orders) {
        this.balance = balance;
        this.orders = orders;
    }
}
