//package com.example.moneyapp.data.local.entity;
//
//import androidx.annotation.NonNull;
//import androidx.room.Entity;
//import androidx.room.Ignore;
//import androidx.room.PrimaryKey;
//
//import java.util.Date;
//import java.util.UUID;
//
//@Entity(
//        tableName = "accounts"
//)
//public class Account {
//    @PrimaryKey
//    @NonNull
//    private String id;
//    private String name;
//    private Double balance;
//    private Integer icon;
//    private Integer color;
//    private String description;
//    private Boolean includeInTotal;
//    private Integer order;
//    private Boolean isSynced;
//    private Boolean isDeleted;
//    private Date createdAt;
//    private Date updatedAt;
//
//    public Account(@NonNull String id,
//                   String name,
//                   Double balance,
//                   Integer icon,
//                   Integer color,
//                   String description, Boolean includeInTotal, Integer order,
//                   boolean isSynced,
//                   boolean isDeleted,
//                   Date createdAt,
//                   Date updatedAt) {
//        this.id = id;
//        this.description = description;
//        this.name = name;
//        this.balance = balance;
//        this.icon = icon;
//        this.color = color;
//        this.includeInTotal = includeInTotal;
//        this.order = order;
//        this.isSynced = isSynced;
//        this.isDeleted = isDeleted;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//    }
//    @Ignore
//    public Account(
//            String name,
//            Double balance,
//            int icon,
//            int color,
//            String description,
//            Boolean includeInTotal,
//            Integer order) {
//        this.description = description;
//        this.includeInTotal = includeInTotal;
//        this.order = order;
//        this.id = UUID.randomUUID().toString();
//        this.name = name;
//        this.balance = balance;
//        this.icon = icon;
//        this.color = color;
//        this.isSynced = false;
//        this.isDeleted = false;
//        Date currentTime = new Date();
//        this.createdAt = currentTime;
//        this.updatedAt = currentTime;
//    }
//
//
//    @NonNull
//    public String getId() {
//        return id;
//    }
//
//    public void setId(@NonNull String id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public Double getBalance() {
//        return balance;
//    }
//    public void setBalance(Double balance) {
//        this.balance = balance;
//    }
//
//    public int getIcon() {
//        return icon;
//    }
//
//    public void setIcon(int icon) {
//        this.icon = icon;
//    }
//
//    public int getColor() {
//        return color;
//    }
//
//    public void setColor(int color) {
//        this.color = color;
//    }
//
//    public Boolean getSynced() {
//        return isSynced;
//    }
//
//    public void setSynced(Boolean synced) {
//        isSynced = synced;
//    }
//
//    public Boolean getDeleted() {
//        return isDeleted;
//    }
//
//    public void setDeleted(Boolean deleted) {
//        isDeleted = deleted;
//    }
//
//    public Date getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(Date createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public Date getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(Date updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String descripton) {
//        this.description = descripton;
//    }
//
//    public Boolean getIncludeInTotal() {
//        return includeInTotal;
//    }
//
//    public void setIncludeInTotal(Boolean includeInTotal) {
//        this.includeInTotal = includeInTotal;
//    }
//
//    public Integer getOrder() {
//        return order;
//    }
//
//    public void setOrder(Integer order) {
//        this.order = order;
//    }
//}
