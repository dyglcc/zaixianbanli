package com.adhoc.beans;

/**
 * Created by dongyuangui on 15/8/11.
 */
public class StatsBean {
    private String statsKey;

    public boolean isDialog() {
        return dialog;
    }

    public void setDialog(boolean dialog) {
        this.dialog = dialog;
    }

    private boolean dialog;

    private PositionBean[] positions;

    private String mapKey;

    public String getStatsKey() {
        return statsKey;
    }

    public void setStatsKey(String statsKey) {
        this.statsKey = statsKey;
    }

    public PositionBean[] getPositions() {
        return positions;
    }

    public void setPositions(PositionBean[] positions) {
        this.positions = positions;
    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey;
    }
}
