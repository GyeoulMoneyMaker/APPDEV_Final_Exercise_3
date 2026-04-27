package com.example.crud_basedappdevelopment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ContactsDB";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone_number";
    public static final String COLUMN_DATE_ADDED = "date_added";
    public static final String COLUMN_IS_FAVORITE = "is_favorite";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_DATE_ADDED + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_CONTACTS + " ADD COLUMN " + COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0");
        }
    }

    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_PHONE, contact.getPhoneNumber());
        values.put(COLUMN_IS_FAVORITE, contact.isFavorite() ? 1 : 0);
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    public List<Contact> getAllContacts(String sortBy) {
        List<Contact> contactList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS + " ORDER BY " + sortBy;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_ADDED)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FAVORITE)) == 1
                );
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contactList;
    }

    public void updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_PHONE, contact.getPhoneNumber());
        values.put(COLUMN_IS_FAVORITE, contact.isFavorite() ? 1 : 0);
        db.update(TABLE_CONTACTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(contact.getId())});
        db.close();
    }

    public void toggleFavorite(int id, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_FAVORITE, isFavorite ? 1 : 0);
        db.update(TABLE_CONTACTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteContact(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Contact> searchContacts(String query, String sortBy) {
        List<Contact> contactList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS + 
                " WHERE " + COLUMN_NAME + " LIKE ? ORDER BY " + sortBy;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{"%" + query + "%"});

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_ADDED)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FAVORITE)) == 1
                );
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contactList;
    }

    public int getContactCount() {
        String countQuery = "SELECT * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
}
