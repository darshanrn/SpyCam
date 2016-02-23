package googleplay.capturephoto;

import android.content.ContentResolver;

public interface ITakePicture 
{
	public void capturePhoto(ContentResolver cr);
	public String getFileNameToStore();
}
