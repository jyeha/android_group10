package edu.skku.team10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter {// extends BaseAdapter {
    LayoutInflater inflater;
    private ArrayList<FurnitureInfo> items;

    public Adapter (Context context, ArrayList<FurnitureInfo> memos) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = memos;
    }

    public int getCount() {
        return items.size();
    }

    public FurnitureInfo getItem(int i) {
        return items.get(i);
    }

    public long getItemId(int i) {
        return i;
    }

    /*@Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if ( view == null ) {
            view = inflater.inflate(R.layout.item_layout, viewGroup, false);
        }

        MemoItem item = items.get(i);

        TextView tv1 = (TextView)view.findViewById(R.id.title_tv);
        TextView tv2 = (TextView)view.findViewById(R.id.owner_tv);
        TextView tv3 = (TextView)view.findViewById(R.id.contents_tv);

        tv1.setText(item.getTitle());
        tv2.setText(item.getOwner());
        tv3.setText(item.getContent());

        return view;
    }*/
}
