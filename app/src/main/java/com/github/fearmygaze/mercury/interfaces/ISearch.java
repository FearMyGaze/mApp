package com.github.fearmygaze.mercury.interfaces;

import com.github.fearmygaze.mercury.model.SearchedUser;

import java.util.List;

public interface ISearch {
    void onSuccess(List<SearchedUser> searchedUserList);
    void onError(String message);
}