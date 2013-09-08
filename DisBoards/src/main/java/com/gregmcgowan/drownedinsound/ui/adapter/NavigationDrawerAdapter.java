package com.gregmcgowan.drownedinsound.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.data.model.NavigationDrawerItem;

import java.util.List;

/**
 * Created by gregmcgowan on 08/09/2013.
 */
public class NavigationDrawerAdapter  extends ArrayAdapter<NavigationDrawerItem> {

    private List<NavigationDrawerItem> navigationDrawerItems;

    public NavigationDrawerAdapter (Context context, int textViewResourceId, List<NavigationDrawerItem> navigationDrawerItems){
        super(context,textViewResourceId,navigationDrawerItems);
        this.navigationDrawerItems = navigationDrawerItems;
    }


    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NavigationDrawerItem item = getItem(position);
        View navigationDrawerItem = convertView;
        if(item != null) {
            if (navigationDrawerItem == null) {
                LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                navigationDrawerItem = layoutInflater.inflate(R.layout.navigation_drawer_list_item,null);
            }
            TextView displayNameTextView = (TextView)navigationDrawerItem.findViewById(R.id.navigation_drawer_item_display_text);
            if(displayNameTextView != null) {
                displayNameTextView.setText(item.getDisplayText());
            }


        }
        return navigationDrawerItem;
    }
}
