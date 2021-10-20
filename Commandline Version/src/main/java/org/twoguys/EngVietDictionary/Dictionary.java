package org.twoguys.EngVietDictionary;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class Dictionary {
    public static void main(String[] args) throws UnsupportedEncodingException {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        DictionaryManagement dictionaryManagement = new DictionaryManagement();
        dictionaryManagement.init();
        dictionaryManagement.run();
        dictionaryManagement.close();
    }
}
