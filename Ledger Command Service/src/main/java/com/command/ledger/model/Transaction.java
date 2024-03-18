package com.command.ledger.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "transaction")
@TableGenerator(name="tab2", initialValue=100000, allocationSize=110)
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tab2")
    private long transactionId;

    @JoinColumn(name = "from_wallet_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private Wallet fromWallet;

    @JoinColumn(name = "to_wallet_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private Wallet toWallet;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionState transactionState;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transaction_create_date", nullable = false, updatable = false)
    private Date creationDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transaction_modify_date", nullable = false)
    private Date modifyDate;

    @Version
    @Column(name="version")
    private long version;


    public Transaction(Wallet fromWallet, Wallet toWallet, BigDecimal amount, TransactionState transactionState) {
        this.fromWallet = fromWallet;
        this.toWallet = toWallet;
        this.amount = amount;
        this.transactionState = transactionState;
    }
}
