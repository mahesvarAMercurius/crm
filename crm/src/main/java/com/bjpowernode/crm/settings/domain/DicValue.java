package com.bjpowernode.crm.settings.domain;

import java.util.Objects;

public class DicValue {
    private String id;
    private String value;
    private String text;
    private String orderNo;
    private String typeCode;

    public DicValue() {
    }

    public DicValue(String id, String value, String text, String orderNo, String typeCode) {
        this.id = id;
        this.value = value;
        this.text = text;
        this.orderNo = orderNo;
        this.typeCode = typeCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DicValue dicValue = (DicValue) o;
        return Objects.equals(id, dicValue.id) &&
                Objects.equals(value, dicValue.value) &&
                Objects.equals(text, dicValue.text) &&
                Objects.equals(orderNo, dicValue.orderNo) &&
                Objects.equals(typeCode, dicValue.typeCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, text, orderNo, typeCode);
    }

    @Override
    public String toString() {
        return "DicValue{" +
                "id='" + id + '\'' +
                ", value='" + value + '\'' +
                ", text='" + text + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", typeCode='" + typeCode + '\'' +
                '}';
    }
}
