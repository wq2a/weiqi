package parser.common.model;

public class WikiLinkDTO{

    private String str;
    private String sab;
    private String pageid;
    private String title;
    // full url
    private String link;
    private String updated;

    public WikiLinkDTO() {
        str = "";
        sab = "";
        title = "";
        link = "";
        updated = "";
    }

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

    public void setPageid(String pageid) {
        this.pageid = pageid;
    }

    public String getPageid() {
        return pageid;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
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
        return sab + " " + str + " " + title + " " + updated + " " + link;
    }

}
