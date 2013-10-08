package org.daniel.android.workbanch.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 统一存储的数据库提供类
 * 
 * @author jiaoyang<br>
 *         email: yangjiao623@gmail.com
 * @date Oct 8, 2013 9:31:08 AM
 * @version 1.0
 * @param <T>
 */
public class DatabaseProvider<T extends DatabaseProvider.BaseBean> extends AbsDataProvider<T> {
	private static SQLiteDatabase mDatabase;
	/** 数据库名称 */
	public static final String DATABASE_NAME = "universal.db";
	/** 数据库版本 */
	public static final int DATABASE_VERSION = 1;

	private static final String KEY_ID = "_uid";
	private static final String KEY_CONTENT = "_content";
	private static final String WHERE_PREFIX = KEY_ID + "=";

	private final String TABLE_NAME;

	public DatabaseProvider(Context context, Class<?> clazz) {
		// 初始化线程数据
		super();
		synchronized (DatabaseProvider.class) {
			if (mDatabase == null) {
				mDatabase = new SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
					@Override
					public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
					}

					@Override
					public void onCreate(SQLiteDatabase db) {
					}
				}.getWritableDatabase();
			}

			TABLE_NAME = clazz.getSimpleName();
			// 查看表是否存在
			Cursor cursor = mDatabase.query("sqlite_master", new String[] { "name" }, "name=?",
					new String[] { TABLE_NAME }, null, null, null);

			if (cursor.getCount() <= 0) {
				// 创建表
				String sqlCreate = new StringBuilder("create table ").append(TABLE_NAME).append(" (").append(KEY_ID)
						.append(" integer primary key autoincrement, ").append(KEY_CONTENT).append(" blob)").toString();
				mDatabase.execSQL(sqlCreate);
			}
			cursor.close();
		}
	}

	@Override
	protected void createItem(T item) {
		System.out.println("insert: " + item);
		mDatabase.insert(TABLE_NAME, null, getContentValues(item));
	}

	@Override
	protected void removeItem(T item) {
		System.out.println("remove: " + item);
		mDatabase.delete(TABLE_NAME, getWhere(item), null);
	}

	@Override
	protected void updateItem(T item) {
		System.out.println("update: " + item);
		mDatabase.update(TABLE_NAME, getContentValues(item), getWhere(item), null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<T> queryItems() {
		Cursor cursor = mDatabase.query(TABLE_NAME, new String[] { KEY_CONTENT, KEY_ID }, null, null, null, null,
				KEY_ID);
		List list = new ArrayList();
		while (cursor.moveToNext()) {
			T t = (T) byte2Object(cursor.getBlob(0));
			t.setUid(cursor.getLong(1));
			list.add(t);
		}

		System.out.println("query: " + list);

		return list;
	}

	private String getWhere(T item) {
		return WHERE_PREFIX + item.getUid();
	}

	private ContentValues getContentValues(T item) {
		ContentValues values = new ContentValues();

		values.put(KEY_CONTENT, object2Byte(item));

		return values;
	}

	private byte[] object2Byte(Object obj) {
		byte[] bytes = null;
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);

			bytes = bo.toByteArray();

			bo.close();
			oo.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bytes;
	}

	private Object byte2Object(byte[] bytes) {
		Object obj = null;
		try {
			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
			ObjectInputStream oi = new ObjectInputStream(bi);

			obj = oi.readObject();

			bi.close();
			oi.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	public static class BaseBean implements Serializable {
		private static final long serialVersionUID = 1L;
		/** 数据库中的自增长数据，可以做为索引 */
		protected long uid;

		public long getUid() {
			return uid;
		}

		public void setUid(long uid) {
			this.uid = uid;
		}

	}
}
