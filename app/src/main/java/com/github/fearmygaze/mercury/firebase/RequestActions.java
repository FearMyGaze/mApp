package com.github.fearmygaze.mercury.firebase;

import android.content.Context;

import com.github.fearmygaze.mercury.firebase.dao.RequestActionsDao;
import com.github.fearmygaze.mercury.firebase.interfaces.CallBackResponse;
import com.github.fearmygaze.mercury.model.Profile;
import com.github.fearmygaze.mercury.model.Request;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class RequestActions implements RequestActionsDao {
    private final FirebaseFirestore database;
    private final Context ctx;

    public RequestActions(Context context) {
        this.database = FirebaseFirestore.getInstance();
        this.ctx = context;
    }

    @Override
    public Query waiting(String myUserID) {
        return database
                .collection("requests")
                .whereEqualTo("receiver", myUserID)
                .whereEqualTo("status", Request.RequestStatus.Waiting)
                .orderBy("created", Query.Direction.DESCENDING);
    }

    @Override
    public Query friends(String myUserID) {
        return database
                .collection("requests")
                .whereArrayContains("visibleTo", myUserID)
                .whereEqualTo("status", Request.RequestStatus.Friends)
                .orderBy("created", Query.Direction.DESCENDING);
    }

    @Override
    public Query blocked(String myUserID) {
        return database
                .collection("requests")
                .whereEqualTo("sender", myUserID)
                .whereEqualTo("status", Request.RequestStatus.Blocked)
                .orderBy("created", Query.Direction.DESCENDING);
    }

    @Override
    public void eventListener(String myUserID, String otherUserID, CallBackResponse<Request> callBackResponse) {
        database.collection("requests")
                .whereIn("sender", Arrays.asList(myUserID, otherUserID))
                .whereIn("receiver", Arrays.asList(myUserID, otherUserID))
                .limit(1)
                .addSnapshotListener((querySnapshot, error) -> {
                    DocumentSnapshot document;

                    if (error != null) {
                        callBackResponse.onFailure(error.getMessage());
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        document = querySnapshot.getDocuments().get(0);
                        if (document != null && document.exists()) {
                            Request request = document.toObject(Request.class);
                            if (request != null) {
                                callBackResponse.onSuccess(request);
                            } else {
                                callBackResponse.onError("Failed to parse the request");
                            }
                        } else {
                            callBackResponse.onError("Failed to parse the request");
                        }
                    } else {
                        callBackResponse.onError("Failed to get the request");
                    }
                });
    }

    @Override
    public void getRequest(String requestID, CallBackResponse<Request> callBackResponse) {
        database.collection("requests")
                .document(requestID)
                .get()
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to get the request"))
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Request request = documentSnapshot.toObject(Request.class);
                        if (request != null) {
                            callBackResponse.onSuccess(request);
                        } else {
                            callBackResponse.onError("Failed to parse the request");
                        }
                    } else {
                        callBackResponse.onError("Failed to parse the request");
                    }
                });
    }

    private void getRequest(String myUserID, String otherUserID, RequestCallBack callBack) {
        database.collection("requests")
                .whereIn("sender", Arrays.asList(myUserID, otherUserID))
                .whereIn("receiver", Arrays.asList(myUserID, otherUserID))
                .limit(1)
                .get()
                .addOnFailureListener(e -> callBack.onReturn(null))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        callBack.onReturn(querySnapshot
                                .getDocuments()
                                .get(0)
                                .toObject(Request.class)
                        );
                    } else
                        callBack.onReturn(null);
                });
    }

    @Override
    public void create(Profile myUser, Profile otherUser, CallBackResponse<String> callBackResponse) {
        getRequest(myUser.getId(), otherUser.getId(), request -> { // we get the request
            if (request != null) {
                callBackResponse.onError("Something something the request exists now");
            } else {
                DocumentReference reference = database.collection("requests").document();
                reference.set(new Request(reference.getId(),
                                Request.RequestStatus.Waiting,
                                myUser.getId(),
                                otherUser.getId(),
                                Arrays.asList(myUser.getId(), otherUser.getId()),
                                myUser,
                                otherUser))
                        .addOnFailureListener(e -> callBackResponse.onFailure("Failed to send the request"))
                        .addOnSuccessListener(unused -> callBackResponse.onSuccess(""));
            }
        });
    }

    @Override
    public void accept(String requestID, CallBackResponse<String> callBackResponse) {
        getRequest(requestID, new CallBackResponse<Request>() {
            @Override
            public void onSuccess(Request request) {
                if (request.getStatus().equals(Request.RequestStatus.Waiting)) {
                    database.collection("requests")
                            .document(requestID)
                            .update("status", Request.RequestStatus.Friends)
                            .addOnFailureListener(e -> callBackResponse.onFailure("Failed to accept the request"))
                            .addOnSuccessListener(unused -> callBackResponse.onSuccess("Request accepted"));
                } else {
                    callBackResponse.onError("The request has changed status");
                }
            }

            @Override
            public void onError(String message) {
                callBackResponse.onError(message);
            }

            @Override
            public void onFailure(String message) {
                callBackResponse.onFailure(message);
            }
        });
    }

    @Override
    public void deny(String requestID, CallBackResponse<String> callBackResponse) {
        getRequest(requestID, new CallBackResponse<Request>() {
            @Override
            public void onSuccess(Request request) {
                delete(requestID, callBackResponse);
            }

            @Override
            public void onError(String message) {

            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    @Override
    public void block(Profile myUser, Profile otherUser, CallBackResponse<String> callBackResponse) { //TODO: maybe use the getRequest
        database.collection("requests")
                .whereIn("sender", Arrays.asList(myUser.getId(), otherUser.getId()))
                .whereIn("receiver", Arrays.asList(myUser.getId(), otherUser.getId()))
                .limit(1)
                .get()
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to get the request"))
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        querySnapshot.getDocuments()
                                .get(0)
                                .getReference()
                                .delete()
                                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to delete the request"))
                                .addOnSuccessListener(unused -> {
                                    DocumentReference reference = database.collection("requests").document();
                                    reference.set(new Request(reference.getId(),
                                                    Request.RequestStatus.Blocked,
                                                    myUser.getId(),
                                                    otherUser.getId(),
                                                    Arrays.asList(myUser.getId(), otherUser.getId()),
                                                    myUser, otherUser))
                                            .addOnFailureListener(e -> callBackResponse.onFailure("Failed to block the user"))
                                            .addOnSuccessListener(unused1 -> callBackResponse.onSuccess(otherUser.getUsername() + "Blocked successfully"));
                                });
                    } else {
                        callBackResponse.onError("Failed to get the request");
                    }
                });
    }

    @Override
    public void unBlock(String requestID, CallBackResponse<String> callBackResponse) {
        delete(requestID, callBackResponse);
    }

    @Override
    public void delete(String requestID, CallBackResponse<String> callBackResponse) {
        database.collection("requests")
                .document(requestID)
                .delete()
                .addOnFailureListener(e -> callBackResponse.onFailure("Failed to delete the request"))
                .addOnSuccessListener(unused -> callBackResponse.onSuccess("Request removed"));
    }

    @Override
    public void report(String myUserID, Profile otherUser, CallBackResponse<String> callBackResponse) {

    }

    ///////////////////////////////////////////////////////////////////////////
    // Local helpers
    ///////////////////////////////////////////////////////////////////////////

    private interface RequestCallBack {
        void onReturn(Request request);
    }
}
