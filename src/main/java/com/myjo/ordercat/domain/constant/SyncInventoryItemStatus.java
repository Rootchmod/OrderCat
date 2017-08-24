package com.myjo.ordercat.domain.constant;

public enum SyncInventoryItemStatus {
    ARE_SYNCHRONIZED("ARE_SYNCHRONIZED"),NOT_SYNCHRONIZED("NOT_SYNCHRONIZED");
    private String v;
    SyncInventoryItemStatus(String v){
        this.v = v;
    }
    public String getValue() {
        return v;
    }

}
