//package com.msadraii.multidex.data;
//
//import java.util.ArrayList;
//
///**
// * Created by Mostafa on 3/23/2015.
// */
//public class EntrySegments {
//    private ArrayList<Integer> mSegmentIds;
//    private ArrayList<Integer> mColorCodeIds;
//
//    public EntrySegments() {
//        mSegmentIds = new ArrayList<>();
//        mColorCodeIds = new ArrayList<>();
//    }
//
//    public ArrayList<Integer> getSegmentIds() {
//        return mSegmentIds;
//    }
//
//    public boolean hasSegment(int id) {
//        for (int i = 0; i < mSegmentIds.size(); i++) {
//            if (mSegmentIds.get(i) == id) {
//                return true;
//            }
//        }
//        return false;
////        return mSegmentIds.get(id);
//    }
//
//    public void removeSegment(int id) {
//        for (int i = 0; i < mSegmentIds.size(); i++) {
//            if (mSegmentIds.get(i) == id) {
//                mSegmentIds.remove(i);
//                mColorCodeIds.remove(i);
//            }
//        }
//    }
//
//    public void setSegmentIdArray(ArrayList<Integer> segmentIds) {
//        this.mSegmentIds = segmentIds;
//    }
//
//    public ArrayList<Integer> getColorCodeIds() {
//        return mColorCodeIds;
//    }
//
//    public int getColorCodeId(int id) {
//        return mColorCodeIds.get(id);
//    }
//
//    public void setColorCodeIdArray(ArrayList<Integer> colorCodeIds) {
//        this.mColorCodeIds = colorCodeIds;
//    }
//
//    public void addSegment(int id, int colorCodeId) {
//        mSegmentIds.add(id);
//        mColorCodeIds.add(colorCodeId);
//    }
//
//    public void addColorCodeId(int id) {
//        mColorCodeIds.add(id);
//    }
//
//    public int size() {
//        return mSegmentIds.size();
//    }
//
//    public int getColorForSegment(int id) {
//        return mSegmentIds.get(id);
//    }
//}
