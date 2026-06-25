package com.securebanking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "internal_transaction")
@PrimaryKeyJoinColumn(name = "id")
public class InternalTransaction extends Transaction {

    @Column(name = "receiver_account", nullable = false)
    private String receiverAccount;

    public InternalTransaction() {}

    public String getReceiverAccount() { return receiverAccount; }
    public void setReceiverAccount(String receiverAccount) { this.receiverAccount = receiverAccount; }
}
