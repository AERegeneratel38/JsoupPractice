package com.example.jsouppractice;

public class ItemModel {

    public String itemtitle;
    public String overviewimg;
    public String description;

    public ItemModel() {
    }

    public ItemModel(String itemtitle, String overviewimg, String description) {
        this.itemtitle = itemtitle;
        this.overviewimg = overviewimg;
        this.description = description;
    }

    public String getItemtitle() {
        return itemtitle;
    }

    public void setItemtitle(String itemtitle) {
        this.itemtitle = itemtitle;
    }

    public String getOverviewimg() {
        return overviewimg;
    }

    public void setOverviewimg(String overviewimg) {
        this.overviewimg = overviewimg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
