package com.github.fearmygaze.mercury.firebase;

import com.github.fearmygaze.mercury.database.model.User1;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.firebase.interfaces.IFireCallback;

import java.util.List;

public interface SearchDao {

    void search(String searchParam, IFireCallback<List<User1>> response);

    void getUserById(String userId, CallBackResponse<User1> response);

    void getUserByUsername(String username, CallBackResponse<User1> response);

}
