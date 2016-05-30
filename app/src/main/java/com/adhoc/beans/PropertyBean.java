package com.adhoc.beans;

/**
 * Created by dongyuangui on 15/8/12.
 */
public class PropertyBean {
    private String property;
    private String type;
    private String value;
    private String old_property;
    private String old_value;

    public boolean isChanged() {
        return changed;
    }

    private boolean changed = false;

    public String getOld_property() {
        return old_property;
    }

    public void setOld_property(String old_property) {
        this.old_property = old_property;
    }

    public String getOld_value() {
        return old_value;
    }

    public void setOld_value(String old_value) {
        this.old_value = old_value;
    }


    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        changed = true;
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
