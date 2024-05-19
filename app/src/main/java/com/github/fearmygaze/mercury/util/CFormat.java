package com.github.fearmygaze.mercury.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.github.fearmygaze.mercury.model.User;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CFormat {

    /*
     *  TODO:
     *      We need to write support for the following formatting (markDown - SteamFormat)
     *          Bold        (D)
     *          Italics     (D)
     *          Heading_1   ()
     *          Heading_2   ()
     *          Body        (not need it)
     *          phone(starting with 69, +30, 2310)
     *          Time(11:20)
     *          Date (13-11-1998, 13-11-98, 13/11/98, 13/11/1998)
     *      and also simple formatting for
     *          profileCreated
     *          messageSend
     *     Also if there is a regEx move it to RegEx File
     *
     * */

    public static String formatMsg(String text, Context context) {
        SpannableString spannable = new SpannableString(text);
        HashMap<String, String> filters = RegEx.msgFormats;
        String val, matchedText;

        for (String key : filters.keySet()) {
            val = filters.get(key);
            assert val != null;
            Pattern pattern = Pattern.compile(val);
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                matchedText = matcher.group();
                int start = matcher.start();
                int end = matcher.end();

                switch (key) {
                    case "italic":
                        setItalic(start, end, spannable);
                        spannable.setSpan(new ForegroundColorSpan(Color.GREEN), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        Log.d("customLog", "CFormat.java:format:Line:63" + "Italic: " + matchedText);
                        break;
                    case "bold":
                        setBold(start, end, spannable);
                        spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        Log.d("customLog", "CFormat.java:format:Line:68" + "Bold: " + matchedText);
                        break;
                    case "Title":
                        setTitle(start, end, spannable, context);
                        spannable.setSpan(new ForegroundColorSpan(Color.YELLOW), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    default:
                        spannable.setSpan(new ForegroundColorSpan(Color.MAGENTA), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        Log.d("customLog", "CFormat.java:format:Line:65" + "not found for key: " + key + " With word: " + matchedText);
                        break;
                }
            }
            if (!matcher.find()) {
                Log.d("customLog", "CFormat.java:format:Line:81" + "inside");
                spannable.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, text.trim().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return spannable.toString();
    }

    private static SpannableString formatBio(String inputText, int color, User.OnTextListener onClickListener) {
        SpannableString spannableString = new SpannableString(inputText);
        String[] list = RegEx.bioFilters;
        for (String regex : list) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(inputText.trim());

            while (matcher.find()) {
                String matchedText = matcher.group();
                int start = matcher.start();
                int end = matcher.end();

                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View view) {
                        if (onClickListener != null) {
                            onClickListener.onClick(matchedText); //TODO: Pass the type of clickable to not make the check more times
                        }
                    }
                };
                spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    private static SpannableString setTitle(int start, int end, SpannableString spS, Context context) {
        spS.setSpan(new TextAppearanceSpan(context, android.R.style.TextAppearance_Material_Title), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spS;
    }

    private static SpannableString setBold(int start, int end, SpannableString spS) {
        spS.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spS;
    }

    private static SpannableString setItalic(int start, int end, SpannableString spS) {
        spS.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spS;
    }

    private static SpannableString setItalic(String text) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private static SpannableString setHighLight(int start, int end, int color, SpannableString spS) {
        spS.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spS;
    }
}
