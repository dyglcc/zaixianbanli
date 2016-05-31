package com.adhoc.adhocsdk;

/**
 * Created by dongyuangui on 16/2/23.
 */
public class RenderExperiUtils {
    private static RenderExperiUtils instance = null;

    public static RenderExperiUtils getInstance() {

        if (instance == null) {
            instance = new RenderExperiUtils();
        }
        return instance;
    }

    private RenderExperiUtils() {
    }


}
