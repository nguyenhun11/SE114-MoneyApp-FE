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
        tableName = "categories",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("userId")}
)
public class Category {
    @PrimaryKey
    @NonNull
    private String id;
    private String userId;
    private String name;
    private Double monthlyTarget;
    private String icon;
    private String color;
    private int type; //0: transfer, 1: income, 2: expense
    private Boolean canDelete;
    private Boolean isSynced;
    private Boolean isDeleted;
    private Date createdAt;
    private Date updatedAt;

    public Category(
            @NonNull String id,
            String userId,
            String name,
            Double monthlyTarget,
            String icon,
            String color,
            int type,
            Boolean canDelete,
            Boolean isSynced,
            Boolean isDeleted,
            Date createdAt,
            Date updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.monthlyTarget = monthlyTarget;
        this.icon = icon;
        this.color = color;
        this.type = type;
        this.canDelete = canDelete;
        this.isSynced = isSynced;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Ignore
    public Category(
            String userId,
            String name,
            Double monthlyTarget,
            String icon,
            String color,
            int type,
            Boolean canDelete
    ) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.name = name;
        this.monthlyTarget = monthlyTarget;
        this.icon = icon;
        this.color = color;
        this.type = type;
        this.canDelete = canDelete;
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

    public Double getMonthlyTarget() {
        return monthlyTarget;
    }

    public void setMonthlyTarget(Double monthlyTarget) {
        this.monthlyTarget = monthlyTarget;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
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
