package star.iota.sakura.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class DBHelper extends SQLiteOpenHelper {
    static final String TABLE_NAME_FANS = "fans";
    static final String TABLE_NAME_FAN = "fan";
    private static final String DB_NAME = "collection.db";
    private static final String CREATE_FANS_TABLE = "create table if not exists " + TABLE_NAME_FANS + "(" +
            "id integer primary key autoincrement," +
            "title text," +
            "category text," +
            "sub text," +
            "magnet text," +
            "size text," +
            "url text," +
            "date text" +
            ")";
    private static final String CREATE_FAN_TABLE = "create table if not exists " + TABLE_NAME_FAN + "(" +
            "id integer primary key autoincrement," +
            "cover text," +
            "name text," +
            "keyword text," +
            "official text," +
            "subs text" +
            ")";

    private static DBHelper mHelper;

    private DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    static DBHelper getInstance(Context context) {
        if (mHelper == null) {
            synchronized (DBHelper.class) {
                if (mHelper == null) {
                    mHelper = new DBHelper(context);
                }
            }
        }
        return mHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FANS_TABLE);
        db.execSQL(CREATE_FAN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
