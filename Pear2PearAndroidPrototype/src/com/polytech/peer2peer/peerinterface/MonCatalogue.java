package com.polytech.peer2peer.peerinterface;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.polytech.pear2pear.manage_local_catalog.ManageLocalCatalog;
import com.polytech.pear2pear.models.CatalogueLocal;
import com.polytech.peer2peer.R;
import com.polytech.peer2peer.util.CustomBaseAdapterCatalogue;
import com.polytech.peer2peer.util.EvenementMenu;
import com.polytech.peer2peer.util.FileChooser;
import com.polytech.peer2peer.util.FileUtils;
import com.polytech.peer2peer.util.RowItemCatalogue;

public class MonCatalogue extends Activity implements OnItemClickListener{

   public static final Integer imageMp3=R.drawable.music;
   public static final Integer imagePdf=R.drawable.pdf;
   public static final Integer imageVideo=R.drawable.video;
   public static final Integer imagePng=R.drawable.png;
   public static final Integer imageJpg=R.drawable.jpeg;
   public static final Integer imageZip=R.drawable.zip;
   public static final Integer imageOther=R.drawable.none;
   
   ListView listViewCatalogue;
   List<RowItemCatalogue> rowItems;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mon_catalogue);
		
		rowItems = new ArrayList<RowItemCatalogue>();
		/* utilisation des informations se trouvants dans le catalogue*/
		ManageLocalCatalog mcl=new ManageLocalCatalog(getApplicationContext());
		for(CatalogueLocal cl: mcl.listCatalog()){
			
			RowItemCatalogue item = new RowItemCatalogue(detectIcon(cl.getNomFichier()), cl.getNomFichier(), cl.getDescriptionFichier(),cl.getCheminFichier());
			rowItems.add(item);
		}
		/*
		for (int i = 0; i < titles.length; i++) {
			RowItemCatalogue item = new RowItemCatalogue(image, titles[i], descriptions[i]);
			rowItems.add(item);
		}*/
		listViewCatalogue = (ListView)findViewById(R.id.listeCatalogue);
		CustomBaseAdapterCatalogue adapter = new CustomBaseAdapterCatalogue(this, rowItems);
		listViewCatalogue.setAdapter(adapter);
		listViewCatalogue.setOnItemClickListener(this);
		
		
	}
	
	public static int detectIcon(String nomFichier){
		String extention=nomFichier.substring(nomFichier.lastIndexOf(".")+1);
		if(extention.equalsIgnoreCase("mp3")){
			
			return imageMp3;
		}else if (extention.equalsIgnoreCase("pdf")) {
			return imagePdf;
		}else if (extention.equalsIgnoreCase("mp4")) {
			return imageVideo;
		}else if (extention.equalsIgnoreCase("jpg")) {
			return imageJpg;
		}else if ( extention.equalsIgnoreCase("png")) {
			return imagePng;
		}else if(extention.equalsIgnoreCase("zip")) {
			return imageZip;
		}else{
			return imageOther;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
	  EvenementMenu.listeAction(item, MonCatalogue.this);
		return true;
			
		}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		Toast toast = Toast.makeText(getApplicationContext(),"Item " + (position + 1) + ": " + rowItems.get(position).getChemin(),
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}

	public  String description="";
	String path = null;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
	    switch (requestCode) {
	        case FileChooser.FILE_SELECT_CODE:
	        	
	        
	        if (resultCode == RESULT_OK) {
	        	Uri uri = data.getData();
 	            
	        	try {
					path = FileUtils.getPath(this, uri);
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
	        	
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setTitle("Title");

	        	// Set up the input
	        	final EditText input = new EditText(this);
	        	input.setMinLines(3);
	        	
	        	input.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_FLAG_MULTI_LINE);
	        	builder.setView(input);
	        	builder.setTitle("Entrez la description du fichier");
	        	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
	        	    @Override
	        	    public void onClick(DialogInterface dialog, int which) {
	        	            description = input.getText().toString();
	    					File fichier = new File(path);
	    					ManageLocalCatalog mcl=new ManageLocalCatalog(getApplicationContext());
	    					Toast.makeText(getApplicationContext(), description, Toast.LENGTH_LONG).show();
	    					mcl.addCatalog(fichier.getName(), description, path);
	    					Toast.makeText(getApplicationContext(), path, Toast.LENGTH_LONG).show();
	    				    /*
	    					String liste = "liste des fichier en BD: \n";
	    					for(CatalogueLocal cl: mcl.listCatalog()){
	    						liste+="\t "+cl.getNomFichier()+" "+cl.getDescriptionFichier()+" "+cl.getCheminFichier()+"\n";
	    					}
	    		            Toast.makeText(getApplicationContext(), liste, Toast.LENGTH_LONG).show();*/
	        	    }
	        	});
	        	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        	    @Override
	        	    public void onClick(DialogInterface dialog, int which) {
	        	        dialog.cancel();
	        	    }
	        	});

	        	builder.show();
	        	
	            // Get the Uri of the selected file 
	           
				
				
	        }
	        break;	
	       
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}

	
}
