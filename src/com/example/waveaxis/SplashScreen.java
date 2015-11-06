package com.example.waveaxis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		Thread t = new Thread(){
			public void run(){
				try {
					sleep(3*1000);
					Intent i = new Intent(SplashScreen.this , Home.class);
					startActivity(i);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};t.start();
		
	}

}
