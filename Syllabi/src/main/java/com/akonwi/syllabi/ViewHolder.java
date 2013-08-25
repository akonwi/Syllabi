package com.akonwi.syllabi;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by akonwi on 6/10/13.
 *
 * This class will keep track of the different
 * view elements being used by the dialogs in the
 * MainActivity in the process of creating a new
 * SyllabusItem
 */
public class ViewHolder {

    /**
     * Parent context
     */
    private Context mContext;
    private EditText nameBox;
    private EditText urlBox;
    private Button getFile;
    private String data;

    public ViewHolder(Context parent) {
        mContext = parent;
    }

    public void setNameBox(EditText box) {
        nameBox = box;
    }

    public String getName() {
        return nameBox.getText().toString();
    }

    public String getUrl() {
        return urlBox.getText().toString();
    }

    public void setUrlBox(EditText urlBox) {
        this.urlBox = urlBox;
    }

    public void setButton(Button button) {
        getFile = button;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
