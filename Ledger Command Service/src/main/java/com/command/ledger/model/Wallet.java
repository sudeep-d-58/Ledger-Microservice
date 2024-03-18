package com.command.ledger.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "wallet")
@TableGenerator(name = "tab", initialValue = 10000, allocationSize = 90)
@NoArgsConstructor
@Getter
@Setter
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tab")
    private Long walletId;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonBackReference
    private Account account;

    private String assetType;

    private BigDecimal prevBalance;

    private BigDecimal currBalance;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "wallet_create_date", nullable = false, updatable = false)
    private Date creationDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "wallet_modify_date", nullable = false)
    private Date modifyDate;

    @Version
    @Column(name = "version")
    private long version;

    public Wallet(String assetType) {
        this.assetType = assetType;
    }


}
