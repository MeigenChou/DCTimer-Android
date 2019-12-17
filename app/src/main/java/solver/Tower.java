package solver;

import java.util.Random;

public class Tower {
    static char[][] cpm = new char[40320][4];
    static byte[][] epm = new byte[6][4];
    static byte[] cpd = new byte[40320];
    static byte[] epd = new byte[6];
    //private static StringBuilder sb;
    private static byte[] faces = {3, 1, 1, 3};
    private static String[] turn = {"U", "R", "F", "D"};
    private static String[] suff = {"'", "2", ""};
    private static int[] seq = new int[20];

    private static boolean ini = false;
    public static void init() {
        if (ini) return;
        int[] arr = new int[8];
    	/*	0	1
		 *	3	2
		 *
		 *	4	5
		 *	7	6
		 */
        for (int i = 0; i < 40320; i++) {
            for (int j = 0; j < 4; j++) {
                Utils.set8Perm(arr, 8, i);
                switch (j) {
                    case 0: Utils.circle(arr, 0, 3, 2, 1); break;	//U
                    case 1: Utils.circle(arr, 1, 2, 5, 6); break;	//R2
                    case 2: Utils.circle(arr, 2, 3, 4, 5); break;	//F2
                    case 3: Utils.circle(arr, 4, 7, 6, 5); break;	//D
                }
                cpm[i][j] = (char) Utils.get8Perm(arr, 8);
            }
        }
        for (int i = 1; i < 40320; i++) cpd[i] = -1;
        cpd[0] = 0;
        //int n = 1;
        for (int d = 0; d < 13; d++) {
            for (int i = 0; i < 40320; i++)
                if (cpd[i] == d)
                    for (int k = 0; k < 4; k++)
                        for (int y = i, m = 0; m < faces[k]; m++) {
                            y = cpm[y][k];
                            if (faces[k] == 1) y = cpm[y][k];
                            if (cpd[y] < 0) {
                                cpd[y] = (byte) (d + 1);
                                //n++;
                            }
                        }
            //System.out.println(d+1+" "+n);
        }
		/*	-	0
		 *	2	1
		 */
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                Utils.idxToPerm(arr, i, 3, false);
                switch (j) {
                    case 1: Utils.swap(arr, 0, 1); break;	//R2
                    case 2: Utils.swap(arr, 1, 2); break;	//F2
                }
                epm[i][j] = (byte) Utils.permToIdx(arr, 3, false);
            }
        }
        for (int i = 1; i < 6; i++) epd[i] = -1;
        epd[0] = 0;
        //n = 1;
        for (int d = 0; d < 3; d++) {
            for (int i = 0; i < 6; i++)
                if (epd[i] == d)
                    for (int k = 1; k < 3; k++) {
                        int y = epm[i][k];
                        if (epd[y] < 0) {
                            epd[y] = (byte) (d + 1);
                            //n++;
                        }
                    }
            //System.out.println(d+1+" "+n);
        }
        ini = true;
    }

    private static boolean search(int cp, int ep, int d, int lf) {
        if (d == 0) return cp == 0 && ep == 0;
        if (cpd[cp] > d || epd[ep] > d) return false;
        int y, s;
        for (int i = 0; i < 4; i++) {
            if (i != lf) {
                y = cp; s = ep;
                for (int k = 0; k < faces[i]; k++) {
                    y = cpm[y][i];
                    if (faces[i] == 1) y = cpm[y][i];
                    s = epm[s][i];
                    if (search(y, s, d - 1, i)) {
                        seq[d] = i * 3 + (faces[i] == 1 ? 1 : k);
                        //sb.append(turn[i]).append(faces[i] == 1 ? "2" : suff[k]).append(' ');
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String scramble() {
        init();
        Random r = new Random();
        int cp = r.nextInt(40320);
        int ep = r.nextInt(6);
        for (int d = 0; d < 20; d++) {
            if (search(cp, ep, d, -1)) {
                if (d < 2) return scramble();
                if (d < 4) {
                    continue;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= d; i++)
                    sb.append(turn[seq[i] / 3]).append(suff[seq[i] % 3]).append(" ");
                return sb.toString();
            }
        }
        return "error";
    }

    private static int[] img = new int[32];
    private static void initColor() {
        img = new int[] {
                      3, 3,
                      3, 3,
                5, 5, 4, 4, 2, 2, 1, 1,
                5, 5, 4, 4, 2, 2, 1, 1,
                5, 5, 4, 4, 2, 2, 1, 1,
                      0, 0,
                      0, 0
        };
    }

    private static void move(int turn) {
        switch (turn) {
            case 0:	//U
                Utils.circle(img,  0,  2,  3,  1);
                Utils.circle(img,  5,  7,  9, 11);
                Utils.circle(img,  4,  6,  8, 10);
                break;
            case 1:	//R
                Utils.swap(img,  1, 29,  3, 31);
                Utils.swap(img,  8, 25,  9, 24);
                Utils.swap(img, 16, 17, 15, 18);
                Utils.swap(img,  7, 26, 23, 10);
                break;
            case 2:	//F
                Utils.swap(img,  2, 29,  3, 28);
                Utils.swap(img,  6, 23,  7, 22);
                Utils.swap(img, 14, 15, 13, 16);
                Utils.swap(img,  5, 24, 21,  8);
                break;
            case 3:	//D
                Utils.circle(img, 28, 30, 31, 29);
                Utils.circle(img, 27, 25, 23, 21);
                Utils.circle(img, 26, 24, 22, 20);
                break;
        }
    }

    public static int[] image(String scramble) {
        initColor();
        for (String s : scramble.split(" "))
            if (s.length() > 0) {
                int mov = "URFD".indexOf(s.charAt(0));
                move(mov);
                if (s.length() > 1 && faces[mov] != 1) {
                    move(mov);
                    if (s.charAt(1) == '\'') move(mov);
                }
            }
        return img;
    }
}
