package org.twoguys.EngVietDictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

public class DictionaryManagement {
    class TrieNode {    
        int stop;
        int cnt;
        int[] child = new int[70];
        Word word;

        public TrieNode() {
            stop = cnt = 0;
            for (int j = 0; j < 70; ++j) {
                child[j] = 0;
            }
        }
    }

    private int nNode = 0;
    private ArrayList<Word> wordList = new ArrayList<Word>();
    private ArrayList<TrieNode> trieList = new ArrayList<TrieNode>();
    private Scanner sc = new Scanner(System.in);

    private void addWord(Word newWord) {
        int r = 0;
        for (int i = 0; i < newWord.getWord().length(); ++i) {
            int k = (int) newWord.getWord().charAt(i);
            int l = k;
            if (l >= (int) '0' && l <= '9') k = k - (int) '0';
            else if (l >= (int) 'A' && l <= 'Z') k = k - (int) 'A' + 10;
            else if (l >= (int) 'a' && l <= 'z') k = k - (int) 'a' + 36; 
            else if (l == (int) '.') k = 63;
            else if (l == (int) '\'') k = 64;
            else if (l == (int) '-') k = 65;
            else if (l == (int) '(' || l == (int) '[') k = 66;
            else if (l == (int) ')' || l == (int) ']') k = 67;
            else if (l == (int) ' ') k = 69;
            else if (l == 246) k = 68;
            else break;
            if (trieList.get(r).child[k] == 0) {
                trieList.get(r).child[k] = ++nNode;
                trieList.add(new TrieNode());
            } 
            r = trieList.get(r).child[k];
        }
        ++trieList.get(r).stop;
        if (Objects.isNull(trieList.get(r).word)) {
            trieList.get(r).word = new Word(newWord);
        }
        else {
            String newExplain = trieList.get(r).word.getExplain().concat(newWord.getExplain().substring(1));
            trieList.get(r).word.setExplain(newExplain);
        }
    }
    
    private void searchWord(String word) {
        int r = 0;
        for (int i = 0; i < word.length(); ++i) {
            int k = (int) word.charAt(i);
            int l = k;
            if (l >= (int) '0' && l <= '9') k = k - (int) '0';
            else if (l >= (int) 'A' && l <= 'Z') k = k - (int) 'A' + 10;
            else if (l >= (int) 'a' && l <= 'z') k = k - (int) 'a' + 36; 
            else if (l == (int) '.') k = 63;
            else if (l == (int) '\'') k = 64;
            else if (l == (int) '-') k = 65;
            else if (l == (int) '(' || l == (int) '[') k = 66;
            else if (l == (int) ')' || l == (int) ']') k = 67;
            else if (l == (int) ' ') k = 69;
            else if (l == 246) k = 68;
            else break;
            if (trieList.get(r).child[k] == 0) {
                System.out.println("Not found this word.");
                return;
            }
            r = trieList.get(r).child[k];
        }
        System.out.println("Meaning: ");
        trieList.get(r).word.getFullExplaination();
    }

