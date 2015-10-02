package com.matthiasko.popularmovies2;

/**
 * Created by matthiasko on 10/2/15.
 */
public class ReviewObject {

    private String rId;
    private String rAuthor;
    private String rContent;
    private String rUrl;

    public ReviewObject (String rId, String rAuthor, String rContent, String rUrl) {

        this.rId = rId;
        this.rAuthor = rAuthor;
        this.rContent = rContent;
        this.rUrl = rUrl;
    }

    public String getRId() {
        return rId;
    }

    public void setRId(String rId) {
        this.rId = rId;
    }

    public String getRAuthor() {
        return rAuthor;
    }

    public void setRAuthor(String rAuthor) {
        this.rAuthor = rAuthor;
    }

    public String getRContent() {
        return rContent;
    }

    public void setRContent(String rContent) {
        this.rContent = rContent;
    }

    public String getRUrl() {
        return rUrl;
    }

    public void setRUrl(String rUrl) {
        this.rUrl = rUrl;
    }
}
