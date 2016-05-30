package com.adhoc.adhocsdk;

import com.adhoc.beans.ChangeBean;

import java.util.ArrayList;

public class AbsListBean {
    public ArrayList<ChangeBean> getList() {
        return list;
    }

    public void setList(ArrayList<ChangeBean> list) {
        this.list = list;
    }

    public boolean isObsered() {
        return isObsered;
    }

    public void setIsObsered(boolean isObsered) {
        this.isObsered = isObsered;
    }

    private ArrayList<ChangeBean> list;
    private boolean isObsered;
}