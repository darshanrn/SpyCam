package googleplay.hidecam;

import googleplay.hidecam.util.PreferencesValues;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageViewAdapter extends BaseAdapter {

	Context ctx;
	int count = 0;
	private ArrayList<LoadedImage> photos = new ArrayList<LoadedImage>();

	public ImageViewAdapter(Context context) {
		this.ctx = context;
	}
	
	public void addPhoto(LoadedImage photo) { 
        photos.add(photo); 
    } 
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(ctx);
            imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(photos.get(position).getBitmap());
        
        
        return imageView;
	}

	public int getCount() {
		return PreferencesValues.max_images;		
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

}

class LoadedImage {
    Bitmap mBitmap;

    LoadedImage(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}