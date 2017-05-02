// DatabaseConnector.java
// Provides easy connection and creation of UserContacts database.
package edu.rit.columcross.permissiongranted;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

// IF THIS IS HERE THE GIT PROPERLY WORKED.

public class DatabaseConnector 
{
   // database name
   private static final String DATABASE_NAME = "PermissionGranted";
   private SQLiteDatabase database; // database object
   private DatabaseOpenHelper databaseOpenHelper; // database helper

   // public constructor for DatabaseConnector
   public DatabaseConnector(Context context) 
   {
      // create a new DatabaseOpenHelper
      databaseOpenHelper = 
         new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
   } // end DatabaseConnector constructor

   // open the database connection
   public void open() throws SQLException 
   {
      // create or open a database for reading/writing
      database = databaseOpenHelper.getWritableDatabase();
   } // end method open

   // close the database connection
   public void close() 
   {
      if (database != null)
         database.close(); // close the database connection
   } // end method close

   // inserts a new form in the database
   public void insertForm(String name, String text)
   {
      ContentValues newContact = new ContentValues();
      newContact.put("name", name);
      newContact.put("body", text);

      Log.i("Insert", name);
      open(); // open the database
      database.insert("forms", null, newContact);
      close(); // close the database
   } // end method insertForm

   // inserts a new contact in the database
   public void insertSignature(String name, String email, String form)
   {
      ContentValues newContact = new ContentValues();
      newContact.put("name", name);
      newContact.put("email", email);
      newContact.put("formText", form);

      Log.i("##############Insert", name);
      open(); // open the database
      database.insert("signatures", null, newContact);
      close(); // close the database
   } // end method insertContact

   // Edits a form in the database
   public void editForm(long id, String name, String body)
   {
      ContentValues editContact = new ContentValues();
      editContact.put("name", name);
      editContact.put("body", body);

      open(); // open the database
      database.update("forms", editContact, "_id=" + id, null);
      close(); // close the database
   } // end method updateContact


   /**
    * return a Cursor with all the information in the database for the relevant table.
    *
    * @param table The name of the table to get the info from.
    * @return a Cursor with all the information in the database for the relevant table.
     */
   public Cursor getAll(String table)
   {
       //TODO: Why is this breaking?
      return database.query(table, new String[] {"_id", "name"},
         null, null, null, null, "name", null);
   }

   // get a Cursor containing all information about the contact specified
   // by the given id
   public Cursor getOne(String table, long id)
   {
      return database.query(
         table, null, "_id=" + id, null, null, null, null);
   } // end method getOnContact

   // delete the contact specified by the given String name
   public void deleteContact(long id) 
   {
      open(); // open the database
      database.delete("forms", "_id=" + id, null);
      close(); // close the database
   } // end method deleteContact
   
   private class DatabaseOpenHelper extends SQLiteOpenHelper 
   {
      // public constructor
      public DatabaseOpenHelper(Context context, String name,
         CursorFactory factory, int version) 
      {
         super(context, name, factory, version);
      } // end DatabaseOpenHelper constructor

      // creates the contacts table when the database is created
      @Override
      public void onCreate(SQLiteDatabase db) 
      {

         String createFormsTable = "CREATE TABLE forms(_id integer primary key autoincrement, name TEXT, body TEXT);";
          String createSigsTable = "CREATE TABLE signatures(_id integer primary key autoincrement, name TEXT, email TEXT, formText TEXT);";
          // query to create a new table named contacts
         String createQuery = "CREATE TABLE forms" +
            "(_id integer primary key autoincrement," +
            "name TEXT, body TEXT);" +
                 "CREATE TABLE signatures" +
                 "(_id integer primary key autoincrement," +
                 "name TEXT, email TEXT, formText TEXT);";
                  
         db.execSQL(createFormsTable);
          db.execSQL(createSigsTable);
      } // end method onCreate

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, 
          int newVersion) 
      {
      } // end method onUpgrade
   } // end class DatabaseOpenHelper
} // end class DatabaseConnector

