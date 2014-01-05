package jp.h13i32maru.githubstreak;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider {
    
    public static void update(Context context){
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, Widget.class));
        
        Intent intent = new Intent(context, Widget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        
        context.sendBroadcast(intent);
    }
	
    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        
        setText(context, appWidgetManager, "...");
        
        new Thread(new Runnable(){
        	@Override
        	public void run(){
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                String userName = sharedPref.getString("userName", "");
        		int currentStreak = getCurrentStreak(userName);
        		String currentStreakText;
        		if (currentStreak != -1){
        		    currentStreakText = currentStreak + "";		
        		} else {
        			currentStreakText = "error";
        		}
        		setText(context, appWidgetManager, currentStreakText);
      	  }
      }).start();
    }
    
    public void setText(Context context, AppWidgetManager appWidgetManager, String text) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        remoteViews.setTextViewText(R.id.currentStreak, text);
        ComponentName cn = new ComponentName(context, Widget.class);
        appWidgetManager.updateAppWidget(cn, remoteViews);
    }
    
    public int getCurrentStreak(String userName) {
        if (userName.isEmpty()) {
            return -1;
        }
        
    	try {
            HttpGet method = new HttpGet("https://github.com/" + userName);
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute( method );
            
            int status = response.getStatusLine().getStatusCode();
            if ( status != HttpStatus.SC_OK ) {
                return -1;
            }
            
            String responseText = EntityUtils.toString( response.getEntity(), "UTF-8" );
            Pattern p = Pattern.compile(" contrib-streak-current.*([0-9]+) days", Pattern.MULTILINE | Pattern.DOTALL);
            Matcher m = p.matcher(responseText);
            if (m.find()){
            	String currentStreak = m.group(1);
            	return Integer.valueOf(currentStreak);
            } else {
            	return -1;
            }
        }
        catch ( Exception e )
        {
            return -1;
        }
    }
}
