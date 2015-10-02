package com.matthiasko.popularmovies2;

/**
 * Created by matthiasko on 9/29/15.
 */
public class YTObject {

    private String ytName;
    private String ytSize;
    private String ytSource;
    private String ytType;

    public YTObject (String ytName, String ytSize, String ytSource, String ytType) {

        this.ytName = ytName;
        this.ytSize = ytSize;
        this.ytSource = ytSource;
        this.ytType = ytType;
    }

    public String getYtName() {
        return ytName;
    }

    public void setYtName(String ytName) {
        this.ytName = ytName;
    }

    public String getYtSize() {
        return ytSize;
    }

    public void setYtSize(String ytSize) {
        this.ytSize = ytSize;
    }

    public String getYtSource() {
        return ytSource;
    }

    public void setYtSource(String ytSource) {
        this.ytSource = ytSource;
    }

    public String getYtType() {
        return ytType;
    }

    public void setYtType(String ytType) {
        this.ytType = ytType;
    }
}
