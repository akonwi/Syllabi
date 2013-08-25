package com.akonwi.syllabi;

/**
 * Created by akonwi on 6/6/13.
 * A fragment representing a section of the app. this one
 * displays a listview containing the Syllabi.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListSyllabi extends Fragment {

    /*
     * Parent context
     * Declaring the type as MainActivity specifically
     * so public method calls can be made on that instance
     */
    public MainActivity mContext;

    /*
     * Parent Fragment Manager
     */
    public FragmentManager mFragmentManager;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Adapter for listview
     */
    private Adapter adapter;

    /**
     * ListView view
     */
    private ListView listView;

    /**
     * Collection of syllabusItems for listview
     */
    private ArrayList<SyllabusItem> items;

    public ListSyllabi(MainActivity parent, FragmentManager fragmentManager) {
        mContext = parent;
        mFragmentManager = fragmentManager;
        items = MainActivity.getItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.activity_list_syllabi, container, false);
        listView = (ListView) layout.findViewById(R.id.listView);
        adapter = new SyllabusItemAdapter(getActivity(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter((ListAdapter) adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SyllabusItem item = items.get(position);

                if (item.getType().equals("web")) {
                    Intent intent = new Intent(mContext, WebPageActivity.class);
                    intent.putExtra("page", item.getData());
                    startActivity(intent);
                }
                else {
                    Intent testIntent = new Intent(Intent.ACTION_VIEW);
                    testIntent.setType("application/pdf");

                    PackageManager packageManager = mContext.getPackageManager();
                    List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    if (list.size() > 0) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getData()));
                        startActivity(intent);
                    }
                    else {
                        new NoPdfViewerDialog().show(getFragmentManager(), "no_pdf_app");
                    }
                }
            }
        });
        registerForContextMenu(listView);
        return layout;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo info) {
        super.onCreateContextMenu(menu, view, info);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.float_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.edit:
                SyllabusItem toEdit = items.get((int) info.id);
                mContext.launchDialog(toEdit);
                return true;
            case R.id.delete:
                deleteItem((int) info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void deleteItem(int id) {
        MainActivity.getItems().remove(id);
        redraw();
    }
    public void redraw() {
        items = MainActivity.getItems();
        adapter = new SyllabusItemAdapter(getActivity(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter((ListAdapter) adapter);
    }

    public void setDeleteAdapter() {
        adapter = new SyllabusItemDeleteAdapter(getActivity(), R.layout.delete_syllabi, items);
        listView.setAdapter((ListAdapter) adapter);
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public ListView getListView() {
        return listView;
    }

    public class NoPdfViewerDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle onSavedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("No app to open Pdf")
                    .setMessage("You don't have an app installed to view Pdf files with. Adobe Reader is the recommended app to use.\n Download it now?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String adobeUrl = "https://play.google.com/store/apps/details?id=com.adobe.reader";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(adobeUrl));
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            return;
                        }
                    });

            return builder.create();
        }
    }
}