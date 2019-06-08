package edu.skku.team10;

import android.graphics.drawable.Drawable;

public class FurnListItem {
    private Drawable iconDrawable ;
    private String name ;
    private String description ;
    private int ID;

    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setName(String name) {
        this.name = name ;
    }
    public void setDescription(String description) {
        this.description = description ;
    }
    public void setID(int ID) {
        this.ID = ID;
    }

    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getName() {
        return this.name ;
    }
    public String getDescription() {
        return this.description ;
    }
    public int getID() {
        return ID;
    }

}
