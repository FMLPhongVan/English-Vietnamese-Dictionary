package org.twoguys.engdictionaryapp;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import org.twoguys.engdictionaryapp.Database.CreateDatabase;
import org.twoguys.engdictionaryapp.TableViewHelper.HistoryTable;
import org.twoguys.engdictionaryapp.TableViewHelper.WordDataTable;
import org.twoguys.engdictionaryapp.TrieTree.Word;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Properties;

public class DictionaryDatabaseHandler {
    public final static String ID_COL_WORDS_TABLE = "wid";
    public final static String WORDS_COL_WORDS_TABLE = "word";
    public final static String PRONOUNCE_COL_WORDS_TABLE = "pronounce";
    public final static String FAVOURITE_COL_WORDS_TABLE = "favourite";
    public final static String TYPE_COL_DESCRIPTION_TABLE = "wordType";
    public final static String DES_COL_DESCRIPTION_TABLE = "wordDescription";
    public final static String EX_COL_DESCRIPTION_TABLE = "wordExample";
    private final static String USER_DICTIONARY_DIR = System.getProperty("user.home").concat("\\Documents\\EngDictionary");
    private final static String DB_URL = "jdbc:sqlite:" + USER_DICTIONARY_DIR + "\\dict.db";

    public DictionaryDatabaseHandler() {
        File userDir = new File(USER_DICTIONARY_DIR);
        File dbFile = new File(USER_DICTIONARY_DIR + "\\dict.db");
        if (!userDir.exists()) {
            if (userDir.mkdir()) {
                if (!dbFile.exists()) {
                    CreateDatabase.run();
                }
            }
        } else {
            if (!dbFile.exists()) {
                try {
                    dbFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CreateDatabase.run();
            }
        }
    }

    private Connection setConnect() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            return conn;
        } catch (SQLException e) {
            System.out.println(1);
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void getAllWord(ArrayList<Word> wordList) {
        try {
            Word newWord = new Word();
            Connection conn = setConnect();
            Statement stmt = conn.createStatement();
            String sql = "SELECT wid, word, pronounce, favourite FROM words";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                newWord.setID(rs.getInt(ID_COL_WORDS_TABLE));
                newWord.setWord(rs.getString(WORDS_COL_WORDS_TABLE));
                newWord.setPronounce(rs.getString(PRONOUNCE_COL_WORDS_TABLE));
                newWord.setFavourite(rs.getInt(FAVOURITE_COL_WORDS_TABLE));
                wordList.add(new Word(newWord));
            }

            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String handleWordData(ArrayList<String> wordData) {
        String html = """
                <!DOCTYPE html>
                <html lang="en">
                    <head>
                        <meta charset="UTF-8"/>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>\s
                        <link rel="stylesheet" href="style.css"/>
                        <!-- <script src="script.js"></script> -->
                    </head><body><div>""";

        boolean hasOlOpenTag = false;
        boolean hasOlCloseTag = true;
        for (int i = 0; i < wordData.size(); ++i) {
            if (wordData.get(i).charAt(0) == '@' && wordData.get(i).length() != 1) {
                if (!hasOlCloseTag) {
                    html += "</ol>";
                    hasOlCloseTag = true;
                    hasOlOpenTag = false;
                }
                html += "<h3>" + wordData.get(i).substring(1, 2).toUpperCase() + wordData.get(i).substring(2) + "</h3>";
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

            ArrayList<String> rawData = new ArrayList<>();

            while (rs.next()) {
                rawData.add(rs.getString(TYPE_COL_DESCRIPTION_TABLE) +
                        "@-" + rs.getString( DES_COL_DESCRIPTION_TABLE) +
                        "@=" + rs.getString(  EX_COL_DESCRIPTION_TABLE));
            }

            rawData.sort(Comparator.comparing(String::toString));

            ArrayList<String> wordData = new ArrayList<>();
            String preWordType = "";
            for (int i = 0; i < rawData.size(); ++i) {
                String[] tmp = rawData.get(i).split("@");
                if (!preWordType.equals(tmp[0])){
                    wordData.add("@" + tmp[0]);
                    preWordType = tmp[0];
                }
                wordData.add(tmp[1]);
                wordData.add(tmp[2]);
            }

            rs.close();
            conn.close();
            String html = handleWordData(wordData);
            wordData.clear();
            rawData.clear();
            return html;
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void updateFavouriteStatus(int wordID, int favStatus) {
        try {
            Connection conn = setConnect();
            var statement = conn.prepareStatement("""
                UPDATE words SET favourite = ?
                WHERE wid = ?
            """);
            statement.setInt(1, favStatus);
            statement.setInt(2, wordID);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateHistoryTable(final int historyType, String word, String format) {
        try {
            Connection conn = setConnect();
            String[] tmp = format.split(" ");

            Statement stmt = conn.createStatement();
            String desTable = "searchHistory";
            if (historyType == HistoryTable.ADD) desTable = "addHistory";
            else if (historyType == HistoryTable.EDIT) desTable = "editHistory";
            else if (historyType == HistoryTable.DELETE) desTable = "deleteHistory";

            String firstEx = "UPDATE " + desTable + " SET time = \'" + tmp[0] + "\', date = \'" + tmp[1] + "\' WHERE word = \'" + word + "\';";
            String secondEx = "INSERT OR IGNORE INTO " + desTable + " (word, time, date) VALUES (\'" + word + "\',\'" + tmp[0] + "\',\'" + tmp[1] + "');";

            System.out.println(firstEx + "\n" + secondEx);

            stmt.addBatch(firstEx);
            stmt.addBatch(secondEx);
            stmt.executeBatch();

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getHistoryDataOf(final int historyType, ObservableList<HistoryTable> data) {
        try {
            Connection conn = setConnect();
            String desTable = "searchHistory";
            if (historyType == HistoryTable.ADD) desTable = "addHistory";
            else if (historyType == HistoryTable.EDIT) desTable = "editHistory";
            else if (historyType == HistoryTable.DELETE) desTable = "deleteHistory";

            Statement statement = conn.createStatement();
            String exc = "SELECT * FROM " + desTable + ";";
            ResultSet rs = statement.executeQuery(exc);

            while (rs.next()) {
                data.add(new HistoryTable(rs.getString("word"), rs.getString("time"), rs.getString("date")));
            }

            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getFavouriteData(ListView<String> listView) {
        try {
            Connection conn = setConnect();
            PreparedStatement statement = conn.prepareStatement("""
                SELECT word FROM words WHERE words.favourite = ?
            """);
            statement.setInt(1, 1);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                listView.getItems().add(rs.getString(WORDS_COL_WORDS_TABLE));
            }

            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     public void deleteWordInDatabase(int wordID) {
        try {
            Connection conn = setConnect();
            var statement = conn.prepareStatement("""
                DELETE FROM words WHERE wid = ?
            """);
            statement.setInt(1, wordID);
            statement.executeUpdate();

            statement = conn.prepareStatement("""
                DELETE FROM description WHERE  wordID = ?
            """);
            statement.setInt(1, wordID);
            statement.executeUpdate();

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
     }

     public void getDataOf(int wordID, ObservableList<WordDataTable> dataList) {
         try {
             Connection conn = setConnect();
             var statement = conn.prepareStatement("""
                SELECT wordType, wordDescription, wordExample
                FROM description
                WHERE wordID = ?
            """);
             statement.setInt(1, wordID);
             ResultSet rs = statement.executeQuery();

             while (rs.next()) {
                 dataList.add(
                         new WordDataTable(
                                 rs.getString(TYPE_COL_DESCRIPTION_TABLE),
                                 rs.getString(DES_COL_DESCRIPTION_TABLE),
                                 rs.getString(EX_COL_DESCRIPTION_TABLE)));
             }

             rs.close();
             conn.close();
         } catch (SQLException e) {
             e.printStackTrace();
         }
     }

     public void updateWordDataToDatabase(int wordID, ObservableList<WordDataTable> tableData) {
         try {
             Connection conn = setConnect();
             var stmt = conn.prepareStatement("""
                DELETE FROM description WHERE wordID = ?
             """);
             stmt.setInt(1, wordID);
             stmt.executeUpdate();

             for (int i = 0; i < tableData.size(); ++i) {

                 stmt = conn.prepareStatement("""
                    INSERT INTO description (wordID, wordType, wordDescription, wordExample)
                    VALUES (?,?,?,?);
                """);
                stmt.setInt(1, wordID);
                stmt.setString(2, tableData.get(i).getWordType());
                 stmt.setString(3, tableData.get(i).getWordDescription());
                 stmt.setString(4, tableData.get(i).getWordExample());

                 stmt.executeUpdate();
             }

             conn.close();

         } catch (SQLException e) {
             e.printStackTrace();
         }
     }

    public void addNewWordtoDatabase(Word newWord, ObservableList<WordDataTable> tableData) {
        try {
            Connection conn = setConnect();
            var stmt = conn.prepareStatement("""
                INSERT INTO words (wid, word, pronounce, favourite)
                VALUES (?,?,?,?);
             """);
            stmt.setInt(1, newWord.getID());
            stmt.setString(2, newWord.getWord());
            stmt.setString(3, newWord.getPronounce());
            stmt.setInt(4, newWord.getFavourite());
            stmt.executeUpdate();

            for (int i = 0; i < tableData.size(); ++i) {

                stmt = conn.prepareStatement("""
                    INSERT INTO description (wordID, wordType, wordDescription, wordExample)
                    VALUES (?,?,?,?);
                """);
                stmt.setInt(1, newWord.getID());
                stmt.setString(2, tableData.get(i).getWordType());
                stmt.setString(3, tableData.get(i).getWordDescription());
                stmt.setString(4, tableData.get(i).getWordExample());

                stmt.executeUpdate();
            }

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
