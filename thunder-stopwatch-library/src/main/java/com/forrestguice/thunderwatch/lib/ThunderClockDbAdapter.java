/**
 Copyright (C) 2012 Forrest Guice
 This file is part of Thunder-Stopwatch.

 Thunder-Stopwatch is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Thunder-Stopwatch is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Thunder-Stopwatch.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.forrestguice.thunderwatch.lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ThunderClockDbAdapter
{
   private final Context context;
   private SQLiteDatabase database;
   private DatabaseHelper databaseHelper;

   private static final String DATABASE_NAME = "thunderclock";
   private static final String DATABASE_TABLE = "log";
   private static final int DATABASE_VERSION = 2;

   public static final String KEY_ROWID = "_id";
   public static final String KEY_START = "start";
   public static final String KEY_ELAPSED = "elapsed";

   private static final String DATABASE_CREATE =
        "create table log (_id integer primary key autoincrement, "
        + "start text not null, elapsed text not null);";

   private static class DatabaseHelper extends SQLiteOpenHelper 
   {
      DatabaseHelper(Context context) 
      {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }

      @Override
      public void onCreate(SQLiteDatabase db) 
      {
         db.execSQL(DATABASE_CREATE);
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
      {
         //Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
         //      + newVersion + ", which will destroy all old data");
         db.execSQL("DROP TABLE IF EXISTS log");
         onCreate(db);
      }
   }

   //////////////////////////////////////////////////
   // Constructor( context : Context )
   //////////////////////////////////////////////////
   public ThunderClockDbAdapter(Context context)
   {
      this.context = context;
   }

   //////////////////////////////////////////////////
   // open()  :  ThunderClockDbAdapter
   //////////////////////////////////////////////////
   /**
      Open the database. If it cannot be opened, try to create a new
      instance of the database. If it cannot be created, throw an exception to
      signal the failure
      
      @throws SQLException if the database could be neither opened or created
   */
   public ThunderClockDbAdapter open() throws SQLException 
   {
   	  if (databaseHelper != null) databaseHelper.close();
	  databaseHelper = new DatabaseHelper(context);
  	  database = databaseHelper.getWritableDatabase();
      return this;
   }

   //////////////////////////////////////////////////
   // close()  :  void
   //////////////////////////////////////////////////
   public void close() 
   {
      databaseHelper.close();
      database = null;
   }
   
   //////////////////////////////////////////////////
   // getEntryCount()
   //////////////////////////////////////////////////
   public int getEntryCount()
   {
	   Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM " + DATABASE_TABLE, null);
	   cursor.moveToFirst();
	   int count = cursor.getInt(0);
	   cursor.close();
	   return count;
   }

   //////////////////////////////////////////////////
   // createEntry()  :  long
   //////////////////////////////////////////////////
   /**
      Create a new entry using the title and body provided. If the entry is
      successfully created return the new rowId for that entry, otherwise return
      a -1 to indicate failure.
      
      @param start the start time for entry
      @param elapsed the elapsed time for entry
      @return row# or -1 if failed
   */
   public long createEntry(String start, String elapsed) 
   {
      ContentValues values = new ContentValues();
      values.put(KEY_START, start);
      values.put(KEY_ELAPSED, elapsed);

      return database.insert(DATABASE_TABLE, null, values);
   }

   //////////////////////////////////////////////////
   // clearEntries()  :  boolean
   //////////////////////////////////////////////////
   public boolean clearEntries()
   {
      return database.delete(DATABASE_TABLE, null, null) > 0;
   }

   //////////////////////////////////////////////////
   // deleteEntry( row : long )  :  boolean
   //////////////////////////////////////////////////
   /**
      Delete the given row.
      @param row id of entry to delete
      @return true if deleted; false otherwise
   */
   public boolean deleteEntry(long row) 
   {
      return database.delete(DATABASE_TABLE, KEY_ROWID + "=" + row, null) > 0;
   }
   
   public boolean deleteOldestEntry()
   {
	   long firstId = -1;
	   boolean result = false;
	   String query = "SELECT " + KEY_ROWID + " from " + DATABASE_TABLE + " order by " + KEY_ROWID + " ASC limit 1";
	   Cursor cursor = database.rawQuery(query, null);
	   if (cursor != null && cursor.moveToFirst()) firstId = cursor.getLong(0);
	   cursor.close();
	   if (firstId >= 0) result = deleteEntry(firstId);
	   return result;
   }
   
   public Cursor fetchLastEntry()
   {
	   long lastId = -1;
	   String query = "SELECT " + KEY_ROWID + " from " + DATABASE_TABLE + " order by " + KEY_ROWID + " DESC limit 1";
	   Cursor cursor = database.rawQuery(query, null);
	   if (cursor != null && cursor.moveToFirst()) lastId = cursor.getLong(0);
	   cursor.close();
	   
	   Cursor result = null;
	   if (lastId >= 0) result = fetchEntry(lastId);
	   return result;
   }

   //////////////////////////////////////////////////
   // fetchAllEntries()  :  Cursor
   //////////////////////////////////////////////////
   /**
      Return a Cursor over the list of all entries in the database
      @return Cursor over all entries
   */
   public Cursor fetchAllEntries(int n) 
   {     
	   return database.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_START,
                            KEY_ELAPSED}, null, null, null, null, "_id DESC", n+"");
   }
   public Cursor fetchAllEntries() 
   {     
	   return database.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_START,
                            KEY_ELAPSED}, null, null, null, null, "_id DESC");
   }

   //////////////////////////////////////////////////
   // fetchEntry( row : long )  :  Cursor
   //////////////////////////////////////////////////
   /**
      @param rowId id of entry
      @return Cursor positioned to matching entry
      @throws SQLException if entry could not be found/retrieved
   */
   public Cursor fetchEntry(long row) throws SQLException 
   {
      Cursor cursor = database.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                KEY_START, KEY_ELAPSED}, KEY_ROWID + "=" + row, null,
                null, null, null, null);

      if (cursor != null) 
      {
         cursor.moveToFirst();
      }

      return cursor;
   }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    //public boolean updateNote(long rowId, String title, String body) {
    //    ContentValues args = new ContentValues();
    //    args.put(KEY_TITLE, title);
    //    args.put(KEY_BODY, body);

    //    return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    //}
}
