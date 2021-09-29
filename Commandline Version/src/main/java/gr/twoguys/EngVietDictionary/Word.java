package gr.twoguys.EngVietDictionary;

public class Word {
    private String word = "";
    private String explain = "";

    public Word(String word, String explain) {
        this.word = word;
        this.explain = explain;
    }

    public void modifyWord(String word) throws Exception {
        if (word.equals("")) {
            throw new Exception("You cannot enter a blank !!!");
        } else {
            this.word = word;
        }
        
    }

    public void modifyExplain(String explain) throws Exception {
        if (explain.equals("")) {
            throw new Exception("You cannot enter a blank !!!");
        } else {
            this.explain = explain;
        }
    }

    /**
     * Replace current word and its explain by a new word and new explain.
     */
    public void replaceWord(Word newWord) throws Exception {
        if (newWord.word.equals("") || newWord.word.equals("")) {
            throw new Exception("You cannot enter a blank !!!");
        } else {
            this.word = newWord.word;
            this.explain = newWord.explain;
        }
    }

    public String getWord() { return word; }
    public String getExplain() { return explain; }
}