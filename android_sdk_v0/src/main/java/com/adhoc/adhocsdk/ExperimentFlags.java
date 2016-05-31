package com.adhoc.adhocsdk;

import com.adhoc.utils.T;

import org.json.JSONObject;

/**
 * Access to experiment flags.
 */
public class ExperimentFlags {
    private JSONObject mFlags = new JSONObject();

    /**
     * 返回 Raw jsonObject
     */
    public JSONObject getRawFlags() {
        return mFlags;
    }

    public ExperimentFlags(JSONObject flags) {
        mFlags = flags;
    }

    /**
     * 返回是否有指定的key
     */
    public boolean has(String key) {
        return mFlags.has(key);
    }

    /**
     * 返回boolean开关的值，并设置默认的boolean值
     */
    public boolean getBooleanFlag(String key, boolean defaultValue) {
        try {
            ExperimentUtils.getInstance().reportRenderRequest(mFlags, key);
            JSONObject objFlags = mFlags.optJSONObject("flags");
            if (objFlags != null) {
                return objFlags.optBoolean(key, defaultValue);
            } else {
                return defaultValue;
            }
        } catch (Throwable e) {
            T.e(e);
        }
        return defaultValue;
    }

    /**
     * 返回int类型开关的值，并设置默认值
     */
    public int getIntegerFlag(String key, int defaultValue) {
        try {
            ExperimentUtils.getInstance().reportRenderRequest(mFlags, key);
            JSONObject objFlags = mFlags.optJSONObject("flags");
            if (objFlags != null) {
                return objFlags.optInt(key, defaultValue);
            } else {
                return defaultValue;
            }
        } catch (Throwable e) {
            T.e(e);
        }
        return defaultValue;
    }

    /**
     * 返回double类型开关的值，并设置默认值
     */
    public double getDoubleFlag(String key, double defaultValue) {
        try {
            ExperimentUtils.getInstance().reportRenderRequest(mFlags, key);
            JSONObject objFlags = mFlags.optJSONObject("flags");
            if (objFlags != null) {
                return objFlags.optDouble(key, defaultValue);
            } else {
                return defaultValue;
            }
        } catch (Throwable e) {
            T.e(e);
        }
        return defaultValue;
    }

    /**
     * 返回double类型开关的值，并设置默认值
     */
    public double getFloatFlag(String key, float defaultValue) {
        try {
            ExperimentUtils.getInstance().reportRenderRequest(mFlags, key);
            JSONObject objFlags = mFlags.optJSONObject("flags");
            if (objFlags != null) {
                return objFlags.optDouble(key, defaultValue);
            } else {
                return defaultValue;
            }
        } catch (Throwable e) {
            T.e(e);
        }
        return defaultValue;
    }

    /**
     * 返回long类型开关的值，并设置默认值
     */
    public long getLongFlag(String key, long defaultValue) {
        try {
            ExperimentUtils.getInstance().reportRenderRequest(mFlags, key);

            JSONObject objFlags = mFlags.optJSONObject("flags");
            if (objFlags != null) {
                return objFlags.optLong(key, defaultValue);
            } else {
                return defaultValue;
            }
        } catch (Throwable e) {
            T.e(e);
        }
        return defaultValue;
    }

    /**
     * 返回String类型开关的值，并设置默认值
     */
    public String getStringFlag(String key, String defaultValue) {
        try {
            ExperimentUtils.getInstance().reportRenderRequest(mFlags, key);
            JSONObject objFlags = mFlags.optJSONObject("flags");
            if (objFlags != null) {
                return objFlags.optString(key, defaultValue);
            } else {
                return defaultValue;
            }
        } catch (Throwable e) {
            T.e(e);
        }
        return defaultValue;
    }

    /**
     * 返回ExpermentFlag状态类型
     */
    public String getFlagState() {
        return flagState;
    }

    /**
     * 设置ExpermentFlag状态类型
     */
    public void setFlagState(String flagState) {
        this.flagState = flagState;
    }

    private String flagState;

    enum ExperimentFlagsState {
        EXPERIMENT_OK,
        EXPERIMENT_NULL
    }

    @Override
    public String toString() {
        return mFlags == null ? "" : mFlags.toString();
    }
}
