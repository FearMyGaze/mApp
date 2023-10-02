package com.github.fearmygaze.mercury.custom;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.github.fearmygaze.mercury.R;
import com.google.android.material.snackbar.Snackbar;

public class Snacking {

    /*
     * This Class is a single file library (sort of) that is easier to handle the snackbars
     *      for my app without having to do the same thing multiple times
     * */

    private static final int SUCCESS_ICON = R.drawable.ic_check_24;
    private static final int WARNING_ICON = R.drawable.ic_info_24;
    private static final int ERROR_ICON = R.drawable.ic_report_24;

    public static final int LENGTH_INF = Snackbar.LENGTH_INDEFINITE;
    public static final int LENGTH_LONG = Snackbar.LENGTH_LONG;
    public static final int LENGTH_SHORT = Snackbar.LENGTH_SHORT;

    public static void event(@NonNull View view, @NonNull String message) {
        hideUserKeyboard(view);
        Snackbar.make(view, " " + message, LENGTH_SHORT).show();
    }

    public static void event(@NonNull View view, @NonNull String message, @IntRange(from = -1) int duration) {
        hideUserKeyboard(view);
        Snackbar.make(view, " " + message, duration).show();
    }

    public static void eventWithAction(@NonNull View view, @NonNull String message, @NonNull String actionName, EventNotifierAction IAction) {
        hideUserKeyboard(view);
        Snackbar.make(view, " " + message, LENGTH_SHORT).setAction(actionName, v -> IAction.onActionClicked()).show();
    }

    public static void eventWithAction(@NonNull View view, @NonNull String message, @NonNull String actionName, @IntRange(from = -2) int duration, EventNotifierAction IAction) {
        hideUserKeyboard(view);
        Snackbar.make(view, " " + message, duration).setAction(actionName, v -> IAction.onActionClicked()).show();
    }

    public static void errorEvent(@NonNull View view, @NonNull String message) {
        hideUserKeyboard(view);
        Snackbar snackbar = Snackbar.make(view, " " + message, LENGTH_SHORT);
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(ERROR_ICON, 0, 0, 0);
        textView.setCompoundDrawablePadding(10);
        snackbar.show();
    }

    public static void errorEvent(@NonNull View view, @NonNull String message, @IntRange(from = -1) int duration) {
        hideUserKeyboard(view);
        Context context = view.getContext();
        Snackbar snackbar = Snackbar.make(view, " " + message, duration);
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(ERROR_ICON, 0, 0, 0);
        textView.setCompoundDrawablePadding(10);
        textView.setTextColor(context.getColor(R.color.textBold));
        snackbar.setBackgroundTint(context.getColor(R.color.basicBackground));
        snackbar.show();
    }

    public static void errorEventWithAction(@NonNull View view, @NonNull String message, @NonNull String actionName, EventNotifierAction action) {
        hideUserKeyboard(view);
        Snackbar snackbar = Snackbar.make(view, " " + message, LENGTH_SHORT);
        snackbar.setAction(actionName, v -> action.onActionClicked());
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(ERROR_ICON, 0, 0, 0);
        textView.setCompoundDrawablePadding(10);
        snackbar.show();
    }

    public static void errorEventWithAction(@NonNull View view, @NonNull String message, @NonNull String actionName, @IntRange(from = -2) int duration, EventNotifierAction action) {
        hideUserKeyboard(view);
        Snackbar snackbar = Snackbar.make(view, " " + message, duration);
        snackbar.setAction(actionName, v -> action.onActionClicked());
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(ERROR_ICON, 0, 0, 0);
        textView.setCompoundDrawablePadding(10);
        snackbar.show();
    }

    public static void warningEvent(@NonNull View view, @NonNull String message) {
        hideUserKeyboard(view);
        Snackbar snackbar = Snackbar.make(view, " " + message, LENGTH_SHORT);
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(WARNING_ICON, 0, 0, 0);
        textView.setCompoundDrawablePadding(10);
        snackbar.show();
    }

    public static void warningEvent(@NonNull View view, @NonNull String message, @IntRange(from = -1) int duration) {
        hideUserKeyboard(view);
        Snackbar snackbar = Snackbar.make(view, " " + message, duration);
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(WARNING_ICON, 0, 0, 0);
        textView.setCompoundDrawablePadding(10);
        snackbar.show();
    }

