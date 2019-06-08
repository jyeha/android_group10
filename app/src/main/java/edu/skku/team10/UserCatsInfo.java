package edu.skku.team10;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UserCatsInfo implements Serializable {
    public String comeTime;
    public String goTime;
    public boolean currentCome;
    public int groundPos;

    public UserCatsInfo(){
        comeTime = new SimpleDateFormat("yyyy/MM/dd/hh/mm").format(Calendar.getInstance().getTime());
        goTime = new SimpleDateFormat("yyyy/MM/dd/hh/mm").format(Calendar.getInstance().getTime());
        currentCome = false;
    }
}
