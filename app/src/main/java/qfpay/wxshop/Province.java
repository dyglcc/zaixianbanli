package qfpay.wxshop;

public class Province{
    private String name;

    public String getName() {
        return name;
    }

    private City[] cities;

    public void setName(String name) {
        this.name = name;
    }

    public City[] getCities() {
        return cities;
    }

    public void setCities(City[] cities) {
        this.cities = cities;
    }

}