package integrum.bioniclimbcontroller.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by Robin on 2016-11-09.
 */
public class DcDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "DirectControlDatabase.db";
    public static String DATABASE_PATH = "";
    public static final String DIRECTCONTROL_TABLE_NAME = "DirectControlData";
    public static final String DIRECTCONTROL_COLUMN_ID = "id";
    public static final String DIRECTCONTROL_COLUMN_TIMESTAMP = "timestamp";
    public static final String DIRECTCONTROL_COLUMN_MOVEMENT = "movement";
    public static final String DIRECTCONTROL_COLUMN_CHANNEL = "channel";
    public static final String DIRECTCONTROL_COLUMN_THRESHOLD = "threshold";
    public static final String DIRECTCONTROL_COLUMN_VALUE = "value";
    public static final String DIRECTCONTROL_COLUMN_ENABLED = "enabled";

    private SQLiteDatabase myDataBase;
    private final Context myContext;

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

    public static final String[] MOVEMENTS_STRING = {SETTINGS_TYPE_OPEN_HAND,SETTINGS_TYPE_CLOSE_HAND,SETTINGS_TYPE_FLEX_HAND,SETTINGS_TYPE_EXTEND_HAND,SETTINGS_TYPE_PRONATION,SETTINGS_TYPE_SUPINATION,SETTINGS_TYPE_SIDE_GRIP,
            SETTINGS_TYPE_FINE_GRIP,SETTINGS_TYPE_FINE_GRIP,SETTINGS_TYPE_AGREE,SETTINGS_TYPE_POINTER,SETTINGS_TYPE_THUMB_EXTEND,SETTINGS_TYPE_THUMB_FLEX,SETTINGS_TYPE_THUMB_ADUCTION,
            SETTINGS_TYPE_THUMB_ADUCTION_2,SETTINGS_TYPE_FLEX_ELBOW,SETTINGS_TYPE_EXTEND_ELBOW,SETTINGS_TYPE_INDEX_FLEX,SETTINGS_TYPE_INDEX_EXTEND,SETTINGS_TYPE_MIDDLE_FLEX,SETTINGS_TYPE_MIDDLE_EXTEND,
            SETTINGS_TYPE_RING_EXTEND,SETTINGS_TYPE_LITTLE_FLEX, SETTINGS_TYPE_LITTLE_EXTEND};


    // Number of columns in returning data array for a request.
    // Format: ID | Timestamp | Movement name | Channel | Threshold | Value | Enabled
    private int nrCols = 7;



    public DcDbHelper(Context context) {
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
        sqLiteDatabase.execSQL("create table DirectControlData " +
                        "(id integer primary key," +
                        "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "movement text," +
                        "channel text," +
                        "threshold text," +
                        "value text," +
                        "enabled text)"
        );
    }

    public int numberOfRows(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(sqLiteDatabase, DIRECTCONTROL_TABLE_NAME);
        return numRows;
    }

    public long getDataBaseSize(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        long size = new File(sqLiteDatabase.getPath()).length();
        return size;
    }

    public String getRowId (String movement){
        String result;
        SQLiteDatabase db = this.getReadableDatabase();
        String q= "SELECT * FROM DirectControlData WHERE movement='" +movement+ "' ORDER BY id DESC LIMIT 1";
        Cursor res =  db.rawQuery(q,null);
        try {
            res.moveToFirst();
            result = res.getString(res.getColumnIndex(DIRECTCONTROL_COLUMN_ID));
        }  catch (Exception e) {
            result=null;
        }
        res.close();
        return result;
    }

    public boolean insertData(String movement, String channel,String threshold,String value,String enabled)
    {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            //Store into contentValues object
            contentValues.put("movement", movement);
            contentValues.put("channel", channel);
            contentValues.put("threshold", threshold);
            contentValues.put("value", value);
            contentValues.put("enabled",enabled);
            // Insert into database
            sqLiteDatabase.insert(DIRECTCONTROL_TABLE_NAME, null, contentValues);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    public String[] getLatestSetting (String settingsType){
        String[] result= new String[nrCols];
        SQLiteDatabase db = this.getReadableDatabase();
        String q= "SELECT * FROM DirectControlData WHERE movement='" +settingsType+ "' ORDER BY id DESC LIMIT 1";
        Cursor res =  db.rawQuery(q,null);
        try {
            res.moveToFirst();
            String id = res.getString(res.getColumnIndex(DIRECTCONTROL_COLUMN_ID));
            String timestamp = res.getString(res.getColumnIndex(DIRECTCONTROL_COLUMN_TIMESTAMP));
            String movement = res.getString(res.getColumnIndex(DIRECTCONTROL_COLUMN_MOVEMENT));
            String channel = res.getString(res.getColumnIndex(DIRECTCONTROL_COLUMN_CHANNEL));
            String threshold = res.getString(res.getColumnIndex(DIRECTCONTROL_COLUMN_THRESHOLD));
            String value = res.getString(res.getColumnIndex(DIRECTCONTROL_COLUMN_VALUE));
            String enabled = res.getString(res.getColumnIndex(DIRECTCONTROL_COLUMN_ENABLED));
            result[0]=id;
            result[1]=timestamp;
            result[2]=movement;
            result[3]=channel;
            result[4]=threshold;
            result[5]=value;
            result[6]=enabled;
        }
        catch (Exception e) {
            result=null;
        }
        res.close();
        return result;
    }

    public  String[][] getAllPatRecMov()
    {
        String[][] result= new String[numberOfRows()][nrCols];
        int count=0;
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from DirectControlData", null);
        res.moveToFirst();

        while(res.isAfterLast() == false) {

            String id = res.getString(res.getColumnIndex(DIRECTCONTROL_COLUMN_ID));
            String timestamp = res.getString(res.getColumnIndex(DIRECTCONTROL_COLUMN_TIMESTAMP));
            String movement = res.getString(res.getColumnIndex(DIRECTCONTROL_COLUMN_MOVEMENT));
            String channel = res.getString(res.getColumnIndex(DIRECTCONTROL_COLUMN_CHANNEL));
            String threshold = res.getString(res.getColumnIndex(DIRECTCONTROL_COLUMN_THRESHOLD));
            String value = res.getString(res.getColumnIndex(DIRECTCONTROL_COLUMN_VALUE));
            String enabled = res.getString(res.getColumnIndex(DIRECTCONTROL_COLUMN_ENABLED));
            result[count][0]=id;
            result[count][1]=timestamp;
            result[count][2]=movement;
            result[count][3]=channel;
            result[count][4]=threshold;
            result[count][5]=value;
            result[count][6]=enabled;
            res.moveToNext();
            count++;
        }
        res.close();
        return result;
    }

    public boolean deleteDataBase(Context context ){
        context.deleteDatabase(DATABASE_NAME);
        return true;
    }

    public boolean updateSetting (String movement, String channel,String threshold,String value,String enabled){
        SQLiteDatabase db = this.getReadableDatabase();
        String q= "SELECT * FROM DirectControlData WHERE movement='" +movement+ "' ORDER BY id DESC LIMIT 1";
        Cursor res =  db.rawQuery(q,null);
        try {
            res.moveToFirst();
            String id = res.getString(res.getColumnIndex(DIRECTCONTROL_COLUMN_ID));
            ContentValues data=new ContentValues();
            data.put(DIRECTCONTROL_COLUMN_MOVEMENT, movement);
            data.put(DIRECTCONTROL_COLUMN_CHANNEL,channel);
            data.put(DIRECTCONTROL_COLUMN_THRESHOLD,threshold);
            data.put(DIRECTCONTROL_COLUMN_VALUE,value);
            data.put(DIRECTCONTROL_COLUMN_ENABLED,enabled);
            db.update("DirectControlData", data, "id=" + id, null);
        }
        catch (Exception e) {
            return false;
        }
        db.close();
        res.close();
        return true;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS DirectControlData");
    }


}
