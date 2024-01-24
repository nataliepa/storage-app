//package com.project.storage.storage.Helper;
//
//
//
//import java.util.ArrayList;
//import java.util.List;
//import static java.lang.Integer.parseInt;
//
//
//public class NamedObject<T> {
//    public String Page;
//    public List<T> allObject;
//    public final static int recordsByPage = 30; // eggrafes an selida
//
//
//    public NamedObject(String Page, List<T> allObject) {
//        this.Page = Page;
//        this.allObject = allObject;
//    }
//
//
//    public List<T> bypages() {
//
//        List<T> pagination = new ArrayList<>();
//
//        int start = 0;
//        int end = (recordsByPage);
//        int currentPage = 1;
//        if (Page == null) {
//            Page = "1";
//        }
//
//        double paging = ((double) (allObject.size()) / recordsByPage);
//        int totalPages = (int) Math.ceil(paging);
//        List<Integer> pages = new ArrayList<>();
//        for (int i = 1; i <= totalPages; i++) {
//            pages.add(i);
//        }
//
//        if (Page.trim() != null) {
//            currentPage = parseInt(Page);
//            for (int j = 1; j <= totalPages; j++) {
//
//                if (currentPage == j) {
//                    if (paging < 1) {
//                        pagination = new ArrayList<>(allObject.subList(start, allObject.size()));  // 0 -1
//                    } else {
//                        pagination = new ArrayList<>(allObject.subList(start, end));  // 0 -1
//                    }
//
//
//                } else {
//                    start = end;
//                    end = start + (recordsByPage);
//
//                    if (j == totalPages - 1) {
//                        end = allObject.size();
//                    }
//                }
//
//
//
//            }
//
//
//        }
//        return pagination;
//    }
//
//}
//
//
//
//
//
//
//
//
//
//
//
