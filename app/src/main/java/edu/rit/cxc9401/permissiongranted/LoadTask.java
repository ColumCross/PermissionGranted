package edu.rit.cxc9401.permissiongranted;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

/**
 * Created by Colum on 2017-12-15.
 */

//TODO: Build more robustly
public class LoadTask extends AsyncTask<Long, Object, Cursor> {

    DatabaseConnector databaseConnector;

    public LoadTask(Context callingContext) {
        databaseConnector =  new DatabaseConnector(callingContext);
    }


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
//        setTitle(signer);
//        formBodyTextView.setText(result.getString(bodyIndex));
//        topText.setText("Created by "+creatorName+" on "+dateCreated);
//        bottomText.setText("Signed by "+signer+" <"+result.getString(emailIndex)+"> on "+result.getString(result.getColumnIndex("dateSigned")));

        result.close(); // close the result cursor
        databaseConnector.close(); // close database connection
    } // end method onPostExecute
} // end class LoadContactTask