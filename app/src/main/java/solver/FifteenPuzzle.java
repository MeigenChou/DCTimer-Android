package solver;

import java.util.Random;

public class FifteenPuzzle {

    private static int[] pz = new int[16];
    private static int[] arr = new int[16];
    private static int[][] moveIdx = {
            {-1, 4, -1, 1}, {-1, 5, 0, 2}, {-1, 6, 1, 3}, {-1, 7, 2, -1}, {0, 8, -1, 5}, {1, 9, 4, 6}, {2, 10, 5, 7}, {3, 11, 6, -1},
            {4, 12, -1, 9}, {5, 13, 8, 10}, {6, 14, 9, 11}, {7, 15, 10, -1}, {8, -1, -1, 13}, {9, -1, 12, 14}, {10, -1, 13, 15}, {11, -1, 14, -1}
    };

    private void randomPuz(Random r) {
        for (int i = 0; i < 16; i++)
            pz[i] = i;
        for (int i = 0; i < 16; i++) {
            int n = i + r.nextInt(16 - i);
            if (n != i)
                Utils.swap(arr, i, n);
        }
    }

    public static int[] image(String scramble, boolean pieceMove) {
        for (int i = 0; i < 16; i++) pz[i] = i;
        int pos = 15;
        String[] s = scramble.split(" ");
        for (int i = 0; i < s.length; i++) {
            int move = 0;
            switch (s[i].charAt(0)) {
                case 'U':
                    move = pieceMove ? 1 : 0;
                    //next = moveIdx[pos][pieceMove ? 1 : 0];
                    break;
                case 'D':
                    move = pieceMove ? 0 : 1;
                    //next = moveIdx[pos][pieceMove ? 0 : 1];
                    break;
                case 'L':
                    move = pieceMove ? 3 : 2;
                    //next = moveIdx[pos][pieceMove ? 3 : 2];
                    break;
                case 'R':
                    move = pieceMove ? 2 : 3;
                    //next = moveIdx[pos][pieceMove ? 2 : 3];
                    break;
            }
            int next = moveIdx[pos][move];
            if (next != -1) {
                Utils.swap(pz, pos, next);
                pos = next;
            }
            if (s[i].length() > 1) {
                next = moveIdx[pos][move];
                if (next != -1) {
                    Utils.swap(pz, pos, next);
                    pos = next;
                }
                if (s[i].charAt(1) == '3') {
                    next = moveIdx[pos][move];
                    if (next != -1) {
                        Utils.swap(pz, pos, next);
                        pos = next;
                    }
                }
            }
        }
        return pz;
    }
}
