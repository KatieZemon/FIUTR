package com.example.fiutr;

import java.util.ArrayList;

import com.example.fiutr.WifiAdapterItem.genericWifiHolder;
import com.google.android.gms.maps.model.Marker;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class NetworkAdapterItem extends ArrayAdapter<Network> {

	Context context;
	int resource;
	ArrayList<Network> networkList;
	
	
	public NetworkAdapterItem(Context context, int textViewResourceId, ArrayList<Network> objects)
	{
		super(context,textViewResourceId,objects);
		this.context = context;
		this.resource = textViewResourceId;
		networkList = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		genericWifiHolder holder = null;
		
		if(row == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(resource, parent, false);
			holder = new genericWifiHolder();
			holder.title = (TextView) row.findViewById(R.id.title);
			holder.desc = (TextView) row.findViewById(R.id.description);
			row.setTag(holder);
		}
		else
		{
			holder = (genericWifiHolder) row.getTag();
		}
		
		Network currentItem = networkList.get(position);
		holder.title.setText(currentItem.getName());
		holder.desc.setText(currentItem.getDetails());
		return row;
	}
	
	static class genericWifiHolder
	{
		TextView title;
		TextView desc;
	}

}
