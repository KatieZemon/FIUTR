package com.example.fiutr;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TestAdapterItem extends ArrayAdapter<String> {

	Context context;
	int resource;
	ArrayList<String> stuff;
	
	public TestAdapterItem(Context context, int resource, ArrayList<String> objects) {
		super(context, resource, objects);
		this.resource = resource;
		this.context = context;
		this.stuff = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(convertView == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(resource,parent,false);
		}
		
		String currentItem = stuff.get(position);
		TextView textViewItem = (TextView) convertView.findViewById(R.id.title);
		textViewItem.setText(currentItem);
		textViewItem.setTag(position);
		return convertView;
	}

}
