/*package edu.skku.team10;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter extends BaseAdapter {
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if ( view == null ) {
            view = inflater.inflate(R.layout.store_list, viewGroup, false);
        }

        FurnitureInfo item = items.get(i);

        ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
        TextView fur_name = (TextView)view.findViewById(R.id.textView8);
        TextView fur_price = (TextView)view.findViewById(R.id.textView9);

        //imageView.setImageResource(R.drawable.item.ImgFileName);
        //imageView.setImageResource(item.ImgFileName);
        Log.d("이미지", String.valueOf(item.furnitureImgID));
        Log.d("가구이름", String.valueOf(item.furniturePrice));
        fur_name.setText(item.furnitureName);
        Log.d("돈", String.valueOf(item.furniturePrice));
        fur_price.setText(String.valueOf(item.furniturePrice));


        return view;
    }
}
*/