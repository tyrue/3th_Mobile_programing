package com.example.song.songui;

/**
 * Created by Song on 2017-12-19.
 */

public class ListView_item
{
    private String titleStr;
    private boolean check;

    public ListView_item(String titleStr) {
        this.titleStr = titleStr;
        this.check = false;
    }
    public void setTitle(String text)
    {
        titleStr = text;
    }
    public String getTitleStr()
    {
        return this.titleStr;
    }
    public void setCheck(boolean c) {
        check = c;
    }
    public boolean getCheck() {
        return check;
    }
}
