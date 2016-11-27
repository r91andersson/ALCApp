package integrum.bioniclimbcontroller.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by Robin on 2016-10-11.
 */
public class EmgDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "EMGDatabase.db";
    public static String DATABASE_PATH = "";
    public static final String EMG_TABLE_NAME = "EMGData";
    public static final String EMG_COLUMN_ID = "id";
    public static final String EMG_COLUMN_TIMESTAMP = "timestamp";
    public static final String EMG_COLUMN_VALUE_CHANNEL_1 = "ch1";
    public static final String EMG_COLUMN_VALUE_CHANNEL_2 = "ch2";
    public static final String EMG_COLUMN_VALUE_CHANNEL_3 = "ch3";
    public static final String EMG_COLUMN_VALUE_CHANNEL_4 = "ch4";
    public static final String EMG_COLUMN_VALUE_CHANNEL_5 = "ch5";
    public static final String EMG_COLUMN_VALUE_CHANNEL_6 = "ch6";
    public static final String EMG_COLUMN_VALUE_CHANNEL_7 = "ch7";
    public static final String EMG_COLUMN_VALUE_CHANNEL_8 = "ch8";


    // Number of columns in returning data array for a request.
    // Format: ID | Timestamp | Channel_1 | Channel_2 | Channel_3 | Channel_4 | Channel_5 | Channel_6 | Channel_7 | Channel_8
    public static final int nrCols = 10;

    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public EmgDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.myContext = context;

         ///data/user/0/integrum.bioniclimbcontroller/databases/

        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }


    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table EMGData " +
                                "(id integer primary key," +
                                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                                "ch1 text," +
                                "ch2 text," +
                                "ch3 text," +
                                "ch4 text," +
                                "ch5 text," +
                                "ch6 text," +
                                "ch7 text," +
                                "ch8 text)"
        );
    }

    public boolean insertEMGdata  (String valueCh1, String valueCh2,String valueCh3,String valueCh4,String valueCh5,String valueCh6,String valueCh7,String valueCh8)
    {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            //Store into contentValues object
            contentValues.put("ch1", valueCh1);
            contentValues.put("ch2", valueCh2);
            contentValues.put("ch3", valueCh3);
            contentValues.put("ch4", valueCh4);
            contentValues.put("ch5", valueCh5);
            contentValues.put("ch6", valueCh6);
            contentValues.put("ch7", valueCh7);
            contentValues.put("ch8", valueCh8);

            // Insert into database
            sqLiteDatabase.insert(EMG_TABLE_NAME, null, contentValues);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    public boolean deleteDataBase(Context context ){
        context.deleteDatabase(DATABASE_NAME);
        return true;
    }

    public int numberOfRows(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(sqLiteDatabase, EMG_TABLE_NAME);
        return numRows;
    }

    public long getDataBaseSize(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        long size = new File(sqLiteDatabase.getPath()).length();
        return size;
    }

    public  String[][] getAllLogs()
    {
        String[][] result= new String[numberOfRows()][nrCols];
        int count=0;
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from EMGData", null);
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            String timestamp = res.getString(res.getColumnIndex(EMG_COLUMN_TIMESTAMP));
            String ch1 = res.getString(res.getColumnIndex(EMG_COLUMN_VALUE_CHANNEL_1));
            String ch2 = res.getString(res.getColumnIndex(EMG_COLUMN_VALUE_CHANNEL_2));
            String ch3 = res.getString(res.getColumnIndex(EMG_COLUMN_VALUE_CHANNEL_3));
            String ch4 = res.getString(res.getColumnIndex(EMG_COLUMN_VALUE_CHANNEL_4));
            String ch5 = res.getString(res.getColumnIndex(EMG_COLUMN_VALUE_CHANNEL_5));
            String ch6 = res.getString(res.getColumnIndex(EMG_COLUMN_VALUE_CHANNEL_6));
            String ch7 = res.getString(res.getColumnIndex(EMG_COLUMN_VALUE_CHANNEL_7));
            String ch8 = res.getString(res.getColumnIndex(EMG_COLUMN_VALUE_CHANNEL_8));

            result[count][0]=timestamp;
            result[count][1]=ch1;
            result[count][2]=ch2;
            result[count][3]=ch3;
            result[count][4]=ch4;
            result[count][5]=ch5;
            result[count][6]=ch6;
            result[count][7]=ch7;
            result[count][8]=ch8;
            res.moveToNext();
            count++;
        }
        res.close();
        return result;
    }

    public String[]getLatestData(){
        String[] result= new String[nrCols+1];
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM EMGData ORDER BY id DESC LIMIT 1", null);
        try {
            res.moveToFirst();
            String id = res.getString(res.getColumnIndex(EMG_COLUMN_ID));
            String timestamp = res.getString(res.getColumnIndex(EMG_COLUMN_TIMESTAMP));
            String ch1 = res.getString(res.getColumnIndex(EMG_COLUMN_VALUE_CHANNEL_1));
            String ch2 = res.getString(res.getColumnIndex(EMG_COLUMN_VALUE_CHANNEL_2));
            String ch3 = res.getString(res.getColumnIndex(EMG_COLUMN_VALUE_CHANNEL_3));
            String ch4 = res.getString(res.getColumnIndex(EMG_COLUMN_VALUE_CHANNEL_4));
            String ch5 = res.getString(res.getColumnIndex(EMG_COLUMN_VALUE_CHANNEL_5));
            String ch6 = res.getString(res.getColumnIndex(EMG_COLUMN_VALUE_CHANNEL_6));
            String ch7 = res.getString(res.getColumnIndex(EMG_COLUMN_VALUE_CHANNEL_7));
            String ch8 = res.getString(res.getColumnIndex(EMG_COLUMN_VALUE_CHANNEL_8));

            result[0]=id;
            result[1]=timestamp;
            result[2]=ch1;
            result[3]=ch2;
            result[4]=ch3;
            result[5]=ch4;
            result[6]=ch5;
            result[7]=ch6;
            result[8]=ch7;
            result[9]=ch8;
        }
                catch (Exception e) {
                    result=null;
            }
        res.close();
        return result;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS EMGData");
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
