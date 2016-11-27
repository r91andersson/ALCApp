package integrum.bioniclimbcontroller.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by Robin on 2016-10-11.
 */
public class SettingsDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SettingsDatabase.db";
    public static String DATABASE_PATH = "";
    public static final String SETTINGS_TABLE_NAME = "SettingsData";
    public static final String SETTINGS_COLUMN_ID = "id";
    public static final String SETTINGS_COLUMN_TIMESTAMP = "timestamp";
    public static final String SETTINGS_COLUMN_SETTINGSTYPE = "settingstype";
    public static final String SETTINGS_COLUMN_VALUE = "value";
    public static final String SETTINGS_COLUMN_SECOND_VALUE ="secondvalue";


    public static final String SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_1 = "threshold_ch_1";
    public static final String SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_2 = "threshold_ch_2";
    public static final String SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_3 = "threshold_ch_3";
    public static final String SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_4 = "threshold_ch_4";
    public static final String SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_5 = "threshold_ch_5";
    public static final String SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_6 = "threshold_ch_6";
    public static final String SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_7 = "threshold_ch_7";
    public static final String SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_8 = "threshold_ch_8";

    public static final String SETTINGS_TYPE_PROGRESS_MAX_VALUE_CH_1 = "max_value_ch1";
    public static final String SETTINGS_TYPE_PROGRESS_MAX_VALUE_CH_2 = "max_value_ch2";
    public static final String SETTINGS_TYPE_PROGRESS_MAX_VALUE_CH_3 = "max_value_ch3";
    public static final String SETTINGS_TYPE_PROGRESS_MAX_VALUE_CH_4 = "max_value_ch4";
    public static final String SETTINGS_TYPE_PROGRESS_MAX_VALUE_CH_5 = "max_value_ch5";
    public static final String SETTINGS_TYPE_PROGRESS_MAX_VALUE_CH_6 = "max_value_ch6";
    public static final String SETTINGS_TYPE_PROGRESS_MAX_VALUE_CH_7 = "max_value_ch7";
    public static final String SETTINGS_TYPE_PROGRESS_MAX_VALUE_CH_8 = "max_value_ch8";

    public static final String[] THRESHOLD_STRING = {SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_1,
            SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_2,
            SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_3,
            SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_4,
            SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_5,
            SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_6,
            SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_7,
            SETTINGS_TYPE_THRESHOLD_PARAMETER_CH_8};

    public static final String[] PROGRESS_MAX_VALUE_STRING ={SETTINGS_TYPE_PROGRESS_MAX_VALUE_CH_1,
            SETTINGS_TYPE_PROGRESS_MAX_VALUE_CH_2,
            SETTINGS_TYPE_PROGRESS_MAX_VALUE_CH_3,
            SETTINGS_TYPE_PROGRESS_MAX_VALUE_CH_4,
            SETTINGS_TYPE_PROGRESS_MAX_VALUE_CH_5,
            SETTINGS_TYPE_PROGRESS_MAX_VALUE_CH_6,
            SETTINGS_TYPE_PROGRESS_MAX_VALUE_CH_7,
            SETTINGS_TYPE_PROGRESS_MAX_VALUE_CH_8};




    public static final String SETTINGS_TYPE_DEBUG_MODE = "debug_mode";
    public static final String SETTINGS_TYPE_EMG_CH1_GRAPH_ACTIVATED = "emg_ch1_graph_activated";
    public static final String SETTINGS_TYPE_EMG_CH2_GRAPH_ACTIVATED = "emg_ch2_graph_activated";
    public static final String SETTINGS_TYPE_EMG_CH3_GRAPH_ACTIVATED = "emg_ch3_graph_activated";
    public static final String SETTINGS_TYPE_EMG_CH4_GRAPH_ACTIVATED = "emg_ch4_graph_activated";
    public static final String SETTINGS_TYPE_EMG_CH5_GRAPH_ACTIVATED = "emg_ch5_graph_activated";
    public static final String SETTINGS_TYPE_EMG_CH6_GRAPH_ACTIVATED = "emg_ch6_graph_activated";
    public static final String SETTINGS_TYPE_EMG_CH7_GRAPH_ACTIVATED = "emg_ch7_graph_activated";
    public static final String SETTINGS_TYPE_EMG_CH8_GRAPH_ACTIVATED = "emg_ch8_graph_activated";


    public static final String SETTINGS_TYPE_TEMP_SWITCH = "temp_switch";
    public static final String SETTINGS_TYPE_EMG_SWITCH = "emg_switch";
    public static final String SETTINGS_TYPE_ORIENTATION_SWITCH = "orientation_switch";
    public static final String SETTINGS_TYPE_DIRECT_CONTROL_SWITCH = "direct_control";
    public static final String SETTINGS_TYPE_EXTRACT_FEATURES_SWITCH = "extract_features";
    public static final String SETTINGS_TYPE_COMMAND_MODE_SWTICH = "command_mode";


    public static final String SETTINGS_TYPE_SENSOR_HAND = "sensor_hand";
    public static final String SETTINGS_TYPE_BATTERY_VOLTAGE= "battery_voltage";
    public static final String SETTINGS_TYPE_TEMPERATURE = "temperature";
    public static final String SETTINGS_TYPE_NEUROSTIMULATOR = "neurostimulator";
    public static final String SETTINGS_TYPE_SDCARD = "sdcard";
    public static final String SETTINGS_TYPE_INEMO = "inemo";
    public static final String SETTINGS_TYPE_CTRLMODE = "ctrl_mode";
    public static final String SETTINGS_TYPE_ENABLENS = "enable_ns";


    public static final String SETTINGS_TYPE_OPEN_HAND = "open_hand";
    public static final String SETTINGS_TYPE_CLOSE_HAND = "close_hand";
    public static final String SETTINGS_TYPE_SWITCH_HAND = "switch_hand";
    public static final String SETTINGS_TYPE_PRONATION= "pronation";
    public static final String SETTINGS_TYPE_SUPINATION = "supination";
    public static final String SETTINGS_TYPE_COCONTRACTION = "cocontraction";
    public static final String SETTINGS_TYPE_FLEX_HAND = "flex_hand";
    public static final String SETTINGS_TYPE_EXTEND_HAND = "extend_hand";
    public static final String SETTINGS_TYPE_SIDE_GRIP = "side_grip";
    public static final String SETTINGS_TYPE_FINE_GRIP = "fine_grip";
    public static final String SETTINGS_TYPE_AGREE = "agree";
    public static final String SETTINGS_TYPE_POINTER = "pointer";
    public static final String SETTINGS_TYPE_THUMB_EXTEND = "thumb_extend";
    public static final String SETTINGS_TYPE_THUMB_FLEX = "thumb_flex";
    public static final String SETTINGS_TYPE_THUMB_ADUCTION = "thumb_aduciton";
    public static final String SETTINGS_TYPE_THUMB_ADUCTION_2 = "tumb_adution2";
    public static final String SETTINGS_TYPE_FLEX_ELBOW = "flex_elbow";
    public static final String SETTINGS_TYPE_EXTEND_ELBOW = "extend_elbow";
    public static final String SETTINGS_TYPE_INDEX_FLEX = "index_flex";
    public static final String SETTINGS_TYPE_INDEX_EXTEND= "index_extend";
    public static final String SETTINGS_TYPE_MIDDLE_FLEX = "middle_flex";
    public static final String SETTINGS_TYPE_MIDDLE_EXTEND = "middle_extend";
    public static final String SETTINGS_TYPE_RING_EXTEND = "ring_extend";
    public static final String SETTINGS_TYPE_LITTLE_FLEX = "little_flex";
    public static final String SETTINGS_TYPE_LITTLE_EXTEND = "little_extend";

    public static final String SETTINGS_TYPE_PATTERN_REC_CALIBRATED = "pattern_rec";


    public static final String[] MOVEMENTS_STRING = {SETTINGS_TYPE_OPEN_HAND,SETTINGS_TYPE_CLOSE_HAND,SETTINGS_TYPE_SWITCH_HAND,SETTINGS_TYPE_PRONATION,SETTINGS_TYPE_SUPINATION,SETTINGS_TYPE_COCONTRACTION,SETTINGS_TYPE_FLEX_HAND,SETTINGS_TYPE_EXTEND_HAND,SETTINGS_TYPE_SIDE_GRIP,
            SETTINGS_TYPE_FINE_GRIP,SETTINGS_TYPE_FINE_GRIP,SETTINGS_TYPE_AGREE,SETTINGS_TYPE_POINTER,SETTINGS_TYPE_THUMB_EXTEND,SETTINGS_TYPE_THUMB_FLEX,SETTINGS_TYPE_THUMB_ADUCTION,
            SETTINGS_TYPE_THUMB_ADUCTION_2,SETTINGS_TYPE_FLEX_ELBOW,SETTINGS_TYPE_EXTEND_ELBOW,SETTINGS_TYPE_INDEX_FLEX,SETTINGS_TYPE_INDEX_EXTEND,SETTINGS_TYPE_MIDDLE_FLEX,SETTINGS_TYPE_MIDDLE_EXTEND,
            SETTINGS_TYPE_RING_EXTEND,SETTINGS_TYPE_LITTLE_FLEX, SETTINGS_TYPE_LITTLE_EXTEND};
    private static final String[] COLUMNS = {SETTINGS_COLUMN_ID,SETTINGS_COLUMN_TIMESTAMP,SETTINGS_COLUMN_SETTINGSTYPE,SETTINGS_COLUMN_VALUE};

    // Number of columns in returning data array for a request.
    // Format: ID | Timestamp | Setting Type | Value | SecondValue
    public static final int nrCols = 5;

    private SQLiteDatabase myDataBase;
    private final Context myContext;



    public SettingsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.myContext = context;

        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table SettingsData " +
                        "(id integer primary key," +
                        "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "settingstype text," +
                        "value text," +
                        "secondvalue text)"
        );
    }

    public boolean insertSetting  (String settingsType, int value)
    {
        String val;
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            switch (settingsType) {
                //Extract and transform into string
                case SETTINGS_TYPE_DEBUG_MODE:
                    if(value==1) {
                        val = "1";
                    }else{
                        val = "0";
                    }
                    break;
                default:
                    val = String.valueOf(value);
                    break;

            }
            //Store into contentValues object
            contentValues.put(SETTINGS_COLUMN_SETTINGSTYPE, settingsType);
            contentValues.put(SETTINGS_COLUMN_VALUE, val);

            // Insert into database
            sqLiteDatabase.insert(SETTINGS_TABLE_NAME, null, contentValues);

            //Always close database after use
            sqLiteDatabase.close();
        } catch (Exception e){
            return false;
        }
        return true;
    }

    public boolean insertSetting  (String settingsType, String value)
    {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            //Store into contentValues object
            contentValues.put(SETTINGS_COLUMN_SETTINGSTYPE, settingsType);
            contentValues.put(SETTINGS_COLUMN_VALUE, value);

            // Insert into database
            sqLiteDatabase.insert(SETTINGS_TABLE_NAME, null, contentValues);

            //Always close database after use
            sqLiteDatabase.close();
        } catch (Exception e){
            return false;
        }
        return true;
    }

    public boolean insertSetting (String settingsType, String value,String secondValue)
    {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            //Store into contentValues object
            contentValues.put(SETTINGS_COLUMN_SETTINGSTYPE, settingsType);
            contentValues.put(SETTINGS_COLUMN_VALUE, value);
            contentValues.put(SETTINGS_COLUMN_SECOND_VALUE,secondValue);

            // Insert into database
            sqLiteDatabase.insert(SETTINGS_TABLE_NAME, null, contentValues);

            //Always close database after use
            sqLiteDatabase.close();
        } catch (Exception e){
            return false;
        }
        return true;
    }


    public boolean updateSetting (String settingsType, String newValue){
        SQLiteDatabase db = this.getReadableDatabase();
        String q= "SELECT * FROM SettingsData WHERE settingstype='" +settingsType+ "' ORDER BY id DESC LIMIT 1";
        Cursor res =  db.rawQuery(q,null);
        try {
            res.moveToFirst();
            String id = res.getString(res.getColumnIndex(SETTINGS_COLUMN_ID));
            ContentValues data=new ContentValues();
            data.put(SETTINGS_COLUMN_SETTINGSTYPE, settingsType);
            data.put(SETTINGS_COLUMN_VALUE,newValue);
            db.update("SettingsData", data, "id=" + id, null);
            res.close();
            db.close();
        }
        catch (Exception e) {
            res.close();
            db.close();
            return false;
        }
        return true;
    }

    public boolean updateSetting (String settingsType, String newValue, String secondNewValue){
        SQLiteDatabase db = this.getReadableDatabase();
        String q= "SELECT * FROM SettingsData WHERE settingstype='" +settingsType+ "' ORDER BY id DESC LIMIT 1";
        Cursor res =  db.rawQuery(q,null);
        try {
            res.moveToFirst();
            String id = res.getString(res.getColumnIndex(SETTINGS_COLUMN_ID));
            ContentValues data=new ContentValues();
            data.put(SETTINGS_COLUMN_SETTINGSTYPE, settingsType);
            data.put(SETTINGS_COLUMN_VALUE,newValue);
            data.put(SETTINGS_COLUMN_SECOND_VALUE,secondNewValue);
            db.update("SettingsData", data, "id=" + id, null);
            res.close();
            db.close();
        }
        catch (Exception e) {
            res.close();
            db.close();
            return false;
        }
        return true;
    }
    /*
    public boolean updateSetting (String settingsType, String id,String newValue){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            ContentValues data=new ContentValues();
            data.put(SETTINGS_COLUMN_SETTINGSTYPE, settingsType);
            data.put(SETTINGS_COLUMN_VALUE,newValue);
            db.update("SettingsData",data,"id=" + id,null);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
*/
    public String getRowId (String settingsType){
        String result;
        SQLiteDatabase db = this.getReadableDatabase();
        String q= "SELECT * FROM SettingsData WHERE settingstype='" +settingsType+ "' ORDER BY id DESC LIMIT 1";
        Cursor res =  db.rawQuery(q,null);
        try {
            res.moveToFirst();
            result = res.getString(res.getColumnIndex(SETTINGS_COLUMN_ID));
        }  catch (Exception e) {
            result=null;
        }
        res.close();
        //Always close database after use
        db.close();
        return result;
    }

    public String[] getLatestSetting (String settingsType){
        String[] result= new String[nrCols];
        SQLiteDatabase db = this.getReadableDatabase();
        String q= "SELECT * FROM SettingsData WHERE settingstype='" +settingsType+ "' ORDER BY id DESC LIMIT 1";
        Cursor res =  db.rawQuery(q,null);
        try {
            res.moveToFirst();
            String id = res.getString(res.getColumnIndex(SETTINGS_COLUMN_ID));
            String timestamp = res.getString(res.getColumnIndex(SETTINGS_COLUMN_TIMESTAMP));
            String setType = res.getString(res.getColumnIndex(SETTINGS_COLUMN_SETTINGSTYPE));
            String value = res.getString(res.getColumnIndex(SETTINGS_COLUMN_VALUE));
            String secondValue=res.getString(res.getColumnIndex(SETTINGS_COLUMN_SECOND_VALUE));
            result[0]=id;
            result[1]=timestamp;
            result[2]=setType;
            result[3]=value;
            result[4]=secondValue;
        }
        catch (Exception e) {
            result=null;
        }
        res.close();
        db.close();
        return result;
    }

    public boolean deleteDataBase(Context context ){
        context.deleteDatabase(DATABASE_NAME);
        return true;
    }

    public int numberOfRows(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(sqLiteDatabase, SETTINGS_TABLE_NAME);
        sqLiteDatabase.close();
        return numRows;
    }

    public long getDataBaseSize(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        long size = new File(sqLiteDatabase.getPath()).length();
        return size;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS SettingsData");
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
