package parser.common.model;

public class CodeDTO{

    private String code;
    private String sab;

    public CodeDTO() {}

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setSab(String sab) {
        this.sab = sab;
    }

    public String getSab() {
        return sab;
    }

    @Override
    public String toString() {
        return sab + code;
    }

}
