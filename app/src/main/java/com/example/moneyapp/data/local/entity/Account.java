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
        tableName = "accounts",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("userId")}
)
public class Account {
    @PrimaryKey
    @NonNull
    private String id;
    private String userId;
    private String name;
    private Double balance;
    private String icon;
    private String color;
    private Boolean isSynced;
    private Boolean isDeleted;
    private Date createdAt;
    private Date updatedAt;

    public Account(@NonNull String id,
                   String userId,
                   String name,
                   Double balance,
                   String icon,
                   String color,
                   boolean isSynced,
                   boolean isDeleted,
                   Date createdAt,
                   Date updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.balance = balance;
        this.icon = icon;
        this.color = color;
        this.isSynced = isSynced;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    @Ignore
    public Account(
            String userId,
            String name,
            Double balance,
            String icon,
            String color) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.name = name;
        this.balance = balance;
        this.icon = icon;
        this.color = color;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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
