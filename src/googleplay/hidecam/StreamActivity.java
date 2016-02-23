package googleplay.hidecam;

import googleplay.capturephoto.ListenerImplementor;

import java.io.File;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class StreamActivity extends Activity {

	ListenerImplementor captureListener;
	String port;
	String max_images;
	ImageViewAdapter imageAdapter;
	
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);
        
        //get port from settings
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		port = prefs.getString("example_text", "1234");
		max_images = prefs.getString("example_max_images", "10");
		
		//Create Directory "HideCam" if it does not exist - to save images
		File theDir = new File(Environment.getExternalStorageDirectory().getPath() + "/HideCam/");
	    if (!theDir.exists()) {
		    System.out.println("creating directory: " + theDir.getName());
		    boolean result = theDir.mkdir();  

		    if(result)    
		    	 Log.v("HideCam", "HideCam directory created");
		     else
		     {
		    	 Log.v("HideCam", "HideCam directory could not be created");
		    	 Toast.makeText(getApplicationContext(), "Unable to create directory to store images", Toast.LENGTH_SHORT).show();
		     }
		  }
		
	    //start the image rendering server
        final String fileName = Environment.getExternalStorageDirectory().getPath() + "/HideCam/Image.jpg";
  	  	captureListener = new ListenerImplementor();
  	  	
	    final Button btnStartTransmitting = (Button)findViewById(R.id.btnStartTransmitting);
	    btnStartTransmitting.setTag(1);
	    btnStartTransmitting.setOnClickListener(new OnClickListener()
	    {

			public void onClick(View v) {
				
				
				if((Integer)v.getTag() == 1)
				{
					captureListener.onImageCapture(fileName, port, true);
					btnStartTransmitting.setText("Stop Transmitting");
					v.setTag(0);
				}
				else
				{
					captureListener.onImageStopCapture();
					btnStartTransmitting.setText("Start Transmitting");
					v.setTag(1);
				}
			}	    
	    });
		
		TextView url = (TextView)findViewById(R.id.accessUrlText);
		try
		{
			//Get ip address of device connected to wi-fi
			WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
			WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
			int ip = wifiInfo.getIpAddress();
			String ipAddress = Formatter.formatIpAddress(ip);
			url.setText("http://" + ipAddress + ":" + port);
		} catch (Exception e1) {
			Log.v("HideCam", "Failed to fetch IP Address of the device");
			e1.printStackTrace();
		}
		
		imageAdapter = new ImageViewAdapter(this);
		
		BitmapFactory.Options opts=new BitmapFactory.Options();
		opts.inSampleSize = 8;
		
		for(int index = 0 ; index < Integer.parseInt(max_images); index ++)
		{
			String file = Environment.getExternalStorageDirectory().getPath() + "/HideCam/Image" + index + ".jpg";
			Bitmap bmp = BitmapFactory.decodeFile(file, opts);
			imageAdapter.addPhoto(new LoadedImage(bmp));
		}
		GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(imageAdapter);

		
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		captureListener.onImageStopCapture(); // to stop Rest server		
	}
}
