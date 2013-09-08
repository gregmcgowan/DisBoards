package com.gregmcgowan.drownedinsound.data.model;

/**
 * Created by gregmcgowan on 08/09/2013.
 */
public class NavigationDrawerItem {


    private String displayText;

    public NavigationDrawerItem(String displayText){
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }
}
