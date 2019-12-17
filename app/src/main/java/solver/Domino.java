package solver;

import java.util.Random;

public class Domino {
    private static char[][] cpm = new char[40320][5];
    private static char[][] epm = new char[40320][5];
    private static byte[] cpd = new byte[40320];
    private static byte[] epd = new byte[40320];
    private static byte[] faces = {3, 1, 1, 1, 1};
    private static String[] turn = {"U", "L", "R", "F", "B"};
    private static String[] suff = {"'", "2", ""};
    private static int[] seq = new int[20];

    private static boolean ini = false;
    private static void init() {
        if (ini) return;
        long t = System.currentTimeMillis();
        int[] arr = new int[8];
        int[] temp = new int[8];
        for (int i = 0; i < 40320; i++) {
            Utils.set8Perm(temp, 8, i);
            for (int j = 0; j < 5; j++) {
                //Util.set8Perm(arr, i);
                System.arraycopy(temp, 0, arr, 0, 8);
                switch (j) {
                    case 0: Utils.circle(arr, 0, 3, 2, 1); break;	//U
                    case 1: Utils.swap(arr, 0, 7, 3, 4); break;	//L
                    case 2: Utils.swap(arr, 1, 6, 2, 5); break;	//R
                    case 3: Utils.swap(arr, 3, 6, 2, 7); break;	//F
                    case 4: Utils.swap(arr, 0, 5, 1, 4); break;	//B
                }
                cpm[i][j] = (char) Utils.get8Perm(arr, 8);
                System.arraycopy(temp, 0, arr, 0, 8);
                //Util.set8Perm(arr, i);
                switch (j) {
                    case 0: Utils.circle(arr, 0, 3, 2, 1); break;	//U
                    case 1: Utils.swap(arr, 3, 7); break;	//L
                    case 2: Utils.swap(arr, 1, 5); break;	//R
                    case 3: Utils.swap(arr, 2, 6); break;	//F
                    case 4: Utils.swap(arr, 0, 4); break;	//B
                }
                epm[i][j] = (char) Utils.get8Perm(arr, 8);
            }
        }

        for (int i = 1; i < 40320; i++)
            cpd[i] = epd[i] = -1;
        cpd[0] = epd[0] = 0;
        //int n = 1;
        for (int d = 0; d < 13; d++) {
            for (int i = 0; i < 40320; i++)
                if (cpd[i] == d)
                    for (int k = 0; k < 5; k++)
                        for (int y = i, m = 0; m < faces[k]; m++) {
                            y = cpm[y][k];
                            if (cpd[y] < 0) {
                                cpd[y] = (byte) (d + 1);
                                //n++;
                            }
                        }
            //System.out.println(d+1+" "+n);
        }
        //n = 1;
        for (int d = 0; d < 11; d++) {
            for (int i = 0; i < 40320; i++)
                if (epd[i] == d)
                    for (int k = 0; k < 5; k++)
                        for (int y = i, m = 0; m < faces[k]; m++) {
                            y = epm[y][k];
                            if (epd[y] < 0) {
                                epd[y] = (byte) (d + 1);
                                //n++;
                            }
                        }
            //System.out.println(d+1+" "+n);
        }
        t = System.currentTimeMillis() - t;
        //Log.w("dct", "init "+t);
        ini = true;
    }

    private static boolean search(int cp, int ep, int d, int lf) {
        if (d == 0) return cp == 0 && ep == 0;
        if (cpd[cp] > d || epd[ep] > d) return false;
        int y, s;
        for (int i = 0; i < 5; i++)
            if (i != lf) {
                y = cp; s = ep;
                for (int k = 0; k < faces[i]; k++) {
                    y = cpm[y][i]; s = epm[s][i];
                    if (search(y, s, d - 1, i)) {
                        seq[d] = i * 3 + (i < 1 ? k : 1);
                        //sb.append(turn[i]+(i<1?suff[k]:"2")+" ");
                        return true;
                    }
                }
            }
        return false;
    }

    public static String scramble() {
        init();
        Random r = new Random();
        int cp = r.nextInt(40320);
        int ep = r.nextInt(40320);

        for (int d = 0; d < 19; d++) {
            if (search(cp, ep, d, -1)) {
                if (d < 2) return scramble();
                if (d < 4) continue;
                StringBuilder s = new StringBuilder();
                for (int i = 1; i <= d; i++) {
                    s.append(turn[seq[i] / 3]).append(suff[seq[i] % 3]).append(' ');
                }
                return s.toString();
            }
        }
        return "error";
    }

    private static int[] img = new int[42];
    private static void initColor() {
        img = new int[] {
                     3, 3, 3,
                     3, 3, 3,
                     3, 3, 3,
            5, 5, 5, 4, 4, 4, 2, 2, 2, 1, 1, 1,
            5, 5, 5, 4, 4, 4, 2, 2, 2, 1, 1, 1,
                     0, 0, 0,
                     0, 0, 0,
                     0, 0, 0
        };
    }
    private static void move(int turn) {
        switch (turn) {
            case 0:	//U
                Utils.circle(img,  0,  6,  8,  2);
                Utils.circle(img,  1,  3,  7,  5);
                Utils.circle(img,  9, 12, 15, 18);
                Utils.circle(img, 10, 13, 16, 19);
                Utils.circle(img, 11, 14, 17, 20);
                break;
            case 1:	//D
                Utils.circle(img, 33, 39, 41, 35);
                Utils.circle(img, 34, 36, 40, 38);
                Utils.circle(img, 30, 27, 24, 21);
                Utils.circle(img, 31, 28, 25, 22);
                Utils.circle(img, 32, 29, 26, 23);
                break;
            case 2:	//L
                Utils.swap(img,  9, 23, 11, 21);
                Utils.swap(img, 10, 22,  3, 36);
                Utils.swap(img,  0, 33,  6, 39);
                Utils.swap(img, 20, 24, 32, 12);
                break;
            case 3:	//R
                Utils.swap(img, 15, 29, 17, 27);
                Utils.swap(img, 16, 28,  5, 38);
                Utils.swap(img,  8, 41,  2, 35);
                Utils.swap(img, 14, 30, 26, 18);
                break;
            case 4:	//F
                Utils.swap(img, 12, 26, 14, 24);
                Utils.swap(img, 13, 25,  7, 34);
                Utils.swap(img,  6, 35,  8, 33);
                Utils.swap(img, 11, 27, 15, 23);
                break;
            case 5:	//B
                Utils.swap(img, 18, 32, 20, 30);
                Utils.swap(img, 19, 31,  1, 40);
                Utils.swap(img,  2, 39,  0, 41);
                Utils.swap(img, 17, 21, 29,  9);
                break;
        }
    }

    public static int[] image(String scr) {
        initColor();
        String[] s = scr.split(" ");
        for (int i = 0; i < s.length; i++)
            if (s[i].length() > 0) {
                int mov = "UDLRFB".indexOf(s[i].charAt(0));
                move(mov);
                if (s[i].length() > 1 && mov < 2) {
                    move(mov);
                    if (s[i].charAt(1) == '\'') move(mov);
                }
            }
        return img;
    }
}
