package com.example.akhileshlamba.smarter;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by akhileshlamba on 29/3/18.
 */

public class ElectricityUsageSimulator {
    private static double[] fridgeRange = {0.3,0.4,0.5,0.6,0.7,0.8};
    private static double[] acRange = {1.0,1.5,2.0,2.5,3.0,3.5,4.0,4.5,5.0};
    private static double[] wmRange = {0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2,1.3};
    private static final int MAX_AC_TIME = 10;
    private static final int MAX_WM_TIME = 3;
    private static int ac_counter = 0;
    private static int wm_counter = 0;
    private static boolean[] ac_flag = {false,true};
    private static boolean[] wm_flag = {false,true};
    private static boolean on = false;


    private static double fridge_Usage = getFridgeUsage();
    private static double ac_Usage = getAcUsage();
    private static double wm_Usage = getWmUsage();

    private static final double final_fridge = fridge_Usage;
    private static final double final_ac = ac_Usage;
    private static final double final_wm = wm_Usage;

    public static double getFridgeUsage(){
        return fridgeRange[getRandom(fridgeRange.length)];
    }

    public static int getRandom(int length){
        Random random = new Random();
        return random.nextInt(length);
    }

    public static double getAcUsage(){
        return acRange[getRandom(acRange.length)];
    }

    public static double getWmUsage(){
        return wmRange[getRandom(wmRange.length)];
    }


    public double setWMUsage(){
        HashMap<String, Double> usage = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(wm_flag[getRandom(wm_flag.length)] && !on)
            on = true;
        if(wm_counter < MAX_WM_TIME && hour < 21 && hour >= 6 && on){
            wm_counter ++;
            return final_wm;
        }else
            return 0.0;
    }

    public double setFRUsage(){
        return final_fridge;
    }

    public double setACUsage(){
        HashMap<String, Double> usage = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(ac_counter < MAX_AC_TIME && hour < 23 && hour >= 9 && ac_flag[getRandom(ac_flag.length)]){
            ac_counter ++;
            return final_ac;
        }else
            return  0.0;
    }

}
