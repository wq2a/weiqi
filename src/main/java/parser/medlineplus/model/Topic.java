package parser.medlineplus.model;

//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//@JsonIgnoreProperties(ignoreUnknown=true)
public class Topic {

    private String title;
    private String organizationName;
    private String altTitle;
    private String fullSummary;
    private String mesh;
    private String groupName;
    private String snippet;
    // getters

    public String getSnippet()
    {
        return snippet;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public String getMesh()
    {
        return mesh;
    }

    public String getFullSummary()
    {
        return fullSummary;
    }

    public String getAltTitle()
    {
        return altTitle;
    }

    public String getOrganizationName()
    {
        return organizationName;
    }

    public String getTitle()
    {
        return title;
    }
    // setters

    public void setSnippet(String snippet)
    {
        this.snippet = snippet;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public void setMesh(String mesh)
    {
        this.mesh = mesh;
    }

    public void setFullSummary(String fullSummary)
    {
        this.fullSummary = fullSummary;
    }

    public void setAltTitle(String altTitle)
    {
        this.altTitle = altTitle;
    }

    public void setOrganizationName(String organizationName)
    {
        this.organizationName = organizationName;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }


}
