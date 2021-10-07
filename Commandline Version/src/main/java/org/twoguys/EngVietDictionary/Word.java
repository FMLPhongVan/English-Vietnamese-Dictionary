package org.twoguys.EngVietDictionary;

import java.io.PrintStream;

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
        this.explainAndExample = this.explain.split("\\|");
    }

    public String getWord() { return word; }
    public String getExplain() { return explain; }

    public void getFullExplaination() {
        String[] meansAndEx, u;
        for (String t : explainAndExample) if (!t.equals("")) {
            meansAndEx = t.split(", =");
            System.out.format("|| %-150s||\n", "- " + meansAndEx[0]);
            if (meansAndEx.length > 1) {
                for (int i = 1; i < meansAndEx.length; ++i) {
                    u = meansAndEx[i].split("\\+");
                    if (i == 1) {
                        System.out.format("|| %-150s||\n", "EX: " + u[0] + " : " + u[1]);
                    }
                    else {
                        System.out.format("|| %-150s||\n", "    " + u[0] + " : " + u[1]);
                    }
                }
            }
        }
    }

    public void exportWord(PrintStream output) {
        String[] meansAndEx, u;
        for (String t : explainAndExample) if (!t.equals("")) {
            meansAndEx = t.split(", =");
            output.println("- " + meansAndEx[0]);
            if (meansAndEx.length > 1) {
                output.print("Ex: ");
                for (int i = 1; i < meansAndEx.length; ++i) {
                    u = meansAndEx[i].split("\\+");
                    if (i == 1) {
                        output.println(u[0] + " : " + u[1]);
                    }
                    else {
                        output.println("     " + u[0] + " : " + u[1]);
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