package parser.common.model;

public class MPLinkDTO{

    private String code;
    private String sab;
    private String title;
    private String link;
    private String updated;
    private String rel;

    public MPLinkDTO() {
        code = "";
        sab = "";
        title = "";
        link = "";
        updated = "";
        rel = "";
    }

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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getRel() {
        return rel;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getUpdated() {
        return updated;
    }

    @Override
    public String toString() {
        return sab + " " + code + " " + title + " " + updated + " " + link;
    }

}
