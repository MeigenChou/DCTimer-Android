package solver;

import java.io.*;
import java.util.Random;

import com.dctimer.DCTimer;

public class Cross {
	private static short[][] eom = new short[7920][6], epm = new short[11880][6];
	private static byte[] epd = new byte[11880], eod = new byte[7920];
	private static int[] ed = new int[23760];
	private static byte[][] fcm = new byte[24][6], fem = new byte[24][6];
	private static byte[][] fecd = new byte[4][576];
	private static int[] goalCo = {12, 15, 18, 21};
	private static int[] goalFeo = {0, 2, 4, 6};
	private static StringBuffer sb;
	public static boolean ini, iniFull;
	private static String[] color = {"D", "U", "L", "R", "F", "B"};
	private static String[][] moveIdx = {
		{ "UDLRFB", "DURLFB", "RLUDFB", "LRDUFB", "BFLRUD", "FBLRDU" },
		{ "UDLRFB", "DURLFB", "RLUDFB", "LRDUFB", "BFRLDU", "FBRLUD" },
		{ "UDLRFB", "DURLFB", "RLUDFB", "LRDUFB", "BFUDRL", "FBUDLR" },
		{ "UDLRFB", "DURLFB", "RLUDFB", "LRDUFB", "BFDULR", "FBDURL" },
		{ "UDLRFB", "DULRBF", "RLBFUD", "LRFBUD", "BFLRUD", "FBRLUD" },
		{ "UDLRFB", "DULRBF", "RLFBDU", "LRBFDU", "BFRLDU", "FBLRDU" }
	};
	private static String[][] rotIdx = {
		{ "", "z2", "z'", "z", "x'", "x" }, { "z2", "", "z", "z'", "x", "x'" },
		{ "z", "z'", "", "z2", "y", "y'" }, { "z'", "z", "z2", "", "y'", "y" },
		{ "x", "x'", "y'", "y", "", "y2" }, { "x'", "x", "y", "y'", "y2", "" }
	};
	private static String[][] turn = {
		{ "U", "D", "L", "R", "F", "B" }, { "D", "U", "R", "L", "F", "B" },
		{ "R", "L", "U", "D", "F", "B" }, { "L", "R", "D", "U", "F", "B" },
		{ "B", "F", "L", "R", "U", "D" }, { "F", "B", "L", "R", "D", "U" }
	};
	private static String[] suff = {"", "2", "'"};
	
	public static void circle(int[] ary, int a, int b, int c, int d, int ori) {
		int t = ary[a];
		ary[a] = ary[d] ^ ori;
		ary[d] = ary[c] ^ ori;
		ary[c] = ary[b] ^ ori;
		ary[b] = t ^ ori;
	}
	
	protected static void idxToPerm(int[] ary, int p) {
		int v;
		for (int q=1; q<=4; q++) {
			int t = p % q;
			for (p=p/q, v=q-2; v>=t; v--)
				ary[v + 1] = ary[v];
			ary[t] = 4 - q;
		}
	}
	
	protected static int permToIdx(int[] s) {
		int i = 0, v, t;
		for (int q=0; q<4; q++) {
			for (v=t=0; v<4 && !(s[v]==q); v++)
				if (s[v] > q) t++;
			i = i * (4 - q) + t;
		}
		return i;
	}
	
	private static int idxToComb(int[] n, int[] s, int c, int o) {
		int q = 4;
		for (int t=0; t<12; t++)
			if (c >= Mapping.Cnk[11 - t][q]) {
				c -= Mapping.Cnk[11 - t][q--];
				n[t] = s[q] << 1 | o & 1;
				o >>= 1;
			} else n[t] = -1;
		return o;
	}
	
