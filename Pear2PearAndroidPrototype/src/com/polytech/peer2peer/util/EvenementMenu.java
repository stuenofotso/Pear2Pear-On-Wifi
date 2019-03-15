package com.polytech.peer2peer.util;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import com.polytech.pear2pear.manage_local_catalog.ManageLocalCatalogold;
import com.polytech.peer2peer.R;
import com.polytech.peer2peer.peerinterface.ListePear;
import com.polytech.peer2peer.peerinterface.MainActivity;
import com.polytech.peer2peer.peerinterface.MonCatalogue;

public class EvenementMenu {
	static String m_chosenDir = "";
	 static ManageLocalCatalogold m=new ManageLocalCatalogold();
	public static boolean listeAction(MenuItem menu, final Activity activite){
		int id=menu.getItemId();
		switch (id) {
		case R.id.profil:
			MonCompte.mesInformations(activite);
			return true;
		case R.id.quitter:
			activite.finish();
			System.exit(0);
			MainActivity.jh.close();
			return true;
			
		case R.id.PFichier:
			FileChooser.showFileChooser(activite);
			return true;
		case R.id.monCatalogue:
			Intent i=new Intent(activite, MonCatalogue.class);
			activite.startActivity(i);
        	return true;
		case R.id.listeDesPeers:
			Intent j=new Intent(activite, ListePear.class);
			activite.startActivity(j);
        	return true;
		default:
			return true;
		}
	}
}
