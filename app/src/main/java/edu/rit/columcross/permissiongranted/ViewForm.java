package edu.rit.columcross.permissiongranted;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ViewForm extends AppCompatActivity {

    private long rowID; // selected forms ID
    private TextView formBodyTextView; // displays forms's text
    private EditText signeeName; // The name of the person signing
    private EditText signeeEmail; // The email of the person signing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_form);

        formBodyTextView = (TextView) findViewById(R.id.vf_formBody);

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

            String formName = result.getString(nameIndex);

            // fill TextViews with the retrieved data
            setTitle(result.getString(nameIndex));
            formBodyTextView.setText(result.getString(bodyIndex));

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
        DatabaseConnector databaseConnector = new DatabaseConnector(this);
        databaseConnector.insertSignature(signeeName.getText().toString(), signeeEmail.getText().toString(), formBodyTextView.getText().toString());
    }

}
