package com.example.hookgethtmlinfo;

public class BookInfo {
    private String title;
    private String url;
    private float star;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    @Override
    public String toString() {
        return "BookInfo{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", star=" + star +
                '}';
    }
}
