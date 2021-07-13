package scrambler;

import android.util.Log;

import java.util.Random;

import static scrambler.Scrambler.cubesuff;

public class MegaScramble {
    private static Random r = new Random();
    private static String rndEl(String[] x) {
        return x[r.nextInt(x.length)];
    }

    static String edgescramble(String start, String[] end, String[] moves, int scrambleLen) {
        int u = 0, d = 0;
        int[] movemis = new int[moves.length];
        String[][] triggers = {{"R","R'"}, {"R'","R"}, {"L","L'"}, {"L'","L"}, {"F'","F"}, {"F","F'"}, {"B","B'"}, {"B'","B"}};
        String[] ud = {"U", "D"};
        //String ss = start;
        StringBuilder sb = new StringBuilder(start);
        //String v = "";
        // initialize move misalignments
        for (int i = 0; i < moves.length; i++)
            movemis[i] = 0;
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < scrambleLen; i++) {
            // apply random moves
            boolean done = false;
            while (!done) {
                s = new StringBuilder();
                for (int j = 0; j < moves.length; j++) {
                    int x = r.nextInt(4);
                    movemis[j] += x;
                    if (x != 0) {
                        done = true;
                        s.append(' ').append(moves[j]).append(cubesuff[x - 1]);
                    }
                }
            }
            sb.append(s.toString());
            // apply random trigger, update U/D
            int trigger = r.nextInt(8);
            int layer = r.nextInt(2);
            int turn = r.nextInt(3);
            sb.append(" ").append(triggers[trigger][0]).append(" ").append(ud[layer]).append(cubesuff[turn]).append(" ").append(triggers[trigger][1]);
            if (layer == 0) u += turn + 1;
            if (layer == 1) d += turn + 1;
        }
        // fix everything
        for (int i = 0; i < moves.length; i++) {
            int x = 4 - (movemis[i] % 4);
            if (x < 4)
                sb.append(" ").append(moves[i]).append(cubesuff[x - 1]);
        }
        u = 4 - (u % 4); d = 4 - (d % 4);
        if (u < 4)
            sb.append(" U").append(cubesuff[u - 1]);
        if (d < 4)
            sb.append(" D").append(cubesuff[d - 1]);
        sb.append(" ").append(rndEl(end));
        return sb.toString();
    }

    static String megascramble(String[][] turns, String[] suffixes, int len, String separator) {
        int[] donemoves = new int[turns[0].length];
        int lastaxis = -1;
        StringBuilder scr = new StringBuilder();
        for (int j = 0; j < len; j++) {
            int done = 0;
            do {
                int first = r.nextInt(turns.length);
                int second = r.nextInt(turns[first].length);
                if (first != lastaxis || donemoves[second] == 0) {
                    if (j != 0) scr.append(separator);
                    if (first == lastaxis) {
                        donemoves[second] = 1;
                        if (suffixes == null) scr.append(turns[first][second]);
                        else scr.append(turns[first][second]).append(rndEl(suffixes));
                    } else {
                        for (int k = 0; k < turns[first].length; k++)
                            donemoves[k] = 0;
                        lastaxis = first;
                        donemoves[second] = 1;
                        if (suffixes == null) scr.append(turns[first][second]);
                        else scr.append(turns[first][second]).append(rndEl(suffixes));
                    }
                    done = 1;
                }
            } while (done == 0);
        }
        return scr.toString();
    }

    static String megascramble(String[][] turns, String[] suffixes, int len) {
        return megascramble(turns, suffixes, len, " ");
    }

    static String megascramble(String[][][] turns, String[] suffixes, int scrambleLen) {
        int[] donemoves = new int[turns[0].length];
        int lastaxis;
        StringBuilder scr = new StringBuilder();
        lastaxis = -1;
        for (int j = 0; j < scrambleLen; j++) {
            int done = 0;
            do {
                int first = r.nextInt(turns.length);
                int second = r.nextInt(turns[first].length);
                if (first != lastaxis) {
                    for (int k = 0; k < turns[first].length; k++)
                        donemoves[k] = 0;
                    lastaxis = first;
                }
                if (donemoves[second] == 0) {
                    donemoves[second] = 1;
                    if (suffixes == null) scr.append(rndEl(turns[first][second])).append(' ');
                    else scr.append(rndEl(turns[first][second])).append(rndEl(suffixes)).append(' ');
                    done = 1;
                }
            } while (done == 0);
        }
        return scr.toString();
    }

    static String helicubescramble(int scrambleLen) {
        int j, k;
        String[] faces = {"UF", "UR", "UB", "UL", "FR", "BR", "BL", "FL", "DF", "DR", "DB", "DL"};
        int[] used = new int[12];
        // adjacency table
        String[] adj = {"010110010000", "101011000000", "010101100000", "101000110000", "110000001100", "011000000110", "001100000011", "100100001001", "000010010101", "000011001010", "000001100101", "000000111010"};
        // now generate the scramble(s)
        StringBuilder sb = new StringBuilder();
        for (j = 0; j < 12; j++)
            used[j] = 0;
        for (j = 0; j < scrambleLen; j++) {
            boolean done = false;
            do {
                int face = r.nextInt(12);
                if (used[face] == 0) {
                    sb.append(faces[face]).append(" ");
                    for (k = 0; k < 12; k++) {
                        if (adj[face].charAt(k) == '1')
                            used[k] = 0;
                    }
                    used[face] = 1;
                    done = true;
                }
            } while (!done);
        }
        return sb.toString();
    }

    static String rediScramble(String[] suff, int scrambleLen) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < scrambleLen; i++) {
            if (i != 0) sb.append("x ");
            sb.append(megascramble(new String[][] {{"R"}, {"L"}}, suff, 3 + (int) (Math.random() * 3)));
        }
        return sb.toString();
    }

    static String gigascramble(int scrambleLen) {
        int i, j;
        StringBuilder sb = new StringBuilder();
        String[] El = {"+", "++", "-", "--"};
        String[] minxsuff = {"", "2", "'", "2'"};
        for (i = 0; i < Math.ceil(scrambleLen / 10.0); i++) {
            for (j = 0; j < 10; j++) {
                sb.append(j % 2 == 0 ? (Math.random() > 0.5 ? "R" : "r") : (Math.random() > 0.5 ? "D" : "d")).append(rndEl(El)).append(' ');
            }
            sb.append('y').append(rndEl(minxsuff)).append(' ');
        }
        return sb.toString();
    }

    static String ssq1tScramble(int scrambleLen) {
        int[][][] seq = new int[2][scrambleLen * 2][2];
        int i;
        sq1Getseq(seq, 0, scrambleLen);
        int[][] s = seq[0], t = seq[1];
        StringBuilder u = new StringBuilder();
        //int[][] temp={{0,0}};
        if (s[0][0] == 7) {
            for (i = 0; i < scrambleLen; i++) {
                s[i * 2][0] = s[i * 2 + 1][0];
                s[i * 2][1] = s[i * 2 + 1][1];
            }
        }
        if (t[0][0] == 7) {
            for (i = 0; i < scrambleLen; i++) {
                t[i * 2][0] = t[i * 2 + 1][0];
                t[i * 2][1] = t[i * 2 + 1][1];
            }
        }
        for (i = 0; i < scrambleLen; i++) {
            u.append("(").append(s[2 * i][0]).append(",").append(t[2 * i][0]).append(",").append(t[2 * i][1]).append(",").append(s[2 * i][1]).append(") / ");
        }
        return u.toString();
    }

    private static void sq1Getseq(int[][][] seq, int type, int scrambleLen) {
        for (int n = 0; n < seq.length; n++) {
            int[] p = {1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
            int cnt = 0;
            int seql = 0;
            while (cnt < scrambleLen) {
                int x = r.nextInt(12) - 5;
                int y = (type == 2) ? 0 : r.nextInt(12) - 5;
                int size = (x == 0 ? 0 : 1) + (y == 0 ? 0 : 1);
                if ((cnt + size <= scrambleLen || type != 1) && (size > 0 || cnt == 0)) {
                    if (sq1Domove(p, x, y)) {
                        if (type == 1) cnt += size;
                        if (size > 0) seq[n][seql++] = new int[] {x, y};
                        if (cnt < scrambleLen) {
                            cnt++;
                            seq[n][seql++] = new int[] {7, 0};
                            sq1Domove(p, 7, 0);
                        }
                    }
                }
            }
        }
    }

    private static boolean sq1Domove(int[] p, int x, int y) {
        int i, temp;
        int[] px, py;
        if (x == 7) {
            for (i = 0; i < 6; i++) {
                temp = p[i + 6];
                p[i + 6] = p[i + 12];
                p[i + 12] = temp;
            }
            return true;
        } else {
            if (p[(17 - x) % 12] != 0 || p[(11 - x) % 12] != 0 || p[12 + (17 - y) % 12] != 0 || p[12 + (11 - y) % 12] != 0) {
                return false;
            } else {
                // do the move itself
                px = new int[12];
                py = new int[12];
                for (int j = 0; j < 12; j++) px[j] = p[j];
                for (int j = 12; j < 24; j++) py[j - 12] = p[j];
                for (i = 0; i < 12; i++) {
                    p[i] = px[(12 + i - x) % 12];
                    p[i + 12] = py[(12 + i - y) % 12];
                }
                return true;
            }
        }
    }

    static String yj4x4(int scrambleLen) {
        // the idea is to keep the fixed center on U and do Rw or Lw, Fw or Bw, to not disturb it
        String[][] turns = {{"U","D"}, {"R","L","r"}, {"F","B","f"}};
        int[] donemoves = new int[3];
        int lastaxis, fpos = 0, // 0 = Ufr, 1 = Ufl, 2 = Ubl, 3 = Ubr
                j, k;
        StringBuilder s = new StringBuilder();
        lastaxis = -1;
        for (j = 0; j < scrambleLen; j++) {
            int done = 0;
            do {
                int first = r.nextInt(turns.length);
                int second = r.nextInt(turns[first].length);
                if (first != lastaxis || donemoves[second] == 0) {
                    if (first == lastaxis) {
                        donemoves[second] = 1;
                        int rs = r.nextInt(3);
                        if (first == 0 && second == 0)
                            fpos = (fpos + 4 + rs) % 4;
                        if (first == 1 && second == 2) { // r or l
                            if (fpos == 0 || fpos == 3) s.append("l").append(cubesuff[rs]).append(" ");
                            else s.append("r").append(cubesuff[rs]).append(" ");
                        } else if (first == 2 && second == 2) { // f or b
                            if (fpos == 0 || fpos == 1) s.append("b").append(cubesuff[rs]).append(" ");
                            else s.append("f").append(cubesuff[rs]).append(" ");
                        } else {
                            s.append(turns[first][second]).append(cubesuff[rs]).append(" ");
                        }
                    }else{
                        for (k = 0; k < turns[first].length; k++)
                            donemoves[k] = 0;
                        lastaxis = first;
                        donemoves[second] = 1;
                        int rs = r.nextInt(cubesuff.length);
                        if (first == 0 && second == 0)
                            fpos = (fpos + 4 + rs) % 4;
                        if (first == 1 && second == 2) { // r or l
                            if (fpos == 0 || fpos == 3) s.append("l").append(cubesuff[rs]).append(" ");
                            else s.append("r").append(cubesuff[rs]).append(" ");
                        } else if (first == 2 && second == 2) { // f or b
                            if (fpos == 0 || fpos == 1) s.append("b").append(cubesuff[rs]).append(" ");
                            else s.append("f").append(cubesuff[rs]).append(" ");
                        } else {
                            s.append(turns[first][second]).append(cubesuff[rs]).append(" ");
                        }
                    }
                    done = 1;
                }
            } while (done == 0);
        }
        return s.toString();
    }

    static String oldminxscramble(int scrambleLen) {
        int j, k;
        String[] minxsuff = {"", "2", "2'", "'"};
        String[] faces = {"F", "B", "U", "D", "L", "DBR", "DL", "BR", "DR", "BL", "R", "DBL"};
        int[] used = new int[12];
        // adjacency table
        String[] adj = {"001010101010", "000101010101", "100010010110", "010001101001", "101000100101", "010100011010", "100110001001", "011001000110", "100101100010", "011010010001", "101001011000", "010110100100"};
        // now generate the scramble(s)
        StringBuilder s = new StringBuilder();
        for (j = 0; j < 12; j++)
            used[j] = 0;
        for (j = 0; j < scrambleLen; j++) {
            boolean done = false;
            do {
                int face = r.nextInt(12);
                if (used[face] == 0) {
                    s.append(faces[face]).append(rndEl(minxsuff)).append(" ");
                    for (k = 0; k < 12; k++) {
                        if (adj[face].charAt(k)=='1')
                            used[k] = 0;
                    }
                    used[face] = 1;
                    done = true;
                }
            } while (!done);
        }
        return s.toString();
    }

    static String sq1Scramble(int type, int scrambleLen) {
        int[][][] seq = new int[1][scrambleLen * 2][2];
        int i;
        int[] k;
        sq1Getseq(seq, type, scrambleLen);
        StringBuilder s = new StringBuilder();
        for (i = 0; i < seq[0].length; i++) {
            k = seq[0][i];
            if (k[0] == 7) s.append("/ ");
            else s.append("(").append(k[0]).append(",").append(k[1]).append(") ");
        }
        return s.toString();
    }

    static String do15puzzle(boolean mirrored, int scrambleLen) {
        String[] moves;
        if (mirrored) moves = new String[] {"U", "L", "R", "D"};
        else moves = new String[] {"D", "R", "L", "U"};
        int[][] effect = {{0, -1}, {1, 0}, {-1, 0}, {0, 1}};
        int x = 0, y = 3, k, m, lastm = 5;
        boolean done;
        StringBuilder s = new StringBuilder();
        for (k = 0; k < scrambleLen; k++) {
            done = false;
            while (!done) {
                m = r.nextInt(4);
                if (x + effect[m][0] >= 0 && x + effect[m][0] <= 3 && y + effect[m][1] >= 0 && y + effect[m][1] <= 3 && m + lastm != 3) {
                    done = true;
                    x += effect[m][0];
                    y += effect[m][1];
                    s.append(moves[m]).append(" ");
                    lastm = m;
                }
            }
        }
        return s.toString();
    }

    private static int[][] d = {{0, 1, 2, 5, 8, 7, 6, 3, 4}, {6, 7, 8, 13, 20, 19, 18, 11, 12}, {0, 3, 6, 11, 18, 17, 16, 9, 10}, {8, 5, 2, 15, 22, 21, 20, 13, 14}};
    private static int[] start = {1, 1, 2, 3, 3, 2, 4, 4, 0, 5, 6, 7, 8, 9, 10, 10, 5, 6, 7, 8, 9, 11, 11};
    private static String[] move = {"U", "F", "L", "R"};
    private static boolean biCanMove(int face) {
        int[] u = new int[8];
        int ulen = 0, i, j, done, z = 0;
        for (i = 0; i < 9; i++) {
            done = 0;
            for (j = 0; j < ulen; j++) {
                if (u[j] == start[d[face][i]])
                    done = 1;
            }
            if (done == 0) {
                u[ulen++] = start[d[face][i]];
                if (start[d[face][i]] == 0) z = 1;
            }
        }
        return (ulen == 5 && z == 1);
    }

    private static void biDoMove(int face, int amount) {
        for (int i = 0; i < amount; i++) {
            int t = start[d[face][0]];
            start[d[face][0]] = start[d[face][6]];
            start[d[face][6]] = start[d[face][4]];
            start[d[face][4]] = start[d[face][2]];
            start[d[face][2]] = t;
            t = start[d[face][7]];
            start[d[face][7]] = start[d[face][5]];
            start[d[face][5]] = start[d[face][3]];
            start[d[face][3]] = start[d[face][1]];
            start[d[face][1]] = t;
        }
    }

    static String bicube(int scrambleLen) {
        StringBuilder sb = new StringBuilder();
        int[][] arr = new int[scrambleLen][];
        int[] poss;
        int arrlen = 0, done, i, j, x = 0, y = 0;
        while (arrlen < scrambleLen) {
            poss = new int[] {1, 1, 1, 1};
            for (j = 0; j < 4; j++) {
                if (poss[j] == 1 && !biCanMove(j))
                    poss[j] = 0;
            }
            done = 0;
            while (done == 0) {
                x = r.nextInt(4);
                if (poss[x] == 1) {
                    y = r.nextInt(3) + 1;
                    biDoMove(x, y);
                    done = 1;
                }
            }
            arr[arrlen++] = new int[] {x, y};
            if (arrlen >= 2) {
                if (arr[arrlen - 1][0] == arr[arrlen - 2][0]) {
                    arr[arrlen - 2][1] = (arr[arrlen - 2][1] + arr[arrlen - 1][1]) % 4;
                    arrlen--;//arr = arr.slice(0,arr.length - 1);
                }
            }
            if (arrlen >= 1) {
                if (arr[arrlen - 1][1] == 0) {
                    arrlen--;//arr = arr.slice(0,arr.length - 1);
                }
            }
        }
        for (i = 0; i < scrambleLen; i++) {
            sb.append(move[arr[i][0]]).append(cubesuff[arr[i][1] - 1]).append(" ");
        }
        return sb.toString();
    }
}
