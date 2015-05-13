package de.p72b.bonitur;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class ActivityAbout extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		TextView tvLink = (TextView) findViewById(R.id.textViewLink);
		tvLink.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	protected void onPause() {
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		super.onPause();
	}
}