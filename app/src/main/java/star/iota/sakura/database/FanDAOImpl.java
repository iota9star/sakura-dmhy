package star.iota.sakura.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import star.iota.sakura.ui.fans.bean.FanBean;
import star.iota.sakura.ui.fans.bean.SubBean;

public class FanDAOImpl implements FanDAO {
    private final DBHelper mHelper;

    public FanDAOImpl(Context context) {
        mHelper = DBHelper.getInstance(context);
    }

    @Override
    public boolean save(FanBean bean) {
        boolean success = true;
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("cover", bean.getCover());
            values.put("name", bean.getName());
            values.put("keyword", bean.getKeyword());
            values.put("official", bean.getOfficial());
            values.put("subs", new Gson().toJson(bean.getSubs()));
            db.insertOrThrow(DBHelper.TABLE_NAME_FAN, null, values);
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    @Override
    public boolean save(List<FanBean> beans) {
        boolean success = true;
        for (FanBean next : beans) {
            if (!exist(next)) {
                if (!save(next)) {
                    success = false;
                }
            }
        }
        return success;
    }

    @Override
    public boolean delete(FanBean bean) {
        boolean success = true;
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.beginTransaction();
            db.delete(DBHelper.TABLE_NAME_FAN, "cover = ? and name = ? and keyword = ? and official = ?",
                    new String[]{bean.getCover(), bean.getName(), bean.getKeyword(), bean.getOfficial()});
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    @Override
    public boolean deleteAll() {
        boolean success = true;
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.beginTransaction();
            db.execSQL("delete from " + DBHelper.TABLE_NAME_FAN);
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    @Override
    public boolean exist(FanBean bean) {
        boolean exist = false;
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME_FAN + " where cover = ? and name = ? and keyword = ? and official = ?",
                    new String[]{bean.getCover(), bean.getName(), bean.getKeyword(), bean.getOfficial()});
            if (cursor.moveToNext()) {
                exist = true;
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exist;
    }

    @Override
    public List<FanBean> query() {
        List<FanBean> fans = new ArrayList<>();
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME_FAN, null);
            while (cursor.moveToNext()) {
                FanBean fan = new FanBean();
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String cover = cursor.getString(cursor.getColumnIndex("cover"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String keyword = cursor.getString(cursor.getColumnIndex("keyword"));
                String official = cursor.getString(cursor.getColumnIndex("official"));
                String subs = cursor.getString(cursor.getColumnIndex("subs"));
                fan.setId(id);
                fan.setCover(cover);
                fan.setName(name);
                fan.setKeyword(keyword);
                fan.setOfficial(official);
                fan.setSubs((List<SubBean>) new Gson().fromJson(subs, new TypeToken<List<SubBean>>() {
                }.getType()));
                fans.add(fan);
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fans;
    }
}
