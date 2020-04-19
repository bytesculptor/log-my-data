package com.bytesculptor.logmydata.database;

/*
  Created by r0 on 22.02.18
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bytesculptor.logmydata.Constants;


public class DbHandler extends SQLiteOpenHelper {

    private static final String TAG = DbHandler.class.getSimpleName();

    private Context dbContext;

    // name and version of the data base
    private static final String DATABASE_NAME = "logMyData.db";
    private static final int DATABASE_VERSION = 1;

    // name and attributes of the main table
    public static final String _ID = "_id";
    public static final String MAIN_TABLE = "LogTable";
    public static final String LOGBOOK = "logbookName";
    public static final String TABLE_HASH = "tableHash";
    public static final String OPTION_1_NAME = "option1_name";
    public static final String OPTION_2_NAME = "option2_name";
    public static final String TYPE = "type";

    public static final String COMMA = ", ";
    public static final String TEXT = " TEXT ";
    public static final String NUMERIC = " NUMERIC ";
    public static final String INT_1 = " INT(1) ";
    public static final String NOT_NULL = " NOT NULL ";
    public static final String DEFAULT_0 = " DEFAULT 0 ";
    public static final String PRIMARY_KEY = " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL";


    // name and attributes of the logbook tables
    public static final String TIMESTAMP = "timestamp";
    public static final String OPTION_1 = "option1";
    public static final String OPTION_2 = "option2";
    public static final String TYPE_NUMBER = "typeNumber";
    public static final String TYPE_TIME = "typeTime";
    public static final String TYPE_TEXT = "typeText";
    public static final String COMMENT = "comment";


    private static final String SQL_TABLE_CREATE = "CREATE TABLE "
            + MAIN_TABLE
            + " ("
            + _ID + PRIMARY_KEY + COMMA
            + TYPE + NUMERIC + NOT_NULL + DEFAULT_0 + COMMA
            + LOGBOOK + TEXT + NOT_NULL + COMMA
            + TABLE_HASH + NUMERIC + NOT_NULL + COMMA
            + OPTION_1_NAME + TEXT + COMMA
            + OPTION_2_NAME + TEXT + ");";


    public DbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dbContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_TABLE_CREATE);
        Log.d("DbHandler", "main table created: " + SQL_TABLE_CREATE);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrade database from  version " + oldVersion + " to " + newVersion);
    }


    public boolean createNewLogbookTable(String logbookName, String logbookHash, Constants.logType type,
                                         boolean bOptionCheck1, String szOptionName1,
                                         boolean bOptionCheck2, String szOptionName2) {

        int bookType = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);

        StringBuilder szCreate = new StringBuilder();
        szCreate.append("CREATE TABLE ")
                .append(logbookHash)
                .append(" (")
                .append(_ID).append(PRIMARY_KEY).append(COMMA)
                .append(TIMESTAMP + NUMERIC).append(COMMA)
                .append(COMMENT + TEXT + NOT_NULL).append(COMMA);

        switch (type) {
            case TEXT:
                szCreate.append(TYPE_TEXT + TEXT);
                bookType = 0;
                break;

            case NUMBER:
                szCreate.append(TYPE_NUMBER + NUMERIC);
                bookType = 1;
                break;

            case TIME:
                szCreate.append(TYPE_TIME + NUMERIC);
                bookType = 2;
                break;
        }

        if (bOptionCheck1) {
            szCreate.append(COMMA).append(OPTION_1).append(INT_1).append(DEFAULT_0);

            if (bOptionCheck2) {
                szCreate.append(COMMA).append(OPTION_2).append(INT_1).append(DEFAULT_0);
            }
        }
        szCreate.append(");");
        Log.d("DbHandler", "create new table string: " + szCreate);

        boolean bResult;
        try {
            db.execSQL(szCreate.toString());
            bResult = true;
            Log.d(TAG, "createNewLogbookTable: new table created: " + logbookHash);
        } catch (SQLiteException e) {
            bResult = false;
        }
        db.close();

        // add entry in main table
        long rowId;
        try {
            if (bResult) {
                db = getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(LOGBOOK, logbookName);
                values.put(TYPE, bookType);
                values.put(TABLE_HASH, logbookHash);
                if (bOptionCheck1) values.put(OPTION_1_NAME, szOptionName1);
                if (bOptionCheck2) values.put(OPTION_2_NAME, szOptionName2);
                rowId = db.insert(MAIN_TABLE, null, values);
                Log.d(TAG, "createNewLogbookTable: inserted " + logbookName + ", rowId " + rowId);
            }
        } catch (Exception e) {
            Log.d(TAG, "createNewLogbookTable: inserted failed: " + e);
        } finally {
            db.close();
        }
        return bResult;
    }


    public Cursor queryAllLogbookNameFromTable(String sort) {
        SQLiteDatabase db = getWritableDatabase();
        return db.query(true, MAIN_TABLE, new String[]{LOGBOOK, _ID}, null, null, null, null, LOGBOOK + " COLLATE NOCASE " + sort, null);
    }


    public Cursor queryAllFromTable(String table) {
        SQLiteDatabase db = getWritableDatabase();
        //return db.query(true, table, new String[]{LOGBOOK, _ID}, null, null, null, null, LOGBOOK + " COLLATE NOCASE ", null);
        //Cursor c = db.execSQL(getHashFromTable(table), );
        Cursor c = db.rawQuery("select * from " + getHashFromTable(table), null);
        //db.close();
        return c;
    }


    public void renameLogbook(String logbookName, String newlogbookName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOGBOOK, newlogbookName);
        db.update(MAIN_TABLE, values, LOGBOOK + " = ?", new String[]{logbookName});
        Log.d(TAG, "renameLogbook =" + newlogbookName);
    }


    public void closeDb() {
        SQLiteDatabase db = getWritableDatabase();
        db.close();
    }

    public void update(String id, String szQ, String szAns, String szHint, String szLogbook) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("BLOCKNAME", szQ);
        values.put("TIME", szAns);
        values.put("COMMENT", szHint);
        values.put("LOGBOOK", szLogbook);
        int numUpdated = db.update(MAIN_TABLE, values, _ID + " = ?",
                new String[]{id});
        Log.d(TAG, "update(): id=" + id + " -> " + numUpdated);
    }

    public boolean insertNewEntryTypeNumeric(String logbook, String value, String comment, boolean bOptionCheck1, boolean bOptionCheck2) {
        SQLiteDatabase db = getWritableDatabase();
        long rowId;
        ContentValues values = new ContentValues();
        values.put(TYPE_NUMBER, value);
        values.put(COMMENT, comment);
        values.put(TIMESTAMP, System.currentTimeMillis());
        if (bOptionCheck1) {
            values.put(OPTION_1_NAME, bOptionCheck1);
        }
        if (bOptionCheck2) {
            values.put(OPTION_2_NAME, bOptionCheck2);
        }
        rowId = db.insert(getHashFromTable(logbook), null, values);
        return true;
    }

    private String getHashFromTable(String logbook) {
        SQLiteDatabase db = getWritableDatabase();
        //db.rawQuery("SELECT " + TABLE_HASH + " FROM " + MAIN_TABLE + " WHERE" , null);
        Cursor c1 = db.query(MAIN_TABLE, new String[]{TABLE_HASH}, LOGBOOK + " = ?", new String[]{logbook}, null, null, null);
        c1.moveToFirst();
        String res = c1.getString(c1.getColumnIndex(DbHandler.TABLE_HASH));
        c1.close();
        return res;
    }

    public void deleteSingleId(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);
        int numDeleted = db.delete(MAIN_TABLE, _ID + " = ?",
                new String[]{Long.toString(id)});
        Log.d(TAG, "deleteSingleId(): id=" + id + " -> " + numDeleted);
    }


    public void deleteLogbookTable(String logbook) {
        SQLiteDatabase db = getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);

        db.execSQL("DROP TABLE IF EXISTS " + logbook);
        int numDeleted = db.delete(MAIN_TABLE, LOGBOOK + " = ?", new String[]{logbook});
        Log.d(TAG, "deleteLogbookTable(): id=" + logbook + " -> " + numDeleted);
    }


    public boolean isOptionOneExisting(String table) {
        SQLiteDatabase db = getWritableDatabase();
        db.rawQuery("SELECT * FROM " + table, null);

        return true;
    }


    public boolean isEntryFieldTime(String table) {
        SQLiteDatabase db = getWritableDatabase();
        //Cursor csr = db.query(true, table,)
        //db.execSQL();
        return true;
    }

}