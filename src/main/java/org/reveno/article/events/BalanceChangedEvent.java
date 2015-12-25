package org.reveno.article.events;

public class BalanceChangedEvent {

    public final long accountId;

    public BalanceChangedEvent(long accountId) {
        this.accountId = accountId;
    }

}
