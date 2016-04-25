package googleplay.hiddencam;

import googleplay.capturephoto.ListenerImplementor;
import googleplay.hidecam.util.PreferencesValues;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

public class LiveCaptureActivity extends Activity {
    
	int requestCode = 007;
	ListenerImplementor captureListener;
	private Timer capturePhoto;
	String port;
	String delay;
	boolean save_images;
	int count = 0;
	Camera mCamera ;
	String picture_size;
	String orientation;
	
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //get port from settings
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		port = prefs.getString("example_text", "1234");
		delay = prefs.getString("example_list", "3000");
		save_images = prefs.getBoolean("example_checkbox", false);
		picture_size = prefs.getString("example_picture_sizes", "Medium");
		PreferencesValues.max_images = Integer.parseInt(prefs.getString("example_max_images", "10"));

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
        String fileName = Environment.getExternalStorageDirectory().getPath() + "/HideCam/Image.jpg";
  	  	captureListener = new ListenerImplementor();
		captureListener.onImageCapture(fileName, port, false);
        
		TextView serverAddress = (TextView)findViewById(R.id.server_address);
		try
		{
			//Get ip address of device connected to wi-fi
			WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
			WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
			int ip = wifiInfo.getIpAddress();
			String ipAddress = Formatter.formatIpAddress(ip);
			serverAddress.setText("http://" + ipAddress + ":" + port);
		} catch (Exception e1) {
			Log.v("HideCam", "Failed to fetch IP Address of the device");
			e1.printStackTrace();
		}
		
		capturePhoto = new Timer();
		mCamera = Camera.open();
		Parameters parameters = mCamera.getParameters();
		//Set picture size based on user settings
		List<Size> sizes = parameters.getSupportedPictureSizes();
		if(picture_size.equalsIgnoreCase("Small"))
			parameters.setPictureSize(sizes.get(sizes.size() - 1).width, sizes.get(sizes.size() - 1).height);
		else if(picture_size.equalsIgnoreCase("Medium"))
			parameters.setPictureSize(sizes.get(sizes.size() / 2).width, sizes.get(sizes.size() / 2).height);
		else if(picture_size.equalsIgnoreCase("Large"))
			parameters.setPictureSize(sizes.get(0).width, sizes.get(0).height);
		
						      			
		parameters.set("orientation", "landscape");
        mCamera.setDisplayOrientation(90);
        parameters.setRotation(90);
        parameters.setPictureFormat(ImageFormat.JPEG);
        mCamera.setParameters(parameters);
        
        capturePhoto.scheduleAtFixedRate(new TimerTask() 
        {
			
			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					public void run() {
						Camera.PictureCallback mCall = new Camera.PictureCallback()
					    {
					  
					       public void onPictureTaken(byte[] data, Camera camera)
					       {
					          FileOutputStream outStream = null;
			                  try{
			                	  	String fileName = Environment.getExternalStorageDirectory().getPath() + "/HideCam/Image.jpg";
			                	  	outStream = new FileOutputStream(fileName);
			                      	outStream.write(data);
			                      	outStream.close();
			                      	
			                      	if(save_images)
			                      	{
			                      		String fileNameToSave = Environment.getExternalStorageDirectory().getPath() + "/HideCam/Image" + count + ".jpg";
				                	  	count++;
				                	  	if(count == PreferencesValues.max_images) count = 1; //Max images are retrieved from settings
				                	  	
				                      	outStream = new FileOutputStream(fileNameToSave);
				                      	outStream.write(data);
				                      	outStream.close();
			                      	}
			                      	
//			                      	captureListener = new ListenerImplementor();
//			          				captureListener.onImageCapture(fileName, port);
			                  } catch (FileNotFoundException e){
			                      Log.d("CAMERA", e.getMessage());
			                  } catch (IOException e){
			                      Log.d("CAMERA", e.getMessage());
			                  }
					     
					       }
					    };
					    
					    SurfaceView sv = new SurfaceView(getApplicationContext());
					    
				      	try {
				      			
				      			
				                mCamera.setPreviewDisplay(sv.getHolder());
				                mCamera.startPreview();
				                mCamera.takePicture(null, null, mCall);
				                 
				            } catch (IOException e) {
				                 e.printStackTrace();
							}
					}
				});
			}
        	
        }, 0, Integer.parseInt(delay));
        
   }
	
	@Override
	public void onPause()
	{
		super.onPause();
		capturePhoto.cancel();
		captureListener.onImageStopCapture(); // to stop Rest server
		mCamera.release(); //release camera
		Toast.makeText(getApplicationContext(), "Stopped Capture", Toast.LENGTH_LONG).show();
	}
    
	public void capturePhoto(ContentResolver cr) {
		// TODO Auto-generated method stub
	}
} 