package org.reveno.article.commands;

public class ChangeBalance {

    public final long accountId;
    public final long amount;

    public ChangeBalance(long accountId, long amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

}
