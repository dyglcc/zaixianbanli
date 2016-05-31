package com.adhoc.beans;

/**
 * Created by dongyuangui on 15/8/11.
 */
public class ChangeBean {

    // 可能多个属性
    private PropertyBean[] properties;

    // 一个位置
    private PositionBean[] positions;

    // 是否是listView的item
    private boolean isAbsListView;

    public boolean isDialog() {
        return isDialog;
    }

    public void setIsDialog(boolean isDialog) {
        this.isDialog = isDialog;
    }

    private boolean isDialog;

    //    // listView name
//    private String listViewName;
    // listView name
    private String mapKey;

    public PropertyBean[] getProperties() {
        return properties;
    }

    public void setProperties(PropertyBean[] properties) {
        this.properties = properties;
    }

    public PositionBean[] getPositions() {
        return positions;
    }

    public void setPositions(PositionBean[] positions) {
        this.positions = positions;
    }

    public boolean isAbsListView() {
        return isAbsListView;
    }

    public void setIsAbsListView(boolean isAbsListView) {
        this.isAbsListView = isAbsListView;
    }
//    public String getListViewName() {
//        return listViewName;
//    }
//
//    public void setListViewName(String listViewName) {
//        this.listViewName = listViewName;
//    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey;
    }
}
