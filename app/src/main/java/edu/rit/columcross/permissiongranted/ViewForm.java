package edu.rit.columcross.permissiongranted;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;

public class ViewForm extends AppCompatActivity {

    private long rowID; // selected forms ID
    private TextView formBodyTextView; // displays forms's text
    private EditText signeeName; // The name of the person signing
    private EditText signeeEmail; // The email of the person signing
    private String formName;
    private TextView creator;
    private TextView createdDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_form);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        formBodyTextView = (TextView) findViewById(R.id.vf_formBody);
        creator = (TextView) findViewById(R.id.vf_creatorName);

        // get the selected contact's row ID
        Bundle extras = getIntent().getExtras();
        rowID = extras.getLong(FormsActivity.ROW_ID);

        signeeName = (EditText) findViewById(R.id.vf_nameEditText);
        signeeEmail = (EditText) findViewById(R.id.vf_email);

        // set event listener for the Save Contact Button
        Button consentButton = (Button) findViewById(R.id.vf_consentButton);
        consentButton.setOnClickListener(formSigned);
    }


    // called when the activity is first created
    @Override
    protected void onResume() {
        super.onResume();

        // create new LoadContactTask and execute it
        new LoadContactTask().execute(rowID);
    } // end method onResume

    // performs database query outside GUI thread
    private class LoadContactTask extends AsyncTask<Long, Object, Cursor> {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(ViewForm.this);

        // perform the database access
        @Override
        protected Cursor doInBackground(Long... params) {
            databaseConnector.open();

            // get a cursor containing all data on given entry
            return databaseConnector.getOne("forms", params[0]);
        } // end method doInBackground

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);

            result.moveToFirst(); // move to the first item

            // get the column index for each data item
            int nameIndex = result.getColumnIndex("name");
            int bodyIndex = result.getColumnIndex("body");
            int creatorIndex = result.getColumnIndex("creator");

            formName = result.getString(nameIndex);
            String creationDate = result.getString(result.getColumnIndex("dateCreated"));

            // fill TextViews with the retrieved data
            setTitle(formName);
            formBodyTextView.setText(result.getString(bodyIndex));
            creator.setText(result.getString(creatorIndex));

            createdDate = (TextView) findViewById(R.id.vf_createdDate);
            createdDate.setText(creationDate);

            result.close(); // close the result cursor
            databaseConnector.close(); // close database connection
        } // end method onPostExecute
    } // end class LoadContactTask


    // responds to event generated when user clicks the Done Button
    View.OnClickListener formSigned = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (signeeName.getText().length() != 0)
            {
                AsyncTask<Object, Object, Object> saveContactTask =
                        new AsyncTask<Object, Object, Object>()
                        {
                            @Override
                            protected Object doInBackground(Object... params)
                            {
                                signContract(); // save contact to the database
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
                        new AlertDialog.Builder(ViewForm.this);

                // set dialog title & message, and provide Button to dismiss
                builder.setTitle(R.string.errorTitle);
                builder.setMessage(R.string.errorMessage);
                builder.setPositiveButton(R.string.errorButton, null);
                builder.show(); // display the Dialog
            } // end else
        } // end method onClick
    }; // end OnClickListener saveContactButtonClicked

    // saves contact information to the database
    private void signContract() {
        // get DatabaseConnector to interact with the SQLite database
        String name = signeeName.getText().toString();
        String conditions = formBodyTextView.getText().toString();
        String email = signeeEmail.getText().toString();

        DatabaseConnector databaseConnector = new DatabaseConnector(this);
        databaseConnector.insertSignature(name, email, conditions, creator.getText().toString(), createdDate.getText().toString());

        // Display the order summary on the screen
        String message = createOrderSummary(name, conditions);

        // Use an intent to launch an email app.
        // Send the order summary in the email body.
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"+email)); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT,
                "The consent form you signed");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        //startActivity(Intent.createChooser(intent, "Test chooser to make sure the intent is firing."));
        startActivity(intent);
    }


    // create the Activity's menu from a menu resource XML file
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_form_menu, menu);
        return true;
    } // end method onCreateOptionsMenu

    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) // switch based on selected MenuItem's ID
        {
            case R.id.action_editForm:
                // create an Intent to launch the AddEditContact Activity
                Intent addEditContact =
                        new Intent(this, AddEditForm.class);

                // pass the selected contact's data as extras with the Intent
                addEditContact.putExtra(FormsActivity.ROW_ID, rowID);
                addEditContact.putExtra("name", formName);
                addEditContact.putExtra("body", formBodyTextView.getText());
                startActivity(addEditContact); // start the Activity
                return true;
            case R.id.action_deleteForm:
                deleteContact(); // delete the displayed contact
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        } // end switch
    } // end method onOptionsItemSelected

    // delete a contact
    private void deleteContact()
    {
        // create a new AlertDialog Builder
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ViewForm.this);

        builder.setTitle(R.string.confirmTitle); // title bar string
        builder.setMessage(R.string.confirmMessage); // message to display

        // provide an OK button that simply dismisses the dialog
        builder.setPositiveButton(R.string.button_delete,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int button)
                    {
                        final DatabaseConnector databaseConnector =
                                new DatabaseConnector(ViewForm.this);

                        // create an AsyncTask that deletes the contact in another
                        // thread, then calls finish after the deletion
                        AsyncTask<Long, Object, Object> deleteTask =
                                new AsyncTask<Long, Object, Object>()
                                {
                                    @Override
                                    protected Object doInBackground(Long... params)
                                    {
                                        databaseConnector.deleteContact(params[0]);
                                        return null;
                                    } // end method doInBackground

                                    @Override
                                    protected void onPostExecute(Object result)
                                    {
                                        finish(); // return to the AddressBook Activity
                                    } // end method onPostExecute
                                }; // end new AsyncTask

                        // execute the AsyncTask to delete contact at rowID
                        deleteTask.execute(new Long[] { rowID });
                    } // end method onClick
                } // end anonymous inner class
        ); // end call to method setPositiveButton

        builder.setNegativeButton(R.string.button_cancel, null);
        builder.show(); // display the Dialog
    } // end method deleteContact


    /**
     * Create summary of the order.
     *
     * @param name            of the signee
     * @param form           The terms they agreed to sign
     * @return text summary
     */
    private String createOrderSummary(String name, String form) {
        String emailBody = name + " has agreed to the following terms and conditions set forth by "+creator.getText().toString()+":";
        emailBody += "\n";
        emailBody += "\n";
        emailBody += form;

        return emailBody;
    }
} // end class ViewContact
