package cs.threephase;

import java.io.*;

import android.os.Handler;
import android.util.Log;

import com.dctimer.APP;

import static solver.Utils.read;
import static solver.Utils.write;

public class Util {
    static char[] colorMap4to3 = {'U', 'D', 'F', 'B', 'R', 'L'};
    private static boolean ini;

    static OutputStream getOutput(String filename) throws IOException {
        return new BufferedOutputStream(new FileOutputStream(filename));
    }

    static InputStream getInput(String filename) throws IOException {
        return new BufferedInputStream(new FileInputStream(filename));
    }

    public static void init(Handler handler) {
        if (ini) return;
        handler.sendEmptyMessage(21);
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(APP.dataPath + "center.dat"));
            Center1.initSym();
            read(Center1.sym2raw, 1113, in);
            read(Center1.ctsmv, 15582, 36, in);
            Center1.createPrun();
            handler.sendEmptyMessage(22);
            Center2.initRl();
            read(Center2.ctrot, in);
            read(Center2.ctmv, in);
            in.read(Center2.ctprun);
            handler.sendEmptyMessage(23);
            read(Center3.ctmove, in);
            Center3.createPrun();
            in.close();
        } catch (Exception e) {
            Center1.initSym();
            Center1.init();
            Center1.createPrun();
            handler.sendEmptyMessage(22);
            Center2.initRl();
            Center2.initCt();
            handler.sendEmptyMessage(23);
            Center3.createMove();
            Center3.createPrun();
            try {
                OutputStream out = new BufferedOutputStream(new FileOutputStream(APP.dataPath + "center.dat"));
                write(Center1.sym2raw, 1113, out);
                write(Center1.ctsmv, 15582, 36, out);
                write(Center2.ctrot, out);
                write(Center2.ctmv, out);
                out.write(Center2.ctprun);
                write(Center3.ctmove, out);
                out.close();
            } catch (Exception e2) { }
        }
        handler.sendEmptyMessage(24);
        Edge3.initMvrot();
        Edge3.initRaw2Sym();
        Edge3.eprun = new int[Edge3.N_EPRUN / 16];
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(APP.dataPath + "edge.dat"));
            read(Edge3.eprun, 1538, in);
            in.close();
        } catch (Exception e) {
            Edge3.createPrun(handler);
            try {
                OutputStream out = new BufferedOutputStream(new FileOutputStream(APP.dataPath + "edge.dat"));
                write(Edge3.eprun, 1538, out);
                out.close();
            } catch (Exception e2) { }
        }
        Log.w("dct", "init 4x4 done");
        ini = true;
    }

    static int temp;
    public static void swap(int[] arr, int a, int b, int c, int d, int key) {
        switch (key) {
            case 0:
                temp = arr[d];
                arr[d] = arr[c];
                arr[c] = arr[b];
                arr[b] = arr[a];
                arr[a] = temp;
                return;
            case 1:
                temp = arr[a];
                arr[a] = arr[c];
                arr[c] = temp;
                temp = arr[b];
                arr[b] = arr[d];
                arr[d] = temp;
                return;
            case 2:
                temp = arr[a];
                arr[a] = arr[b];
                arr[b] = arr[c];
                arr[c] = arr[d];
                arr[d] = temp;
                return;
        }
    }

//    static byte tempb;
//    public static void swap(byte[] arr, int a, int b, int c, int d, int key) {
//        switch (key) {
//            case 0:
//                tempb = arr[d];
//                arr[d] = arr[c];
//                arr[c] = arr[b];
//                arr[b] = arr[a];
//                arr[a] = tempb;
//                return;
//            case 1:
//                tempb = arr[a];
//                arr[a] = arr[c];
//                arr[c] = tempb;
//                tempb = arr[b];
//                arr[b] = arr[d];
//                arr[d] = tempb;
//                return;
//            case 2:
//                tempb = arr[a];
//                arr[a] = arr[b];
//                arr[b] = arr[c];
//                arr[c] = arr[d];
//                arr[d] = tempb;
//                return;
//        }
//    }

//    static void set8Perm(int[] arr, int idx) {
//        int val = 0x76543210;
//        for (int i=0; i<7; i++) {
//            int p = fact[7-i];
//            int v = idx / p;
//            idx -= v*p;
//            v <<= 2;
//            arr[i] = (val >> v) & 0x7;
//            int m = (1 << v) - 1;
//            val = (val & m) + ((val >> 4) & ~m);
//        }
//        arr[7] = val;
//    }

//    static void set8Perm(byte[] arr, int idx) {
//        int val = 0x76543210;
//        for (int i=0; i<7; i++) {
//            int p = fact[7-i];
//            int v = idx / p;
//            idx -= v*p;
//            v <<= 2;
//            arr[i] = (byte) ((val >> v) & 0xf);
//            int m = (1 << v) - 1;
//            val = (val & m) + ((val >> 4) & ~m);
//        }
//        arr[7] = (byte)val;
//    }

    static int parity(int[] arr) {
        int parity = 0;
        for (int i=0, len=arr.length; i<len; i++) {
            for (int j=i; j<len; j++) {
                if (arr[i] > arr[j]) {
                    parity ^= 1;
                }
            }
        }
        return parity;
    }

//    static int parity(byte[] arr) {
//        int parity = 0;
//        for (int i=0, len=arr.length; i<len; i++) {
//            for (int j=i; j<len; j++) {
//                if (arr[i] > arr[j]) {
//                    parity ^= 1;
//                }
//            }
//        }
//        return parity;
//    }
}
