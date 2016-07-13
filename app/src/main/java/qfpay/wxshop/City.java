package qfpay.wxshop;

public class City{
    private String name;
    private String[] yidong;
    private String[] liantong;
    private String[] dianxin;

    public String[] getYidong() {
        return yidong;
    }

    public void setYidong(String[] yidong) {
        this.yidong = yidong;
    }

    public String[] getLiantong() {
        return liantong;
    }

    public void setLiantong(String[] liantong) {
        this.liantong = liantong;
    }

    public String[] getDianxin() {
        return dianxin;
    }

    public void setDianxin(String[] dianxin) {
        this.dianxin = dianxin;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}