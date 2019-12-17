package cs.threephase;

import static cs.threephase.Util.*;
import static cs.threephase.Moves.*;

import java.util.Arrays;

import solver.Utils;

class Center1 {

    static int[][] ctsmv = new int[15582][36];
    static int[] sym2raw = new int[15582];
    static byte[] csprun = new byte[15582];

    static int[][] symmult = new int[48][48];
    static int[][] symmove = new int[48][36];
    static int[] syminv = new int[48];
    static int[] finish = new int[48];

    public static void init() {
        Center1 c = new Center1();
        int[] raw2sym = new int[735471];
        int count = 0;
        for (int i = 0; i < 735471; i++) {
            if ((raw2sym[i] & 1) == 0) {
                c.set(i);
                for (int j = 0; j < 48; j++) {
                    int idx = c.get();
                    raw2sym[idx] = (count << 6 | syminv[j]) << 1 | 1;
                    c.rot(0);
                    if ((j & 1) == 1) c.rot(1);
                    if ((j & 7) == 7) c.rot(2);
                    if ((j & 15) == 15) c.rot(3);
                }
                sym2raw[count++] = i;
            }
        }
        System.out.println("c1 move");
        Center1 d = new Center1();
        for (int i = 0; i < 15582; i++) {
            d.set(sym2raw[i]);
            for (int m = 0; m < 36; m++) {
                c.set(d);
                c.move(m);
                ctsmv[i][m] = raw2sym[c.get()] >> 1;
            }
        }
        raw2sym = null;
        System.gc();
    }

    int[] ct = new int[24];

    public Center1() {
        for (int i = 0; i < 8; i++) {
            ct[i] = 1;
        }
        for (int i = 8; i < 24; i++) {
            ct[i] = 0;
        }
    }

    private Center1(int[] ct) {
        for (int i = 0; i < 24; i++) this.ct[i] = ct[i];
        //System.arraycopy(ct, 0, this.ct, 0, 24);
    }

    Center1(CenterCube c, int urf) {
        for (int i = 0; i < 24; i++) {
            this.ct[i] = (byte) ((c.ct[i] / 2 == urf) ? 1 : 0);
        }
    }

    public void move(int m) {
        int key = m % 3;
        m /= 3;
        switch (m) {
            case 0:	//U
                swap(ct, 0, 1, 2, 3, key);
                break;
            case 1:	//R
                swap(ct, 16, 17, 18, 19, key);
                break;
            case 2:	//F
                swap(ct, 8, 9, 10, 11, key);
                break;
            case 3:	//D
                swap(ct, 4, 5, 6, 7, key);
                break;
            case 4:	//L
                swap(ct, 20, 21, 22, 23, key);
                break;
            case 5:	//B
                swap(ct, 12, 13, 14, 15, key);
                break;
            case 6:	//u
                swap(ct, 0, 1, 2, 3, key);
                swap(ct, 8, 20, 12, 16, key);
                swap(ct, 9, 21, 13, 17, key);
                break;
            case 7:	//r
                swap(ct, 16, 17, 18, 19, key);
                swap(ct, 1, 15, 5, 9, key);
                swap(ct, 2, 12, 6, 10, key);
                break;
            case 8:	//f
                swap(ct, 8, 9, 10, 11, key);
                swap(ct, 2, 19, 4, 21, key);
                swap(ct, 3, 16, 5, 22, key);
                break;
            case 9:	//d
                swap(ct, 4, 5, 6, 7, key);
                swap(ct, 10, 18, 14, 22, key);
                swap(ct, 11, 19, 15, 23, key);
                break;
            case 10://l
                swap(ct, 20, 21, 22, 23, key);
                swap(ct, 0, 8, 4, 14, key);
                swap(ct, 3, 11, 7, 13, key);
                break;
            case 11://b
                swap(ct, 12, 13, 14, 15, key);
                swap(ct, 1, 20, 7, 18, key);
                swap(ct, 0, 23, 6, 17, key);
                break;
        }
    }

    public void set(int idx) {
        Utils.idxToComb(ct, idx, 8, 24);
//        int r = 8;
//        for (int i=23; i>=0; i--) {
//            ct[i] = 0;
//            if (idx >= Cnk[i][r]) {
//                idx -= Cnk[i][r--];
//                ct[i] = 1;
//            }
//        }
    }

    private int get() {
        return Utils.combToIdx(ct, 8, 24);
//        int idx = 0;
//        int r = 8;
//        for (int i=23; i>=0; i--) {
//            if (ct[i] == 1) {
//                idx += Cnk[i][r--];
//            }
//        }
//        return idx;
    }

    int getsym() {
        for (int j = 0; j < 48; j++) {
            int cord = raw2sym(get());
            if (cord != -1)
                return cord * 64 + j;
            rot(0);
            if ((j & 1) == 1) rot(1);
            if ((j & 7) == 7) rot(2);
            if ((j & 15) == 15) rot(3);
        }
        System.out.print('e');
        return -1;
    }

    static int raw2sym(int n) {
        int m = Arrays.binarySearch(sym2raw, n);
        return (m>=0 ? m : -1);
    }

    public void set(Center1 c) {
        for (int i = 0; i < 24; i++) this.ct[i] = c.ct[i];
        //System.arraycopy(c.ct, 0, this.ct, 0, 24);
    }

