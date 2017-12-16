// DatabaseConnector.java
// Provides easy connection and creation of UserContacts database.
package edu.rit.cxc9401.permissiongranted;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

// IF THIS IS HERE THE GIT PROPERLY WORKED.

public class DatabaseConnector {

   private static final String DATABASE_NAME = "PermissionGranted"; // database name
    private static final String FORM_TABLE_NAME = "forms";
    private static final String SIGS_TABLE_NAME = "signatures";

   private SQLiteDatabase database; // database object
   private DatabaseOpenHelper databaseOpenHelper; // database helper
    private Context callingContext;

   // public constructor for DatabaseConnector
   public DatabaseConnector(Context context)
   {
      // create a new DatabaseOpenHelper
      databaseOpenHelper = 
         new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
      callingContext = context;
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
   public void insertForm(String name, String text, String creator) {
      ContentValues newContact = new ContentValues();
      newContact.put("name", name);
      newContact.put("body", text);
      newContact.put("creator", creator);

      Date creationDate = new Date();
      newContact.put("dateCreated", creationDate.toString());

      String insertMessage = name+" '"+text+"' created by "+creator+" on "+creationDate;
      Log.i("Insert new form", insertMessage);
      open(); // open the database
      database.insert("forms", null, newContact);
      close(); // close the database
   } // end method insertForm

   // inserts a new contact in the database
   public void insertSignature(String name, String email, String formText, String creatorName, String dateCreated)
   {
      ContentValues newContact = new ContentValues();
      newContact.put("name", name);
      newContact.put("email", email);
      newContact.put("formText", formText);
      newContact.put("formCreator", creatorName);
      newContact.put("creationDate", dateCreated);
      newContact.put("dateSigned", new Date().toString());


      Log.i("##############Insert", name);
      open(); // open the database
      database.insert("signatures", null, newContact);
      close(); // close the database
   } // end method insertContact

   // Edits a form in the database
   public void editForm(long id, String name, String body, String creator) {
      ContentValues editContact = new ContentValues();
      editContact.put("name", name);
      editContact.put("body", body);
      editContact.put("creator", creator);

      editContact.put("dateCreated", new Date().toString());

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

    /**
     * Deletes the requested item from the internal database. //TODO: Allow for the deletion of signatures
     * @param id The id of the LIST to be deleted.
     * @param message The name of the grammatical object in the deletion dialog.
     */
   public void deleteItem(long id, String message) {

       // create a new AlertDialog Builder
       AlertDialog.Builder builder =
               new AlertDialog.Builder(callingContext);

       builder.setTitle("Delete "+message+"?"); // title bar string
       builder.setMessage("Are you sure you want to delete "+message+"? This action is permanent and cannot be undone."); // message to display

       // provide an OK button that simply dismisses the dialog
       builder.setPositiveButton(R.string.button_delete,
               new DialogInterface.OnClickListener()
               {
                   @Override
                   public void onClick(DialogInterface dialog, int button)
                   {
                       String tableToDeleteFrom;
                       if(callingContext.getClass() == ViewForm.class) {
                           Log.i("CONTEXT SWITCH", "It's a form");
                           tableToDeleteFrom = FORM_TABLE_NAME;
                       } else if(callingContext.getClass() == ViewSignature.class) {
                           Log.i("CONTEXT SWITCH", "It's a signature");
                           tableToDeleteFrom = SIGS_TABLE_NAME;
                       } else {
                           Log.i("CONTEXT SWITCH", "It's NOTHING!!!");
                           throw new RuntimeException("Bad calling class");
                       }
                       open(); // open the database
                       database.delete(tableToDeleteFrom, "_id=" + id, null);
                       close(); // close the database
                       ((Activity)callingContext).finish();
//                       callingContext.finish();
                   } // end method onClick
               } // end anonymous inner class
       ); // end call to method setPositiveButton

       builder.setNegativeButton(R.string.button_cancel, null);
       builder.show(); // display the Dialog
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

         String createFormsTable = "CREATE TABLE forms(_id integer primary key autoincrement, name TEXT, body TEXT, creator TEXT, dateCreated DATE);";
          String createSigsTable = "CREATE TABLE signatures(_id integer primary key autoincrement, name TEXT, email TEXT, formText TEXT, formCreator TEXT, creationDate DATE, dateSigned DATE);";
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

