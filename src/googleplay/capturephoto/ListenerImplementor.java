package googleplay.capturephoto;

import googleplay.listeners.ImageCaptureListener;
import googleplay.restful.RESTServer;
import android.util.Log;

public class ListenerImplementor implements ImageCaptureListener{

	RESTServer server;
	public void onImageCapture(String imageFile, String port, boolean streamOfflineImages) {
		Log.d("HideCam", "On captured event raised");
		server = new RESTServer();
		server.startServer(imageFile, port, streamOfflineImages);
	}

	public void onImageStopCapture() {
		if(server != null)
			server.stopServer();		
	}
}
