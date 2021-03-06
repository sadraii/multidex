package com.sadraii.hyperdex;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table ENTRY.
 */
public class Entry {

    private Long id;
    private java.util.Date date;
    private String segments;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Entry() {
    }

    public Entry(Long id) {
        this.id = id;
    }

    public Entry(Long id, java.util.Date date, String segments) {
        this.id = id;
        this.date = date;
        this.segments = segments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public String getSegments() {
        return segments;
    }

    public void setSegments(String segments) {
        this.segments = segments;
    }

    // KEEP METHODS - put your custom methods here
    public boolean hasSegments() {
        return !(segments == null || segments.equals(""));
    }
    // KEEP METHODS END

}
