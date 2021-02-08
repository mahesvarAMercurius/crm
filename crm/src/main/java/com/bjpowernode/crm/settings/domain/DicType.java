package com.bjpowernode.crm.settings.domain;

import java.util.Objects;

public class DicType {
    private String code;
    private String name;
    private String description;

    public DicType() {
    }

    public DicType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DicType dicType = (DicType) o;
        return Objects.equals(code, dicType.code) &&
                Objects.equals(name, dicType.name) &&
                Objects.equals(description, dicType.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, description);
    }

    @Override
    public String toString() {
        return "DicType{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
