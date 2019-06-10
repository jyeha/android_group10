package edu.skku.team10;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Store extends AppCompatActivity {

    private DatabaseReference mPostReference;

    ListView listView;
    String username;
    TextView money;
    Integer minus_money;
    UserInfo user;

    ArrayList<FurnitureInfo> memos;
    FurnitureInfo mem;
    Adapter adapter;

    int waitingprocesses = 2;
    public interface Callback{
        void success(String msg);
        void fail();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        Intent intent2 = getIntent();
        username = intent2.getStringExtra("my_name");
        mPostReference = FirebaseDatabase.getInstance().getReference();
        memos = new ArrayList<FurnitureInfo>();
        listView = (ListView)findViewById(R.id.listview);
        money = (TextView)findViewById(R.id.textView7);

        getFirebaseDatabaseInfo(new Callback() {
            @Override
            public void success(String msg) {
                if(msg=="furniture"){
                    for(FurnitureInfo f : memos) {
                        f.furnitureImgID = getResources().getIdentifier(f.ImgFileName, "drawable", getPackageName());
                    }
                }
                else{
                    money.setText("보유한 금액 : " + user.now_money);
                }
                --waitingprocesses;
                Log.d("waiting", String.valueOf(waitingprocesses));
                if(waitingprocesses == 0) updateButtonAvailable();
                else if(waitingprocesses < 0){
                    for(int i=0; i<memos.size(); ++i) {
                        adapter.setButtonActivate(i, !(user.now_money < memos.get(i).furniturePrice || Check(memos.get(i).furnitureID)));
                    }
                }
            }
            @Override
            public void fail() { }
        });

    }

    @Override
    protected void onDestroy() {
        mPostReference = null;
        user = null;
        super.onDestroy();
    }

    private void updateButtonAvailable() {
        adapter = new Adapter();
        listView.setAdapter(adapter);
    }

    public void getFirebaseDatabaseInfo(Callback callback) {
        Log.d("순서", "1");
        mPostReference.child("FurnitureInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FurnitureInfo get = postSnapshot.getValue(FurnitureInfo.class);
                    memos.add(get);
                    Log.d("Name", memos.get(memos.size()-1).furnitureName);
                }
                callback.success("furniture");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.fail();
            }
        });
        final ValueEventListener postListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // 연동돼있는 database에 변화가 생기면 이 함수가 불림
                Log.d("가구 있나", dataSnapshot.getKey().toString());
                UserInfo get = dataSnapshot.getValue(UserInfo.class);
                user = get;
                if(get == null) Log.d("userdata","failed");
                Log.d("groundFurn", get.groundFurn.toString());
                Log.d("hasFurniture", get.hasFurniture.toString());

                callback.success("userinfo");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        Log.d("순서", "3");
        mPostReference.child("UserInfo").child(username).addValueEventListener(postListener2);  // mPost가 가리키고 있던 곳을 child쪽으로 옮김(root에서 id_list로 옮김)
    }

    public class Adapter extends BaseAdapter {

        private ArrayList<FurnitureInfo> items;
        private ArrayList<Button> buttons;

        public Adapter () {
            items = memos;
            buttons = new ArrayList<>();
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
            final Context context = viewGroup.getContext();
            if ( view == null ) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.store_list, viewGroup, false);
            }

            FurnitureInfo item = items.get(i);

            ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
            TextView fur_name = (TextView)view.findViewById(R.id.textView8);
            TextView fur_price = (TextView)view.findViewById(R.id.textView9);

            //imageView.setImageResource(R.drawable.item.ImgFileName);
            imageView.setImageResource(item.furnitureImgID);
            Log.d("이미지", String.valueOf(item.furnitureImgID));
            Log.d("가구값", String.valueOf(item.furniturePrice));
            fur_name.setText(item.furnitureName);
            Log.d("돈", String.valueOf(item.furniturePrice));
            fur_price.setText(String.valueOf("가격 : " + item.furniturePrice));

            Button button = (Button)view.findViewById(R.id.button4);
            buttons.add(button);
            if(user.now_money < item.furniturePrice || Check(item.furnitureID)){
                button.setEnabled(false);
            }
            else
                button.setEnabled(true);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    minus_money = item.furniturePrice;
                    Log.d("버튼", String.valueOf(item.furniturePrice));
                    Log.d("가구번호", String.valueOf(item.furnitureID));

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/UserInfo/"+username+"/hasFurniture/" + item.furnitureID , true);
                    childUpdates.put("/UserInfo/"+username+"/now_money", user.now_money - minus_money);
                    mPostReference.updateChildren(childUpdates);
                }
            });

            return view;
        }

        public void setButtonActivate(int idx, boolean activate){
            buttons.get(idx).setEnabled(activate);
        }
    }
    public boolean Check(int furnitureID){
        return user.hasFurniture.get(furnitureID);
    }
}
