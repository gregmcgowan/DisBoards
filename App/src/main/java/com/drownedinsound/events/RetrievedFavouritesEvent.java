package com.drownedinsound.events;

import com.drownedinsound.data.model.BoardPost;

import java.util.ArrayList;

/**
 * Created by gregmcgowan on 29/10/2013.
 */
public class RetrievedFavouritesEvent {

    private ArrayList<BoardPost> favourites;

    public RetrievedFavouritesEvent(ArrayList<BoardPost> favourites) {
        this.favourites = favourites;
    }

    public ArrayList<BoardPost> getFavourites() {
        return favourites;
    }
}
