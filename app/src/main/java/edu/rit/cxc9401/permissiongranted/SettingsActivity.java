package edu.rit.cxc9401.permissiongranted;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingsActivity extends BaseActivity {

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_settings;
    }

    private ListView activityListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.newListFab);
        fab.hide();
        activityListView = (ListView) findViewById(R.id.primaryListView); // get the ListView

        RelativeLayout emptyText = (RelativeLayout) View.inflate(this,
                R.layout.activity_settings, null);
        emptyText.setVisibility(View.GONE);

        ((ViewGroup) activityListView.getParent()).addView(emptyText);
        activityListView.setEmptyView(emptyText);

        Spanned policy = fromHtml(getString(R.string.about_info));
        TextView termsOfUse = (TextView)findViewById(R.id.about_text);
        termsOfUse.setText(policy);
        termsOfUse.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        if(html == null){
            // return an empty spannable if the html is null
            return new SpannableString("");
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
            // we are using this flag to give a consistent behaviour
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }


}