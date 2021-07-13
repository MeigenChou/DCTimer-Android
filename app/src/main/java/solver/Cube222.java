package solver;

import android.util.Log;

import java.util.Random;

import static solver.Utils.suffInv;

public class Cube222 {
    private static int[][] state = new int[2][8];
    private static byte[] perm = new byte[5040];
    private static byte[] twst = new byte[729];
    private static short[][] permmv = new short[5040][3];
    private static short[][] twstmv = new short[729][3];

    private static String[] turn = {"U", "R", "F"};
    private static byte[][] cFacelet = {
            { 3, 4, 9 }, { 1, 20, 5 }, { 2, 8, 17 }, { 0, 16, 21 },
            { 13, 11, 6 }, { 15, 7, 22 }, { 12, 19, 10 }, { 14, 23, 18 }
    };
    //private static int[] seq = new int[12];
    private static Random r = new Random();

    static {
        calcperm();
    }

    private static void permMove(int[] ps, int m) {
        switch (m) {
            case 0:	//U
                Utils.circle(ps, 0, 1, 3, 2); break;
            case 1:	//R
                Utils.circle(ps, 0, 4, 5, 1); break;
            case 2:	//F
                Utils.circle(ps, 0, 2, 6, 4); break;
            case 3:	//D
                Utils.circle(ps, 4, 6, 7, 5); break;
            case 4:	//L
                Utils.circle(ps, 2, 3, 7, 6); break;
            case 5:	//B
                Utils.circle(ps, 1, 5, 7, 3); break;
        }
    }

    private static void twistMove(int[] ps, int m) {
        int c;
        switch (m) {
            case 0:
                Utils.circle(ps, 0, 1, 3, 2);	//U
                break;
            case 1:
                Utils.circle(ps, 0, 4, 5, 1, new int[] {2, 1, 2, 1});   //R
                //c = ps[0]; ps[0] = ps[4] + 2; ps[4] = ps[5] + 1; ps[5] = ps[1] + 2; ps[1] = c + 1;
                break;
            case 2:
                Utils.circle(ps, 0, 2, 6, 4, new int[] {1, 2, 1, 2});   //F
                //c = ps[0]; ps[0] = ps[2] + 1; ps[2] = ps[6] + 2; ps[6] = ps[4] + 1; ps[4] = c + 2;   //F
                break;
            case 3:
                Utils.circle(ps, 4, 6, 7, 5);	//D
                break;
            case 4:
                Utils.circle(ps, 2, 3, 7, 6, new int[] {1, 2, 1, 2});   //L
                //c = ps[2]; ps[2] = ps[3] + 1; ps[3] = ps[7] + 2; ps[7] = ps[6] + 1; ps[6] = c + 2;   //L
                break;
            case 5:
                Utils.circle(ps, 1, 5, 7, 3, new int[] {2, 1, 2, 1});   //B
                //c = ps[1]; ps[1] = ps[5] + 2; ps[5] = ps[7] + 1; ps[7] = ps[3] + 2; ps[3] = c + 1;   //B
                break;
        }
    }

    private static void doMove(int m, int n) {
        n %= 4;
        if (n > 0) {
            switch (m) {
                case 0:	//U
                case 1:	//R
                case 2:	//F
                case 3:	//D
                case 4:	//L
                case 5:	//B
                    for (int i = 0; i < n; i++) {
                        permMove(state[0], m);
                        twistMove(state[1], m);
                    }
                    break;
                case 6:	//y
                case 7:	//x
                case 8:	//z
                    for (int i = 0; i < n; i++) {
                        permMove(state[0], m - 6);
                        twistMove(state[1], m - 6);
                    }
                    for (int i = 0; i < 4 - n; i++) {
                        permMove(state[0], m - 3);
                        twistMove(state[1], m - 3);
                    }
                    break;
            }
        }
    }

