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

    private String repeat(String t, int times) {
        String ans = "";
        for (int i = 0; i < times; ++i) {
            ans += t;
        }
        return ans;
    }

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

    public void run() {
        System.out.println("||" + repeat("-", 151) + "||");
        System.out.format("|| %-150s||\n", "                                TU DIEN ANH - VIET");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "                                Viet boi Twoguys");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "Chao mung ban da den voi Tu dien Anh-Viet. Duoi day la huong dan su dung:");
        System.out.format("|| %-150s||\n", "Tu dien Anh-Viet co cac chuc nang chinh sau day:");
        System.out.format("|| %-150s||\n", "   0 : Hien thi tat ca tu trong tu dien (In ra se lau do co gan 109k tu).");
        System.out.format("|| %-150s||\n", "   1 : Them/Xoa tu vao trong tu dien.");
        System.out.format("|| %-150s||\n", "   2 : Tim kiem tu (chinh xac tu ban nhap).");
        System.out.format("|| %-150s||\n", "   3 : Tim kiem cac tu co phan dau giong tu ban nhap.");
        System.out.format("|| %-150s||\n", "   4 : Xuat du lieu tu dien ra file txt.");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "Muon su dung 1 chuc nang cua tu dien, hay nhan so va nhan Enter.");
        System.out.format("|| %-150s||\n", "Vi du: - Tim kiem tu \"home\", dau tien nhap 2 sau do Enter.");
        System.out.format("|| %-150s||\n", "       - Sau do ban go \"home\", an Enter va tu dien se tra ket qua.");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "Neu khong muon su dung cac chuc nang lien quan den tim kiem, hay nhap \"EXIT\" va an Enter.");
        System.out.format("|| %-150s||\n", "Neu muon thoat khoi tu dien, nhap \"QUIT\" va an Enter.");

        boolean enable = true;
        String input = "";
        while (enable) {
            System.out.print("|| Hay nhap chuc nang ma ban muon dung: ");
            input = sc.nextLine();
            if (input.equals("0")) showAllWord();
            else if (input.equals("1")) insertEraseFromCommandline();
            else if (input.equals("2")) dictionaryLookup();
            else if (input.equals("3")) dictionarySearcher();
            else if (input.equals("4")) dictionaryExportToFile();
            else if (input.equals("QUIT")) {
                enable = false;
                System.out.format("|| %-150s||\n", "");
                System.out.format("|| %-150s||\n", "                      Cam on ban da su dung tu dien cua Twoguys.");
                System.out.println("||" + repeat("-", 151) + "||");
            }
            else {
                System.out.format("|| %-150s||\n", "Error! Khong nhan ra cau lenh nay. Hay thu lai!");
            }
        }
    }

    public void close() {
        wordList.clear();
        userWordList.clear();
        trieList.clear();
        sc.close();
    }

    public void insertEraseFromCommandline() {
        System.out.println("||" + repeat("-", 151) + "||");
        System.out.format("|| %-150s||\n", "Co 4 lenh sau co the dung trong phan Them/Xoa:");
        System.out.format("|| %-150s||\n", "  ADD    : Them 1 tu vao tu dien.");
        System.out.format("|| %-150s||\n", "  DELETE : Xoa 1 tu trong nhung tu ban da them vao.");
        System.out.format("|| %-150s||\n", "  SAVE   : Luu cac thay doi ma ban da thuc hien (Khong the hoan tac).");
        System.out.format("|| %-150s||\n", "  EXIT   : Thoat khoi chuc nang Them/Xoa.");
        System.out.format("|| %-150s||\n", "Neu muon dung thao tac ADD hay delete, hay nhap ABORT.");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "Luu y: Ban phai nhap cac tu moi theo dung dang duoi day.");
        System.out.format("|| %-150s||\n", "@tienganh    |* loai tu* nghia cua tu, = vi du + dich vi du|");
        System.out.format("|| %-150s||\n", "Cac khoang cach bat buoc phai bang 1 Tab (4 dau cach) va tat ca phai nhap tren 1 dong.");
        System.out.format("|| Vi du: @breakeven    |* tinh tu* can bang giua so tien bo ra va so tien thu vao; khong loi khong lo; hoa von, = Breakeven point+Diem hoa von, = Breakeven price+Gia ban hoa von|\n");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "");
        
        String input = "";
        String command = "";
        String addRegex = "^@[a-zA-Z0-9[-\'/.;,]+]+( [a-zA-Z0-9[-\'/.;,]+]+)* {4}([|][*][a-zA-Z0-9[-()\'/.;,] ]+[*] [a-zA-Z0-9[-()\'/.;,]+ ]+(=([a-zA-Z0-9[-()\'/.;,]+ ]+[+][a-zA-Z0-9[-()\'/.;,]+ ]+)*)*)+[|]$";
        boolean enable = true;
        boolean save = true;

        while (enable) {
            while (true) {
                System.out.format("|| %-150s||\n", "Nhap 1 trong 4 lenh \"ADD\", \"DELETE\", \"SAVE\", \"EXIT\":");
                System.out.format("|| ");
                command = sc.nextLine().trim();
                if (command.equals("ADD")) {
                    while (true) {
                        System.out.format("|| ");
                        input = sc.nextLine().trim();
                        if (input.equals("ABORT")) {
                            break;
                        } else if (!Pattern.matches(addRegex, input)) {
                            System.out.format("|| %-150s||\n", "Ban da nhap sai dinh dang, hay nhap lai.");
                        } else {
                            handleWordFromSource(input, 1);
                            save = false;
                            break;
                        }
                    }
                } else if (command.equals("DELETE")) {

                    if (userWordList.isEmpty()) {
                        System.out.format("|| %-150s||\n", "Error! Ban khong co tu nao de xoa.");
                        System.out.format("|| %-150s||\n", "Ban chi duoc phep xoa nhung tu ban da them vao, ko dc phep xoa du lieu co san cua tu dien.");
                        continue;
                    } 

                    System.out.format("|| %-150s||\n", "Danh sach cac tu ban co the xoa: ");
                        
                    System.out.format("|| %-8s| %-140s||\n", "STT", "Cac tu co the xoa");
                    for (int i = 0; i < userWordList.size(); ++i) {
                        System.out.format("|| %-8d| %-140s||\n", i + 1, userWordList.get(i).getWord());
                    }

                    System.out.format("|| %-150s||\n", "");

                    while (true) {
                        System.out.format("|| ");
                        input = sc.nextLine().trim();
                        if (!Pattern.matches("^[a-zA-Z0-9[-.\'/()]+]+( [a-zA-Z0-9[-.\'/()]+]+)*", input)) {
                            System.out.format("|| %-150s||\n", "Tu cua ban khong duoc de trong va chi duoc chua cac ki tu a-z, A-Z, 0-9, -, \', /, (), space.");
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
                                System.out.format("|| %-150s||\n", "Error! Khong tim thay tu nay. Hay thu lai!");
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
                        System.out.format("|| %-150s||\n", "Da luu thanh cong file user-vie-dictionaries.txt !");
                        System.out.format("|| File duoc luu tai " + userWordDir + "\n");
                        System.out.format("|| %-150s||\n", "");
                        System.out.format("|| %-150s||\n", "Danh sach cac tu ban da them trong tu dien: ");
                        
                        System.out.format("|| %-8s| %-140s||\n", "STT", "Cac tu da them");
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
                        System.out.format("|| %-150s||\n", "Warning! Ban chua luu nhung thay doi cua minh, hay nhap \"SAVE\" de luu");
                    } else {
                        enable = false;
                        break;
                    }
                } else {
                    System.out.format("|| %-150s||\n", "Error! Khong nhan ra cau lenh nay. Hay thu lai!");
                }
                System.out.format("|| %-150s||\n", "");
            }
        }
        System.out.println("||" + repeat("-", 151) + "||");
    }

    public void showAllWord() {
        System.out.println("||" + repeat("-", 151) + "||");
        System.out.format("|| %-8s| %-40s| %-98s||\n", "STT", "Tieng Anh", "Tieng Viet");
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
                System.out.format("|| %s", "Nhap tu ma ban muon tim hoac nhap \"EXIT\" de dung tra cuu: ");
                input = sc.nextLine().trim();
                if (!Pattern.matches("^[a-zA-Z0-9[-.\'/()]+]+( [a-zA-Z0-9[-.\'/()]+]+)*", input)) {
                    System.out.format("|| %-150s||\n", "Tu cua ban khong duoc de trong va chi duoc chua cac ki tu a-z, A-Z, 0-9, -, \', /, (), space.");
                } else {
                    if (input.equals("EXIT")) {
                        enable = false;
                        break;
                    }
                    wordPos = searchWordPos(input);
                    if (wordPos == -1) {
                        System.out.format("|| %-150s||\n", "Khong tim thay tu nay. Hay thu lai!");
                    } else if (Objects.isNull(trieList.get(wordPos).word)) {
                        System.out.format("|| %-150s||\n", "Khong tim thay tu nay. Hay thu lai!");
                    } else {
                        System.out.format("|| %-150s||\n", "Nghia:");
                        trieList.get(wordPos).word.getFullExplaination();
                    }
                    System.out.println("||" + repeat("-", 151) + "||");
                }
            }
        }
    }

    public void dictionarySearcher() {
        int wordPos;
        String input = "";
        boolean enable = true;
        ArrayDeque<Integer> trieNode = new ArrayDeque<Integer>();
        ArrayList<Integer> wordFoundPos = new ArrayList<Integer>();

        System.out.println("||" + repeat("-", 151) + "||");
        while (enable) {
            while (true) {
                System.out.format("|| %s", "Nhap tu ma ban muon tim hoac nhap \"EXIT\" de dung tra cuu: ");
                input = sc.nextLine().trim();
                //System.out.format("%-80s", "");
                if (!Pattern.matches("^[a-zA-Z0-9[-.\'/()]+]+( [a-zA-Z0-9[-.\'/()]+]+)*", input)) {
                    System.out.format("|| %-150s||\n", "Tu cua ban khong duoc de trong va chi duoc chua cac ki tu a-z, A-Z, 0-9, -, \', /, (), space.");
                } else {
                    if (input.equals("EXIT")) {
                        enable = false;
                        break;
                    }
                    wordPos = searchWordPos(input);
                    if (wordPos == -1) {
                        String spaces = String.format("%" + (117 - input.length()) + "s", "");
                        System.out.format("|| Khong tim thay tu bat dau voi \"%s\"." + spaces + "||\n", input);
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
                        System.out.format("|| Tim thay %d tu bat dau voi \"%s\":" + spaces + "||\n", wordFoundPos.size(), input, "");
                        for (int i = 0; i < wordFoundPos.size(); ++i) {
                            System.out.format("|| %-8d| %-140s||\n", i + 1, trieList.get(wordFoundPos.get(i)).word.getWord());
                        }

                        int pos = 0;
                        while (true) {
                            System.out.format("|| %s", "Nhap so thu tu cua 1 trong cac tu tren hoac nhap \"EXIT\" de dung tra cuu cac tu tren: ");
                            input = sc.nextLine().trim();
                            if (input.equals("EXIT")) {
                                break;
                            } else {
                                try {
                                    pos = Integer.parseInt(input);
                                    if (pos <= 0 || pos > wordFoundPos.size()) {
                                        System.out.format("|| %-150s||\n", "Error! Ban phai nhap 1 so tu nhien trong khoang tren. Hay thu lai!");
                                    } else {
                                        System.out.format("|| %-150s||\n", trieList.get(wordFoundPos.get(pos - 1)).word.getWord());
                                        trieList.get(wordFoundPos.get(pos - 1)).word.getFullExplaination();
                                        System.out.format("|| %-150s||\n", "");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.format("|| %-150s||\n", "Error! Ban phai nhap 1 so tu nhien trong khoang tren. Hay thu lai!");
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

            System.out.format("|| Da xuat file dictionaryData.txt thanh cong, dia chi luu file: \"%s\".\n", dictionaryDir);
            output.close();
            outputURL.close();
        } catch (FileNotFoundException e) {
            System.out.format("|| %-150s||\n", "Error! Khong the xuat file." + e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            System.out.format("|| %-150s||\n", "Error! Khong the xuat file." + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.format("|| %-150s||\n", "Error! Khong the xuat file." + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("||" + repeat("-", 151) + "||");
    }
}
