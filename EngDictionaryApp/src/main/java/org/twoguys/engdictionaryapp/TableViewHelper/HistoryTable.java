package org.twoguys.engdictionaryapp.TableViewHelper;

public class HistoryTable {
    public final static int SEARCH = 0;
    public final static int ADD    = 1;
    public final static int EDIT   = 2;
    public final static int DELETE  = 3;
    private String word = "";
    private String time = "";
    private String date = "";

    public HistoryTable() {}

    public HistoryTable(String word, String time, String date) {
        this.word = word;
        this.time = time;
        this.date = date;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWord() {
        return word;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }
}
