package com.adhoc.beans;

/**
 * Created by dongyuangui on 15/8/11.
 */
public class PositionBean {

    private int pos;
    private int curitem = -1;
    private int row;
    private int offset;
    private String activity;
    private String view;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCuritem() {
        return curitem;
    }

    public void setCuritem(int curitem) {
        this.curitem = curitem;
    }
}