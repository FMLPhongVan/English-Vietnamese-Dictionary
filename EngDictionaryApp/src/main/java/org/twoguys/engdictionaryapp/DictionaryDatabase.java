package org.twoguys.engdictionaryapp;

import org.twoguys.engdictionaryapp.TrieNode.Word;

import java.sql.*;
import java.util.ArrayList;

public class DictionaryDatabase {
    public final static String ID_COL_WORDS_TABLE = "wid";
    public final static String WORDS_COL_WORDS_TABLE = "word";
    public final static String PRONOUNCE_COL_WORDS_TABLE = "pronounce";
    public final static String TYPE_COL_DESCRIPTION_TABLE = "wordType";
    public final static String DES_COL_DESCRIPTION_TABLE = "wordDescription";
    public final static String EX_COL_DESCRIPTION_TABLE = "wordExample";
    private final static String DB_URL = "jdbc:sqlite:C:\\Users\\fmlph\\Documents\\EngDictionary\\dict.db";

    private Connection setConnect() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            return conn;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void getAllWord(ArrayList<Word> wordList) {
        try {
            Word newWord = new Word();
            Connection conn = setConnect();
            Statement stmt = conn.createStatement();
            String sql = "SELECT wid, word, pronounce FROM words";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                newWord.setID(rs.getInt(ID_COL_WORDS_TABLE));
                newWord.setWord(rs.getString(WORDS_COL_WORDS_TABLE));
                newWord.setPronounce(rs.getString(PRONOUNCE_COL_WORDS_TABLE));
                wordList.add(new Word(newWord));
            }

            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
     }

     private String handleWordData(ArrayList<String> wordData) {
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "    <head>\n" +
                "        <meta charset=\"UTF-8\"/>\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/> \n" +
                "        <link rel=\"stylesheet\" href=\"style.css\"/>\n" +
                "        <!-- <script src=\"script.js\"></script> -->\n" +
                "    </head><body><div>";

        boolean hasOlOpenTag = false;
        boolean hasOlCloseTag = true;
        for (int i = 0; i < wordData.size(); ++i) {
            if (wordData.get(i).charAt(0) == '@' && wordData.get(i).length() != 1) {
                if (!hasOlCloseTag) {
                    html += "</ol>";
                    hasOlCloseTag = true;
                    hasOlOpenTag = false;
                }
                html += "<h3>" + wordData.get(i).substring(1) + "</h3>";
                hasOlOpenTag = false;
                hasOlCloseTag = true;
            } else if (wordData.get(i).charAt(0) == '-' && wordData.get(i).length() != 1){
                if (!hasOlOpenTag) {
                    html += "<ol>";
                    hasOlOpenTag = true;
                    hasOlCloseTag = false;
                }
                html += "<li>" + wordData.get(i).substring(1);
                if (i + 1 < wordData.size() && (wordData.get(i + 1).charAt(0) != '=' || (wordData.get(i + 1).charAt(0) == '=' && wordData.get(i + 1).length() == 1))) {
                    html += "</li>";
                } else {
                    html += "<br>";
                    for (int j = i + 1; j < wordData.size() && wordData.get(j).charAt(0) == '='; ++j) {
                        String[] exs = wordData.get(j).substring(1).split("\n");
                        html += "<ul>";
                        for (int k = 0; k < exs.length; ++k) {
                            String[] tmp = exs[k].split("[+]");
                            html += "<li>" + tmp[0] + "<br><i>" + tmp[1] + "</i></li>";
                        }
                        html += "</ul>";
                        i = j;
                    }
                    html += "</li>";
                }
            }
        }
        if (hasOlOpenTag && !hasOlCloseTag) html += "</ol></div></body></html>";

        return html;
     }

     public String getWordDescriptionData(int wordID) {
        try {
            Connection conn = setConnect();
            var statement = conn.prepareStatement("""
                SELECT wordType, wordDescription, wordExample
                FROM description
                WHERE wordID = ?
            """);
            statement.setInt(1, wordID);
            ResultSet rs = statement.executeQuery();

            ArrayList<String> wordData = new ArrayList<String>();

            while (rs.next()) {
                wordData.add("@" + rs.getString(TYPE_COL_DESCRIPTION_TABLE));
                wordData.add("-" + rs.getString(DES_COL_DESCRIPTION_TABLE));
                wordData.add("=" + rs.getString(EX_COL_DESCRIPTION_TABLE));
            }

            rs.close();
            conn.close();
            String html = handleWordData(wordData);
            wordData.clear();
            return html;
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
     }
}
