package edu.rit.columcross.permissiongranted;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class FormsActivity extends AppCompatActivity {

    // used as a key in a key-value pair that's passed between activities
    public static final String ROW_ID = "row_id";

    // refer to the ListActivity's built-in ListView so we can deal with it programmatically
    private ListView formListView;

    // adapter for populating the ListView
    private CursorAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        formListView = (ListView) findViewById(R.id.formListView);; // get the ListView
        formListView.setOnItemClickListener(viewFormListener);
        formListView.setBackgroundColor(Color.BLACK);
        // display message on empty list
        TextView emptyText = (TextView)View.inflate(this,
                R.layout.forms_list_empty_item, null);
        emptyText.setVisibility(View.GONE);

        ((ViewGroup)formListView.getParent()).addView(emptyText);
        formListView.setEmptyView(emptyText);

        // map each contact's name to a TextView in the ListView layout
        String[] from = new String[] { "name" };
        int[] to = new int[] { R.id.contactTextView };
        contactAdapter = new SimpleCursorAdapter(
                AddressBook.this, R.layout.contact_list_item, null, from, to, 0);

        contactListView.setAdapter(contactAdapter); // set contactView's adapter to bind the ListView to the CursorAdapter so the ListView can display the data.


        // FAB Controls
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create a new Intent to launch the AddEditContact Activity
                Intent addNewContact =
                        new Intent(FormsActivity.this, NewForm.class);
                startActivity(addNewContact); // start the AddEditContact Activity
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forms, menu);
        return true;
    }



    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        /* create a new Intent to launch the AddEditContact Activity
        Intent addNewContact =
                new Intent(FormsActivity.this, .class);
        startActivity(addNewContact); // start the AddEditContact Activity
        */
        return super.onOptionsItemSelected(item); // call super's method
    } // end method onOptionsItemSelected

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

            // pass the selected contact's row ID as an extra with the Intent
            viewContact.putExtra(ROW_ID, id);
            startActivity(viewContact); // start the ViewContact Activity
        } // end method onItemClick
    }; // end viewContactListener
}
