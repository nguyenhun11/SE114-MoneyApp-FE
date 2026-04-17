package com.example.moneyapp.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

@Entity(
        tableName = "transactions",
        foreignKeys = {
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "sourceAccountId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "destAccountId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Category.class,
                        parentColumns = "id",
                        childColumns = "categoryId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("sourceAccountId"),
                @Index("destAccountId"),
                @Index("categoryId")
        }
)
public class Transaction {
    @PrimaryKey
    @NonNull
    private String id;
    private int transactionType; //0: Transfer, 1: Expense, 2: Income
    private double amount;
    private String sourceAccountId;
    private String destAccountId;
    private String categoryId;
    private String note;
    private Date date;
    private Boolean isSynced;
    private Boolean isDeleted;
    private Date createdAt;
    private Date updatedAt;

    public Transaction(
            @NonNull String id,
            int transactionType,
            double amount,
            String sourceAccountId,
            String destAccountId,
            String categoryId,
            String note,
            Date date,
            Boolean isSynced,
            Boolean isDeleted,
            Date createdAt,
            Date updatedAt
    ) {
        this.id = id;
        this.transactionType = transactionType;
        this.amount = amount;
        this.sourceAccountId = sourceAccountId;
        this.destAccountId = destAccountId;
        this.categoryId = categoryId;
        this.note = note;
        this.date = date;
        this.isSynced = isSynced;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Ignore
    public Transaction(
            int transactionType,
            double amount,
            String sourceAccountId,
            String destAccountId,
            String categoryId,
            String note,
            Date date
    ) {
        this.id = UUID.randomUUID().toString();
        this.transactionType = transactionType;
        this.amount = amount;
        this.sourceAccountId = sourceAccountId;
        this.destAccountId = destAccountId;
        this.categoryId = categoryId;
        this.note = note;
        this.date = date;
        this.isSynced = false;
        this.isDeleted = false;
        Date currentTime = new Date();
        this.createdAt = currentTime;
        this.updatedAt = currentTime;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(String sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public String getDestAccountId() {
        return destAccountId;
    }

    public void setDestAccountId(String destAccountId) {
        this.destAccountId = destAccountId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getSynced() {
        return isSynced;
    }

    public void setSynced(Boolean synced) {
        isSynced = synced;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
