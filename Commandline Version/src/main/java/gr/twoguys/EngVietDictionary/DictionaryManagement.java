package gr.twoguys.EngVietDictionary;

import java.util.ArrayList;
import java.util.Scanner;

public class DictionaryManagement {
    private static int maxWordLength = 5; 
    private static ArrayList<Word> wordList = new ArrayList<Word>();

    public static void insertFromCommandline() {
        int numOfNewWords = 0;
        String word = "", explain = "";
        Scanner sc = new Scanner(System.in);

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

        for (int i = 0; i < numOfNewWords; ++i) {
            word = ""; explain = "";
            word = sc.nextLine();
            explain = sc.nextLine();
            wordList.add(new Word(word, explain));
            if (maxWordLength < word.length() + 5) {
                maxWordLength = word.length() + 5;
            }
        }

        sc.close();
    }

    public static void showAllWord() {
        //String spaces = String.format("%" + maxWordLength + "s", "");
        //System.out.print(spaces);
        System.out.println("No" + String.format("%7s", "") + "|English" + String.format("%" + (maxWordLength - 8) + "s", "") + "|Vietnamese");

        for (int i = 0; i < wordList.size(); ++i) {
            String word = wordList.get(i).getWord();
            String explain = wordList.get(i).getExplain();
            System.out.print((i + 1) + String.format("%9s", "") + word);
            System.out.println(String.format("%" + (maxWordLength - word.length()) + "s", "") + explain);
        }
    }
}
