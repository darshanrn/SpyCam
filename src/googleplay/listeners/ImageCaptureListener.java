package googleplay.listeners;



public interface ImageCaptureListener {
public void onImageCapture(String imageFile, String port, boolean streamOfflineImages);
public void onImageStopCapture();
}
