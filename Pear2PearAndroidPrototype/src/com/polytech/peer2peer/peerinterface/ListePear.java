package com.polytech.peer2peer.peerinterface;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.polytech.peer2peer.R;

public class ListePear extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_liste_pear);
		List<String> weekforecast=new ArrayList<String>();
		if(MainActivity.jh.isRoot()){
			weekforecast = MainActivity.jh.getSetupHotspot().rootListPeersConnected();
		}
		
		ArrayAdapter<String> mforecast=new ArrayAdapter<String>(
                this,
                R.layout.pear,
                R.id.nompeer,
                weekforecast);
       ListView m=(ListView)findViewById(R.id.listepeer);
       m.setAdapter(mforecast);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
		
	}

}
