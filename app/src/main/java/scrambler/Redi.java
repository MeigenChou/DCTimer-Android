package scrambler;

import solver.Utils;

public class Redi {
    private static int[] img = new int[48];
    private static void initColor() {
        img = new int[] {
                         3, 3, 3,
                         3,    3,
                         3, 3, 3,
                5, 5, 5, 4, 4, 4, 2, 2, 2, 1, 1, 1,
                5,    5, 4,    4, 2,    2, 1,    1,
                5, 5, 5, 4, 4, 4, 2, 2, 2, 1, 1, 1,
                         0, 0, 0,
                         0,    0,
                         0, 0, 0
        };
    }

    private static void move(int turn) {
        switch (turn) {
            case 0: //L
                Utils.circle(img,  5, 10, 11);
                Utils.circle(img,  3, 21, 12);
                Utils.circle(img,  9, 22,  6);
                break;
            case 1: //R
                Utils.circle(img,  7, 13, 14);
                Utils.circle(img,  4, 12, 24);
                Utils.circle(img,  6, 23, 15);
                break;
            case 2: //x
                Utils.circle(img,  0, 11, 40, 39);
                Utils.circle(img,  1, 12, 41, 38);
                Utils.circle(img,  2, 13, 42, 37);
                Utils.circle(img,  3, 22, 43, 27);
                Utils.circle(img,  4, 23, 44, 26);
                Utils.circle(img,  5, 31, 45, 19);
                Utils.circle(img,  6, 32, 46, 18);
                Utils.circle(img,  7, 33, 47, 17);
                Utils.circle(img,  8, 10, 30, 28);
                Utils.circle(img,  9, 21, 29, 20);
                Utils.circle(img, 14, 34, 36, 16);
                Utils.circle(img, 15, 24, 35, 25);
                break;
        }
    }

    private static String moveIdx = "LRx";
    public static int[] image(String scr) {
        initColor();
        String[] s = scr.split(" ");
        for (int i = 0; i < s.length; i++)
            if (s[i].length() > 0) {
                int mov = moveIdx.indexOf(s[i].charAt(0));
                move(mov);
                if (s[i].length() > 1 && mov < 2) {
                    move(mov);
                }
            }
        return img;
    }
}
