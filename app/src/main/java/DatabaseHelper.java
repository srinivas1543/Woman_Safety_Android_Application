package com.example.womensafe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database information
    private static final String DATABASE_NAME = "WomenSafe.db";
    private static final int DATABASE_VERSION = 1;

    // Table and columns
    private static final String TABLE_CONTACTS = "contacts";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CONTACT = "contact";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL query to create the contacts table
        String createTable = "CREATE TABLE " + TABLE_CONTACTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CONTACT + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the table if it exists and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    /**
     * Adds a new contact to the database.
     *
     * @param contact The contact to be added.
     * @return True if the contact was added successfully, false otherwise.
     */
    public boolean addContact(String contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACT, contact);

        long result = db.insert(TABLE_CONTACTS, null, values);
        return result != -1; // If result == -1, insertion failed
    }

    /**
     * Retrieves all contacts from the database.
     *
     * @return A list of all saved contacts.
     */
    public ArrayList<String> getAllContacts() {
        ArrayList<String> contacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);
        if (cursor.moveToFirst()) {
            do {
                contacts.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contacts;
    }

    /**
     * Deletes a contact from the database.
     *
     * @param contact The contact to be deleted.
     * @return True if the contact was deleted successfully, false otherwise.
     */
    public boolean deleteContact(String contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CONTACTS, COLUMN_CONTACT + "=?", new String[]{contact}) > 0;
    }
}
