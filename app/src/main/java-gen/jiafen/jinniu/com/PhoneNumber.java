package jiafen.jinniu.com;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "PHONE_NUMBER".
 */
@Entity
public class PhoneNumber {

    @Id
    private Long id;
    private long his_id;
    private int phonenum;

    @Generated
    public PhoneNumber() {
    }

    public PhoneNumber(Long id) {
        this.id = id;
    }

    @Generated
    public PhoneNumber(Long id, long his_id, int phonenum) {
        this.id = id;
        this.his_id = his_id;
        this.phonenum = phonenum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getHis_id() {
        return his_id;
    }

    public void setHis_id(long his_id) {
        this.his_id = his_id;
    }

    public int getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(int phonenum) {
        this.phonenum = phonenum;
    }

}
