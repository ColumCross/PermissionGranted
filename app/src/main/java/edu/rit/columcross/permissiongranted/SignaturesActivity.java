package edu.rit.columcross.permissiongranted;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SignaturesActivity extends AppCompatActivity {

    // used as a key in a key-value pair that's passed between activities
    public static final String ROW_ID = "row_id";

    // refer to the ListActivity's built-in ListView so we can deal with it programmatically
    private ListView activityListView;

    // adapter for populating the ListView
    private CursorAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signatures);

        activityListView = (ListView) findViewById(R.id.signaturesListView);; // get the ListView
        activityListView.setOnItemClickListener(viewFormListener);
        //activityListView.setBackgroundColor(Color.BLACK);
        // display message on empty list
        TextView emptyText = (TextView) View.inflate(this,
                R.layout.signature_list_empty_item, null);
        emptyText.setVisibility(View.GONE);

        ((ViewGroup) activityListView.getParent()).addView(emptyText);
        activityListView.setEmptyView(emptyText);

        // map each contact's name to a TextView in the ListView layout
        String[] from = new String[] { "name" };
        int[] to = new int[] { R.id.signatureTextView };
        contactAdapter = new SimpleCursorAdapter(
                SignaturesActivity.this, R.layout.signature_list_item, null, from, to, 0);

        activityListView.setAdapter(contactAdapter); // set contactView's adapter to bind the ListView to the CursorAdapter so the ListView can display the data.


    }



        @Override
    //called each time an Activity returns to the foreground including when it is
    //first created
        protected void onResume()
        {
            super.onResume(); // call super's onResume method

            // create new GetContactsTask and execute it
            //this is an AsyncTask that gets the complete list of contacts from the db
            //and sets the contactAdapter’s Cursor for populating the ListView.
            //AsyncTask method: execute performs the task in a separate thread
            //Every time this happens, a task is created because an AsyncTask can only
            //be executed once
            new SignaturesActivity.GetContactsTask().execute((Object[]) null);
        } // end method onResume

    // performs database query outside GUI thread
    private class GetContactsTask extends AsyncTask<Object, Object, Cursor>
    {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(SignaturesActivity.this);

        // perform the database access
        @Override
        protected Cursor doInBackground(Object... params)
        {
            databaseConnector.open();

            // get a cursor containing call contacts
            return databaseConnector.getAll("signatures");
        } // end method doInBackground

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result)
        {
            contactAdapter.changeCursor(result); // set the adapter's Cursor
            databaseConnector.close();
        } // end method onPostExecute
    } // end class GetContactsTask








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forms, menu);
        return true;
    }

    /**
     * handle choice from options menu
     * @param item  The menu item selected.
     * @return  Who the fuck knows.
     */
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_forms:
                startActivity(new Intent(this, FormsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            case R.id.action_signatures:
                startActivity(new Intent(this, SignaturesActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    // event listener that responds to the user touching a contact's name
    // in the ListView
    AdapterView.OnItemClickListener viewFormListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            // create an Intent to launch the ViewContact Activity
            Intent viewContact =
                    new Intent(SignaturesActivity.this, ViewSignature.class);

            // pass the selected contact's row ID as an extra with the Intent
            viewContact.putExtra(ROW_ID, id);
            startActivity(viewContact); // start the ViewContact Activity
        } // end method onItemClick
    }; // end viewContactListener
}
