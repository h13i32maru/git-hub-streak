package jp.h13i32maru.githubstreak;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        ((TextView)findViewById(R.id.userName)).setText(sharedPref.getString("userName", ""));
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        String userName = ((TextView)findViewById(R.id.userName)).getText().toString();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        
        if (userName.equals(sharedPref.getString("userName", ""))) {
            return;
        }
        
        Editor editor = sharedPref.edit();
        editor.putString("userName", userName);
        editor.commit();
        
        Widget.update(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
