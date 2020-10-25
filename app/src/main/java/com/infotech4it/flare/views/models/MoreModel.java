package com.infotech4it.flare.views.models;

/**
 * Created by Bilal Zaman on 17/10/2020.
 */
public class MoreModel {
    private String item;
    private int icon;

    public MoreModel(String item, int icon) {
        this.item = item;
        this.icon = icon;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
