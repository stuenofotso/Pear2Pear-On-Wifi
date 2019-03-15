package com.polytech.peer2peer.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.polytech.peer2peer.R;


public class MonCompte {
	public static void mesInformations(final Context context){

		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View promptView = layoutInflater.inflate(R.layout.moncompte, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setView(promptView);

		alertDialogBuilder.setCancelable(false)
				.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					
					}
				})
				.setNegativeButton("Annuler",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create an alert dialog
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

}
