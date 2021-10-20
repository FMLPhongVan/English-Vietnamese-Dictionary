package org.twoguys.engdictionaryapp.TableViewHelper;

public class WordDataTable {
    private String wordType = "";
    private String wordDescription = "";
    private String wordExample = "";

    public WordDataTable() {};

    public WordDataTable(String wordType, String wordDescription, String wordExample) {
        this.wordType = wordType;
        this.wordDescription = wordDescription;
        this.wordExample = wordExample;
    }

    public String getWordType() {
        return wordType;
    }

    public String getWordDescription() {
        return wordDescription;
    }

    public String getWordExample() {
        return wordExample;
    }

    public void setWordType(String wordType) {
        this.wordType = wordType;
    }

    public void setWordDescription(String wordDescription) {
        this.wordDescription = wordDescription;
    }

    public void setWordExample(String wordExample) {
        this.wordExample = wordExample;
    }
}
