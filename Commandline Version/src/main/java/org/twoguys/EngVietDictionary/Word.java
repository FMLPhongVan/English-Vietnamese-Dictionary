package org.twoguys.EngVietDictionary;

public class Word {
    private String word = "";
    private String explain = "";
    private String[] explainAndExample;

    public Word() {}

    public Word(Word newWord) {
        word = newWord.word;
        explain = newWord.explain;
        this.explainAndExample = explain.split("\\|");
    }

    public Word(String word, String explain) {
        this.word = word;
        this.explain = explain;
        this.explainAndExample = explain.split("\\|");
    }

    public void setWord(String word) {
        this.word = word.trim();
    }

    public void setExplain(String explain) {
        this.explain = explain.trim();
    }

    /**
     * Replace current word and its explain by a new word and new explain.
     */
    public void replaceWord(Word newWord) throws Exception {
        if (newWord.word.equals("") || newWord.explain.equals("")) {
            throw new Exception("You cannot enter a blank !!!");
        } else {
            this.word = newWord.word;
            this.explain = newWord.explain;
            this.explainAndExample = explain.split("\\|");
        }
    }

    public String getWord() { return word; }
    public String getExplain() { return explain; }

    public void getFullExplaination() {
        String[] meansAndEx, u;
        for (String t : explainAndExample) if (!t.equals("")) {
            meansAndEx = t.split(", =");
            System.out.println("- " + meansAndEx[0]);
            if (meansAndEx.length > 1) {
                System.out.print("Ex: ");
                for (int i = 1; i < meansAndEx.length; ++i) {
                    u = meansAndEx[i].split("\\+");
                    if (i == 1) {
                        System.out.println(u[0] + " : " + u[1]);
                    }
                    else {
                        System.out.println("    " + u[0] + " : " + u[1]);
                    }
                }
            }
        }
    }

    public String getSampleExplain() {
        int id = 0;
        while (explainAndExample[id].equals("")) ++id;
        return explainAndExample[id].split(", =")[0];
    }
}