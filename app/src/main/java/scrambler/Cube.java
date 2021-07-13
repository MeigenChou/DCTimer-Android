package scrambler;

public class Cube {
    //Default settings
    private static int size;
    private static int seqLen;
    private static int seql;
    private static boolean mult = true;
    // list of available colours
    private static int[] colors = {0, 5, 1, 3, 2, 4}; //stores colours used
    private static int[] seq;  // move sequences
    private static int[] posit;  // facelet array
    private static int[] colorPerm = {0, 1, 2, 3, 4, 5};
    //private static StringBuilder sb;
    private static int[] img;

    public static void appendmoves(int[] sq, int[] axsl, int tl, int la) {
        for (int sl = 0; sl < tl; sl++) {  // for each move type
            if (axsl[sl] != 0) {       // if it occurs
                int q = axsl[sl] - 1;
                // get semi-axis of this move
                int sa = la;
                int m = sl;
                if (sl + sl + 1 >= tl) { // if on rear half of this axis
                    sa += 3; // get semi-axis (i.e. face of the move)
                    m = tl - 1 - m; // slice number counting from that face
                    q = 2 - q; // opposite direction when looking at that face
                }
                // store move
                sq[seql++] = ((m * 6 + sa) * 4 + q);
            }
        }
    }

//    public static void parse(int s) {
//        size = s;
//    }

    private static void scramble() {
        int i;
        //tl=number of allowed moves (twistable layers) on axis -- middle layer ignored
        int tl = size;
        if (mult || (size & 1) != 0) tl--;
        //set up bookkeeping
        int[] axsl = new int[tl];    // movement of each slice/movetype on this axis
        int[] axam = {0, 0, 0}; // number of slices moved each amount
        int la = -1; // last axis moved
        // initialise this scramble
        seq = new int[seqLen]; // moves generated so far
        seql = 0;
        // reset slice/direction counters
        int moved = 0;
        // while generated sequence not long enough
        while (seql + moved < seqLen) {
            int ax, sl, q;
            do {
                do {
                    // choose a random axis
                    ax = (int) (Math.random() * 3);
                    // choose a random move type on that axis
                    sl = (int) (Math.random() * tl);
                    // choose random amount
                    q = (int) (Math.random() * 3);
                } while (ax == la && axsl[sl] != 0);    // loop until have found an unused movetype
            } while (ax == la   // loop while move is reducible: reductions only if on same axis as previous moves
                && !mult        // multislice moves have no reductions so always ok
                && tl == size   // only even-sized cubes have reductions (odds have middle layer as reference)
                && (2 * axam[0] == tl ||    // reduction if already have half the slices move in same direction
                    2 * axam[1] == tl ||
                    2 * axam[2] == tl ||
                    (2 * (axam[q] + 1) == tl  // reduction if move makes exactly half the slices moved in same direction and
                        && axam[0] + axam[1] + axam[2] - axam[q] > 0 // some other slice also moved
                    )
                )
            );
            // if now on different axis, dump cached moves from old axis
            if (ax != la) {
                appendmoves(seq, axsl, tl, la);
                // reset slice/direction counters
                for (i = 0; i < tl; i++) axsl[i] = 0;
                axam[0] = axam[1] = axam[2] = 0;
                moved = 0;
                // remember new axis
                la = ax;
            }
            // adjust counters for this move
            axam[q]++;// adjust direction count
            moved++;
            axsl[sl] = q + 1;// mark the slice has moved amount
        }
        // dump the last few moves
        appendmoves(seq, axsl, tl, la);
    }

