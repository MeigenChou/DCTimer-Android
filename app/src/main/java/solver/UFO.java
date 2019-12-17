package solver;

import java.util.*;

public class UFO {
    static int[] pd;
    static int[] movei = {5, 1, 1, 1};
    static boolean ini = false;
    static void init() {
        if (ini) return;
        int[] arr = new int[11];
        int[] temp = new int[11];
        pd = new int[39916800 / 8];
        Arrays.fill(pd, -1);
        Utils.setPruning(pd, 0, 0);
        for (int d = 0; d < 14; d++) {
            int n = 0;
            for (int i = 0; i < 39916800; i++) {
                if (Utils.getPruning(pd, i) == d) {
                    Utils.set11Perm(temp, i, 11);
                    for (int k = 0; k < 4; k++) {
                        System.arraycopy(temp, 0, arr, 0, 11);
                        //Map.set11Perm(arr, i);
                        for (int m = 0; m < movei[k]; m++) {
                            move(arr, k);
                            int next = Utils.get11Perm(arr, 11);
                            if (Utils.getPruning(pd, next) == 0xf) {
                                Utils.setPruning(pd, next, d + 1);
                                n++;
                            }
                        }
                    }
                }
            }
            //Log.w("dct", d+1+"\t"+n);
        }
        ini = true;
    }

    static void move(int[] perm, int m) {
        switch (m) {
            case 0:	//U
                int a = perm[0]; perm[0] = perm[5]; perm[5] = perm[4]; perm[4] = perm[3]; perm[3] = perm[2]; perm[2] = perm[1]; perm[1] = a;
                break;
            case 1:	//A
                Utils.swap(perm, 4, 10, 0, 7); Utils.swap(perm, 5, 6);
                break;
            case 2:	//B
                Utils.swap(perm, 5, 6, 3, 8); Utils.swap(perm, 4, 7);
                break;
            case 3:	//C
                Utils.swap(perm, 2, 7, 4, 9); Utils.swap(perm, 3, 8);
                break;
        }
    }

}
