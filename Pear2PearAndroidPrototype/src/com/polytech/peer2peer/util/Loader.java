package com.polytech.peer2peer.util;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import com.polytech.peer2peer.R;

public class Loader extends DialogFragment {

	public Context context=null;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View promptView = layoutInflater.inflate(R.layout.loader, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Création du reseau");
        builder.setView(promptView);
        WebView wv=(WebView) promptView.findViewById(R.id.webView1);
        wv.loadUrl("file:///android_asset/loader.html");
        return builder.create();
    }
}