    public static String scramblestring(int siz, int sel) {
        seqLen = sel;
        size = siz;
        scramble();
        int j, k;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < seqLen; i++) {
            if (i != 0) sb.append(" ");
            k = seq[i] >> 2;
            j = k % 6;
            k = (k - j) / 6;
            if (size <= 5) {
                sb.append("DLBURF".charAt(j));
                if (k > 0) sb.append("w"); // use w only for double layers on 4x4x4 and 5x5x5
            } else {
                if (k > 0) sb.append(k + 1);
                sb.append("DLBURF".charAt(j));
                //if (k>0) sb.append("w");
            }
            j = seq[i] & 3;
            if (j != 0) sb.append(" 2'".charAt(j));
        }
        return sb.toString();
    }

    public static int[] image(String scramble, int size) {
        switch (Cube.size = size) {
            case 3:
                scramble = scramble.replace("M'", "r R'").replace("M2", "r2 R2").replace("M", "r' R")
                        .replace("x'", "r' L").replace("x2", "r2 L2").replace("x", "r L'")
                        .replace("y'", "u' D").replace("y2", "u2 D2").replace("y", "u D'")
                        .replace("z'", "f' B").replace("z2", "f2 B2").replace("z", "f B'");
                break;
            case 4:
                scramble = scramble.replace("x'", "r' l").replace("x2", "r2 l2").replace("x", "r l'")
                        .replace("y'", "u' d").replace("y2", "u2 d2").replace("y", "u d'")
                        .replace("z'", "f' b").replace("z2", "f2 b2").replace("z", "f b'");
                break;
            case 6:
            case 7:
                //scramble = scramble.replace("3R", "3r r'");
                break;
        }
        String[] s = scramble.split(" ");
        int k, len = s.length;
        if (len > 0) {
            seq = new int[len];
            int count = 0;
            for (int i = 0; i < len; i++) {
                k = 0;
                if (s[i].length() > 0) {
                    switch (s[i].charAt(0)) {
                        case '5': k = 4;  break;
                        case '4': k = 3;  break;
                        case '3': k = 2;  break;
                        case '2': k = 1;  break;
                        case 'R': seq[count] = 16;  break;
                        case 'L': seq[count] = 4;   break;
                        case 'U': seq[count] = 12;  break;
                        case 'D': seq[count] = 0;   break;
                        case 'F': seq[count] = 20;  break;
                        case 'B': seq[count] = 8;   break;
                        case 'r': seq[count] = 16;  k = 1;  break;
                        case 'l': seq[count] = 4;   k = 1;  break;
                        case 'u': seq[count] = 12;  k = 1;  break;
                        case 'd': seq[count] = 0;   k = 1;  break;
                        case 'f': seq[count] = 20;  k = 1;  break;
                        case 'b': seq[count] = 8;   k = 1;  break;
                    }
                    if (s[i].contains("top") || s[i].contains("顶") || s[i].contains("頂")) seq[count] = 12;
                    if (s[i].contains("bottom") || s[i].contains("底")) seq[count] = 0;
                    if (s[i].contains("left") || s[i].contains("左")) seq[count] = 4;
                    if (s[i].contains("right") || s[i].contains("右")) seq[count] = 16;
                    if (s[i].contains("front") || s[i].contains("前")) seq[count] = 20;
                    if (s[i].contains("back") || s[i].contains("后") || s[i].contains("後")) seq[count] = 8;
                    if (s[i].contains("180 deg") || s[i].contains("180度")) seq[count]++;
                    else if (s[i].contains("counter") || s[i].contains("逆")) seq[count] += 2;
                    if (s[i].length() > 1) {
                        switch (s[i].charAt(1)) {
                            case '\'': seq[count] += 2; break;
                            case '2': seq[count]++;     break;
                            case 0xb3: k = 2;   break;
                            case 0xb2: k = 1;   break;
                            case 'w': seq[count] += 24; break;
                            case 'r':
                            case 'R': seq[count] = 16;  break;
                            case 'l':
                            case 'L': seq[count] = 4;   break;
                            case 'u':
                            case 'U': seq[count] = 12;  break;
                            case 'd':
                            case 'D': seq[count] = 0;   break;
                            case 'f':
                            case 'F': seq[count] = 20;  break;
                            case 'b':
                            case 'B': seq[count] = 8;   break;
                        }
                        if (s[i].length() > 2) {
                            switch (s[i].charAt(2)) {
                                case '\'': seq[count] += 2; break;
                                case '2':  seq[count]++;    break;
                            }
                            if (s[i].length() > 3) {
                                switch (s[i].charAt(3)) {
                                    case '\'': seq[count] += 2; break;
                                    case '2':  seq[count]++;    break;
                                }
                            }
                        }
                    }
                    seq[count++] += k * 24;
                }
            }
        }
        return image();
    }

    public static int[] image() {
        img = new int[size * size * 6];
        posit = new int[size * size * 6];
        int i, j, l = 0, f, q, d = 0;
        // initialise colours
        for (i = 0; i < 6; i++)
            for (f = 0; f < size * size; f++)
                posit[d++] = i;
        // do move sequence
        for (i = 0; i < seq.length; i++) {
            q = seq[i] & 3;
            f = seq[i] >> 2;
            d = 0;
            while (f > 5) { f -= 6; d++; }
            do {
                doslice(f, d, q + 1);
                d--;
            } while (mult && d >= 0);
        }
        // build lookup table
        int[] flat2posit = new int[12 * size * size];
        for (i = 0; i < flat2posit.length; i++) flat2posit[i] = -1;
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                flat2posit[4*size*(3*size-i-1)+  size+j  ] =         i *size+j; //D
                flat2posit[4*size*(  size+i  )+  size-j-1] = (  size+i)*size+j; //L
                flat2posit[4*size*(  size+i  )+4*size-j-1] = (2*size+i)*size+j; //B
                flat2posit[4*size*(       i  )+  size+j  ] = (3*size+i)*size+j; //U
                flat2posit[4*size*(  size+i  )+2*size+j  ] = (4*size+i)*size+j; //R
                flat2posit[4*size*(  size+i  )+  size+j  ] = (5*size+i)*size+j; //F
            }
        }
        d = 0;
        for (i = 0; i < 3 * size; i++) {
            for (f = 0; f < 4 * size; f++) {
                if (flat2posit[d] >= 0) {
                    int c = colorPerm[posit[flat2posit[d]]];
                    img[l++] = colors[c];
                }
                d++;
            }
        }
        return img;
    }

    private static void doslice(int f, int d, int q) {
        //do move of face f, layer d, q quarter turns
        int f1 = 0, f2 = 0, f3 = 0, f4 = 0;
        int s2 = size * size;
        int i, j, k, c;
        if (f > 5) f -= 6;
        // cycle the side facelets
        for (k = 0; k < q; k++) {
            for (i = 0; i < size; i++) {
                if (f == 0) {
                    f1 = 6*s2-size*d-size+i;
                    f2 = 2*s2-size*d-   1-i;
                    f3 = 3*s2-size*d-   1-i;
                    f4 = 5*s2-size*d-size+i;
                } else if (f == 1) {
                    f1 = 3*s2+d+size* i;
                    f2 = 3*s2+d-size*(i+1);
                    f3 =   s2+d-size*(i+1);
                    f4 = 5*s2+d+size* i;
                } else if (f == 2) {
                    f1 = 3*s2+       d*size+i;
                    f2 = 4*s2+size-1-d+size*i;
                    f3 =    d*size+size-1-i;
                    f4 = 2*s2-     1-d-size*i;
                } else if (f == 3) {
                    f1 = 4*s2+d*size+size-1-i;
                    f2 = 2*s2+d*size+       i;
                    f3 =   s2+d*size+       i;
                    f4 = 5*s2+d*size+size-1-i;
                } else if (f == 4) {
                    f1 = 6*s2-1-d-size*i;
                    f2 = size-1-d+size*i;
                    f3 = 2*s2+size-1-d+size*i;
                    f4 = 4*s2-1-d-size*i;
                } else if (f == 5) {
                    f1 = 4*s2-size-d*size+i;
                    f2 = 2*s2-size+d-size*i;
                    f3 = s2-1-d*size-i;
                    f4 = 4*s2+d+size*i;
                }
                c = posit[f1];
                posit[f1] = posit[f2];
                posit[f2] = posit[f3];
                posit[f3] = posit[f4];
                posit[f4] = c;
            }
			/* turn face */
            if (d == 0) {
                for (i = 0; i + i < size; i++) {
                    for (j = 0; j + j < size - 1; j++) {
                        f1 = f*s2+         i+         j*size;
                        f3 = f*s2+(size-1-i)+(size-1-j)*size;
                        if (f < 3) {
                            f2 = f*s2+(size-1-j)+         i*size;
                            f4 = f*s2+         j+(size-1-i)*size;
                        } else {
                            f4 = f*s2+(size-1-j)+         i*size;
                            f2 = f*s2+         j+(size-1-i)*size;
                        }
                        c = posit[f1];
                        posit[f1] = posit[f2];
                        posit[f2] = posit[f3];
                        posit[f3] = posit[f4];
                        posit[f4] = c;
                    }
                }
            }
        }
    }
}
