package com.hsbc.transaction.management.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Table(name = "transaction", indexes = {
        @Index(name = "idx_transaction_no", columnList = "transaction_no", unique = true)
})
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "update transaction set deleted_at = current_timestamp where id = ? and updated_at = ?")
@SQLRestriction("deleted_at is null")
public class Transaction {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "transaction_no", nullable = false, length = 10)
    private String transactionNo;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "description", nullable = false, length = 50)
    private String description;

    @Column(name = "from_account_id", nullable = false)
    private Long fromAccountId;

    @Column(name = "to_account_id", nullable = false)
    private Long toAccountId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version //make high concurrent modification safe, has passed integration test
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