    private static void swap(int first, int second) {
        if (first < 0 || second < 0 || first > 7 || second > 7 || first == second) {
            return;
        }
        //位置交换
        int tmp = state[0][first];
        state[0][first] = state[0][second];
        state[0][second] = tmp;
        //色相交换
        tmp = state[1][first];
        state[1][first] = state[1][second];
        state[1][second] = tmp;
    }

    private static void twist(int corner, int value) {
        if (value < 0) return;
        state[1][corner] += value;
    }

    private static void reset() {
        for (int i = 0; i < 8; i++) {
            state[0][i] = i;
            state[1][i] = 0;
        }
    }

    private static int getprmmv(int p, int m) {
        //given position p<5040 and move m<3, return new position number
        //convert number into array;
        int[] ps = new int[7];
        Utils.set8Perm(ps, 7, p);
        //perform move on array
        permMove(ps, m);
        //convert array back to number
        return Utils.get8Perm(ps, 7);
    }

    private static int gettwsmv(int p, int m) {
        //given orientation p<729 and move m<3, return new orientation number
        //convert number into array;
        int[] ps = new int[7];
        Utils.idxToOri(ps, p, 7, true);
        //perform move on array
        twistMove(ps, m);
        //convert array back to number
        return Utils.oriToIdx(ps, 7, true);
    }

    private static void calcperm() {
        //calculate solving arrays
        //first permutation
        for (int p = 0; p < 5040; p++) {
            perm[p] = -1;
            for (int m = 0; m < 3; m++)
                permmv[p][m] = (short) getprmmv(p, m);
        }
        perm[0] = 0;
        Utils.createPrun(perm, 7, permmv, 3);

        //then twist
        for (int p = 0; p < 729; p++) {
            twst[p] = -1;
            for (int m = 0; m < 3; m++)
                twstmv[p][m] = (short) gettwsmv(p, m);
        }
        twst[0] = 0;
        Utils.createPrun(twst, 6, twstmv, 3);
    }

