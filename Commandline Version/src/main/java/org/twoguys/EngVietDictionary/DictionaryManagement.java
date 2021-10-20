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
        System.out.format("|| %-150s||\n", "                                TỪ ĐIỂN ANH - VIỆT");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "                                Viết bởi Twoguys");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "Chào mừng bạn đã đến với Từ điển Anh-Việt. Dưới đây là hướng dẫn sử dụng:");
        System.out.format("|| %-150s||\n", "Từ điển Anh-Việt có các chức năng chính sau đây:");
        System.out.format("|| %-150s||\n", "   0 : Hiển thị tất cả từ trong từ điển (In ra sẽ lâu do có gần 109k từ).");
        System.out.format("|| %-150s||\n", "   1 : Thêm/Xóa từ vào trong từ điển.");
        System.out.format("|| %-150s||\n", "   2 : Tìm kiếm từ (chính xác từ bạn nhập).");
        System.out.format("|| %-150s||\n", "   3 : Tìm kiếm các từ có phần đầu giống từ bạn nhập.");
        System.out.format("|| %-150s||\n", "   4 : Xuất dữ liệu từ điển ra file txt.");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "Muốn sử dụng 1 chức năng của từ điển, hãy nhấn số và nhấn Enter.");
        System.out.format("|| %-150s||\n", "Ví dụ: - Tìm kiếm từ \"home\", đầu tiên nhập 2 sau đó Enter.");
        System.out.format("|| %-150s||\n", "       - Sau dó bạn gõ \"home\", ấn Enter và từ điển sẽ trả kết quả.");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "Nếu không muốn sử dụng các chức năng liên quan đến tìm kiếm, hãy nhập \"EXIT\" và ấn Enter.");
        System.out.format("|| %-150s||\n", "Nếu muốn thoát khỏi từ điển, nhập \"QUIT\" và ấn Enter.");

        boolean enable = true;
        String input = "";
        while (enable) {
            System.out.print("|| Hãy nhập chức năng mà bạn muốn dùng: ");
            input = sc.nextLine();
            if (input.equals("0")) showAllWord();
            else if (input.equals("1")) insertEraseFromCommandline();
            else if (input.equals("2")) dictionaryLookup();
            else if (input.equals("3")) dictionarySearcher();
            else if (input.equals("4")) dictionaryExportToFile();
            else if (input.equals("QUIT")) {
                enable = false;
                System.out.format("|| %-150s||\n", "");
                System.out.format("|| %-150s||\n", "                      Cảm ơn bạn đã sử dụng từ điển của Twoguys.");
                System.out.println("||" + repeat("-", 151) + "||");
            }
            else {
                System.out.format("|| %-150s||\n", "Error! Không nhận ra câu lệnh này.  Hãy thử lại!");
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
        System.out.format("|| %-150s||\n", "Có 4 lệnh sau có thể dùng trong phần Thêm/Xóa:");
        System.out.format("|| %-150s||\n", "  ADD    : Thêm 1 từ vào từ điển.");
        System.out.format("|| %-150s||\n", "  DELETE : Xóa 1 từ trong những từ bạn đã thêm vào.");
        System.out.format("|| %-150s||\n", "  SAVE   : Lưu các thay đổi mà bạn đã thực hiện (Không thể hoàn tác).");
        System.out.format("|| %-150s||\n", "  EXIT   : Thoát khỏi chức năng Thêm/Xóa.");
        System.out.format("|| %-150s||\n", "Nếu muốn dừng thao tác ADD hay DELETE, hãy nhập ABORT.");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "Lưu ý: Bạn phải nhập các từ mới theo đúng định dạng dưới đây.");
        System.out.format("|| %-150s||\n", "@từtiếnganh    |* loại từ* nghĩa của từ, = ví dụ + dịch ví dụ|");
        System.out.format("|| %-150s||\n", "Cac khoảng cách bắt buộc phải bằng 1 Tab (4 dấu cách) và tất cả phải nhập trên 1 dòng.");
        System.out.format("|| Vi du: @breakeven    |* tính từ* cân bằng giữa số tiền bỏ ra và số tiền thu vào; không lời không lỗ; hòa vốn, = Breakeven point+Điểm hòa vốn, = Breakeven price+Giá bán hòa vốn|\n");
        System.out.format("|| %-150s||\n", "");
        System.out.format("|| %-150s||\n", "");
        
        String input = "";
        String command = "";
        String addRegex = "^@[a-zA-Z0-9[-\'/.;,]+]+( [a-zA-Z0-9[-\'/.;,]+]+)* {4}([|][*][a-zA-Z0-9[-()\'/.;,] ]+[*] [a-zA-Z0-9[-()\'/.;,]+ ]+(=([a-zA-Z0-9[-()\'/.;,]+ ]+[+][a-zA-Z0-9[-()\'/.;,]+ ]+)*)*)+[|]$";
        boolean enable = true;
        boolean save = true;

        while (enable) {
            while (true) {
                System.out.format("|| %-150s||\n", "Nhập 1 trong 4 lệnh \"ADD\", \"DELETE\", \"SAVE\", \"EXIT\":");
                System.out.format("|| ");
                command = sc.nextLine().trim();
                if (command.equals("ADD")) {
                    while (true) {
                        System.out.format("|| ");
                        input = sc.nextLine().trim();
                        if (input.equals("ABORT")) {
                            break;
                        } else if (!Pattern.matches(addRegex, input)) {
                            System.out.format("|| %-150s||\n", "Bạn đã nhập sai định dạng, hay nhập lại.");
                        } else {
                            handleWordFromSource(input, 1);
                            save = false;
                            break;
                        }
                    }
                } else if (command.equals("DELETE")) {

                    if (userWordList.isEmpty()) {
                        System.out.format("|| %-150s||\n", "Error! Bạn không có từ nào để xóa.");
                        System.out.format("|| %-150s||\n", "Bạn chỉ được phép xóa những từ bạn đã thêm vào, ko dc phép xóa dữ liệu có sẵn của từ điển.");
                        continue;
                    } 

                    System.out.format("|| %-150s||\n", "Danh sách các từ bạn có thể xóa: ");
                        
                    System.out.format("|| %-8s| %-140s||\n", "STT", "Các từ có thể xóa");
                    for (int i = 0; i < userWordList.size(); ++i) {
                        System.out.format("|| %-8d| %-140s||\n", i + 1, userWordList.get(i).getWord());
                    }

                    System.out.format("|| %-150s||\n", "");

                    while (true) {
                        System.out.format("|| ");
                        input = sc.nextLine().trim();
                        if (!Pattern.matches("^[a-zA-Z0-9[-.\'/()]+]+( [a-zA-Z0-9[-.\'/()]+]+)*", input)) {
                            System.out.format("|| %-150s||\n", "Từ của bạn không được để trống và chỉ được chứa các kí tự a-z, A-Z, 0-9, -, \', /, (), space.");
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
                                System.out.format("|| %-150s||\n", "Error! Không tìm thấy từ này. Hãy thử lại!");
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
                        System.out.format("|| %-150s||\n", "Đã lưu thành công file user-vie-dictionaries.txt !");
                        System.out.format("|| File được lưu tại " + userWordDir + "\n");
                        System.out.format("|| %-150s||\n", "");
                        System.out.format("|| %-150s||\n", "Danh sách các từ bạn đã thêm trong từ điển: ");
                        
                        System.out.format("|| %-8s| %-140s||\n", "STT", "Các từ đã thêm");
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
                        System.out.format("|| %-150s||\n", "Warning! Bạn chưa lưu những thay đổi của mình, hãy nhập \"SAVE\" để lưu");
                    } else {
                        enable = false;
                        break;
                    }
                } else {
                    System.out.format("|| %-150s||\n", "Error! Không nhận ra câu lệnh này. Hãy thử lại!");
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
        System.out.format("|| %-8s| %-40s| %-98s||\n", "STT", "Tiếng Anh", "Tiếng Việt");
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
                System.out.format("|| %s", "Nhập từ mà bạn muốn tìm hoặc nhập \"EXIT\" để dừng tra cứu: ");
                input = sc.nextLine().trim();
                if (!Pattern.matches("^[a-zA-Z0-9[-.\'/()]+]+( [a-zA-Z0-9[-.\'/()]+]+)*", input)) {
                    System.out.format("|| %-150s||\n", "Từ của bạn không đươc để trống và chỉ được chứa các kí tự a-z, A-Z, 0-9, -, \', /, (), space.");
                } else {
                    if (input.equals("EXIT")) {
                        enable = false;
                        break;
                    }
                    wordPos = searchWordPos(input);
                    if (wordPos == -1) {
                        System.out.format("|| %-150s||\n", "Không tìm thấy từ này. Hãy thử lại!");
                    } else if (Objects.isNull(trieList.get(wordPos).word)) {
                        System.out.format("|| %-150s||\n", "Không tìm thấy từ này. Hãy thử lại!");
                    } else {
                        System.out.format("|| %-150s||\n", "Nghĩa:");
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
                System.out.format("|| %s", "Nhập từ mà bạn muốn tìm hoặc nhập \"EXIT\" để dừng tra cứu: ");
                input = sc.nextLine().trim();
                //System.out.format("%-80s", "");
                if (!Pattern.matches("^[a-zA-Z0-9[-.\'/()]+]+( [a-zA-Z0-9[-.\'/()]+]+)*", input)) {
                    System.out.format("|| %-150s||\n", "Từ của bạn không được để trong và chỉ được chứa các kí tự a-z, A-Z, 0-9, -, \', /, (), space.");
                } else {
                    if (input.equals("EXIT")) {
                        enable = false;
                        break;
                    }
                    wordPos = searchWordPos(input);
                    if (wordPos == -1) {
                        String spaces = String.format("%" + (117 - input.length()) + "s", "");
                        System.out.format("|| Không tìm thấy từ bắt đầu với \"%s\"." + spaces + "||\n", input);
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
                        System.out.format("|| Tìm thấy %d từ bắt đầu với \"%s\":" + spaces + "||\n", wordFoundPos.size(), input, "");
                        for (int i = 0; i < wordFoundPos.size(); ++i) {
                            System.out.format("|| %-8d| %-140s||\n", i + 1, trieList.get(wordFoundPos.get(i)).word.getWord());
                        }

                        int pos = 0;
                        while (true) {
                            System.out.format("|| %s", "Nhập số thứ tự của 1 trong các từ trên hoặc nhập \"EXIT\" để dùng tra cứu các từ trên: ");
                            input = sc.nextLine().trim();
                            if (input.equals("EXIT")) {
                                break;
                            } else {
                                try {
                                    pos = Integer.parseInt(input);
                                    if (pos <= 0 || pos > wordFoundPos.size()) {
                                        System.out.format("|| %-150s||\n", "Error! Bạn phải nhập 1 số tự nhiên trong khoảng trên. Hãy thử lại!");
                                    } else {
                                        System.out.format("|| %-150s||\n", trieList.get(wordFoundPos.get(pos - 1)).word.getWord());
                                        trieList.get(wordFoundPos.get(pos - 1)).word.getFullExplaination();
                                        System.out.format("|| %-150s||\n", "");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.format("|| %-150s||\n", "Error! Bạn phải nhập 1 số tự nhiên trong khoảng trên. Hãy thử lại!");
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

            System.out.format("|| Đã xuất file dictionaryData.txt thành công, địa chỉ lưu file: \"%s\".\n", dictionaryDir);
            output.close();
            outputURL.close();
        } catch (FileNotFoundException e) {
            System.out.format("|| %-150s||\n", "Error! Không thể xuất file." + e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            System.out.format("|| %-150s||\n", "Error! Không thể xuất file." + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.format("|| %-150s||\n", "Error! Không thể xuất file." + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("||" + repeat("-", 151) + "||");
    }
}
