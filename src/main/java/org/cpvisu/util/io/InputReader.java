package org.cpvisu.util.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * shameless copy of the input reader of MiniCP, described at http://www.minicp.org/
 */
public class InputReader {

    private BufferedReader in;
    private StringTokenizer tokenizer;

    public InputReader(String file) {
        try {

            FileInputStream istream = new FileInputStream(file);
            in = new BufferedReader(new InputStreamReader(istream));
            tokenizer = new StringTokenizer("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTokenizer() {
        if (!tokenizer.hasMoreTokens()) {
            try {
                String line;
                do {
                    line = in.readLine();
                    if (line == null) {
                        System.out.println("No more line to read");
                        throw new RuntimeException("End of file");
                    }
                    tokenizer = new StringTokenizer(line);
                } while (!tokenizer.hasMoreTokens());

            } catch (IOException e) {
                throw new RuntimeException(e.toString());
            }
        }
    }

    public Integer getInt() throws RuntimeException {
        updateTokenizer();
        return Integer.parseInt(tokenizer.nextToken());
    }

    public Double getDouble() throws RuntimeException {
        updateTokenizer();
        return Double.parseDouble(tokenizer.nextToken());
    }

    public int[][] getMatrix(int n, int m) throws RuntimeException {
        int[][] matrix = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = getInt();
            }
        }
        return matrix;
    }


    public Integer[] getIntLine() throws RuntimeException {
        updateTokenizer();
        Integer[] res = new Integer[tokenizer.countTokens()];
        for (int i = 0; i < res.length; i++) {
            res[i] = Integer.parseInt(tokenizer.nextToken());
        }
        return res;
    }

    public String getString() throws RuntimeException {
        updateTokenizer();
        return tokenizer.nextToken();
    }

}
