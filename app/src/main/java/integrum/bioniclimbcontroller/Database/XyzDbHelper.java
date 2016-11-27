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
public class XyzDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "XYZDatabase.db";
    public static String DATABASE_PATH = "";
    public static final String XYZ_TABLE_NAME = "XYZData";
    public static final String XYZ_COLUMN_ID = "id";
    public static final String XYZ_COLUMN_TIMESTAMP = "timestamp";
    public static final String XYZ_COLUMN_ROLL = "roll";
    public static final String XYZ_COLUMN_PITCH = "pitch";
    public static final String XYZ_COLUMN_YAW = "yaw";
    private static final String[] COLUMNS = {XYZ_COLUMN_ID,XYZ_COLUMN_TIMESTAMP,XYZ_COLUMN_ROLL,XYZ_COLUMN_PITCH,XYZ_COLUMN_YAW};

    // Number of columns in returning data array for a request.
    // Format: ID | Timestamp | Roll | Pitch | Yaw
    public static final int nrCols = 5;

    private SQLiteDatabase myDataBase;
    private final Context myContext;


    public XyzDbHelper(Context context) {
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
        sqLiteDatabase.execSQL("create table XYZData " +
                        "(id integer primary key," +
                        "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "roll text," +
                        "pitch text, " +
                        "yaw text)"
        );
    }

    public boolean insertXYZdata  (int roll, int pitch, int yaw)
    {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            //Extract and transform into string
            String rollVal = String.valueOf(roll);
            String pitchVal = String.valueOf(pitch);
            String yawVal =  String.valueOf(yaw);

            //Store into contentValues object
            contentValues.put("roll", rollVal);
            contentValues.put("pitch", pitchVal);
            contentValues.put("yaw", yawVal);

            // Insert into database
            sqLiteDatabase.insert(XYZ_TABLE_NAME, null, contentValues);
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
        int numRows = (int) DatabaseUtils.queryNumEntries(sqLiteDatabase, XYZ_TABLE_NAME);
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
        Cursor res =  db.rawQuery("select * from XYZData", null);
        res.moveToFirst();

        while(res.isAfterLast() == false) {
            String id = res.getString(res.getColumnIndex(XYZ_COLUMN_ID));
            String timestamp = res.getString(res.getColumnIndex(XYZ_COLUMN_TIMESTAMP));
            String roll = res.getString(res.getColumnIndex(XYZ_COLUMN_ROLL));
            String pitch = res.getString(res.getColumnIndex(XYZ_COLUMN_PITCH));
            String yaw = res.getString(res.getColumnIndex(XYZ_COLUMN_YAW));

            result[count][0]=id;
            result[count][1]=timestamp;
            result[count][2]=roll;
            result[count][3]=pitch;
            result[count][4]=yaw;

            res.moveToNext();
            count++;
        }
        res.close();
        return result;
    }

    public String[]getLatestData(){
        String[] result= new String[nrCols];
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM XYZData ORDER BY id DESC LIMIT 1", null);
        try {
            res.moveToFirst();
            String id = res.getString(res.getColumnIndex(XYZ_COLUMN_ID));
            String timestamp = res.getString(res.getColumnIndex(XYZ_COLUMN_TIMESTAMP));
            String roll = res.getString(res.getColumnIndex(XYZ_COLUMN_ROLL));
            String pitch = res.getString(res.getColumnIndex(XYZ_COLUMN_PITCH));
            String yaw = res.getString(res.getColumnIndex(XYZ_COLUMN_YAW));

            result[0]=id;
            result[1]=timestamp;
            result[2]=roll;
            result[3]=pitch;
            result[4]=yaw;

        }
        catch (Exception e) {
            result=null;
        }
        res.close();
        return result;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS XYZData");
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
