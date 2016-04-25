package googleplay.hiddencam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class LaunchScreenActivity extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launchscreen);
		
		ImageButton liveBtn = (ImageButton)findViewById(R.id.camera);
		ImageButton streamBtn = (ImageButton)findViewById(R.id.stream);
		ImageButton settingsBtn = (ImageButton)findViewById(R.id.settings);
		ImageView info = (ImageView)findViewById(R.id.btnInfo);
		
		liveBtn.setOnClickListener(this);
		streamBtn.setOnClickListener(this);
		settingsBtn.setOnClickListener(this);
		info.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.camera:
			startActivityForResult(new Intent(this, LiveCaptureActivity.class), 111);
			break;
		case R.id.stream: 
			startActivity(new Intent(this, StreamActivity.class));
			break;
		case R.id.settings: 
			startActivityForResult(new Intent(this, SettingsActivity.class), 333);
			break;
		case R.id.btnInfo:
			startActivity(new Intent(this, InfoActivity.class));
			break;
		}
	}
}
