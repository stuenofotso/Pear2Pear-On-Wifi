package com.polytech.peer2peer.peerinterface;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.polytech.pear2pear.p2p_network_management.JoinHotspot;
import com.polytech.peer2peer.R;
import com.polytech.peer2peer.util.EvenementMenu;
import com.polytech.peer2peer.util.Loader;
public class MainActivity extends Activity {
	private static final int REQUEST_CHOOSER = 1234;
	public Dialog n;
	public static JoinHotspot jh;
	Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        if (msg.what == 1) { //creation finish
	        	if(jh.isAlreadyConnected()){
	        		if(jh.isRoot()){
	    			Toast.makeText(getApplicationContext(), "Création du reseau reussi: je suis le root", Toast.LENGTH_LONG).show();
	        		}else{
	        	    Toast.makeText(getApplicationContext(), "j ai pu rejoindre un reseau: je suis peer ", Toast.LENGTH_LONG).show();
	        		}
	        			
	    			n.cancel();
	    			Intent i=new Intent(MainActivity.this, FileList.class);
					startActivity(i);
					
	    		}
	        }
	       
	        else  {
	        	
	        }
	    }
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Loader no=new Loader();
		no.context=MainActivity.this;
	    n=no.onCreateDialog(null);
	    
	   
		
		((Button)findViewById(R.id.start)).setOnClickListener(
				new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						//Intent i=new Intent(MainActivity.this, FileList.class);
						//startActivity(i);
						Thread t = new Thread(new Runnable() {
							
							@Override
							public void run() {
							jh = new JoinHotspot(getApplicationContext(), handler);
							jh.compute();
							}
						});
						t.start();
						Toast.makeText(MainActivity.this, "debut de la creation",Toast.LENGTH_SHORT).show();
						n.show();
						
					}
				}
				
				);	
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch (requestCode) {
	        
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		//if(jh!=null) jh.close();
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
	  EvenementMenu.listeAction(item, MainActivity.this);
		return true;
			
		}
	
	public  void toggleActivationWifi(WifiManager wifi, boolean enable){
		try {

			boolean b=wifi.isWifiEnabled();
			if(b != enable){
				wifi.setWifiEnabled(enable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}}
	
	
	

}
