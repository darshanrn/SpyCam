package googleplay.restful;

import googleplay.hidecam.util.PreferencesValues;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import android.os.Environment;
import android.util.Log;

public class RESTServer {

	Server server;
	private String mImageFileName = null;
	boolean streamOffline = false;
	int count = 0;
	int max_images;
	
	Handler handler = new AbstractHandler()
    {	
    	//@Override
		public void handle(String target, Request request, HttpServletRequest MainRequestObject,
				HttpServletResponse response) throws IOException, ServletException
		{
			try
			{
				//How to get Query String/
				Log.i("Query String", target);
				
				//URI format
				//http://127.0.0.1:1234/Function/para1/para2
				
				//Http Request Type: GET/POST/PUT/DELETE
				Log.i("HTTP Verb", MainRequestObject.getMethod());
				
				BufferedReader in = new BufferedReader(new InputStreamReader(MainRequestObject.getInputStream()));
				String line = null;
				                   
				StringBuilder PostedData = new StringBuilder();
				
				while ((line = in.readLine()) != null)
				{    							
					Log.i("Received Message Line by Line", line);
					PostedData.append(line);					
				}				
				
				//Http Request Data Type
				//Log.i("Posted Data Type", MainRequestObject.getContentType());
				
				//Http Request Type: GET/POST/PUT/DELETE
				Log.i("Posted Data", PostedData.toString());
				
				//How To Send Responce Back
//				response.setContentType("text/html");
//	            response.setStatus(HttpServletResponse.SC_OK);
//	            response.getWriter().println("<h1>Hello</h1>");
				File f;
				InputStream stream;
				OutputStream out;
				if(streamOffline)
				{
					String fileNameToSave = Environment.getExternalStorageDirectory().getPath() + "/HideCam/Image" + count + ".jpg";
            	  	count++;
            	  	if(count == max_images) count = 0; //Max images retrieved from settings
            	  	
					f = new File(fileNameToSave);
					stream = new FileInputStream(f); 
					out = response.getOutputStream();
				}
				else
				{
					f = new File(mImageFileName);
					stream = new FileInputStream(f); 
					out = response.getOutputStream(); 
				}
				response.setContentType("image/jpeg");
	            response.setStatus(HttpServletResponse.SC_OK);
	            response.setContentLength((int) f.length());
	            response.addHeader("Connection", "keep-alive");
	            byte[] response_body = new byte[stream.available()];
	            stream.read(response_body);
	            stream.close();
	            out.write(response_body);
	            out.close();
	            
	            
	            ((Request)MainRequestObject).setHandled(true);	
	            
			}
        	catch (Exception ex)
        	{
        		Log.i("Error", ex.getMessage());
			}
		}			
    };
    
    public void startServer(String fileName, String serverPort, boolean streamOfflineImages)
    {
    	mImageFileName = fileName;
    	streamOffline = streamOfflineImages;
    	server = new Server(Integer.parseInt(serverPort));
    	server.setHandler(handler);
    	try {
			server.start();
			Log.v("HideCam", "Started Server");
		} catch (Exception e) {
			Log.d("HideCam", "Failed to start server" + e.getMessage());
			e.printStackTrace();
		}
    	
    	max_images = PreferencesValues.max_images;
    }
    
    public void stopServer()
    {
    	try {
			server.stop();
			Log.v("HideCam", "Stopped Server");
		} catch (Exception e) {
			Log.v("HideCam", "Unable to stop server. Message - " + e.getMessage());
			e.printStackTrace();
		}
    }
}
