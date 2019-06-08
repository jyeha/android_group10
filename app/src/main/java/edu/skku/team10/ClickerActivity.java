package edu.skku.team10;

import android.content.Intent;
import android.os.AsyncTask;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickerActivity extends AppCompatActivity {
    private DatabaseReference mPostReference;
    public UserInfo user;
    public String userName;
    public int check_long=0;
    Intent ToShop;
    TextView info;
    Button promote, shop;
    GestureDetector detector;

    public interface Callback{
        void success(String msg);
        void fail();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicker);

        info = (TextView)findViewById(R.id.info);
        promote = (Button)findViewById(R.id.upgrade);
        shop = (Button)findViewById(R.id.GotoShop);

        ToShop = new Intent(ClickerActivity.this, Store.class);

        mPostReference = FirebaseDatabase.getInstance().getReference();
        //userName = "TestAccount";
        userName = getIntent().getStringExtra("my_name");
        getFirebaseDatabaseInfo(new Callback() {
            @Override
            public void success(String msg) {
                info.setText("직급 : "+ user.rank_name+"\n"+
                "현재 소지 : " + user.now_money+ "\n" +
                "다음 진급까지 남은 돈 : "+user.now_need_money);
                AfterDataLoad();
            }

            @Override
            public void fail() {

            }
        });

        shop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ToShop.putExtra("my_name", userName);
                startActivity(ToShop);
            }
        });
    }

    private void AfterDataLoad() {
        // earn money per time
        MyAsyncTask EarnMoneyPerTime = new MyAsyncTask();
        EarnMoneyPerTime.execute();

        // Earn money for each touch
        detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                check_long = 0;
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (check_long == 0){
                    user.now_money+=user.touch;
                    user.now_need_money=user.need_money-user.now_money;
                    if(user.now_need_money<0){
                        user.now_need_money=0;
                    }
                    info.setText("직급 : "+ user.rank_name+"\n"+
                            "현재 소지 : " + user.now_money+ "\n" +
                            "다음 진급까지 남은 돈 : "+user.now_need_money);
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                check_long = 1;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });

        View touch_area = findViewById(R.id.view);
        touch_area.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){
                detector.onTouchEvent(motionEvent);
                return true;
            }
        });

        // Promotion with clicking button
        promote.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(user.can_promote && user.now_money>user.need_money){
                    int next_rank=user.rank+1;
                    user.now_money-=user.need_money;
                    user.promotion(next_rank);
                    user.now_need_money=user.need_money-user.now_money;
                    info.setText("직급 : "+ user.rank_name+"\n"+
                            "현재 소지 : " + user.now_money+ "\n" +
                            "다음 진급까지 남은 돈 : "+user.now_need_money);
                    postFirebaseUserInfo(true);
                }
            }
        });
    }

    private void getFirebaseDatabaseInfo(final Callback callback) {
        // Get UserInfo from Firebase
        mPostReference.child("UserInfo/"+userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInfo get = dataSnapshot.getValue(UserInfo.class);
                user = new UserInfo(get.rank);
                user.now_money=get.now_money;
                if(get == null) Log.d("userdata","failed");
                Log.d("groundFurn", get.groundFurn.toString());
                Log.d("hasFurniture", get.hasFurniture.toString());

                callback.success("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.fail();
            }
        });
    }

//    public class UpdateAsyncTask extends AsyncTask<Integer, Integer, Integer>{
//        @Override
//        protected void onPreExecute(){
//            super.onPreExecute();
//        }
//        @Override
//        protected void onProgressUpdate(Integer... values){
//            super.onProgressUpdate(values);
//        }
//        @Override
//        protected Integer doInBackground(Integer... params){
//
//            // Firebase MoneyInfo update
//            while(true) {
//                try {
//                    Thread.sleep(60000);
//                    postFirebaseUserInfo(true);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        @Override
//        protected void onPostExecute(Integer result){
//            super.onPostExecute(result);
//        }
//    }

    public class MyAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            // textview updates
            info.setText("직급 : "+ user.rank_name+"\n"+
                    "현재 소지 : " + user.now_money+ "\n" +
                    "다음 진급까지 남은 돈 : "+user.now_need_money);
        }
        @Override
        protected Integer doInBackground(Integer... params){
            int cnt=0;
            // 현재 소지금액 update
            while(true) {
                try {
                    cnt++;
                    if(cnt%12==0){
                        postFirebaseUserInfo(true);
                    }
                    Thread.sleep(5000);
                    user.now_money += user.auto;
                    user.now_need_money=user.need_money-user.now_money;
                    if(user.now_need_money<0){
                        user.now_need_money=0;
                    }
                    publishProgress(user.now_money);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        protected void onPostExecute(Integer result){
            super.onPostExecute(result);
        }
    }

    public void postFirebaseUserInfo(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            postValues = user.toMap();
        }
        childUpdates.put("/UserInfo/"+userName, postValues);
        mPostReference.updateChildren(childUpdates);
    }
}
