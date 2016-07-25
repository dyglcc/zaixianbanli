package jiafen.jinniu.com;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "GIRL".
 */
@Entity
public class Girl {

    @NotNull
    private String iconUrl;

    @NotNull
    private String mediumUrl;

    @NotNull
    private String name;
    private int height;
    private int width;

    @Generated
    public Girl() {
    }

    @Generated
    public Girl(String iconUrl, String mediumUrl, String name, int height, int width) {
        this.iconUrl = iconUrl;
        this.mediumUrl = mediumUrl;
        this.name = name;
        this.height = height;
        this.width = width;
    }

    @NotNull
    public String getIconUrl() {
        return iconUrl;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setIconUrl(@NotNull String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @NotNull
    public String getMediumUrl() {
        return mediumUrl;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setMediumUrl(@NotNull String mediumUrl) {
        this.mediumUrl = mediumUrl;
    }

    @NotNull
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(@NotNull String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

}