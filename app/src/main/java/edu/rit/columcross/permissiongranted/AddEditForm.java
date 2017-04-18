// AddEditContact.java
// Activity for adding a new entry to or  
// editing an existing entry in the address book.
package edu.rit.columcross.permissiongranted;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class AddEditForm extends Activity
{
   private long rowID; // id of contact being edited, if any
   
   // EditTexts for contact information
   private EditText nameEditText;
   private EditText formEditText;

   
   // called when the Activity is first started
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState); // call super's onCreate
      setContentView(R.layout.activity_new_form); // inflate the UI

      nameEditText = (EditText) findViewById(R.id.editFormName);
      formEditText = (EditText) findViewById(R.id.editFormText);
      
      Bundle extras = getIntent().getExtras(); // get Bundle of extras

      // if there are extras, use them to populate the EditTexts
      if (extras != null)
      {
         rowID = extras.getLong("row_id");
         nameEditText.setText(extras.getString("name"));  
         formEditText.setText(extras.getString("body"));

      } // end if
      
      // set event listener for the Save Contact Button
      Button saveContactButton = 
         (Button) findViewById(R.id.saveFormButton);
      saveContactButton.setOnClickListener(saveContactButtonClicked);
   } // end method onCreate

   // responds to event generated when user clicks the Done Button
   OnClickListener saveContactButtonClicked = new OnClickListener() 
   {
      @Override
      public void onClick(View v) 
      {
         if (nameEditText.getText().length() != 0)
         {
            AsyncTask<Object, Object, Object> saveContactTask = 
               new AsyncTask<Object, Object, Object>() 
               {
                  @Override
                  protected Object doInBackground(Object... params) 
                  {
                     saveContact(); // save contact to the database
                     return null;
                  } // end method doInBackground
      
                  @Override
                  protected void onPostExecute(Object result) 
                  {
                     finish(); // return to the previous Activity
                  } // end method onPostExecute
               }; // end AsyncTask
               
            // save the contact to the database using a separate thread
            saveContactTask.execute((Object[]) null); 
         } // end if
         else
         {
            // create a new AlertDialog Builder
            AlertDialog.Builder builder = 
               new AlertDialog.Builder(AddEditForm.this);
      
            // set dialog title & message, and provide Button to dismiss
            builder.setTitle(R.string.errorTitle); 
            builder.setMessage(R.string.errorMessage);
            builder.setPositiveButton(R.string.errorButton, null); 
            builder.show(); // display the Dialog
         } // end else
      } // end method onClick
   }; // end OnClickListener saveContactButtonClicked

   // saves contact information to the database
   private void saveContact() 
   {
      // get DatabaseConnector to interact with the SQLite database
      DatabaseConnector databaseConnector = new DatabaseConnector(this);

      if (getIntent().getExtras() == null)
      {

         // insert the contact information into the database - new contact
         databaseConnector.insertForm(
            nameEditText.getText().toString(),
            formEditText.getText().toString()
                 );
      } // end if
      else //edit the contact
      {
         databaseConnector.editForm(rowID,
            nameEditText.getText().toString(),
            formEditText.getText().toString());
      } // end else
   } // end class saveContact
} // end class AddEditContact
