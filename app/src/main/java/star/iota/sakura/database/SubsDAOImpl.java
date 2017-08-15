package star.iota.sakura.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import star.iota.sakura.ui.post.PostBean;
import star.iota.sakura.ui.post.SubBean;

public class SubsDAOImpl implements SubsDAO {
    private DBHelper mHelper;

    private SubsDAOImpl() {
        throw new UnsupportedOperationException("u can't do it");
    }

    public SubsDAOImpl(Context context) {
        mHelper = DBHelper.getInstance(context);
    }

    @Override
    public boolean save(PostBean bean) {
        boolean success = true;
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("date", bean.getDate());
            contentValues.put("category", bean.getCategory());
            contentValues.put("sub", new Gson().toJson(bean.getSub()));
            contentValues.put("title", bean.getTitle());
            contentValues.put("magnet", bean.getMagnet());
            contentValues.put("size", bean.getSize());
            contentValues.put("url", bean.getUrl());
            db.insertOrThrow(DBHelper.TABLE_NAME_FANS, null, contentValues);
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
    public boolean save(List<PostBean> beans) {
        boolean success = true;
        for (PostBean next : beans) {
            if (!exist(next.getUrl())) {
                if (!save(next)) {
                    success = false;
                }
            }
        }
        return success;
    }

    @Override
    public boolean delete(Integer id) {
        boolean success = true;
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.beginTransaction();
            db.delete(DBHelper.TABLE_NAME_FANS, "id = ?", new String[]{String.valueOf(id)});
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
            db.execSQL("delete from " + DBHelper.TABLE_NAME_FANS);
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
    public boolean exist(String url) {
        boolean exist = false;
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME_FANS + " where url = ?",
                    new String[]{url});
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
    public List<PostBean> query() {
        List<PostBean> posts = new ArrayList<>();
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("select * from " + DBHelper.TABLE_NAME_FANS, null);
            while (cursor.moveToNext()) {
                PostBean post = new PostBean();
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String category = cursor.getString(cursor.getColumnIndex("category"));
                String sub = cursor.getString(cursor.getColumnIndex("sub"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String magnet = cursor.getString(cursor.getColumnIndex("magnet"));
                String size = cursor.getString(cursor.getColumnIndex("size"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                post.setId(id);
                post.setDate(date);
                post.setCategory(category);
                post.setSub(new Gson().fromJson(sub, SubBean.class));
                post.setTitle(title);
                post.setMagnet(magnet);
                post.setSize(size);
                post.setUrl(url);
                posts.add(post);
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }
}
