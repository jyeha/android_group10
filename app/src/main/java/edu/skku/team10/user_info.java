package edu.skku.team10;

public class user_info extends base_info {
    String user_name;

    public user_info(){}

    public user_info(int num){
        if(num==1){
            rank=1;
            rank_name="알바";
            need_money=3000;
            touch=100;
            auto=500;
            can_promote=true;
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
}
