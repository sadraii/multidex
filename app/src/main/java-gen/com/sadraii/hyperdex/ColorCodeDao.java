package com.sadraii.hyperdex;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.sadraii.hyperdex.ColorCode;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table COLOR_CODE.
*/
public class ColorCodeDao extends AbstractDao<ColorCode, Long> {

    public static final String TABLENAME = "COLOR_CODE";

    /**
     * Properties of entity ColorCode.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Argb = new Property(1, String.class, "argb", false, "ARGB");
        public final static Property Task = new Property(2, String.class, "task", false, "TASK");
    };


    public ColorCodeDao(DaoConfig config) {
        super(config);
    }
    
    public ColorCodeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'COLOR_CODE' (" + //
                "'_id' INTEGER PRIMARY KEY UNIQUE ," + // 0: id
                "'ARGB' TEXT," + // 1: argb
                "'TASK' TEXT);"); // 2: task
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'COLOR_CODE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ColorCode entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String argb = entity.getArgb();
        if (argb != null) {
            stmt.bindString(2, argb);
        }
 
        String task = entity.getTask();
        if (task != null) {
            stmt.bindString(3, task);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ColorCode readEntity(Cursor cursor, int offset) {
        ColorCode entity = new ColorCode( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // argb
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // task
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ColorCode entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setArgb(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTask(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ColorCode entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ColorCode entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
