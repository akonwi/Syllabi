package com.akonwi.syllabi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class MainActivity extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    /**
     * The object to store information used in making a new SyllabusItem
     */
    private ViewHolder vHolder;

    /**
     * Collection of SyllabusItems to be displayed in listview
     */
    private static ArrayList<SyllabusItem> items;

    /**
     * Collection of child fragments
     */
    private ArrayList<Fragment> childrenFragments;

    /**
     * ActionMode
     */
    protected Object mActionMode;

    /**
     * File name for the file storing json collection of SyllabusItems
     */
    private final String FILE_NAME = "Syllabi.json";

    /**
     * The Syllabusitem to edit. will be null unless one exists
     */
    private SyllabusItem toEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = new ArrayList<SyllabusItem>();
        toEdit = null;
        childrenFragments = new ArrayList<Fragment>();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case  R.id.add:
                new WhichSyllabiDialog().show(getSupportFragmentManager(), "which_syllabi_dialog");
                return true;
            case R.id.delete:
                mActionMode = this.startActionMode(mActionModeCallback);
                ((ListSyllabi) childrenFragments.get(0)).getListView().setSelected(true);
                return true;
            default:
                return false;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == 0)
            if (data != null)
                vHolder.setData(data.getData().toString());
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            ListSyllabi listFragment = (ListSyllabi) childrenFragments.get(0);
            listFragment.setDeleteAdapter();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.delete:
                    Iterator<SyllabusItem> it = items.iterator();
                    while(it.hasNext())
                        if(it.next().isSelected())
                            it.remove();

                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            Adapter adapter = ((ListSyllabi) childrenFragments.get(0)).getAdapter();
            for(int i = 0; i < adapter.getCount(); i++) {
                SyllabusItem item = (SyllabusItem) adapter.getItem(i);
                if(item.isSelected())
                    item.setSelected(false);
            }
            mActionMode = null;
            onPause();
            ((ListSyllabi) childrenFragments.get(0)).redraw();
        }
    };

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment listFragment = new ListSyllabi(MainActivity.this, getSupportFragmentManager());
            Fragment settingsFragment = new ListSyllabi(MainActivity.this, getSupportFragmentManager());
            childrenFragments.add(listFragment);
            childrenFragments.add(settingsFragment);
            Bundle args = new Bundle();
            args.putInt(ListSyllabi.ARG_SECTION_NUMBER, position + 1);
            listFragment.setArguments(args);
            return childrenFragments.get(position);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    public class WhichSyllabiDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("What type of syllabus?")
                    .setItems(R.array.syllabusType, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(which == 0) {
                                new WebSyllabiDialog().show(getSupportFragmentManager(), "web_syllabi");
                                vHolder = new ViewHolder(getBaseContext());
                                dialog.dismiss();
                            }
                            else {
                                new PdfSyllabiDialog().show(getSupportFragmentManager(), "pdf_syllabi");
                                vHolder = new ViewHolder(getBaseContext());
                                dialog.dismiss();
                            }
                        }
                    });

            return builder.create();
        }
    }

    public class PdfSyllabiDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View layout = inflater.inflate(R.layout.add_pdf_syllabus, null);
            Button button = (Button) layout.findViewById(R.id.getFile);
            EditText name = (EditText) layout.findViewById(R.id.editText1);
            if(toEdit != null) {
                name.setText(toEdit.getName());
            }
            vHolder.setButton(button);
            vHolder.setNameBox(name);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getPdf();
                }
            });

            builder.setTitle(R.string.new_syllabus_title)
                    .setView(layout)
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            items.remove(toEdit);
                            toEdit = null;
                            SyllabusItem item = new SyllabusItem(vHolder.getName(), "pdf", vHolder.getData());
                            items.add(item);
                            ListSyllabi list = (ListSyllabi) childrenFragments.get(0);
                            list.redraw();
                            vHolder = null;

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            PdfSyllabiDialog.this.getDialog().cancel();
                            toEdit = null;
                            vHolder = null;
                        }
                    });

            return builder.create();
        }
    }

    public class WebSyllabiDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View layout = inflater.inflate(R.layout.add_web_syllabus, null);
            EditText nameField = (EditText) layout.findViewById(R.id.nameField);
            EditText urlField = (EditText) layout.findViewById(R.id.urlField);
            if(toEdit != null) {
                nameField.setText(toEdit.getName());
                urlField.setText(toEdit.getData().toString());
            }
            vHolder.setNameBox(nameField);
            vHolder.setUrlBox(urlField);

            builder.setTitle(R.string.new_syllabus_title)
                    .setView(layout)
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if(toEdit != null) {
                                items.remove(toEdit);
                                toEdit = null;
                            }
                            SyllabusItem item = new SyllabusItem(vHolder.getName(), "web", vHolder.getUrl());
                            items.add(item);
                            ListSyllabi list = (ListSyllabi) childrenFragments.get(0);
                            list.redraw();
                            vHolder = null;
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            WebSyllabiDialog.this.getDialog().cancel();
                            vHolder = null;
                        }
                    });

            return builder.create();
        }
    }

    /**
     * create an intent and allow the user to find pdf file
     */
    public void getPdf() {
        Intent imp = new Intent();
        imp.setType("application/pdf");
        imp.setAction(Intent.ACTION_GET_CONTENT);
        imp.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(imp, 0);
    }

    /**
     * Get the collection of syllabusitems for listview
     * @return ArrayList<SyllabusItem>
     */
    public static ArrayList<SyllabusItem> getItems() {
        if(items == null)
            items = new ArrayList<SyllabusItem>();
        return items;
    }

    /**
     * Launch a new dialog routine for editing an item
     * @param item the item to edit
     */
    public void launchDialog(SyllabusItem item) {
        vHolder = new ViewHolder(getBaseContext());
        toEdit = item;
        if(item.getType().equals("pdf"))
            new PdfSyllabiDialog().show(getSupportFragmentManager(), "pdf_syllabi_dialog");
        else
            new WebSyllabiDialog().show(getSupportFragmentManager(), "web_syllabi_dialog");
    }

    public void onStop() {
        super.onStop();

        File file = new File(getFilesDir(), FILE_NAME);
        if(!file.exists())
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(items);

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            writer.write(json);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (writer != null)
                try {
                    writer.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void onResume() {
        super.onResume();
        File file = new File(getFilesDir(), FILE_NAME);

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        Gson gson = new Gson();
        Type arrayListType = new TypeToken<ArrayList<SyllabusItem>>() {}.getType();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            items = gson.fromJson(reader, arrayListType);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}