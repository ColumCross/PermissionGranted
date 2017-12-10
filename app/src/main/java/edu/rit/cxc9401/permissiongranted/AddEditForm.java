// AddEditContact.java
// Activity for adding a new entry to or  
// editing an existing entry in the address book.
package edu.rit.cxc9401.permissiongranted;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddEditForm extends AppCompatActivity {
    private long rowID; // id of contact being edited, if any

    // EditTexts for form information
    private EditText nameEditText;
    private EditText formEditText;
    private EditText creatorEditText;

    /**
     * called when the Activity is first started
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // call super's onCreate
        setContentView(R.layout.activity_new_form); // inflate the UI
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameEditText = (EditText) findViewById(R.id.editFormName);
        formEditText = (EditText) findViewById(R.id.editFormText);
        creatorEditText = (EditText) findViewById(R.id.nf_creatorName);

        Bundle extras = getIntent().getExtras(); // get Bundle of extras
        // if there are extras, use them to populate the EditTexts
        if (extras != null) {
            rowID = extras.getLong("row_id");
            nameEditText.setText(extras.getString("name"));
            formEditText.setText(extras.getString("body"));
            creatorEditText.setText(extras.getString("creator"));

        }

        // set event listener for the Save Contact Button
        Button saveContactButton =
                (Button) findViewById(R.id.saveFormButton);
        saveContactButton.setOnClickListener(saveContactButtonClicked);

    }

    /**
     * responds to event generated when user clicks the Done Button
     */
    OnClickListener saveContactButtonClicked = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (nameEditText.getText().length() != 0) {
                AsyncTask<Object, Object, Object> saveContactTask =
                        new AsyncTask<Object, Object, Object>() {
                            @Override
                            protected Object doInBackground(Object... params) {
                                saveToDB();
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object result) {
                                finish(); // return to the previous Activity
                            }
                        };

                // save the contact to the database using a separate thread
                saveContactTask.execute((Object[]) null);
            } else {
                // create a new AlertDialog Builder
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(AddEditForm.this);

                // set dialog title & message, and provide Button to dismiss
                builder.setTitle(R.string.errorTitle);
                builder.setMessage(R.string.errorMessage);
                builder.setPositiveButton(R.string.errorButton, null);
                builder.show(); // display the Dialog
            }
        } // end method onClick
    };

    /**
     * saves contact information to the database
     */
    private void saveToDB() {
        // get DatabaseConnector to interact with the SQLite database
        DatabaseConnector databaseConnector = new DatabaseConnector(this);
        if (getIntent().getExtras() == null) {
            databaseConnector.insertForm(nameEditText.getText().toString(), formEditText.getText().toString(), creatorEditText.getText().toString());
        } else {
            databaseConnector.editForm(rowID, nameEditText.getText().toString(), formEditText.getText().toString(), creatorEditText.getText().toString());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
