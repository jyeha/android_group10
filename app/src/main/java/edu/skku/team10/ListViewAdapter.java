package edu.skku.team10;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<FurnListItem> listViewItemList = new ArrayList<>() ;
    public ListViewAdapter(){

    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.has_furn_list_item, parent, false);
        }

        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.furnImg) ;
        TextView nameTextView = (TextView) convertView.findViewById(R.id.furnName) ;
        TextView descTextView = (TextView) convertView.findViewById(R.id.furnDesc) ;

        FurnListItem listViewItem = listViewItemList.get(position);

        iconImageView.setImageDrawable(listViewItem.getIcon());
        nameTextView.setText(listViewItem.getName());
        descTextView.setText(listViewItem.getDescription());

        return convertView;
    }

    public void addItem(Drawable icon, String name, String desc, Integer id) {
        FurnListItem item = new FurnListItem();

        item.setIcon(icon);
        item.setName(name);
        item.setDescription(desc);
        item.setID(id);

        listViewItemList.add(item);
    }
}