package edu.rit.cxc9401.permissiongranted;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class FormsActivity extends BaseActivity {

    public static final String ROW_ID = "row_id"; // used as a key in a key-value pair that's passed between activities
    private ListView activityListView; // refer to the ListActivity's built-in ListView so we can deal with it programmatically
    private CursorAdapter contactAdapter; // adapter for populating the ListView
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fab = (FloatingActionButton) findViewById(R.id.newListFab);
        fab.show();
        activityListView = (ListView) findViewById(R.id.primaryListView);; // get the ListView
        activityListView.setOnItemClickListener(viewFormListener);
        //activityListView.setBackgroundColor(Color.BLACK);
        // display message on empty list
        TextView emptyText = (TextView) View.inflate(this,
                R.layout.forms_list_empty_item, null);
        emptyText.setVisibility(View.GONE);

        ((ViewGroup) activityListView.getParent()).addView(emptyText);
        activityListView.setEmptyView(emptyText);

        // map each contact's name to a TextView in the ListView layout
        String[] from = new String[] { "name" };
        int[] to = new int[] { R.id.formTextView };
        contactAdapter = new SimpleCursorAdapter(
                FormsActivity.this, R.layout.form_list_item, null, from, to, 0);

        activityListView.setAdapter(contactAdapter); // set contactView's adapter to bind the ListView to the CursorAdapter so the ListView can display the data.


        // FAB Controls
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("***************", "******************");
                // create a new Intent to launch the AddEditContact Activity
                Intent addNewContact =
                        new Intent(FormsActivity.this, AddEditForm.class);
                startActivity(addNewContact); // start the AddEditContact Activity
            }
        });

        //setTitle(R.string.title_home);
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
        new GetContactsTask().execute((Object[]) null);
    } // end method onResume

    // performs database query outside GUI thread
    private class GetContactsTask extends AsyncTask<Object, Object, Cursor>
    {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(FormsActivity.this);

        // perform the database access
        @Override
        protected Cursor doInBackground(Object... params)
        {
            databaseConnector.open();

            // get a cursor containing call contacts
            return databaseConnector.getAll("forms");
        } // end method doInBackground

        // use the Cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result)
        {
            contactAdapter.changeCursor(result); // set the adapter's Cursor
            databaseConnector.close();
        } // end method onPostExecute
    } // end class GetContactsTask


    // event listener that responds to the user touching a contact's name
    // in the ListView
    AdapterView.OnItemClickListener viewFormListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            // create an Intent to launch the ViewContact Activity
            Intent viewContact =
                    new Intent(FormsActivity.this, ViewForm.class);
            viewContact.putExtra(ROW_ID, id);

            //TaskStackBuilder viewForm = new TaskStackBuilder.create(this);
            //viewForm.addParentStack(FormsActivity.class);
           // viewForm.addNextIntent(viewContact);

            // pass the selected contact's row ID as an extra with the Intent

            startActivity(viewContact); // start the ViewContact Activity
        } // end method onItemClick
    }; // end viewContactListener

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }

}
