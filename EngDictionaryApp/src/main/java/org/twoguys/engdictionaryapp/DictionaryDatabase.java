package org.twoguys.engdictionaryapp;

import org.twoguys.engdictionaryapp.TrieNode.Word;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class DictionaryDatabase {

    public void selectAll(ArrayList<Word> wordList) {
        String url = "jdbc:sqlite:C:\\Users\\fmlph\\Documents\\EngDictionary\\dict_hh.db";
        System.out.println(url);
        Word newWord = new Word();
        Connection conn = null;
        try {

            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            String sql = "SELECT id, word, html, description, pronounce FROM av";
            ResultSet rs   = stmt.executeQuery(sql);

            while (rs.next()) {
                newWord.setWord(rs.getString("word"));
                newWord.setDescription(rs.getString("description"));
                newWord.setPronounce(rs.getString("pronounce"));
                newWord.setHtmlData(rs.getString("html"));
                wordList.add(new Word(newWord));
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
     }
}
