package com.akonwi.syllabi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by akonwi on 6/7/13.
 *
 * An adapter for the SyllabusItem to construct into
 * a listview
 */
public class SyllabusItemAdapter extends ArrayAdapter<SyllabusItem> {

    /*
     * Collection of SyllabusItems
     */
    private ArrayList<SyllabusItem> items;

    /*
     * Context
     */
    private Context mContext;

    public SyllabusItemAdapter(Context context, int textViewResourceId, ArrayList<SyllabusItem> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            LayoutInflater vi = (LayoutInflater) LayoutInflater.from(mContext);
            v = vi.inflate(R.layout.row_layout, null);
        }

        SyllabusItem item = items.get(position);

        if(item != null) {
            TextView textView = (TextView) v.findViewById(R.id.text);
            if(textView != null)
                textView.setText(item.getName());
        }

        return v;
    }
}