    private static boolean search(int p, int t, int l, int lm, int[] seq) {
        //searches for solution, from position p|t, in l moves exactly. last move was lm, current depth=d
        if (l == 0) return p == 0 && t == 0;
        if (perm[p] > l || twst[t] > l) return false;
        if (lm == -2) {
            int n = r.nextInt(9);
            int m = n / 3;
            n %= 3;
            int q = p, s = t;
            for (int a = 0; a <= n; a++) {
                q = permmv[q][m];
                s = twstmv[s][m];
            }
            if (search(q, s, l - 1, m, seq)) {
                seq[l] = m * 3 + n;
                return true;
            }
        } else for (int m = 0; m < 3; m++) {
            if (m != lm) {
                int q = p, s = t;
                for (int a = 0; a < 3; a++) {
                    q = permmv[q][m];
                    s = twstmv[s][m];
                    if (search(q, s, l - 1, m, seq)) {
                        seq[l] = m * 3 + a;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static String solve(int p, int o) {
        int[] seq = new int[12];
        for (int l = 0; l < 12; l++) {
            if (search(p, o, l, -1, seq))  {
                if (l < 2) return "error";
                if (l < 5) {
                    //sol = new StringBuilder();
                    continue;
                }
                StringBuilder sol = new StringBuilder();
                for (int i = 1; i <= l; i++)
                    sol.append(turn[seq[i] / 3]).append(suffInv[seq[i] % 3]).append(" ");
                return sol.toString();
            }
        }
        return "error";
    }

    private static boolean checkNobar(int[] perm, int[] ori) {
        char[] ts = {1, 2, 4, 8, 16, 32};
        char[] f = new char[24];
        Utils.fillFacelet(cFacelet, f, perm, ori, ts, 4);
        for (int i = 0; i < 24; i += 4) {
            if (((f[i] | f[i + 3]) & (f[i + 1] | f[i + 2])) != 0)
                return false;
        }
        return true;
    }

    public static void randomEG(int type, String olls) {
        reset();
        //整体转动
        //for (int i = 0; i < 3; i++)
        doMove(6, r.nextInt(4));
        //交换底层2块
        //doMove(3, r.nextInt(4));
        switch (type) {
            case 4:	//不交换
                break;
            case 2:	//交换相邻两块
                swap(4, 6);
                break;
            case 1:	//交换相对两块
                swap(5, 6);
                break;
            case 6:	//不交换或交换相邻块
                if (r.nextInt(2) == 1)
                    swap(4, 5);
                break;
            case 5:	//不交换或交换相对块
                if (r.nextInt(2) == 1)
                    swap(5, 6);
                break;
            case 3:	//交换任意两块
                swap(4 + r.nextInt(2), 6);
                break;
            default:
                switch (r.nextInt(3)) {
                    case 0:
                        break;
                    case 1:
                        swap(4, 6);
                        break;
                    case 2:
                        swap(5, 6);
                        break;
                }
                break;
        }
        //随机顶层
        for (int i = 0; i < 4; i++) {
            swap(i, i + r.nextInt(4 - i));
        }
        if (olls.equals(""))
            Utils.idxToOri(state[1], r.nextInt(27), 4, true);
        else if (olls.equals("X") || olls.equals("PHUTLSA")) {
            Utils.idxToOri(state[1], r.nextInt(26) + 1, 4, true);
        } else {
            char oll = olls.charAt(r.nextInt(olls.length()));
            switch (oll) {
                case 'P':
                    twist(0, 2); twist(1, 1); twist(2, 2); twist(3, 1);
                    break;
                case 'H':
                    twist(0, 2); twist(1, 1); twist(2, 1); twist(3, 2);
                    break;
                case 'U':
                    twist(2, 2); twist(3, 1);
                    break;
                case 'T':
                    twist(2, 1); twist(3, 2);
                    break;
                case 'L':
                    twist(0, 2); twist(3, 1);
                    break;
                case 'S':
                    twist(0, 2); twist(1, 2); twist(3, 2);
                    break;
                case 'A':
                    twist(0, 1); twist(1, 1); twist(2, 1);
                    break;
                case 'N':
                    break;
            }
        }
        doMove(0, r.nextInt(4));
        //将DBL块放到D层
        while (state[0][4] != 7 && state[0][5] != 7 && state[0][6] != 7 && state[0][7] != 7) {
            doMove(7, 1);
        }
        //将DBL块放回原位
        while (state[0][7] != 7) {
            doMove(6, 1);
        }
        //调整DBL块色向
        while (state[1][7] % 3 != 0) {
            doMove(7, 1);
            doMove(6, 1);
        }
    }

    public static void randomTEG(int type, int twist) {
        reset();
        //整体转动
        //for (int i = 0; i < 3; i++)
        doMove(6, r.nextInt(4));
        //交换底层2块
        //doMove(3, r.nextInt(4));
        switch (type) {
            case 4:	//不交换
                break;
            case 2:	//交换相邻两块
                swap(4, 6);
                break;
            case 1:	//交换相对两块
                swap(5, 6);
                break;
            case 6:	//不交换或交换相邻块
                if (r.nextInt(2) == 1)
                    swap(4, 5);
                break;
            case 5:	//不交换或交换相对块
                if (r.nextInt(2) == 1)
                    swap(5, 6);
                break;
            case 3:	//交换任意两块
                swap(4+r.nextInt(2), 6);
                break;
            default:
                switch (r.nextInt(3)) {
                    case 0:
                        break;
                    case 1:
                        swap(4, 6);
                        break;
                    case 2:
                        swap(5, 6);
                        break;
                }
                break;
        }
        //随机顶层
        for (int i = 0; i < 4; i++) {
            swap(i, i + r.nextInt(4 - i));
        }
        Utils.idxToOri(state[1], r.nextInt(27), 4, true);
        //一个底角翻转
        twist(4, twist);
        //随机一个顶角翻转
        twist(r.nextInt(4), 3 - twist);
        doMove(0, r.nextInt(4));
        //将DBL块放到D层
        while (state[0][4] != 7 && state[0][5] != 7 && state[0][6] != 7
                && state[0][7] != 7) {
            doMove(7, 1);
        }
        //将DBL块放回原位
        while (state[0][7] != 7) {
            doMove(6, 1);
        }
        //调整DBL块色向
        while (state[1][7] % 3 != 0) {
            doMove(7, 1);
            doMove(6, 1);
        }
    }

    public static String scramble() {
        String scramble;
        do {
            int p = r.nextInt(5040);
            int o = r.nextInt(729);
            scramble = solve(p, o);
        } while (scramble.equals("error"));
        return scramble;
    }

    public static String scrambleEG(int type) {
        String scramble;
        do {
            switch (type) {
                case 0:
                    randomEG(4, "X");
                    break;
                case 1:
                    randomEG(2, "X");
                    break;
                case 2:
                    randomEG(1, "X");
                    break;
            }
            int p = Utils.get8Perm(state[0], 7);
            int o = Utils.oriToIdx(state[1], 7, true);
            scramble = solve(p, o);
        } while (scramble.equals("error"));
        return scramble;
    }

    public static String scramblePBL() {
        String scramble;
        do {
            randomEG(0, "N");
            int p = Utils.get8Perm(state[0], 7);
            int o = Utils.oriToIdx(state[1], 7, true);
            scramble = solve(p, o);
        } while (scramble.equals("error"));
        return scramble;
    }

    public static String scrambleTCLL(int twist) {
        String scramble;
        do {
            randomTEG(4, twist);
            int p = Utils.get8Perm(state[0], 7);
            int o = Utils.oriToIdx(state[1], 7, true);
            scramble = solve(p, o);
        } while (scramble.equals("error"));
        return scramble;
    }

    public static String scrambleTEG1(int twist) {
        String scramble;
        do {
            randomTEG(2, twist);
            int p = Utils.get8Perm(state[0], 7);
            int o = Utils.oriToIdx(state[1], 7, true);
            scramble = solve(p, o);
        } while (scramble.equals("error"));
        return scramble;
    }

    public static String scrambleTEG2(int twist) {
        String scramble;
        do {
            randomTEG(1, twist);
            int p = Utils.get8Perm(state[0], 7);
            int o = Utils.oriToIdx(state[1], 7, true);
            scramble = solve(p, o);
        } while (scramble.equals("error"));
        return scramble;
    }

    public static String scrambleEG(int type, String olls) {
        String scramble;
        do {
            randomEG(type, olls);
            int p = Utils.get8Perm(state[0], 7);
            int o = Utils.oriToIdx(state[1], 7, true);
            scramble = solve(p, o);
        } while (scramble.equals("error"));
        return scramble;
    }

    public static String scrambleNobar() {
        int[] perm = new int[8];
        int[] ori = new int[8];
        perm[7] = 7;
        int p, o;
        do {
            p = r.nextInt(5040);
            o = r.nextInt(729);
            Utils.idxToPerm(perm, p, 7, false);
            Utils.idxToOri(ori, o, 7, true);
        } while (!checkNobar(perm, ori));
        return solve(p, o);
    }

    private static String scramble(int minLen) {
        int p = r.nextInt(5040);
        int o = r.nextInt(729);
        int[] seq = new int[12];
        for (int l = 0; l < 12; l++) {
            if (search(p, o, l, -1, seq)) {
                if (l < minLen) return "error";
                if (l < 11) {
                    search(p, o, 11, -2, seq);
                }
                StringBuilder sol = new StringBuilder();
                int last = -1;
                for (int i = 1; i <= 11; i++) {
                    if (last == seq[i] / 3) return "error";
                    sol.append(turn[seq[i] / 3]).append(suffInv[seq[i] % 3]).append(" ");
                    last = seq[i] / 3;
                }
                return sol.toString();
            }
        }
        return "error";
    }

    public static String scrambleWCA() {
        String scramble;
        do {
            scramble = scramble(4);
        } while (scramble.equals("error"));
        return scramble;
    }
}
