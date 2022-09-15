package com.fearmygaze.mApp.interfaces;

public interface IConversationAdapter {
    void onConversation(int pos);
    void onDeleteConversation(int pos);
    void onRemoveFriend(int pos);
    void onReportUser(int pos);
}