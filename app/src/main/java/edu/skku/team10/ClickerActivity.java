package edu.skku.team10;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ClickerActivity extends AppCompatActivity {

    public user_info user=new user_info(1);
    TextView info;
    Button promote;
    GestureDetector detector;
    public int check_long=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicker);

        info = (TextView)findViewById(R.id.info);
        promote = (Button)findViewById(R.id.upgrade);
        MyAsyncTask EarnMoneyPerTime = new MyAsyncTask();
        EarnMoneyPerTime.execute();

        info.setText("직급 : "+ user.rank_name+"\n"+
                "현재 소지 : " + user.now_money+ "\n" +
                "다음 진급까지 남은 돈 : "+user.now_need_money);

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
                }
            }
        });
    }

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

            // 현재 소지금액 update
            while(true) {
                try {
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
}
