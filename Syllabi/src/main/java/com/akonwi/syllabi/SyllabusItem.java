package com.akonwi.syllabi;

/**
 * Created by akonwi on 6/7/13.
 *
 * This class represents the model of what each
 * syllabus in the application is.
 */
public class SyllabusItem {

    /**
     * Name of this item
     */
    private String name;

    /**
     * Will either be "pdf" or "webpage"
     */
    private String type;

    /**
     * Depending on this instance's 'type',
     * this will be either the url or absolute path to a file
     */
    private String data;

    private boolean selected;

    public SyllabusItem(String name, String type, String data) {
        this.name = name;
        this.type = type;
        this.data = data;
        selected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

