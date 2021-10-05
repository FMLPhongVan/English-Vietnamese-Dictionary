package org.twoguys.EngVietDictionary;

public class Dictionary {

    public static void main(String[] args) {
        DictionaryManagement dictionaryManagement = new DictionaryManagement();
        dictionaryManagement.init();
        dictionaryManagement.run();
        dictionaryManagement.close();
    }
}
