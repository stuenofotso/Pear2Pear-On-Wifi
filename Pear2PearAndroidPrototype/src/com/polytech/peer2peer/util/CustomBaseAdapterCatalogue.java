package com.polytech.peer2peer.util;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.polytech.peer2peer.R;

public class CustomBaseAdapterCatalogue extends BaseAdapter {

	Context context;
	List<RowItemCatalogue> rowItems;
	
	public CustomBaseAdapterCatalogue(Context context, List<RowItemCatalogue> items) {
		this.context = context;
		this.rowItems = items;
	}
	
	/*private view holder class*/
	private class ViewHolder {
		ImageView imageView;
		TextView txtTitle;
		TextView txtDesc;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		LayoutInflater mInflater = (LayoutInflater) 
			context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_catalogue, null);
			holder = new ViewHolder();
			holder.txtDesc = (TextView) convertView.findViewById(R.id.descC);
			holder.txtTitle = (TextView) convertView.findViewById(R.id.titleC);
			holder.imageView = (ImageView) convertView.findViewById(R.id.iconC);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		RowItemCatalogue rowItemCatalogue = (RowItemCatalogue) getItem(position);
		
		holder.txtDesc.setText(rowItemCatalogue.getDesc());
		holder.txtTitle.setText(rowItemCatalogue.getTitle());
		holder.imageView.setImageResource(rowItemCatalogue.getImageId());
		return convertView;
	}
	
	
	
	@Override
	public int getCount() {
		return rowItems.size();
	}

	@Override
	public Object getItem(int position) {
		return rowItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return rowItems.indexOf(getItem(position));
	}


}
