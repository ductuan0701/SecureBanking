package com.securebanking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "interbank_transaction")
@PrimaryKeyJoinColumn(name = "id")
public class InterbankTransaction extends Transaction {

    @Column(name = "receiver_bank", nullable = false)
    private String receiverBank;

    @Column(name = "swift_code", nullable = false)
    private String swiftCode;

    @Column(name = "digital_signature")
    private String digitalSignature;

    public InterbankTransaction() {}

    public String getReceiverBank() { return receiverBank; }
    public void setReceiverBank(String receiverBank) { this.receiverBank = receiverBank; }

    public String getSwiftCode() { return swiftCode; }
    public void setSwiftCode(String swiftCode) { this.swiftCode = swiftCode; }

    public String getDigitalSignature() { return digitalSignature; }
    public void setDigitalSignature(String digitalSignature) { this.digitalSignature = digitalSignature; }
}
