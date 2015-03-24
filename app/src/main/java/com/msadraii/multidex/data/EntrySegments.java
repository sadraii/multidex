package com.msadraii.multidex.data;

import java.util.ArrayList;

/**
 * Created by Mostafa on 3/23/2015.
 */
public class EntrySegments {
    private ArrayList<Integer> mSegmentIds;
    private ArrayList<Integer> mColorCodeIds;

    public EntrySegments() {
        mSegmentIds = new ArrayList<>();
        mColorCodeIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSegmentIds() {
        return mSegmentIds;
    }

    public int getSegmentId(int id) {
        return mSegmentIds.get(id);
    }

    public void setSegmentIdArray(ArrayList<Integer> segmentIds) {
        this.mSegmentIds = segmentIds;
    }

    public ArrayList<Integer> getColorCodeIds() {
        return mColorCodeIds;
    }

    public int getColorCodeId(int id) {
        return mColorCodeIds.get(id);
    }

    public void setColorCodeIdArray(ArrayList<Integer> colorCodeIds) {
        this.mColorCodeIds = colorCodeIds;
    }

    public void addSegmentId(int id) {
        mSegmentIds.add(id);
    }

    public void addColorCodeId(int id) {
        mColorCodeIds.add(id);
    }

    public int size() {
        return mSegmentIds.size();
    }

//    public int getColorForSegment(int id) {
//        return mSegmentIds.get(id);
//    }
}
