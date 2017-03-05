package parser.common.model;

public class KeywordDTO{

    private String str;
    private String sab;

    public KeywordDTO() {}

    public void setStr(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }

    public void setSab(String sab) {
        this.sab = sab;
    }

    public String getSab() {
        return sab;
    }

    @Override
    public String toString() {
        return sab + str;
    }

}
