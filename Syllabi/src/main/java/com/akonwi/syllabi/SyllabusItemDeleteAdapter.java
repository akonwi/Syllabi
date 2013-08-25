package com.akonwi.syllabi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by akonwi on 7/16/13.
 */
public class SyllabusItemDeleteAdapter extends ArrayAdapter<SyllabusItem> {

    private ArrayList<SyllabusItem> items;
    private Context context;

    public SyllabusItemDeleteAdapter(Context context, int textViewResourceId, ArrayList<SyllabusItem> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.context = context;
    }

    static class ViewHolder {
        protected TextView text;
        protected CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.delete_syllabi, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.label);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
            viewHolder.checkbox
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            SyllabusItem element = (SyllabusItem) viewHolder.checkbox
                                    .getTag();
                            element.setSelected(buttonView.isChecked());

                        }
                    });
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(items.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(items.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(items.get(position).getName());
        holder.checkbox.setChecked(items.get(position).isSelected());
        return view;
    }
}
