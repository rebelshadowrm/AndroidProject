package com.example.project1;

public class SearchItem {
    String searchText, searchId;

    public SearchItem(String searchText, String searchId) {
        this.searchText = searchText;
        this.searchId = searchId;
    }

    public String getSearchText() {
        return searchText;
    }

    public String getSearchId() {
        return searchId;
    }
}
