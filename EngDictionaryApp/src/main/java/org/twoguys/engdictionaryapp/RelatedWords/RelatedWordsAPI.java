package org.twoguys.engdictionaryapp.RelatedWords;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class RelatedWordsAPI {

    private final String USER_AGENT = "Mozilla/5.0";
    public final static int SYNONYMS = 0;
    public final static int ANTONYMS = 1;

    public ArrayList<String> searchRelated(final int relatedType, String wordToSearch) throws Exception {
        System.out.println("Sending request...");

        String url;
        if (relatedType == SYNONYMS) {
            url = "https://api.datamuse.com/words?rel_syn=" + wordToSearch;
        } else {
            url = "https://api.datamuse.com/words?rel_ant=" + wordToSearch;
        }
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();

        // ordering the response
        StringBuilder response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        con.disconnect();

        ObjectMapper mapper = new ObjectMapper();

        ArrayList<String> ans = new ArrayList<>();
        try {
            // converting JSON array to ArrayList of words
            ArrayList<Words> words = mapper.readValue(
                    response.toString(),
                    mapper.getTypeFactory().constructCollectionType(ArrayList.class, Words.class)
            );

            System.out.println("Synonym word of '" + wordToSearch + "':");
            if (words.size() > 0) {
                for (Words word : words) {
                    ans.add(word.getWord());
                }
            } else {
                System.out.println("none synonym word!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }

    // word and score attributes are from DataMuse API
    public static class Words {
        public String word;
        public int score;

        public String getWord() {
            return this.word;
        }

        public int getScore() {
            return this.score;
        }
    }
}
