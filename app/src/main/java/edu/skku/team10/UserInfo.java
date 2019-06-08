package edu.skku.team10;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UserInfo implements Serializable {
    public String rank_name;
    public int need_money;
    public int touch;
    public int auto;
    public boolean can_promote;
    public int rank;

    public int now_money;
    public int now_need_money;

    public List<Integer> groundFurn;
    public List<Boolean> hasFurniture;
    public List<UserCatsInfo> catsInfo;

    public boolean isCatChanged;

    public UserInfo(){
        groundFurn = new ArrayList<>();
        hasFurniture = new ArrayList<>();
        catsInfo = new ArrayList<>();
        for(int i=0; i<10; ++i) groundFurn.add(-1);
        for(int i=0; i<10; ++i) hasFurniture.add(false);
    }

    public UserInfo(int num){
        if(num==1){
            rank=1;
            rank_name="알바";
            now_money = 0;
            need_money=3000;
            touch=100;
            auto=500;
            can_promote=true;
        }
        else if(num==2){
            rank=2;
            rank_name="인턴";
            need_money=10000;
            touch=500;
            auto=2000;
            can_promote=true;
        }
        else if(num==3){
            rank=3;
            rank_name="정직원";
            need_money=0;
            touch=1000;
            auto=5000;
            can_promote=false;
        }
        groundFurn = new ArrayList<>();
        hasFurniture = new ArrayList<>();
        catsInfo = new ArrayList<>();
        for(int i=0; i<10; ++i) groundFurn.add(-1);
        for(int i=0; i<10; ++i) hasFurniture.add(false);
    }

    public void promotion(int num){
        if(num==2){
            rank=2;
            rank_name="인턴";
            need_money=10000;
            touch=500;
            auto=2000;
            can_promote=true;
        }
        else if(num==3){
            rank=3;
            rank_name="정직원";
            need_money=0;
            touch=1000;
            auto=5000;
            can_promote=false;
        }
        return;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("rank", rank);
        result.put("now_money", now_money);
        result.put("groundFurn", groundFurn);
        result.put("hasFurniture", hasFurniture);
        return result;
    }

    public List<ObjectOnGround> updateCatsInfo(List<CatInfo> catInfos, List<ObjectOnGround> objectOnGrounds){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/hh/mm");
        List<ObjectOnGround> onGrounds = objectOnGrounds;
        Log.d("size of catInfos", String.valueOf(catInfos.size()));
        for(int i = 0; i < catInfos.size(); ++i) {
            UserCatsInfo c = catsInfo.get(i);
            try{
                Date comeDate = sdf.parse(c.comeTime);
                Date goDate = sdf.parse(c.goTime);
                Date current = Calendar.getInstance().getTime();
                long come_diff = (comeDate.getTime() - current.getTime()) / 60000; // into minutes
                long go_diff = (goDate.getTime() - current.getTime()) / 60000; // into minutes
                Log.d("come_diff", String.valueOf(come_diff));
                Log.d("go_diff", String.valueOf(go_diff));

                while(come_diff < 0 || go_diff < 0){
                    isCatChanged = true;
                    if(come_diff < go_diff){
                        catsInfo.get(i).currentCome = false;
                        long appearmin = 3600000*(catInfos.get(i).appearTime.get(0)).longValue();
                        long appearmax = 3600000*(catInfos.get(i).appearTime.get(1)).longValue();
                        long betweentime = (long)(new Random().nextInt((int)(appearmax-appearmin+1))) + appearmin;
                        Log.d("between time", String.valueOf(betweentime));
                        comeDate.setTime(goDate.getTime() + betweentime);
                        catsInfo.get(i).comeTime = sdf.format(comeDate);
                        Log.d("changed comeTime", catsInfo.get(i).comeTime);
                        come_diff = (comeDate.getTime() - current.getTime()) / 60000;

                        for(int x : this.groundFurn){
                            if(x==i) x = -1;
                        }
                    }
                    else{
                        catsInfo.get(i).currentCome = true;
                        long lifemin = 3600000*(catInfos.get(i).lifeTime.get(0)).longValue();
                        long lifemax = 3600000*(catInfos.get(i).lifeTime.get(1)).longValue();
                        long betweentime = (long)(new Random().nextInt((int)(lifemax-lifemin+1))) + lifemin;
                        Log.d("between time", String.valueOf(betweentime));
                        goDate.setTime(comeDate.getTime() + betweentime);
                        catsInfo.get(i).goTime = sdf.format(goDate);
                        Log.d("changed goTime", catsInfo.get(i).goTime);
                        go_diff = (goDate.getTime() - current.getTime()) / 60000;

                        List<Integer> temp = new ArrayList<>();
                        for(int j = 0; j < onGrounds.size(); ++j){
                            if(onGrounds.get(j).furnitureID == -1)
                                continue;
                            if(onGrounds.get(j).catID != -1)
                                continue;
                            temp.add(j);
                        }
                        if(temp.size() == 0) continue;
                        int res = temp.get(new Random().nextInt(temp.size()));
                        onGrounds.get(res).catID = i;
                    }
                }

            } catch (Exception e){
                e.printStackTrace();
            }

        }
        return onGrounds;
    }
}

