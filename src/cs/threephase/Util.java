package cs.threephase;

import java.io.*;

import android.os.Handler;

public class Util {
	static int[][] Cnk = new int[25][25];
	static int[] fact = new int[13];
	static char[] colorMap4to3 = {'U', 'D', 'F', 'B', 'R', 'L'};
	public static int c4prog;
	private static boolean ini;

	static {
		for (int i=0; i<25; i++) {
			Cnk[i][i] = 1;
			Cnk[i][0] = 1;
		}
		for (int i=1; i<25; i++) {
			for (int j=1; j<=i; j++) {
				Cnk[i][j] = Cnk[i-1][j] + Cnk[i-1][j-1];
			}
		}
		fact[0] = 1;
		for (int i=0; i<12; i++) {
			fact[i+1] = fact[i] * (i+1);
		}
	}
	
	static OutputStream getOutput(String filename) throws IOException {
		return new BufferedOutputStream(new FileOutputStream(filename));
	}
	
	static InputStream getInput(String filename) throws IOException {
		return new BufferedInputStream(new FileInputStream(filename));
	}
	
	public static void init(Handler handler) {
		if(ini) return;
		handler.sendEmptyMessage(18);
		try {
			InputStream in = new BufferedInputStream(new FileInputStream("sdcard/center.dat"));
			Center1.initSym();
			read(Center1.sym2raw, 1113, in);
			read(Center1.ctsmv, 15582, 36, in);
			Center1.createPrun();
			handler.sendEmptyMessage(19);
			Center2.initRl();
			cs.min2phase.Tools.read(Center2.ctrot, in);
			cs.min2phase.Tools.read(Center2.ctmv, in);
			in.read(Center2.ctprun);
			handler.sendEmptyMessage(20);
			cs.min2phase.Tools.read(Center3.ctmove, in);
			Center3.createPrun();
			in.close();
		} catch (Exception e) {
			Center1.initSym();
			Center1.init();
			Center1.createPrun();
			handler.sendEmptyMessage(19);
			Center2.initRl();
			Center2.initCt();
			handler.sendEmptyMessage(20);
			Center3.createMove();
			Center3.createPrun();
			try {
				OutputStream out = new BufferedOutputStream(new FileOutputStream("sdcard/center.dat"));
				write(Center1.sym2raw, 1113, out);
				write(Center1.ctsmv, 15582, 36, out);
				cs.min2phase.Tools.write(Center2.ctrot, out);
				cs.min2phase.Tools.write(Center2.ctmv, out);
				out.write(Center2.ctprun);
				cs.min2phase.Tools.write(Center3.ctmove, out);
				out.close();
			} catch (Exception e2) { }
		}
		handler.sendEmptyMessage(21);
		Edge3.initMvrot();
		Edge3.initRaw2Sym();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream("sdcard/edge.dat"));
			read(Edge3.eprun, 1538, in);
			in.close();
		} catch (Exception e) {
			System.out.println("e3 prun");
			Edge3.createPrun(handler);
			System.out.println("edge done");
			try {
				OutputStream out = new BufferedOutputStream(new FileOutputStream("sdcard/edge.dat"));
				write(Edge3.eprun, 1538, out);
				out.close();
			} catch (Exception e2) { }
		}
		System.out.println("init done");
		ini = true;
	}
	
	static void read(int[] arr, int s, InputStream in) throws IOException {
		int len = arr.length / s;
		byte[] buf = new byte[s * 4];
		int start = 0;
		for (int i=0; i<len; i++) {
			in.read(buf);
			for (int j=0; j<s; j++) {
				arr[start++] = (buf[j*4]<<24)&0xff000000 | (buf[j*4+1]<<16)&0xff0000 | (buf[j*4+2]<<8)&0xff00 | (buf[j*4+3])&0xff;
			}
		}
	}
	
	static void write(int[] arr, int s, OutputStream out) throws Exception {
		int len = arr.length / s;
		byte[] buf = new byte[s * 4];
		int start = 0;
		for (int i=0; i<len; i++) {
			int idx = 0;
			for (int j=0; j<s; j++) {
				buf[idx++] = (byte)((arr[start]>>>24) & 0xff);
				buf[idx++] = (byte)((arr[start]>>>16) & 0xff);
				buf[idx++] = (byte)((arr[start]>>>8) & 0xff);
				buf[idx++] = (byte)(arr[start] & 0xff);
				start++;
			}
			out.write(buf);
		}
	}
	
	static void read(int[][] data, int h, int w, InputStream is) throws Exception {
		byte[] buf = new byte[w * 4];
		for (int i=0; i<h; i++) {
			is.read(buf);
			for (int j=0; j<w; j++) {
				data[i][j] = (buf[j*4])&0xff | (buf[j*4+1]<<8)&0xff00 | (buf[j*4+2]<<16)&0xff0000 | (buf[j*4+3]<<24)&0xff000000;
			}
		}
	}
	
	static void write(int[][] data, int h, int w, OutputStream os) throws Exception {
		byte[] buf = new byte[w * 4];
		for (int i=0; i<h; i++) {
			int idx = 0;
			for (int j=0; j<w; j++) {
				buf[idx++] = (byte)(data[i][j] & 0xff);
				buf[idx++] = (byte)((data[i][j]>>>8) & 0xff);
				buf[idx++] = (byte)((data[i][j]>>>16) & 0xff);
				buf[idx++] = (byte)((data[i][j]>>>24) & 0xff);
			}
			os.write(buf);
		}
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
	
	static byte tempb;
	public static void swap(byte[] arr, int a, int b, int c, int d, int key) {
		switch (key) {
		case 0:
			tempb = arr[d];
			arr[d] = arr[c];
			arr[c] = arr[b];
			arr[b] = arr[a];
			arr[a] = tempb;
			return;
		case 1:
			tempb = arr[a];
			arr[a] = arr[c];
			arr[c] = tempb;
			tempb = arr[b];
			arr[b] = arr[d];
			arr[d] = tempb;
			return;
		case 2:
			tempb = arr[a];
			arr[a] = arr[b];
			arr[b] = arr[c];
			arr[c] = arr[d];
			arr[d] = tempb;
			return;
		}
	}
	
	static void set8Perm(int[] arr, int idx) {
		int val = 0x76543210;
		for (int i=0; i<7; i++) {
			int p = fact[7-i];
			int v = idx / p;
			idx -= v*p;
			v <<= 2;
			arr[i] = (val >> v) & 0xf;
			int m = (1 << v) - 1;
			val = (val & m) + ((val >> 4) & ~m);
		}
		arr[7] = val;
	}
	
	static void set8Perm(byte[] arr, int idx) {
		int val = 0x76543210;
		for (int i=0; i<7; i++) {
			int p = fact[7-i];
			int v = idx / p;
			idx -= v*p;
			v <<= 2;
			arr[i] = (byte) ((val >> v) & 0xf);
			int m = (1 << v) - 1;
			val = (val & m) + ((val >> 4) & ~m);
		}
		arr[7] = (byte)val;
	}
	
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
	
	static int parity(byte[] arr) {
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
}