	private static int getmv(int c, int p, int o, int f) {
		int[] n = new int[12], s = new int[4];
		int q, t;
		idxToPerm(s, p);
		o = idxToComb(n, s, c, o);
		switch (f) {
		case 0:
			circle(n, 0, 1, 2, 3, 0);
			break;
		case 1:
			circle(n, 11, 10, 9, 8, 0);
			break;
		case 2:
			circle(n, 1, 4, 9, 5, 0);
			break;
		case 3:
			circle(n, 3, 6, 11, 7, 0);
			break;
		case 4:
			circle(n, 0, 7, 8, 4, 1);
			break;
		case 5:
			circle(n, 2, 5, 10, 6, 1);
			break;
		}
		c = 0;
		q = 4;
		for (t = 0; t<12; t++)
			if (n[t] >= 0) {
				c += Mapping.Cnk[11 - t][q--];
				s[q] = n[t] >> 1;
				o |= (n[t] & 1) << 3 - q;
			}
		int i = permToIdx(s);
		// for(q=0;4>q;q++){
		// for(v=t=0;4>v&&!(s[v]==q);v++)if(s[v]>q)t++;
		// i=i*(4-q)+t;
		// }
		return 24 * c + i << 4 | o;
	}
	
	protected static void read(short[][] arr, InputStream in) throws IOException {
		int len = arr.length;
		byte[] buf = new byte[len * 2];
		for (int i=0; i<6; i++) {
			in.read(buf);
			for (int j=0; j<len; j++) {
				arr[j][i] = (short) (buf[j*2]&0xff | (buf[j*2+1]<<8) & 0xff00);
			}
		}	
	}
	
	protected static void write(short[][] arr, OutputStream out) throws IOException {
		int len=arr.length;
		byte[] buf = new byte[len * 2];
		for (int i=0; i<6; i++) {
			int idx = 0;
			for (int j=0; j<len; j++) {
				buf[idx++] = (byte)(arr[j][i] & 0xff);
				buf[idx++] = (byte)((arr[j][i]>>>8) & 0xff);
			}
			out.write(buf);
		}	
	}
	