    public void init() {
        trieList.add(new TrieNode());
        
        String data = "";
        Word newWord = new Word();
        try {
            InputStream input = DictionaryManagement.class.getResourceAsStream("/eng-dictionaries.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            while ((data = reader.readLine()) != null) {
                int i = 0;
                for (; i < data.length(); ++i) {
                    if (data.charAt(i) == '/' || data.charAt(i) == '|') {
                        newWord.setWord(data.substring(1, i - 1));
                        if (data.charAt(i) == '|') {
                            --i;
                            break;
                        }
                        ++i;
                        for (; i < data.length(); ++i) {
                            if (data.charAt(i) == '/') {
                                ++i; 
                                break;
                            }
                        }
                        break;
                    }
                }

                if (i + 1 <= data.length()) {
                    newWord.setExplain(data.substring(i + 1));
                    wordList.add(new Word(newWord));
                    addWord(newWord);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("An error occurred when loading source file.");
            e.printStackTrace();
        }
        Collections.sort(wordList, new Comparator<Word>() {
            @Override
            public int compare(Word a, Word b) {
                return a.getWord().compareTo(b.getWord());
            }
        });
    }

    public void run() {
        System.out.println("                English-Vietnames Dictionary\n" + 
                           "                Written by                  \n" + 
                           "                                              "); 
        
        System.out.println("Chao mung ban da den voi Tu dien Anh-Viet. Duoi day la huong dan su dung:\n");
        System.out.println("Tu dien Anh-Viet co cac chuc nang chinh sau day:\n");
        System.out.println("0 : Hien thi tat ca tu trong tu dien (In ra se lau do co gan 109k tu).\n");
        System.out.println("1 : Them tu vao trong tu dien.\n");
        System.out.println("2 : Tim kiem tu (chinh xac tu ban nhap).\n\n");
        System.out.println("Muon su dung 1 chuc nang cua tu dien, hay nhan so va nhan Enter.\n");
        System.out.println("Vi du: - Tim kiem tu \"home\", dau tien nhap 2 sau do Enter.\n");
        System.out.println("       - Sau do ban go \"home\", an Enter va tu dien se tra ket qua.\n\n");
        System.out.println("Neu khong muon su dung cac chuc nang lien quan den tim kiem, hay nhap \"EXIT\" va an Enter.\n");
        System.out.println("Neu muon thoat khoi tu dien, nhap \"QUIT\" va an Enter.\n\n");

        boolean enable = true;
        String input = "";
        while (enable) {
            input = sc.nextLine();
            if (input.equals("0")) showAllWord();
            else if (input.equals("1")) insertFromCommandline();
            else if (input.equals("2")) dictionaryLookup();
            else if (input.equals("QUIT")) enable = false;
            else {
                System.out.println("Error! Not recognize this command.");
            }
        }
    }

    public void close() {
        sc.close();
    }

    public void insertFromCommandline() {
        int numOfNewWords = 0;

        String input = ""; 
        while (true) {
            try {
                input = sc.nextLine();
                numOfNewWords = Integer.parseInt(input);
                if (numOfNewWords <= 0) {
                    System.out.println("Error! You must enter a natural number. Try again!");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error! You must enter a natural number. Try again!");
            }
        }

        Word newWord = new Word();
        String word = "", explain = "";
        for (int i = 0; i < numOfNewWords; ++i) {
            word = ""; explain = "";
            while (true) {
                word = sc.nextLine().trim();
                if (word.equals("")) {
                    System.out.println("Error! You are not allowed to leave this blank.");
                } else {
                    explain = sc.nextLine().trim(); 
                    if (explain.equals("")) {
                        System.out.println("Error! You are not allowed to leave this blank.");
                    } else {
                        newWord.setWord(word);
                        newWord.setExplain(explain);
                        break;
                    }
                }
            }
            wordList.add(newWord);
            addWord(newWord);
        }
    }

    public void showAllWord() {
        System.out.println("=".repeat(114));
        System.out.format("||%-8s|%-30s|%-70s||\n", " STT", " Tieng Anh", " Tieng Viet");
        String word = "", sampleExplain = "";

        for (int i = 0; i < wordList.size(); ++i) {
            word = wordList.get(i).getWord();
            if (word.length() > 25)  word = word.substring(0, 26) + "..."; 
                
            sampleExplain = wordList.get(i).getSampleExplain();
            if (sampleExplain.length() > 65) sampleExplain = sampleExplain.substring(0, 66) + "...";

            System.out.format("|| %-8d| %-30s| %-70s||\n", i, word, sampleExplain);
        }
        System.out.println("=".repeat(114));
    }

    public void dictionaryLookup() {
        String input = "";
        boolean enable = true;

        while (enable) {
            while (true) {
                input = sc.nextLine();
                if (!Pattern.matches("[a-zA-Z]+", input)) {
                    System.out.println("Your word can only contain letters from a-z and A-Z");
                    
                } else {
                    if (input.equals("EXIT")) {
                        enable = false;
                        break;
                    }
                    searchWord(input);
                }
            }
        }
    }
}