    private void rot(int r) {
        switch (r) {
            case 0:
                move(ux2);
                move(dx2);
                break;
            case 1:
                move(rx1);
                move(lx3);
                break;
            case 2:
                swap(ct, 0, 3, 1, 2, 1);
                swap(ct, 8, 11, 9, 10, 1);
                swap(ct, 4, 7, 5, 6, 1);
                swap(ct, 12, 15, 13, 14, 1);
                swap(ct, 16, 19, 21, 22, 1);
                swap(ct, 17, 18, 20, 23, 1);
                break;
            case 3:
                move(ux1);
                move(dx3);
                move(fx1);
                move(bx3);
                break;
        }
    }
    /*
    0	I
    1	y2
    2	x
    3	xy2
    4	x2
    5	z2
    6	x'
    7	x'y2
    16	yz
    17	y'z'
    18	y2z
    19	z'
    20	y'z
    21	yz'
    22	z
    23	zy2
    32	y'x'
    33	yx
    34	y'
    35	y
    36	y'x
    37	yx'
    38	yz2
    39	y'z2
     */
    static String[] rot2str = {"", "y2", "x", "x y2", "x2", "z2", "x'", "x' y2", "", "", "", "", "", "", "", "",
            "y z", "y' z'", "y2 z", "z'", "y' z", "y z'", "z", "z y2", "", "", "", "", "", "", "", "",
            "y' x'", "y x", "y'", "y", "y' x", "y x'", "y z2", "y' z2",  "", "", "", "", "", "", "", ""};


    private void rotate(int r) {
        for (int j = 0; j < r; j++) {
            rot(0);
            if ((j &1) == 1) rot(1);
            if ((j &7) == 7) rot(2);
            if ((j &15) == 15) rot(3);
        }
    }

    static int getSolvedSym(CenterCube cube) {
        Center1 c = new Center1(cube.ct);
        for (int j = 0; j < 48; j++) {
            boolean check = true;
            for (int i = 0; i < 24; i++) {
                if (c.ct[i] != i/4) {
                    check = false;
                    break;
                }
            }
            if (check) {
                return j;
            }
            c.rot(0);
            if ((j & 1) == 1) c.rot(1);
            if ((j & 7) == 7) c.rot(2);
            if ((j & 15) == 15) c.rot(3);
        }
        return -1;
    }

    public boolean equals(Center1 c) {
        for (int i=0; i<24; i++) {
            if (ct[i] != c.ct[i]) {
                return false;
            }
        }
        return true;
    }

    static void initSym() {
        Center1 c = new Center1();
        for (byte i=0; i<24; i++) {
            c.ct[i] = i;
        }
        Center1 d = new Center1(c.ct);
        Center1 e = new Center1(c.ct);
        Center1 f = new Center1(c.ct);

        for (int i = 0; i < 48; i++) {
            for (int j = 0; j < 48; j++) {
                for (int k = 0; k < 48; k++) {
                    if (c.equals(d)) {
                        symmult[i][j] = k;
                        if (k==0) {
                            syminv[i] = j;
                        }
                    }
                    d.rot(0);
                    if ((k & 1) == 1) d.rot(1);
                    if ((k & 7) == 7) d.rot(2);
                    if ((k & 15) == 15) d.rot(3);
                }
                c.rot(0);
                if ((j & 1) == 1) c.rot(1);
                if ((j & 7) == 7) c.rot(2);
                if ((j & 15) == 15) c.rot(3);
            }
            c.rot(0);
            if ((i & 1) == 1) c.rot(1);
            if ((i & 7) == 7) c.rot(2);
            if ((i & 15) == 15) c.rot(3);
        }

        for (int i = 0; i < 48; i++) {
            c.set(e);
            c.rotate(syminv[i]);
            for (int j = 0; j < 36; j++) {
                d.set(c);
                d.move(j);
                d.rotate(i);
                for (int k = 0; k < 36; k++) {
                    f.set(e);
                    f.move(k);
                    if (f.equals(d)) {
                        symmove[i][j] = k;
                        break;
                    }
                }
            }
        }

        c.set(0);
        for (int i = 0; i < 48; i++) {
            finish[syminv[i]] = c.get();
            c.rot(0);
            if ((i & 1) == 1) c.rot(1);
            if ((i & 7) == 7) c.rot(2);
            if ((i & 15) == 15) c.rot(3);
        }
    }

    static void createPrun() {
        for (int i = 1; i < 15582; i++)
            csprun[i] = -1;
        csprun[0] = 0;
        int depth = 0;
        //int done = 1;

        while (depth < 8) {
            boolean inv = depth > 4;
            int select = inv ? -1 : depth;
            int check = inv ? depth : -1;
            depth++;
            for (int i = 0; i < 15582; i++) {
                if (csprun[i] == select) {
                    for (int m = 0; m < 27; m++) {
                        int idx = ctsmv[i][m] >>> 6;
                        if (csprun[idx] == check) {
                            //++done;
                            if (inv) {
                                csprun[i] = (byte) depth;
                                break;
                            } else {
                                csprun[idx] = (byte) depth;
                            }
                        }
                    }
                }
            }
            //System.out.println(depth+", "+done);
        }
    }
}