    public static void warningEventWithAction(@NonNull View view, @NonNull String message, @NonNull String actionName, EventNotifierAction action) {
        hideUserKeyboard(view);
        Snackbar snackbar = Snackbar.make(view, " " + message, LENGTH_SHORT);
        snackbar.setAction(actionName, v -> action.onActionClicked());
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(WARNING_ICON, 0, 0, 0);
        textView.setCompoundDrawablePadding(10);
        snackbar.show();
    }

    public static void warningEventWithAction(@NonNull View view, @NonNull String message, @NonNull String actionName, @IntRange(from = -2) int duration, EventNotifierAction action) {
        hideUserKeyboard(view);
        Snackbar snackbar = Snackbar.make(view, " " + message, duration);
        snackbar.setAction(actionName, v -> action.onActionClicked());
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(WARNING_ICON, 0, 0, 0);
        textView.setCompoundDrawablePadding(10);
        snackbar.show();
    }

    public static void successEvent(@NonNull View view, @NonNull String message) {
        hideUserKeyboard(view);
        Snackbar snackbar = Snackbar.make(view, " " + message, LENGTH_SHORT);
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(SUCCESS_ICON, 0, 0, 0);
        textView.setCompoundDrawablePadding(10);
        snackbar.show();
    }

    public static void successEvent(@NonNull View view, @NonNull String message, @IntRange(from = -1) int duration) {
        hideUserKeyboard(view);
        Snackbar snackbar = Snackbar.make(view, " " + message, duration);
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(SUCCESS_ICON, 0, 0, 0);
        textView.setCompoundDrawablePadding(10);
        snackbar.show();
    }

    public static void successEventWithAction(@NonNull View view, @NonNull String message, @NonNull String actionName, EventNotifierAction action) {
        hideUserKeyboard(view);
        Snackbar snackbar = Snackbar.make(view, " " + message, LENGTH_SHORT);
        snackbar.setAction(actionName, v -> action.onActionClicked());
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(SUCCESS_ICON, 0, 0, 0);
        textView.setCompoundDrawablePadding(10);
        snackbar.show();
    }

    public static void successEventWithAction(@NonNull View view, @NonNull String message, @NonNull String actionName, @IntRange(from = -2) int duration, EventNotifierAction action) {
        hideUserKeyboard(view);
        Snackbar snackbar = Snackbar.make(view, " " + message, duration);
        snackbar.setAction(actionName, v -> action.onActionClicked());
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(SUCCESS_ICON, 0, 0, 0);
        textView.setCompoundDrawablePadding(10);
        snackbar.show();
    }

    public static void customEvent(@NonNull View view, int iconID, @NonNull String message) {
        hideUserKeyboard(view);
        Snackbar snackbar = Snackbar.make(view, " " + message, LENGTH_SHORT);
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(iconID, 0, 0, 0);
        textView.setCompoundDrawablePadding(10);
        snackbar.show();
    }

    public static void customEvent(@NonNull View view, int iconID, @NonNull String message, @IntRange(from = -1) int duration) {
        hideUserKeyboard(view);
        Snackbar snackbar = Snackbar.make(view, " " + message, duration);
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(iconID, 0, 0, 0);
        textView.setCompoundDrawablePadding(10);
        snackbar.show();
    }

    public static void customEventWithAction(@NonNull View view, int iconID, @NonNull String message, @NonNull String actionName, EventNotifierAction action) {
        hideUserKeyboard(view);
        Snackbar snackbar = Snackbar.make(view, " " + message, LENGTH_SHORT);
        snackbar.setAction(actionName, v -> action.onActionClicked());
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(iconID, 0, 0, 0);
        textView.setCompoundDrawablePadding(10);
        snackbar.show();
    }

    public static void customEventWithAction(@NonNull View view, int iconID, @NonNull String message, @NonNull String actionName, @IntRange(from = -2) int duration, EventNotifierAction action) {
        hideUserKeyboard(view);
        Snackbar snackbar = Snackbar.make(view, " " + message, duration);
        snackbar.setAction(actionName, v -> action.onActionClicked());
        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(iconID, 0, 0, 0);
        textView.setCompoundDrawablePadding(10);
        snackbar.show();
    }

    private static void hideUserKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public interface EventNotifierAction {
        void onActionClicked();
    }

}
