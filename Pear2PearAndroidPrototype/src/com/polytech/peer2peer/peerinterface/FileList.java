package com.polytech.peer2peer.peerinterface;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.polytech.pear2pear.file_download_management.DownloadFile;
import com.polytech.pear2pear.manage_local_catalog.ManageLocalCatalog;
import com.polytech.pear2pear.models.CatalogueDeFichiers;
import com.polytech.pear2pear.models.CatalogueLocal;
import com.polytech.peer2peer.R;
import com.polytech.peer2peer.filesearch.RequestCatalogTask;
import com.polytech.peer2peer.util.CustomBaseAdapter;
import com.polytech.peer2peer.util.EvenementMenu;
import com.polytech.peer2peer.util.FileChooser;
import com.polytech.peer2peer.util.FileUtils;
import com.polytech.peer2peer.util.RowItem;

public class FileList extends Activity implements OnItemClickListener {

	public static final String[] titles = new String[] { "Coller la petite",
		"Introduction aux ontologies", "La dense du chef", "FIFFA2016","Cour de STI","Ca sort comme ca sort" };

	//public ArrayList<String> nomFichier=new ArrayList<String>();
	//public ArrayList<String> desc=new ArrayList<String>();	
	
      
   public static final String[] descriptions = new String[] {
		"la musique de l'heure qui fait bouger tous le monde entier",
		"Le cour de base pour apprendre les ontologies informatiques",
		"La video illustrant la danse du chef dans la region de l'Ouest Cameroun",
		"le jeu FIFFA 2016 avec les nouvelles options",
		" pour avoir les bases sur les systémes tuteurs intelligents",
		"La musique qui fait le show dans tous les night club du Cameroun"};

    public static final Integer[] images = { R.drawable.music,
		R.drawable.pdf, R.drawable.video, R.drawable.game,R.drawable.pdf,R.drawable.music };
    public static final Integer download=R.drawable.getapp;
   ListView listView;
   List<RowItem> rowItems;
   ImageView choisir_type_fichier;
   Object[] cats ;
   ProgressDialog progress;
   public EditText elementChercher;
   /**declairation du handler*/
   Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	    	Toast.makeText(FileList.this, "affichage des fichiers", Toast.LENGTH_LONG).show();
	    	Log.d("Fichier","ddddddddddddddddddd");
	        if (msg.getData().containsKey("files")) { //creation finish
	        	 cats = (Object[]) msg.getData().get("files");
	        	Log.d("Info","Le fichier à traiter est de taille "+cats.length);
	        	rowItems = new ArrayList<RowItem>();
	        	for(Object cat:cats){
	        		CatalogueDeFichiers ca = (CatalogueDeFichiers) cat;
	        		Log.d("Fichier",ca.getNomFichier());
	        		//nomFichier.add(ca.getNomFichier());
	        		//desc.add(ca.getDescriptionFichier());
	        		RowItem item = new RowItem(MonCatalogue.detectIcon(ca.getNomFichier()), ca.getNomFichier(), ca.getDescriptionFichier(),download, ca.getNbreTelechargements());
	    			rowItems.add(item);
	        		Toast.makeText(FileList.this, ca.getNomFichier()+" "+ca.getDescriptionFichier()+" disponible sur "+ca.getAdressePeer(), Toast.LENGTH_LONG).show();
	        	}
	        	listView = (ListView)findViewById(R.id.liste);
	    		CustomBaseAdapter adapter = new CustomBaseAdapter(FileList.this, rowItems);
	    		listView.setAdapter(adapter);
	        	
	        	/*rowItems = new ArrayList<RowItem>();
	    		for(Object cat:cats){
	        		CatalogueDeFichiers ca = (CatalogueDeFichiers) cat;
	        		RowItem item = new RowItem(images[1], ca.getNomFichier(),ca.getDescriptionFichier() ,download);
	    			rowItems.add(item);
	        		Toast.makeText(FileList.this, ca.getNomFichier(), Toast.LENGTH_LONG).show();
	        	}*/
	        }
	       
