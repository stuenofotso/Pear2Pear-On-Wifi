package com.polytech.peer2peer.peerinterface;

import java.util.List;

import ndonna.example.peerinterface.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.polytech.peer2peer.util.EvenementMenu;
import com.polytech.peer2peer.util.RowItem;

public class FileList extends Activity implements OnItemClickListener {

	public static final String[] titles = new String[] { "Coller la petite",
		"Introduction aux ontologies", "La dense du chef", "FIFFA2016" };

   public static final String[] descriptions = new String[] {
		"la musique de l'heure qui fait bouger tous le monde entier",
		"Le cour de base pour apprendre les ontologies informatiques",
		"La video illustrant la danse du chef dans la region de l'Ouest Cameroun",
		"le jeu FIFFA 2016 avec les nouvelles options" };

    public static final Integer[] images = { R.drawable.music,
		R.drawable.pdf, R.drawable.video, R.drawable.game };
    public static final Integer download=R.drawable.getapp;
   ListView listView;
   List<RowItem> rowItems;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_list);
		
		String[] listeFichier=getResources().getStringArray(R.array.listeFichier);
		ListView l=(ListView)findViewById(R.id.liste);
		l.setAdapter(new ArrayAdapter<String>(this,R.layout.list_item,R.id.element,listeFichier));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
	  EvenementMenu.listeAction(item, FileList.this);
		return true;
			
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		Toast toast = Toast.makeText(getApplicationContext(),"Item " + (position + 1) + ": " + rowItems.get(position),
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}


}
