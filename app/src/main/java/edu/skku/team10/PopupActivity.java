package edu.skku.team10;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class PopupActivity extends AppCompatActivity {

    int idx;
    ArrayList<FurnitureInfo> furnitureInfos;
    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        getSupportActionBar().hide();

        idx = getIntent().getIntExtra("ObjectOnGroundID", -1);
        furnitureInfos = (ArrayList<FurnitureInfo>) getIntent().getSerializableExtra("furn_info");
        userInfo = (UserInfo) getIntent().getSerializableExtra("user_info");

        ListView listView = findViewById(R.id.hasFurnList);
        ListViewAdapter adapter = new ListViewAdapter();
        if(listView == null) Log.d("listview", "null");
        listView.setAdapter(adapter);

        for(FurnitureInfo f : furnitureInfos) {
            if(!userInfo.hasFurniture.get(f.furnitureID)) continue;
            adapter.addItem(resize(getDrawable(f.furnitureImgID)), f.furnitureName, f.Description, f.furnitureID);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                FurnListItem item = (FurnListItem) parent.getItemAtPosition(position);

                Integer furnID = item.getID();

                Intent intent = new Intent();
                intent.putExtra("ObjectOnGroundID", idx);
                intent.putExtra("furnID", furnID);
                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 50, 50, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }
}
