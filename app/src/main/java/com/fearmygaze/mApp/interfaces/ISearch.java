package com.fearmygaze.mApp.interfaces;

import com.fearmygaze.mApp.model.SearchedUser;

import java.util.List;

public interface ISearch {
    void onSuccess(List<SearchedUser> searchedUserList);
    void onError(String message);
}