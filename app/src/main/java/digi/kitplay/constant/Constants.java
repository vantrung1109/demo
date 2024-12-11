package digi.kitplay.constant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Constants {
    public static final String DB_NAME = "mvvm.db";
    public static final String PREF_NAME = "mvvm.prefs";

    public static final String VALUE_BEARER_TOKEN_DEFAULT="NULL";

    //Local Action manager
    public static final String ACTION_EXPIRED_TOKEN ="ACTION_EXPIRED_TOKEN";

    public static final String SHOP_BASE_URL = "https://update.digibes.de/";

    //default app
    public static final String APP = "ANDROID";
    public static final String LANG = "vi";
    public static final int PLATFORM = 2;
    public static final String VERSION = "1.0";


    //view type order
    public static final int VT_ORDER_EXPANDED_HEADER = 11111;
    public static final int VT_ORDER_COLLAPSED_HEADER = 22222;
    public static final int VT_ORDER_FOOD = 33333;
    public static final int VT_ORDER_FOOTER = 44444;

    //state order
    public static final int ORDER_STATE_WORKING = 1;
    public static final int ORDER_STATE_DONE = 2;
    public static final int ORDER_STATE_WAITING = 0;
    public static final int ORDER_FOOD_STATUS_WAITING = 0;
    public static final int ORDER_FOOD_STATUS_WORKING = 1;
    public static final int ORDER_FOOD_STATUS_DONE = 2;

    //order type
    public static final int ORDER_TYPE_DINE_IN = 1;
    public static final int ORDER_TYPE_TAKE_OUT = 2;
    public static final int ORDER_TYPE_DELIVERY = 3;

    //setting column device
    public static int ORDER_COLUMN_COUNT = 4;
    public static double DIMENSION_RATIO = 24.0/ORDER_COLUMN_COUNT;
    public static String dimenRatio1 = DIMENSION_RATIO+":1";
    public static String dimenRatioTest = DIMENSION_RATIO+":0.7";
    public static String dimenRatio2 = DIMENSION_RATIO+":2";
    public static String dimenRatio3 = DIMENSION_RATIO+":3";

    public static void setDevice(Integer numberOfColumns){
        if(numberOfColumns == null || numberOfColumns ==0){
            return;
        }
        ORDER_COLUMN_COUNT = numberOfColumns;
        DIMENSION_RATIO = 24.0/ORDER_COLUMN_COUNT;
        dimenRatio1 = DIMENSION_RATIO+":1";
        dimenRatio2 = DIMENSION_RATIO+":2";
    }

    //setting myprinter
    public static Integer myPrinterId;
    public static Long myPrinterGroupId;
    public static Long waitPrinterGroupId;
    public static final Map<Long, Bitmap> mapPrinterGroupIcon = new HashMap<>();
    public static final Map<Long, Integer> mapPrinterGroupSortId = new HashMap<>();

    //GET FOOD BY PINTER GROUP KIND
    public static final Integer GET_FOOD_BY_PRINTER_GROUP = 2;
    public static final Integer GET_FOOD_BY_PRINTER = 1;
    public static Integer printerGroupKind;
    private Constants(){

    }
}
