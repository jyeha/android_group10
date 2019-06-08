package edu.skku.team10;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfo {
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

    public UserInfo(){
        groundFurn = new ArrayList<>();
        hasFurniture = new ArrayList<>();
    }

    public UserInfo(int num){
        if(num==1){
            rank=1;
            rank_name="알바";
            need_money=3000;
            touch=100;
            auto=500;
            can_promote=true;

            groundFurn = new ArrayList<>();
            hasFurniture = new ArrayList<>();
        }
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

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("now_money", now_money);
        result.put("groundFurn", groundFurn);
        result.put("hasFurniture", hasFurniture);
        result.put("rank", rank);
        return result;
    }
}
