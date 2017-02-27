package parser.common.model;

public class DrugLink {

    private String rxcui;
    private String title;
    private String rel;
    private String link;
    private String updated;

    public DrugLink() {

    }

    public void setRxcui(String rxcui) {
        this.rxcui = rxcui;
    }

    public String getRxcui() {
        return rxcui;
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
        return rxcui + link;
    }
}
