package com.infigent.stocksense;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slpash);
		Thread background = new Thread() {
            public void run() {
                 
                try {
                    sleep(2*1000);
                    Intent i=new Intent(getBaseContext(),MainActivity.class);
                    startActivity(i);
                     
                    finish();
                     
                } catch (Exception e) {
                 
                }
            }
        };
        background.start();
	}
	
}
