package edu.skku.team10;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserCatsInfo implements Serializable {
    public String comeTime;
    public String goTime;
    public boolean currentCome;
    public int groundPos;

    public UserCatsInfo(){
        comeTime = new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(Calendar.getInstance().getTime());
        goTime = new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(Calendar.getInstance().getTime());
        currentCome = false;
        groundPos = 0;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("comeTime", comeTime);
        result.put("currentCome", currentCome);
        result.put("goTime", goTime);
        result.put("groundPos", groundPos);
        return result;
    }
}
