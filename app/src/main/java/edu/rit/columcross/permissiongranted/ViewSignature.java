package edu.rit.columcross.permissiongranted;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class ViewSignature extends AppCompatActivity {

    private long rowID; // selected forms ID
    private TextView formBodyTextView; // displays forms's text
    private TextView topText; // The name of the person signing
    private TextView bottomText; // The email of the person signing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_signature);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        formBodyTextView = (TextView) findViewById(R.id.vs_formBody);

        // get the selected contact's row ID
        Bundle extras = getIntent().getExtras();
        rowID = extras.getLong(FormsActivity.ROW_ID);

        topText = (TextView) findViewById(R.id.vs_name);
        bottomText = (TextView) findViewById(R.id.vs_email);

    }


    // called when the activity is first created
    @Override
    protected void onResume() {
        super.onResume();

        // create new LoadContactTask and execute it
        new ViewSignature.LoadContactTask().execute(rowID);
    } // end method onResume

    // performs database query outside GUI thread
    private class LoadContactTask extends AsyncTask<Long, Object, Cursor> {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(ViewSignature.this);

        // perform the database access
        @Override
        protected Cursor doInBackground(Long... params) {
            databaseConnector.open();

            // get a cursor containing all data on given entry
            return databaseConnector.getOne("signatures", params[0]);
        } // end method doInBackground

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);

            result.moveToFirst(); // move to the first item

            // get the column index for each data item
            int nameIndex = result.getColumnIndex("name");
            int bodyIndex = result.getColumnIndex("formText");
            int emailIndex = result.getColumnIndex("email");

            String signer = result.getString(nameIndex);
            String creatorName = result.getString(result.getColumnIndex("formCreator"));
            String dateCreated = result.getString(result.getColumnIndex("creationDate"));

            // fill TextViews with the retrieved data
            setTitle(signer);
            formBodyTextView.setText(result.getString(bodyIndex));
            topText.setText("Created by "+creatorName+" on "+dateCreated);
            bottomText.setText("Signed by "+signer+" <"+result.getString(emailIndex)+"> on "+result.getString(result.getColumnIndex("dateSigned")));

            result.close(); // close the result cursor
            databaseConnector.close(); // close database connection
        } // end method onPostExecute
    } // end class LoadContactTask

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
