package scrambler;

public class MasterPyraminx {
    static int[] colors = new int[64];

    static void initColor() {
        colors = new int[] {
                1, 1, 1, 1, 1, 1, 1,    2,    3, 3, 3, 3, 3, 3, 3,
                   1, 1, 1, 1, 1,    2, 2, 2,    3, 3, 3, 3, 3,
                      1, 1, 1,    2, 2, 2, 2, 2,    3, 3, 3,
                         1,    2, 2, 2, 2, 2, 2, 2,    3,

                               4, 4, 4, 4, 4, 4, 4,
                                  4, 4, 4, 4, 4,
                                     4, 4, 4,
                                        4
        };
    }

    static void move(int m) {
        switch (m) {
            case 8: //Lw
            case 0: // L
                break;
            case 9: //Rw
            case 1: // R
                break;
            case 10:    //Bw
            case 2: // B
                break;
            case 11:    //Uw
            case 3: // U
                break;
            case 4: // l
                break;
            case 5: // r
                break;
            case 6: // b
                break;
            case 7: // u
                break;
        }
    }

    public int[] image(String scr) {
        String[] s = scr.split(" ");
        initColor();
        int turn, suff;
        for (int i = 0; i < s.length; i++) {
            suff = s[i].length();
            if (suff > 0) {
                turn = "LRBUlrbu".indexOf(s[i].charAt(0));
                if (suff > 1) {
                    if (s[i].charAt(1) == 'w') turn += 8;
                }
            }
        }
        return colors;
    }
}
