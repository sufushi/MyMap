package com.rdc.mymap.utils;

import android.util.Log;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.ArrayList;
import java.util.List;

public class SuggestionSearchUtil {

    private SuggestionSearch mSuggestionSearch;
    private MySuggestionSearchListener mMySuggestionSearchListener;

    private List<SuggestionResult.SuggestionInfo> mSuggestionInfoList = new ArrayList<SuggestionResult.SuggestionInfo>();

    public SuggestionSearchUtil() {
        mSuggestionSearch = SuggestionSearch.newInstance();
        mMySuggestionSearchListener = new MySuggestionSearchListener();
        mSuggestionSearch.setOnGetSuggestionResultListener(mMySuggestionSearchListener);
    }

    public List<SuggestionResult.SuggestionInfo> getSuggestionInfoList() {
        return mSuggestionInfoList;
    }

    public void setSuggestionInfoList(List<SuggestionResult.SuggestionInfo> suggestionInfoList) {
        this.mSuggestionInfoList = suggestionInfoList;
    }

    public void searchSuggestion(String city, String keyword) {
        mSuggestionSearch.requestSuggestion(new SuggestionSearchOption().city(city).keyword(keyword));
    }

    public void destroy() {
        mSuggestionSearch.destroy();
    }

    private class MySuggestionSearchListener implements OnGetSuggestionResultListener {

        @Override
        public void onGetSuggestionResult(SuggestionResult suggestionResult) {
            if(suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                return;
            }
            mSuggestionInfoList = suggestionResult.getAllSuggestions();
            for(SuggestionResult.SuggestionInfo suggestionInfo : mSuggestionInfoList) {
                Log.e("error", "city=" + suggestionInfo.city + "  district=" + suggestionInfo.district + "  key=" + suggestionInfo.key);
            }
        }
    }

}
