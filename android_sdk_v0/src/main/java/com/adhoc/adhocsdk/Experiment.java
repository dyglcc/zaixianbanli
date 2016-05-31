package com.adhoc.adhocsdk;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dongyuangui on 16/2/23.
 */
public class Experiment {
//    public JSONObject getRaw() {
//        if (raw == null) {
//            return new JSONObject();
//        }
//        return raw;
//    }

//    public void setRaw(JSONObject raw) {
//        this.raw = raw;
//    }

    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public HashMap<String, Boolean> getUsedFlags() {
        return usedFlags;
    }

//    public void setUsedFlags(HashMap<String, Boolean> usedFlags) {
//        this.usedFlags = usedFlags;
//    }

    //    private JSONObject raw;
    private HashMap<String, Boolean> usedFlags = new HashMap<String, Boolean>();

    public void setCalled(String key) {

        usedFlags.put(key, true);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean checkAllCalled() {
        boolean allcalled = true;
        for (Map.Entry<String, Boolean> entry : usedFlags.entrySet()) {
            if (!entry.getValue()) {
                allcalled = false;
            }
        }
        return allcalled;
    }

    public void initKeys(String key, boolean value) {

        usedFlags.put(key, value);

    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    private boolean isUpdate;

    public boolean isAllcalled() {
        return isAllcalled;
    }

    public void setIsAllcalled(boolean isAllcalled) {
        this.isAllcalled = isAllcalled;
    }

    private boolean isAllcalled;

    public boolean checkIsCalled(String key) {
        return usedFlags.get(key);
    }

}
