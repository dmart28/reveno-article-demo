package org.reveno.article.model;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

public class TradeAccount {
    public final long id;
    public final long balance;
    public final String currency;
    private final LongSet orders;

    public TradeAccount(long id, String currency) {
        this(id, 0, currency, new LongOpenHashSet());
    }

    private TradeAccount(long id, long balance, String currency, LongSet orders) {
        this.id = id;
        this.balance = balance;
        this.currency = currency;
        this.orders = orders;
    }

    public TradeAccount addBalance(long amount) {
        return new TradeAccount(id, balance + amount, currency, orders);
    }

    public TradeAccount addOrder(long orderId) {
        LongSet orders = new LongOpenHashSet(this.orders);
        orders.add(orderId);
        return new TradeAccount(id, balance, currency, orders);
    }

    public TradeAccount removeOrder(long orderId) {
        LongSet orders = new LongOpenHashSet(this.orders);
        orders.remove(orderId);
        return new TradeAccount(id, balance, currency, orders);
    }

    public LongCollection orders() {
        return orders;
    }

}
