package org.twoguys.EngVietDictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
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
        int[] child = new int[71];
        Word word;

        public TrieNode() {
            stop = cnt = 0;
            for (int j = 0; j < 71; ++j) {
                child[j] = 0;
            }
        }
    }

    private int nNode = 0;
    private ArrayList<Word> wordList = new ArrayList<Word>();
    private ArrayList<Word> userWordList = new ArrayList<Word>();
    private ArrayList<String> rawUserWordList = new ArrayList<String>();
    private ArrayList<TrieNode> trieList = new ArrayList<TrieNode>();
    private Scanner sc = new Scanner(System.in);
    private final String ORIGINAL_WORD_SOURCE = "/vie-dictionaries.txt";
    private final String USER_WORD_SOURCE = "\\user-vie-dictionaries.txt";
    private final String USER_DICTIONARY_DIR = System.getProperty("user.home").concat("\\Documents\\Eng-VietDictionaryCMD");

    /**
     * Repeat a String several times.
     */
    private String repeat(String t, int times) {
        String ans = "";
        for (int i = 0; i < times; ++i) {
            ans += t;
        }
        return ans;
    }

    /**
     * Add word to the Trie list.
     */
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
            else if (l == (int) '/') k = 70;
            else if (l == 246) k = 68;
            else break;
            if (trieList.get(r).child[k] == 0) {
                trieList.get(r).child[k] = ++nNode;
                trieList.add(nNode, new TrieNode());
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

    /**
     * Find position of a word in Trie list.
     */
    private int searchWordPos(String word) {
        int r = 0;
        for (int i = 0; i < word.length(); ++i) {
            int k = (int) word.charAt(i);
            int l = k;
            if (l >= (int) '0' && l <= '9') k = k - (int) '0';
            else if (l >= (int) 'A' && l <= (int) 'Z') k = k - (int) 'A' + 10;
            else if (l >= (int) 'a' && l <= (int) 'z') k = k - (int) 'a' + 36; 
            else if (l == (int) '.') k = 63;
            else if (l == (int) '\'') k = 64;
            else if (l == (int) '-') k = 65;
            else if (l == (int) '(' || l == (int) '[') k = 66;
            else if (l == (int) ')' || l == (int) ']') k = 67;
            else if (l == (int) ' ') k = 69;
            else if (l == (int) '/') k = 70;
            else if (l == 246) k = 68;
            else break;
            if (trieList.get(r).child[k] == 0) {
                return -1;
            }
            r = trieList.get(r).child[k];
        }
        return r;
    }

    private void handleWordFromSource(String data, int type) {
        Word newWord = new Word();
        int i = 0;
        int spaces = 0;
        for (int j = 0; j < data.length(); ++j) {
            if (data.charAt(j) == ' ') ++spaces;
            else spaces = 0;
            if (spaces == 4) {
                i = j + 1; break;
            }
        }
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
            if (type == 0) wordList.add(new Word(newWord));
            else {
                userWordList.add(new Word(newWord));
                rawUserWordList.add(data);
            }
            addWord(newWord);
        }
    }

    /**
     * Check existence of User dictionary directory.
     */
    private boolean checkCreatedUserDir() {
        File userDir = new File(USER_DICTIONARY_DIR);
        if (userDir.mkdir()) {
            File userDataDir = new File(USER_DICTIONARY_DIR + "\\User Data");
            if (userDataDir.mkdir()) {
                File userFile = new File(USER_DICTIONARY_DIR + "\\User Data" + USER_WORD_SOURCE);
                try {
                    userFile.createNewFile();
                    return true;
                } catch (IOException e) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    public void init() {
        trieList.add(new TrieNode());
        
        String data = "";
        try {
            for (int type = 0; type < 2; ++type) {
                InputStream input;
                if (type == 0) {
                    input = DictionaryManagement.class.getResourceAsStream(ORIGINAL_WORD_SOURCE);
                } else {
                    if (checkCreatedUserDir()) {
                        break;
                    } else {
                        input = new FileInputStream(USER_DICTIONARY_DIR + "\\User Data" + USER_WORD_SOURCE);
                    }
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                while ((data = reader.readLine()) != null) {
                    handleWordFromSource(data, type);
                }
                reader.close();
            }
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

    /**
     * Initialize first application scene and listen to user's commands.
     */
    public void run() {
        System.out.println("||" + repeat("-", 151) + "||");
        System.out.format("|| %-150s||\n", "                                T??? ??I???N ANH - VI???T");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "                                Vi???t b???i Twoguys");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "Ch??o m???ng b???n ???? ?????n v???i T??? ??i???n Anh-Vi???t. D?????i ????y l?? h?????ng d???n s??? d???ng:");
        System.out.format("|| %-150s||\n", "T??? ??i???n Anh-Vi???t c?? c??c ch???c n??ng ch??nh sau ????y:");
        System.out.format("|| %-150s||\n", "   0 : Hi???n th??? t???t c??? t??? trong t??? ??i???n (In ra s??? l??u do c?? g???n 109k t???).");
        System.out.format("|| %-150s||\n", "   1 : Th??m/X??a t??? v??o trong t??? ??i???n.");
        System.out.format("|| %-150s||\n", "   2 : T??m ki???m t??? (ch??nh x??c t??? b???n nh???p).");
        System.out.format("|| %-150s||\n", "   3 : T??m ki???m c??c t??? c?? ph???n ?????u gi???ng t??? b???n nh???p.");
        System.out.format("|| %-150s||\n", "   4 : Xu???t d??? li???u t??? ??i???n ra file txt.");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "Mu???n s??? d???ng 1 ch???c n??ng c???a t??? ??i???n, h??y nh???n s??? v?? nh???n Enter.");
        System.out.format("|| %-150s||\n", "V?? d???: - T??m ki???m t??? \"home\", ?????u ti??n nh???p 2 sau ???? Enter.");
        System.out.format("|| %-150s||\n", "       - Sau d?? b???n g?? \"home\", ???n Enter v?? t??? ??i???n s??? tr??? k???t qu???.");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "N???u kh??ng mu???n s??? d???ng c??c ch???c n??ng li??n quan ?????n t??m ki???m, h??y nh???p \"EXIT\" v?? ???n Enter.");
        System.out.format("|| %-150s||\n", "N???u mu???n tho??t kh???i t??? ??i???n, nh???p \"QUIT\" v?? ???n Enter.");

        boolean enable = true;
        String input = "";
        while (enable) {
            System.out.print("|| H??y nh???p ch???c n??ng m?? b???n mu???n d??ng: ");
            input = sc.nextLine();
            if (input.equals("0")) showAllWord();
            else if (input.equals("1")) insertEraseFromCommandline();
            else if (input.equals("2")) dictionaryLookup();
            else if (input.equals("3")) dictionarySearcher();
            else if (input.equals("4")) dictionaryExportToFile();
            else if (input.equals("QUIT")) {
                enable = false;
                System.out.format("|| %-150s||\n", "");
                System.out.format("|| %-150s||\n", "                      C???m ??n b???n ???? s??? d???ng t??? ??i???n c???a Twoguys.");
                System.out.println("||" + repeat("-", 151) + "||");
            }
            else {
                System.out.format("|| %-150s||\n", "Error! Kh??ng nh???n ra c??u l???nh n??y.  H??y th??? l???i!");
            }
        }
    }

    /**
     * Close the program.
     */
    public void close() {
        wordList.clear();
        userWordList.clear();
        trieList.clear();
        sc.close();
    }

    /**
     * Insert/Erase command handler.
     */
    public void insertEraseFromCommandline() {
        System.out.println("||" + repeat("-", 151) + "||");
        System.out.format("|| %-150s||\n", "C?? 4 l???nh sau c?? th??? d??ng trong ph???n Th??m/X??a:");
        System.out.format("|| %-150s||\n", "  ADD    : Th??m 1 t??? v??o t??? ??i???n.");
        System.out.format("|| %-150s||\n", "  DELETE : X??a 1 t??? trong nh???ng t??? b???n ???? th??m v??o.");
        System.out.format("|| %-150s||\n", "  SAVE   : L??u c??c thay ?????i m?? b???n ???? th???c hi???n (Kh??ng th??? ho??n t??c).");
        System.out.format("|| %-150s||\n", "  EXIT   : Tho??t kh???i ch???c n??ng Th??m/X??a.");
        System.out.format("|| %-150s||\n", "N???u mu???n d???ng thao t??c ADD hay DELETE, h??y nh???p ABORT.");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "L??u ??: B???n ph???i nh???p c??c t??? m???i theo ????ng ?????nh d???ng d?????i ????y.");
        System.out.format("|| %-150s||\n", "@t???ti???nganh    |* lo???i t???* ngh??a c???a t???, = v?? d??? + d???ch v?? d???|");
        System.out.format("|| %-150s||\n", "Cac kho???ng c??ch b???t bu???c ph???i b???ng 1 Tab (4 d???u c??ch) v?? t???t c??? ph???i nh???p tr??n 1 d??ng.");
        System.out.format("|| Vi du: @breakeven    |* t??nh t???* c??n b???ng gi???a s??? ti???n b??? ra v?? s??? ti???n thu v??o; kh??ng l???i kh??ng l???; h??a v???n, = Breakeven point+??i???m h??a v???n, = Breakeven price+Gi?? b??n h??a v???n|\n");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "");
        
        String input = "";
        String command = "";
        String addRegex = "^@[a-zA-Z0-9[-\'/.;,]+]+( [a-zA-Z0-9[-\'/.;,]+]+)* {4}([|][*][a-zA-Z0-9[-()\'/.;,] ]+[*] [a-zA-Z0-9[-()\'/.;,]+ ]+(=([a-zA-Z0-9[-()\'/.;,]+ ]+[+][a-zA-Z0-9[-()\'/.;,]+ ]+)*)*)+[|]$";
        boolean enable = true;
        boolean save = true;

        while (enable) {
            while (true) {
                System.out.format("|| %-150s||\n", "Nh???p 1 trong 4 l???nh \"ADD\", \"DELETE\", \"SAVE\", \"EXIT\":");
                System.out.format("|| ");
                command = sc.nextLine().trim();
                if (command.equals("ADD")) {
                    while (true) {
                        System.out.format("|| ");
                        input = sc.nextLine().trim();
                        if (input.equals("ABORT")) {
                            break;
                        } else if (!Pattern.matches(addRegex, input)) {
                            System.out.format("|| %-150s||\n", "B???n ???? nh???p sai ?????nh d???ng, hay nh???p l???i.");
                        } else {
                            handleWordFromSource(input, 1);
                            save = false;
                            break;
                        }
                    }
                } else if (command.equals("DELETE")) {

                    if (userWordList.isEmpty()) {
                        System.out.format("|| %-150s||\n", "Error! B???n kh??ng c?? t??? n??o ????? x??a.");
                        System.out.format("|| %-150s||\n", "B???n ch??? ???????c ph??p x??a nh???ng t??? b???n ???? th??m v??o, ko dc ph??p x??a d??? li???u c?? s???n c???a t??? ??i???n.");
                        continue;
                    } 

                    System.out.format("|| %-150s||\n", "Danh s??ch c??c t??? b???n c?? th??? x??a: ");
                        
                    System.out.format("|| %-8s| %-140s||\n", "STT", "C??c t??? c?? th??? x??a");
                    for (int i = 0; i < userWordList.size(); ++i) {
                        System.out.format("|| %-8d| %-140s||\n", i + 1, userWordList.get(i).getWord());
                    }

                    System.out.format("|| %-150s||\n", "");

                    while (true) {
                        System.out.format("|| ");
                        input = sc.nextLine().trim();
                        if (!Pattern.matches("^[a-zA-Z0-9[-.\'/()]+]+( [a-zA-Z0-9[-.\'/()]+]+)*", input)) {
                            System.out.format("|| %-150s||\n", "T??? c???a b???n kh??ng ???????c ????? tr???ng v?? ch??? ???????c ch???a c??c k?? t??? a-z, A-Z, 0-9, -, \', /, (), space.");
                        } else if (input.equals("ABORT")) {
                            break;
                        } else {
                            int id = -1;

                            for (int i = 0; i < userWordList.size(); ++i) {
                                if (userWordList.get(i).getWord().equals(input)) {
                                    id = i; break;
                                }
                            }

                            if (id != -1) {
                                userWordList.remove(id);
                                rawUserWordList.remove(id);
                                System.out.format("|| %-150s||\n", "Da xoa tu \"" + input + "\".");
                                save = false;
                                break;
                            } else {
                                System.out.format("|| %-150s||\n", "Error! Kh??ng t??m th???y t??? n??y. H??y th??? l???i!");
                            }
                        }
                    }
                } else if (command.equals("SAVE")) {
                    try {
                        checkCreatedUserDir();
                        String userWordDir = System.getProperty("user.home").concat("\\Documents\\Eng-VietDictionaryCMD\\User Data");
                        FileOutputStream outputURL = new FileOutputStream(userWordDir.concat(USER_WORD_SOURCE));
                        PrintStream output = new PrintStream(outputURL, true, "UTF-8");
            
                        Collections.sort(rawUserWordList, new Comparator<String>() {
                            @Override
                            public int compare(String a, String b) {
                                return a.compareTo(b);
                            }
                        });

                        Collections.sort(userWordList, new Comparator<Word>() {
                            @Override
                            public int compare(Word a, Word b) {
                                return a.getWord().compareTo(b.getWord());
                            }
                        });

                        for (int i = 0; i < rawUserWordList.size(); ++i) {
                            output.println(rawUserWordList.get(i));
                        }

                        save = true;
                        System.out.format("|| %-150s||\n", "???? l??u th??nh c??ng file user-vie-dictionaries.txt !");
                        System.out.format("|| File ???????c l??u t???i " + userWordDir + "\n");
                        System.out.format("|| %-150s||\n", "");
                        System.out.format("|| %-150s||\n", "Danh s??ch c??c t??? b???n ???? th??m trong t??? ??i???n: ");
                        
                        System.out.format("|| %-8s| %-140s||\n", "STT", "C??c t??? ???? th??m");
                        for (int i = 0; i < userWordList.size(); ++i) {
                            System.out.format("|| %-8d| %-140s||\n", i + 1, userWordList.get(i).getWord());
                        }
                        
                        output.close();
                        outputURL.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (command.equals("EXIT")) {
                    if (!save) {
                        System.out.format("|| %-150s||\n", "Warning! B???n ch??a l??u nh???ng thay ?????i c???a m??nh, h??y nh???p \"SAVE\" ????? l??u");
                    } else {
                        enable = false;
                        break;
                    }
                } else {
                    System.out.format("|| %-150s||\n", "Error! Kh??ng nh???n ra c??u l???nh n??y. H??y th??? l???i!");
                }
                System.out.format("|| %-150s||\n", "");
            }
        }
        System.out.println("||" + repeat("-", 151) + "||");
    }

    /**
     * Show all words to the screen.
     */
    public void showAllWord() {
        System.out.println("||" + repeat("-", 151) + "||");
        System.out.format("|| %-8s| %-40s| %-98s||\n", "STT", "Ti???ng Anh", "Ti???ng Vi???t");
        String word = "", sampleExplain = "";

        for (int i = 0; i < wordList.size(); ++i) {
            word = wordList.get(i).getWord();
            if (word.length() > 35)  word = word.substring(0, 36) + "..."; 
                
            sampleExplain = wordList.get(i).getSampleExplain();
            if (sampleExplain.length() > 93) sampleExplain = sampleExplain.substring(0, 94) + "...";

            System.out.format("|| %-8d| %-40s| %-98s||\n", i, word, sampleExplain);
        }
        System.out.println("||" + repeat("-", 151) + "||");
    }

    /**
     * Look up the exact word user entered.
     */
    public void dictionaryLookup() {
        int wordPos;
        String input = "";
        boolean enable = true;

        System.out.println("||" + repeat("-", 151) + "||");
        while (enable) {
            while (true) {
                System.out.format("|| %s", "Nh???p t??? m?? b???n mu???n t??m ho???c nh???p \"EXIT\" ????? d???ng tra c???u: ");
                input = sc.nextLine().trim();
                if (!Pattern.matches("^[a-zA-Z0-9[-.\'/()]+]+( [a-zA-Z0-9[-.\'/()]+]+)*", input)) {
                    System.out.format("|| %-150s||\n", "T??? c???a b???n kh??ng ??????c ????? tr???ng v?? ch??? ???????c ch???a c??c k?? t??? a-z, A-Z, 0-9, -, \', /, (), space.");
                } else {
                    if (input.equals("EXIT")) {
                        enable = false;
                        break;
                    }
                    wordPos = searchWordPos(input);
                    if (wordPos == -1) {
                        System.out.format("|| %-150s||\n", "Kh??ng t??m th???y t??? n??y. H??y th??? l???i!");
                    } else if (Objects.isNull(trieList.get(wordPos).word)) {
                        System.out.format("|| %-150s||\n", "Kh??ng t??m th???y t??? n??y. H??y th??? l???i!");
                    } else {
                        System.out.format("|| %-150s||\n", "Ngh??a:");
                        trieList.get(wordPos).word.getFullExplaination();
                    }
                    System.out.println("||" + repeat("-", 151) + "||");
                }
            }
        }
    }

    /**
     * Search words begin with given string.
     */
    public void dictionarySearcher() {
        int wordPos;
        String input = "";
        boolean enable = true;
        ArrayDeque<Integer> trieNode = new ArrayDeque<Integer>();
        ArrayList<Integer> wordFoundPos = new ArrayList<Integer>();

        System.out.println("||" + repeat("-", 151) + "||");
        while (enable) {
            while (true) {
                System.out.format("|| %s", "Nh???p t??? m?? b???n mu???n t??m ho???c nh???p \"EXIT\" ????? d???ng tra c???u: ");
                input = sc.nextLine().trim();
                //System.out.format("%-80s", "");
                if (!Pattern.matches("^[a-zA-Z0-9[-.\'/()]+]+( [a-zA-Z0-9[-.\'/()]+]+)*", input)) {
                    System.out.format("|| %-150s||\n", "T??? c???a b???n kh??ng ???????c ????? trong v?? ch??? ???????c ch???a c??c k?? t??? a-z, A-Z, 0-9, -, \', /, (), space.");
                } else {
                    if (input.equals("EXIT")) {
                        enable = false;
                        break;
                    }
                    wordPos = searchWordPos(input);
                    if (wordPos == -1) {
                        String spaces = String.format("%" + (117 - input.length()) + "s", "");
                        System.out.format("|| Kh??ng t??m th???y t??? b???t ?????u v???i \"%s\"." + spaces + "||\n", input);
                    } else {
                        trieNode.add(wordPos);
                        while (!trieNode.isEmpty()) {
                            int u = trieNode.peek();
                            if (!Objects.isNull(trieList.get(u).word)) {
                                wordFoundPos.add(u);
                            }
                            trieNode.poll();
                            for (int k = 0; k < 71; ++k) {
                                if (trieList.get(u).child[k] != 0) {
                                    trieNode.add(trieList.get(u).child[k]);
                                }
                            }
                        }
                        
                        String spaces = String.format("%" + (122 - String.valueOf(wordFoundPos.size()).length() - input.length()) + "s", "");
                        System.out.format("|| T??m th???y %d t??? b???t ?????u v???i \"%s\":" + spaces + "||\n", wordFoundPos.size(), input, "");
                        for (int i = 0; i < wordFoundPos.size(); ++i) {
                            System.out.format("|| %-8d| %-140s||\n", i + 1, trieList.get(wordFoundPos.get(i)).word.getWord());
                        }

                        int pos = 0;
                        while (true) {
                            System.out.format("|| %s", "Nh???p s??? th??? t??? c???a 1 trong c??c t??? tr??n ho???c nh???p \"EXIT\" ????? d??ng tra c???u c??c t??? tr??n: ");
                            input = sc.nextLine().trim();
                            if (input.equals("EXIT")) {
                                break;
                            } else {
                                try {
                                    pos = Integer.parseInt(input);
                                    if (pos <= 0 || pos > wordFoundPos.size()) {
                                        System.out.format("|| %-150s||\n", "Error! B???n ph???i nh???p 1 s??? t??? nhi??n trong kho???ng tr??n. H??y th??? l???i!");
                                    } else {
                                        System.out.format("|| %-150s||\n", trieList.get(wordFoundPos.get(pos - 1)).word.getWord());
                                        trieList.get(wordFoundPos.get(pos - 1)).word.getFullExplaination();
                                        System.out.format("|| %-150s||\n", "");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.format("|| %-150s||\n", "Error! B???n ph???i nh???p 1 s??? t??? nhi??n trong kho???ng tr??n. H??y th??? l???i!");
                                }
                            }
                        }

                        trieNode.clear();
                        wordFoundPos.clear();
                    }
                    System.out.println("||" + repeat("-", 151) + "||");
                }
            }
        }
    }

    /**
     * Export user's dictionary to file.
     */
    public void dictionaryExportToFile() {
        checkCreatedUserDir();
        String userHome = System.getProperty("user.home");
        String dictionaryDir = userHome.concat("\\Documents\\Eng-VietDictionaryCMD");
        File f = new File(dictionaryDir);
        System.out.println("||" + repeat("-", 151) + "||");
        f.mkdir();
        try {
            FileOutputStream outputURL = new FileOutputStream(dictionaryDir.concat("\\dictionaryData.txt"));
            PrintStream output = new PrintStream(outputURL, true, "UTF-8");

            for (int i = 0; i < wordList.size(); ++i) {
                output.println(wordList.get(i).getWord());
                wordList.get(i).exportWord(output);
                output.print("\n");
            }

            System.out.format("|| ???? xu???t file dictionaryData.txt th??nh c??ng, ?????a ch??? l??u file: \"%s\".\n", dictionaryDir);
            output.close();
            outputURL.close();
        } catch (FileNotFoundException e) {
            System.out.format("|| %-150s||\n", "Error! Kh??ng th??? xu???t file." + e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            System.out.format("|| %-150s||\n", "Error! Kh??ng th??? xu???t file." + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.format("|| %-150s||\n", "Error! Kh??ng th??? xu???t file." + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("||" + repeat("-", 151) + "||");
    }
}
