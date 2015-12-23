package org.reveno.article;

import org.reveno.article.commands.*;
import org.reveno.article.model.Order;
import org.reveno.article.model.TradeAccount;
import org.reveno.article.view.OrderView;
import org.reveno.article.view.TradeAccountView;
import org.reveno.atp.api.Reveno;
import org.reveno.atp.core.Engine;

public class Main {
    private static final long DECIMAL_POWER = (long) Math.pow(10, 6);

    public static void main(String[] args) {
        Reveno reveno = new Engine(args[0]);

        // Commands declarations
        declareCommands(reveno);

        // Transaction actions declarations
        declareTransactions(reveno);

        // Views mapping
        reveno.domain().viewMapper(TradeAccount.class, TradeAccountView.class, (id,e,r) ->
                new TradeAccountView(fromLong(e.balance), r.linkSet(e.orders(), OrderView.class)));
        reveno.domain().viewMapper(Order.class, OrderView.class, (id,e,r) ->
                new OrderView(fromLong(e.price), e.size, e.symbol, r.get(TradeAccountView.class, e.accountId)));

        reveno.startup();

        long accountId = reveno.executeSync(new CreateAccount("USD", 5.15));
        long orderId = reveno.executeSync(new MakeOrder(accountId, "EUR/USD", 1, 3.145));

        // circular dynamic view references works this way
        System.out.println(reveno.query().find(TradeAccountView.class, accountId).orders.iterator().next().account.balance);

        reveno.executeSync(new ExecuteOrder(orderId));

        System.out.println(reveno.query().find(TradeAccountView.class, accountId).balance);

        reveno.shutdown();
    }

    protected static void declareCommands(Reveno reveno) {
        reveno.domain().command(CreateAccount.class, long.class, (c, ctx) -> {
            long accountId = ctx.id(TradeAccount.class);
            ctx.executeTransaction(new CreateAccount.CreateAccountAction(c, accountId));
            if (c.initialBalance > 0) {
                ctx.executeTransaction(new ChangeBalance(accountId, toLong(c.initialBalance)));
            }
            return accountId;
        });
        reveno.domain().command(ChangeBalance.class, (c, ctx) -> {
            if (ctx.repo().has(TradeAccount.class, c.accountId)) {
                TradeAccount account = ctx.repo().get(TradeAccount.class, c.accountId);
                if (c.amount < 0 && account.balance < Math.abs(c.amount)) {
                    throw new IllegalArgumentException("Can't withdraw from account - not enough money.");
                }
                ctx.executeTransaction(c);
            } else throw new RuntimeException("No account " + c.accountId + " found!");
        });
        reveno.domain().command(MakeOrder.class, long.class, (c, ctx) -> {
            if (c.size > 0 && !eq(c.price, 0) && ctx.repo().has(TradeAccount.class, c.accountId)) {
                if (c.price < 0 && ctx.repo().get(TradeAccount.class, c.accountId).balance < Math.abs(c.price)) {
                    throw new RuntimeException("Not sufficient finance!");
                }
                long orderId = ctx.id(Order.class);
                ctx.executeTransaction(new MakeOrder.MakeOrderAction(orderId, toLong(c.price), c));
                return orderId;
            } else {
                throw new IllegalArgumentException("One of the order command arguments are not correct.");
            }
        });
        reveno.domain().command(CancellOrder.class, (c, ctx) -> {
            if (ctx.repo().has(Order.class, c.orderId)) {
                ctx.executeTransaction(c);
            } else {
                throw new IllegalArgumentException("Order with id=" + c.orderId + " not found.");
            }
        });
        reveno.domain().command(AdjustOrder.class, (c, ctx) -> {
            if (ctx.repo().has(Order.class, c.orderId)) {
                if (c.newSize <= 0) {
                    ctx.executeTransaction(new CancellOrder(c.orderId));
                } else {
                    ctx.executeTransaction(new AdjustOrder.AdjustOrderAction(c, toLong(c.newPrice)));
                }
            } else {
                throw new IllegalArgumentException("Order with id=" + c.orderId + " not found.");
            }
        });
        // we don't even need here special Transaction Action, since
        // order execution is naturally conjunction of two transaction actions: balance change and order remove
        reveno.domain().command(ExecuteOrder.class, (c, ctx) -> {
            if (ctx.repo().has(Order.class, c.orderId)) {
                Order order = ctx.repo().get(Order.class, c.orderId);
                ctx.executeTransaction(new ChangeBalance(order.accountId, order.price));
                ctx.executeTransaction(new CancellOrder(c.orderId));
            } else {
                throw new IllegalArgumentException("Order with id=" + c.orderId + " not found.");
            }
        });
    }

    protected static void declareTransactions(Reveno reveno) {
        reveno.domain().transactionAction(CreateAccount.CreateAccountAction.class, (a, ctx) ->
                ctx.repo().store(a.id, new TradeAccount(a.id, a.info.currency)));
        reveno.domain().transactionAction(ChangeBalance.class, (a, ctx) ->
                ctx.repo().store(a.accountId,
                        ctx.repo().get(TradeAccount.class, a.accountId).addBalance(a.amount)));
        reveno.domain().transactionAction(MakeOrder.MakeOrderAction.class, (a, ctx) -> {
            Order order = new Order(a.id, a.command.accountId, a.command.symbol, a.command.size, a.price);
            ctx.repo().store(a.id, order);
            ctx.repo().store(a.command.accountId, ctx.repo().get(TradeAccount.class, a.command.accountId).addOrder(a.id));
        });
        reveno.domain().transactionAction(CancellOrder.class, (a, ctx) -> {
            Order order = ctx.repo().remove(Order.class, a.orderId);
            ctx.repo().store(order.accountId, ctx.repo().get(TradeAccount.class, order.accountId).removeOrder(a.orderId));
        });
        reveno.domain().transactionAction(AdjustOrder.AdjustOrderAction.class, (a, ctx) ->
                ctx.repo().store(a.cmd.orderId,
                        ctx.repo().get(Order.class, a.cmd.orderId).adjust(a.cmd.newSize, a.newPrice)));
    }

    protected static long toLong(double value) {
        return (long)(value * DECIMAL_POWER);
    }

    protected static double fromLong(long value) {
        return (double)value / DECIMAL_POWER;
    }

    protected static boolean eq(double a, double b) {
        final double epsilon = 1 / DECIMAL_POWER;
        final double absA = Math.abs(a);
        final double absB = Math.abs(b);
        final double diff = Math.abs(a - b);

        if (a == b) {
            return true;
        } else if (a == 0 || b == 0 || diff < Double.MIN_NORMAL) {
            return diff < (epsilon * Double.MIN_NORMAL);
        } else {
            return diff / (absA + absB) < epsilon;
        }
    }
}
