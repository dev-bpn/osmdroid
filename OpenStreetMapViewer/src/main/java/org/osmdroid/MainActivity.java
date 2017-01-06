// Created by plusminus on 18:23:13 - 03.10.2008
package org.osmdroid;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.debug.CacheAnalyzerActivity;
import org.osmdroid.debug.browser.CacheBrowserActivity;
import org.osmdroid.samples.SampleExtensive;
import org.osmdroid.samples.SampleWithMinimapItemizedoverlay;
import org.osmdroid.samples.SampleWithMinimapZoomcontrols;
import org.osmdroid.samples.SampleWithTilesOverlay;
import org.osmdroid.samples.SampleWithTilesOverlayAndCustomTileSource;
import org.osmdroid.tileprovider.modules.SqlTileWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final String TAG = "OSM";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Generate a ListView with Sample Maps
        final ArrayList<String> list = new ArrayList<>();
        list.add("OSMDroid Sample map (Start Here)");
        list.add("OSMapView with Minimap, ZoomControls, Animations, Scale Bar and MyLocationOverlay");
        list.add("OSMapView with ItemizedOverlay");
        list.add("OSMapView with Minimap and ZoomControls");
        list.add("Sample with tiles overlay");
        list.add("Sample with tiles overlay and custom tile source");
        list.add("More Samples");
        list.add("Bug Drivers");
        list.add("Report a bug");
        list.add("Settings");
        if (BuildConfig.VERSION_CODE >= 11)
            list.add("Cache Analyzer");

        ListView lv = (ListView) findViewById(R.id.activitylist);
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                this.startActivity(new Intent(this, StarterMapActivity.class));
                break;
            case 1:
                this.startActivity(new Intent(this, SampleExtensive.class));
                break;
            case 2:
                this.startActivity(new Intent(this, SampleWithMinimapItemizedoverlay.class));
                break;
            case 3:
                this.startActivity(new Intent(this, SampleWithMinimapZoomcontrols.class));
                break;
            case 4:
                this.startActivity(new Intent(this, SampleWithTilesOverlay.class));
                break;
            case 5:
                this.startActivity(new Intent(this, SampleWithTilesOverlayAndCustomTileSource.class));
                break;
            case 6:
                this.startActivity(new Intent(this, ExtraSamplesActivity.class));
                break;
            case 7:
                this.startActivity(new Intent(this, BugsTestingActivity.class));
                break;
            case 8:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/osmdroid/osmdroid/issues"));
                startActivity(browserIntent);
                break;
            case 9:
                Intent i = new Intent(this,PreferenceActivity.class);
                startActivity(i);
                break;
            case 10:
            {
                if (BuildConfig.VERSION_CODE >= 11){
                    Intent starter = new Intent(this,CacheAnalyzerActivity.class);
                    startActivity(starter );
                    break;
                }
            }
        }
    }


    public void onResume(){
        super.onResume();
        updateStorageInfo();
    }

    /**
     * refreshes the current osmdroid cache paths with user preferences plus soe logic to work around
     * file system permissions on api23 devices. it's primarily used for out android tests.
     * @param ctx
     * @return current cache size in bytes
     */
    public static long updateStoragePrefreneces(Context ctx){

        //loads the osmdroid config from the shared preferences object.
        //if this is the first time launching this app, all settings are set defaults with one exception,
        //the tile cache. the default is the largest write storage partition, which could end up being
        //this app's private storage, depending on device config and permissions

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        //also note that our preference activity has the corresponding save method on the config object, but it can be called at any time.


        File dbFile = new File(Configuration.getInstance().getOsmdroidTileCache().getAbsolutePath() + File.separator + SqlTileWriter.DATABASE_FILENAME);
        if (Build.VERSION.SDK_INT >= 9 && dbFile.exists()) {
            return dbFile.length();
        }
        return -1;
    }

    /**
     * gets storage state and current cache size
     */
    private void updateStorageInfo(){

        long cacheSize = updateStoragePrefreneces(this);
        //cache management ends here

        TextView tv = (TextView) findViewById(R.id.sdcardstate_value);
        final String state = Environment.getExternalStorageState();

        boolean mSdCardAvailable = Environment.MEDIA_MOUNTED.equals(state);
        tv.setText((mSdCardAvailable ? "Mounted" : "Not Available") + "\n" + Configuration.getInstance().getOsmdroidTileCache().getAbsolutePath() + "\n" +
            "Cache size: " + Formatter.formatFileSize(this,cacheSize));
        if (!mSdCardAvailable) {
            tv.setTextColor(Color.RED);
            tv.setTypeface(null, Typeface.BOLD);
        }

        tv = (TextView) findViewById(R.id.version_text);
        tv.setText(BuildConfig.VERSION_NAME + " " + BuildConfig.BUILD_TYPE);
    }


}