	private static void init() {
		if (ini)
			return;
		int a, b, c, d, e, f;
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(DCTimer.dataPath + "cross.dat"));
			read(epm, in);
			read(eom, in);
			in.close();
		} catch (Exception ex) {
			for (a=0; a<495; a++) {
				for (b=0; b<24; b++) {
					for (f=0; f<6; f++) {
						c = getmv(a, b, b, f);
						epm[24 * a + b][f] = (short) (c >> 4);
						if (b < 16)
							eom[16 * a + b][f] = (short) (((c >> 4) / 24) << 4 | c & 15);
					}
				}
			}
			try {
				OutputStream out = new BufferedOutputStream(new FileOutputStream(DCTimer.dataPath + "cross.dat"));
				write(epm, out);
				write(eom, out);
				out.close();
			} catch (Exception ex2) { }
		}
		for (a=0; a<11880; a++)
			epd[a] = -1;
		epd[0] = 0;
		a = 1;
		for (b=0; b<6; b++)
			for (c=0; c<11880; c++)
				if (epd[c] == b)
					for (f=0; f<6; f++)
						for (d=c, e=0; e<3; e++) {
							d = epm[d][f];
							if (epd[d] == -1) {
								epd[d] = (byte) (b + 1);
								a++;
							}
						}
		for (a=0; a<7920; a++)
			eod[a] = -1;
		eod[0] = 0;
		a = 1;
		for (b=0; b<7; b++)
			for (c=0; c<7920; c++)
				if (eod[c] == b)
					for (f=0; f<6; f++) {
						d = c;
						for (e=0; e<3; e++) {
							d = eom[d][f];
							if (eod[d] == -1)
								eod[d] = (byte) (b + 1);
							a++;
						}
					}
		//Xcross
		byte[][] p = {
				{1,0,3,0,0,4},{2,1,1,5,1,0},{3,2,2,1,6,2},{0,3,7,3,2,3},
				{4,7,0,4,4,5},{5,4,5,6,5,1},{6,5,6,2,7,6},{7,6,4,7,3,7}
		};
		byte[][] o = {
				{0,0,1,0,0,2},{0,0,0,2,0,1},{0,0,0,1,2,0},{0,0,2,0,1,0},
				{0,0,2,0,0,1},{0,0,0,1,0,2},{0,0,0,2,1,0},{0,0,1,0,2,0}
		};
		for(a=0; a<8; a++)
			for(b=0; b<3; b++)
				for(c=0; c<6; c++)
					fcm[a*3+b][c] = (byte) (p[a][c]*3+(o[a][c]+b)%3);
		p = new byte[][] {
				{0,0,7,0,0,8},{1,1,1,9,1,4},{2,2,2,5,10,2},{3,3,11,3,6,3},
				{5,4,4,4,4,0},{6,5,5,1,5,5},{7,6,6,6,2,6},{4,7,3,7,7,7},
				{8,11,8,8,8,1},{9,8,9,2,9,9},{10,9,10,10,3,10},{11,10,0,11,11,11}
		};
		o = new byte[][] {
				{0,0,0,0,0,1},{0,0,0,0,0,1},{0,0,0,0,1,0},{0,0,0,0,1,0},
				{0,0,0,0,0,1},{0,0,0,0,0,0},{0,0,0,0,1,0},{0,0,0,0,0,0},
				{0,0,0,0,0,1},{0,0,0,0,0,0},{0,0,0,0,1,0},{0,0,0,0,0,0}
		};
		for (a=0; a<12; a++)
			for (b=0; b<2; b++)
				for (c=0; c<6; c++)
					fem[a*2+b][c] = (byte) (p[a][c]*2+(o[a][c]^b));
		for(f=0; f<4; f++) {
			for(a=0; a<576; a++) fecd[f][a] = -1;
			fecd[f][f*51+12] = 0;
			for(d=0; d<6; d++)
				for(a=0; a<576; a++)
					if(fecd[f][a] == d)
						for(b=0; b<6; b++)
							for(e=a,c=0; c<3; c++) {
								e = 24 * fem[e/24][b] + fcm[e%24][b];
								if(fecd[f][e] == -1)
									fecd[f][e] = (byte)(d + 1);
							}
		}
		ini = true;
	}
	
	private static boolean idacross(int ep, int eo, int d, int lm, int face) {
        if(d == 0) return 0 == ep && 0 == eo;
        if(epd[ep] > d || eod[eo] > d) return false;
        for(int i=0; i<6; i++)
        	if(i != lm) {
        		int epx = ep, eox = eo;
        		for(int j=0; j<3; j++) {
        			epx = epm[epx][i]; eox = eom[eox][i];
        			if(idacross(epx, eox, d - 1, i, face)){
        				sb.insert(0, " " + turn[face][i] + suff[j]);
        				return true;
        			}
        		}
        	}
        return false;
	}
	
	private static boolean idaxcross(int ep, int eo, int co, int feo, int idx, int d, int l) {
		if (d == 0) return ep == 0 && eo == 0 && co == goalCo[idx] && feo == goalFeo[idx];
		if (epd[ep] > d || eod[eo] > d || fecd[idx][feo*24+co] > d) return false;
		for (int i = 0; i < 6; i++)
			if (i != l) {
				int cox = co, epx = ep, eox = eo, fx = feo;
				for (int j = 0; j < 3; j++) {
					cox = fcm[cox][i]; fx = fem[fx][i];
					epx = epm[epx][i]; eox = eom[eox][i];
					if (idaxcross(epx, eox, cox, fx, idx, d - 1, i)) {
						sb.insert(0, " " + turn[0][i] + suff[j]);
						return true;
					}
				}
			}
		return false;
	}
	
	public static String cross(String scr, int face, int side) {
		init();
		String[] q = scr.split(" ");
		if(side == 6) {
			StringBuffer cross = new StringBuffer();
			for(int i=0; i<6; i++) {
				cross.append(cross(scr, face, i));
				//if(i < 5) cross.append("\n");
			}
			return cross.toString();
		}
		int eox = 0, epx = 0, i;
		for(i=0; i<q.length; i++)
			if(q[i].length() != 0) {
				int m = moveIdx[face][side].indexOf(q[i].charAt(0));
				eox = eom[eox][m]; epx = epm[epx][m];
				if(1 < q[i].length()) {
					eox = eom[eox][m];
					epx = epm[epx][m];
					if(q[i].charAt(1) == '\'') {
						eox = eom[eox][m];
						epx = epm[epx][m];
					}
				}
			}
		sb = new StringBuffer();
		for(i=0; i<9 && !idacross(epx, eox, i, -1, face); i++);
		return "\nCross(" + color[side] + "): " + rotIdx[face][side] + sb.toString();
	}
	
	public static String xcross(String scr, int face) {
		init();
		if(face == 6) {
			StringBuffer s = new StringBuffer();
			for (int i = 0; i < 6; i++) s.append(xcross(scr, i));
			return s.toString();
		}
		String[] s = scr.split(" ");
		int[] co = new int[4], feo = new int[4];
		for (int i=0; i<4; i++) {
			co[i] = goalCo[i];
			feo[i] = goalFeo[i];
		}
		int ep = 0, eo = 0;
		for (int d=0; d<s.length; d++)
			if (s[d].length() != 0) {
				int m = moveIdx[0][face].indexOf(s[d].charAt(0));
				for (int i=0; i<4; i++) {
					co[i] = fcm[co[i]][m];
					feo[i] = fem[feo[i]][m];
				}
				ep = epm[ep][m]; eo = eom[eo][m];
				if (s[d].length() > 1) {
					for (int i=0; i<4; i++) {
						co[i] = fcm[co[i]][m];
						feo[i] = fem[feo[i]][m];
					}
					eo = eom[eo][m]; ep = epm[ep][m];
					if (s[d].charAt(1) == '\'') {
						for (int i=0; i<4; i++) {
							co[i] = fcm[co[i]][m];
							feo[i] = fem[feo[i]][m];
						}
						eo = eom[eo][m]; ep = epm[ep][m];
					}
				}
			}
		sb = new StringBuffer();
		for (int d=0; ; d++)
			for (int slot=0; slot<4; slot++)
				if (idaxcross(ep, eo, co[slot], feo[slot], slot, d, -1))
					return "\nXCross(" + color[face] + "): " + rotIdx[0][face] + sb.toString();
	}
	
	private static int getPruning(int[] table, int index) {
		return table[index >> 3] >> ((index & 7) << 2) & 15;
	}
	
	private static void setPruning(int[] table, int index, int value) {
		table[index >> 3] ^= (15 ^ value) << ((index & 7) << 2);
	}
	
	public static byte[][] easyCross(int depth) {
		if (!iniFull) {
			init();
			for(int i=0; i<23760; i++) ed[i] = -1;
			setPruning(ed, 0, 0);
			// int c=1;
			for(int d=0; d<8; d++) {
				// c=0;
				for(int i=0; i<190080; i++)
					if (getPruning(ed, i) == d)
						for (int s=0; s<6; s++) {
							int y = i;
							for (int C=0; C<3; C++) {
								int ori = y & 15;
								int p = epm[y >> 4][s];
								int o = eom[y / 384 << 4 | ori][s];
								y = p << 4 | (o & 15);
								if (getPruning(ed, y) == 15) {
									setPruning(ed, y, d + 1);
									// c++;
								}
							}
						}
				// System.out.println(d+" "+c);
			}
			iniFull = true;
		}
		Random r = new Random();
		int i;// r.nextInt(190080);
		if(depth == 0) i = 0;
		else do {
			i = r.nextInt(190080);
		} while (getPruning(ed, i) > depth);
		int comb = i / 384;
		int perm = (i >> 4) % 24;
		int ori = i & 15;
		int[] c = new int[12];
		int[] p = new int[4];
		idxToPerm(p, perm);
		idxToComb(c, p, comb, ori);
		byte[][] arr = new byte[2][12];
		int[] idx = { 7, 6, 5, 4, 10, 9, 8, 11, 3, 2, 1, 0 };
		for (i = 0; i < 12; i++) {
			if (c[i] == -1)
				arr[0][idx[i]] = arr[1][idx[i]] = -1;
			else {
				arr[0][idx[i]] = (byte) (c[i] >> 1);
				arr[1][idx[i]] = (byte) (c[i] & 1);
			}
		}
		return arr;
	}
}
