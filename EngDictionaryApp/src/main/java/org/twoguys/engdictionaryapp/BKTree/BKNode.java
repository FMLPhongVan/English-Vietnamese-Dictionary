package org.twoguys.engdictionaryapp.BKTree;

public class BKNode {
    public final static int MAX_WORD_LEN = 42;
    private String word = "";
    private BKNode[] next = new BKNode[2 * MAX_WORD_LEN];

    public BKNode() {
        word = "";
        for (int i = 0; i < 2 * MAX_WORD_LEN; ++i) {
            next[i] = null;
        }
    }

    public BKNode(BKNode newNode) {
        this.word = newNode.word;
        for (int i = 0; i < 2 * MAX_WORD_LEN; ++i) {
            this.next[i] = newNode.next[i];
        }
    }

    public BKNode(String word) {
        this.word = word;
        for (int i = 0; i < 2 * MAX_WORD_LEN; ++i) {
            next[i] = null;
        }
    }

    public String getWord() {
        return word;
    }

    public BKNode getNext(int k) {
        return next[k];
    }

    public void setNext(int k, BKNode nextNode) {
        this.next[k] = nextNode;
    }

    public int levenshteinDistance(String otherWord) {
        int m = word.length(), n = otherWord.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; ++i) dp[i][0] = i;
        for (int j = 0; j <= n; ++j) dp[0][j] = j;

        for (int i = 1; i <= m; ++i) {
            for (int j = 1; j <= n; ++j) {
                if (word.charAt(i - 1) != otherWord.charAt(j - 1)) {
                    dp[i][j] = Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1])) + 1;
                } else {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }
        return dp[m][n];
    }
}
