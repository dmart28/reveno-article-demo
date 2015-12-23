package org.reveno.article.commands;

public class AdjustOrder {

    public final long orderId;
    public final int newSize;
    public final double newPrice;

    public AdjustOrder(long orderId, int newSize, double newPrice) {
        this.orderId = orderId;
        this.newSize = newSize;
        this.newPrice = newPrice;
    }

    public static class AdjustOrderAction {
        public final AdjustOrder cmd;
        public final long newPrice;

        public AdjustOrderAction(AdjustOrder cmd, long newPrice) {
            this.cmd = cmd;
            this.newPrice = newPrice;
        }
    }

}
