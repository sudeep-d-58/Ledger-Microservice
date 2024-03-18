package com.command.ledger.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "account")
@TableGenerator(name="tab1", initialValue=50000, allocationSize=100)
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tab1")
    private long accountId;

    @Column(nullable = false)
    private String accountName;

    @Column
    private long entityId;

    @Enumerated(EnumType.STRING)
    @Column
    private AccountState accountState;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, targetEntity = Wallet.class, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Wallet> wallets;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "account_create_date", nullable = false, updatable = false)
    private Date creationDate;

    @Version
    @Column(name="version")
    private long version;

    public Account(String accountName, Long entityId, AccountState accountState) {
        this.accountName = accountName;
        this.entityId = entityId;
        this.accountState = accountState;
    }

}