	        else  {
	        	
	        }
	    }
	};

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_list);
		/* Gestion de la recherche*/
		
		((Button)findViewById(R.id.chercher)).setOnClickListener(
				new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						elementChercher=(EditText) findViewById(R.id.CherchezFichier);
						String chaine=elementChercher.getText().toString();
						RequestCatalogTask rc = new RequestCatalogTask(chaine,handler);
						rc.execute();
					}
				}
				);
		
		
		/*Fin gestion de recherche*/
		 progress= new ProgressDialog(FileList.this);
		progress.setIndeterminate(false);
		progress.setTitle("progression du telechargement");
		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		 
		//nomFichier.add("ndonna");
		//desc.add("le debut de la creation du resau");
		/*la gestion du popup menu*/
		choisir_type_fichier=(ImageView)findViewById(R.id.choisir_type_fichier);
		choisir_type_fichier.setOnClickListener(
				new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						PopupMenu popup = new PopupMenu(FileList.this, choisir_type_fichier); 
						 popup.getMenuInflater().inflate(R.menu.choisir_type_fichier, popup.getMenu());  
						 popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
				             public boolean onMenuItemClick(MenuItem item) {  
				              Toast.makeText(FileList.this,"Vous avez cliquez : " + item.getTitle(),Toast.LENGTH_SHORT).show();  
				              return true;  
				             }  
				            });  
				  
				            popup.show();  
					}
				}
				);
		
		
		/**/
		
		
		/*Fin du popup menu*/
		
		/* envoi de la requete pour recupérer la liste initiale des fichiers */
		
		
		RequestCatalogTask rc = new RequestCatalogTask(handler);
		Toast.makeText(FileList.this, "bonjour logcat", Toast.LENGTH_LONG).show();
		rc.execute();
		/*
		rowItems = new ArrayList<RowItem>();
		for(Object cat:cats){
    		CatalogueDeFichiers ca = (CatalogueDeFichiers) cat;
    		RowItem item = new RowItem(images[1], ca.getNomFichier(),ca.getDescriptionFichier() ,download);
			rowItems.add(item);
    		Toast.makeText(FileList.this, ca.getNomFichier(), Toast.LENGTH_LONG).show();
    	}*/
		/*
		rowItems = new ArrayList<RowItem>();
		for (int i = 0; i < nomFichier.size(); i++) {
			RowItem item = new RowItem(images[i], nomFichier.get(i), desc.get(i),download);
			rowItems.add(item);
		}
		*/
		rowItems=new ArrayList<RowItem>();
		ManageLocalCatalog mlc = new ManageLocalCatalog(getApplicationContext());
		for(CatalogueLocal cl: mlc.listCatalog()){
			rowItems.add(new RowItem(MonCatalogue.detectIcon(cl.getNomFichier()), cl.getNomFichier(), cl.getDescriptionFichier(), download, cl.getNbreTelechargements()));
		}
		listView = (ListView)findViewById(R.id.liste);
		CustomBaseAdapter adapter = new CustomBaseAdapter(this, rowItems);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
		
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
		afficherInfotmationFichier(position);
		/*Toast toast = Toast.makeText(getApplicationContext(),"Item " + (position + 1) + ": " + rowItems.get(position),
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();*/
	}
	
	public  String description="";
	String path = null;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
	    switch (requestCode) {
	        case FileChooser.FILE_SELECT_CODE:
	        	
	        
	        if (resultCode == RESULT_OK) {
	        	final Uri uri = data.getData();
	        	Log.d("valeur uri", "uri "+uri);
	        	try {
					path = FileUtils.getPath(FileList.this, uri);
				} catch (Exception e1) {
					
					e1.printStackTrace();
					Log.e("erro r", " "+e1.getMessage());
				}
	        	
	        	
	        	Log.d("valeur path", "path "+path);
	        	
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
	    	//				path = fichier.getAbsolutePath();
	    					Log.d("valeur path 1", "path "+path);
	    					ManageLocalCatalog mcl=new ManageLocalCatalog(getApplicationContext());
	    					Toast.makeText(getApplicationContext(), description, Toast.LENGTH_LONG).show();
	    					
	    					MainActivity.jh.publieCatalogueFichiers(mcl.addCatalog(fichier.getName(), description, path));
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


	public void  afficherInfotmationFichier(final int position){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	// Set up the input
    	final EditText input = new EditText(this);
    	input.setTypeface(null, Typeface.BOLD);
    	input.setTextColor(Color.BLACK);
    	
    	input.setMinLines(5);
    	String  infor="";
    	infor =rowItems.get(position).getDesc();
        input.setText(infor);
    	input.setEnabled(false);
    	input.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    	builder.setView(input);
    	builder.setTitle(rowItems.get(position).getTitle()+"("+rowItems.get(position).getNbreTelechargements()+")");
    	builder.setIcon(rowItems.get(position).getImageId());
    	builder.setPositiveButton("Download", new DialogInterface.OnClickListener() { 
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	    	
    	    	
    	    	if(cats !=null){
    	    		Toast.makeText(getApplicationContext(),"initialisation du telechargement",Toast.LENGTH_SHORT).show();
        	    	
    	    		downloadFile((CatalogueDeFichiers) cats[position]);
    	    	}
    	    	
    	    	
				
    	    }
    	});
    	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        dialog.cancel();
    	    }
    	});

    	builder.show();
	}
	
	private void downloadFile(CatalogueDeFichiers fichier){
		DownloadFile df=new DownloadFile(getApplicationContext(), handlerTelechargement);
		df.downloadFile(fichier);
	}
	
	boolean progressIsShow = false;
	
	Handler handlerTelechargement = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			
//			Log.e("recu ", " "+msg.what);

			 if(msg.what == -1){
				Toast.makeText(getApplicationContext(), "Erreur de telechargement. Verifiez votre connexion... ", Toast.LENGTH_LONG).show();
				progress.dismiss();
				progressIsShow = false;
			}

			else if(msg.what == 0){
				Toast.makeText(getApplicationContext(), "Telechargement terminé avec succès. Fichier sauvegardé dans la carte SD ", Toast.LENGTH_LONG).show();
				progress.dismiss();
				progressIsShow = false;
			}


			else if(msg.what > 0)  {
				Log.e("recu progress", " "+msg.what);
				if(progressIsShow){
					Log.e("progress", "progress "+msg.what);
					progress.setProgress(msg.what);
					
				}
				else{
					progressIsShow = true;
					Toast.makeText(getApplicationContext(), "Début du téléchargement... ", Toast.LENGTH_LONG).show();
					progress.setMax(msg.what);
					progress.setProgress(0);
					progress.show();
				}
				
			}
		}
	};
}
