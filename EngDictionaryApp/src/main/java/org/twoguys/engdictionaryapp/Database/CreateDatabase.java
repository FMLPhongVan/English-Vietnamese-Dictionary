package org.twoguys.engdictionaryapp.Database;

import org.twoguys.engdictionaryapp.DictionaryDatabaseHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class CreateDatabase {
    private final static String DB_URL = System.getProperty("user.home").concat("\\Documents\\EngDictionary\\dict.db");


    private static boolean checkNumeric(String input) {
        try {
            int t = Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void run() {
        try {
            System.out.println(DB_URL);
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_URL);
            createTable(conn);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(DictionaryDatabaseHandler.class.getResourceAsStream("English.txt"), "UTF-8"));
            String data;
            int cnt = 0;
            conn.setAutoCommit(false);
            while ((data = reader.readLine()) != null) {
                if (checkNumeric(data)) {
                    int wordID = Integer.parseInt(data);

                    String word = "";
                    String pronouce = "";
                    String wordType = "";
                    String wordExample = "";
                    String wordDescription = "";

                    word = reader.readLine();
                    data = reader.readLine();

                    int first = -1;
                    int second = 0;
                    for (int i = 0; i < data.length(); ++i) {
                        if (data.charAt(i) == '/' && data.charAt(i - 1) == ' ') {
                            first = i;
                            for (int j = i + 1; j < data.length(); ++j) {
                                if (data.charAt(j) == '/') {
                                    second = j;
                                    break;
                                }
                            }
                            break;
                        }
                    }

                    if (first != -1 && second != 0) {
                        pronouce = data.substring(first + 1, second);
                    }

                    insertWordTable(conn, word, pronouce);

                    ArrayList<String> wordData = new ArrayList<>();
                    while (!(data = reader.readLine()).equals("")) {
                        wordData.add(data);
                    }

                    wordType = "định nghĩa";
                    for (int i = 0; i < wordData.size(); ++i) {
                        if (wordData.get(i).charAt(0) == '*' || wordData.get(i).charAt(0) == '!') {
                            wordType = wordData.get(i).substring(1).trim();
                            if (wordData.get(i).charAt(0) == '!') wordType = "thành ngữ: " + wordType;
                            ++i;
                        }
                        if (i < wordData.size()) {
                            wordDescription = wordData.get(i).substring(1).trim();
                            ++i;
                            for (int j = i; j < wordData.size() && wordData.get(j).charAt(0) == '='; ++j) {
                                wordExample += wordData.get(j).substring(1).trim() + "\n";
                                i = j;
                            }
                        }
                        //System.out.println(cnt + " " + wordID + " " + wordType + " " + wordDescription + " " + wordExample);
                        ++cnt;
                        insertDescriptionTable(conn, cnt, wordID, wordType, wordDescription, wordExample);
                        wordDescription = "";
                        wordExample = "";
                    }

                }
            }
            conn.commit();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            String wordTable = "CREATE TABLE IF NOT EXISTS \"words\" (\n" +
                    "\t\"wid\"\tINTEGER NOT NULL UNIQUE,\n" +
                    "\t\"word\"\tTEXT NOT NULL,\n" +
                    "\t\"pronounce\"\tTEXT,\n" +
                    "\t\"favourite\"\tINTEGER NOT NULL,\n" +
                    "\tPRIMARY KEY(\"wid\")\n" +
                    ");";
            stmt.execute(wordTable);

            stmt = conn.createStatement();
            String descriptionTable = "CREATE TABLE \"description\" (\n" +
                    "\t\"id\"\tINTEGER NOT NULL UNIQUE,\n" +
                    "\t\"wordID\"\tINTEGER NOT NULL,\n" +
                    "\t\"wordType\"\tTEXT,\n" +
                    "\t\"wordDescription\"\tTEXT,\n" +
                    "\t\"wordExample\"\tTEXT,\n" +
                    "\tPRIMARY KEY(\"id\")\n" +
                    ")";
            stmt.execute(descriptionTable);

            String addHistoryTable = "CREATE TABLE \"addHistory\" (\n" +
                    "\t\"word\"\tTEXT NOT NULL UNIQUE,\n" +
                    "\t\"time\"\tTEXT NOT NULL,\n" +
                    "\t\"date\"\tTEXT NOT NULL,\n" +
                    "\tPRIMARY KEY(\"word\")\n" +
                    ")";
            stmt.execute(addHistoryTable);

            String editHistoryTable = "CREATE TABLE \"editHistory\" (\n" +
                    "\t\"word\"\tTEXT NOT NULL UNIQUE,\n" +
                    "\t\"time\"\tTEXT NOT NULL,\n" +
                    "\t\"date\"\tTEXT NOT NULL,\n" +
                    "\tPRIMARY KEY(\"word\")\n" +
                    ")";
            stmt.execute(editHistoryTable);

            String deleteHistoryTable = "CREATE TABLE \"deleteHistory\" (\n" +
                    "\t\"word\"\tTEXT NOT NULL UNIQUE,\n" +
                    "\t\"time\"\tTEXT NOT NULL,\n" +
                    "\t\"date\"\tTEXT NOT NULL,\n" +
                    "\tPRIMARY KEY(\"word\")\n" +
                    ")";
            stmt.execute(deleteHistoryTable);

            String searchHistoryTable = "CREATE TABLE \"searchHistory\" (\n" +
                    "\t\"word\"\tTEXT NOT NULL UNIQUE,\n" +
                    "\t\"time\"\tTEXT NOT NULL,\n" +
                    "\t\"date\"\tTEXT NOT NULL,\n" +
                    "\tPRIMARY KEY(\"word\")\n" +
                    ")";
            stmt.execute(searchHistoryTable);
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertWordTable(Connection conn, String word, String pronouce) {
        try {
            String sql = "INSERT INTO words (word, pronounce, favourite) VALUES(?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, word);
            pstmt.setString(2, pronouce);
            pstmt.setInt(3, 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insertDescriptionTable(Connection conn, int id, int wordID, String wordType, String wordDescription, String wordExample) {
        try {
            String sql = "INSERT INTO description(id, wordID, wordType, wordDescription, wordExample) VALUES(?,?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setInt(2, wordID);
            pstmt.setString(3, wordType);
            pstmt.setString(4, wordDescription);
            pstmt.setString(5, wordExample);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
