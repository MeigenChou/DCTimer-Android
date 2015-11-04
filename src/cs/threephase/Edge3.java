package cs.threephase;
import java.util.*;

import android.os.Handler;
import static cs.threephase.Util.*;

class Edge3 {

	static final int N_SYM = 1538;
	static final int N_RAW = 20160;
	static final int N_EPRUN = N_SYM * N_RAW;
	static final int MAX_DEPTH = 10;
	static final int[] prunValues = {1, 4, 16, 55, 324, 1922, 12275, 77640, 485359, 2778197, 11742425, 27492416, 31002941, 31006080};
	static int[] eprun = new int[N_EPRUN / 16];

	static int[] sym2raw = new int[N_SYM];
	static char[] symstate = new char[N_SYM];
	static int[] raw2sym = new int[11880];

	static int[] syminv = {0, 1, 6, 3, 4, 5, 2, 7};

	int[] edge = new int[12];
	int[] edgeo = new int[12];
	int[] temp;
	boolean isStd = true;

	static int[][] mvrot = new int[20 * 8][12];
	static int[][] mvroto = new int[20 * 8][12];
	
	static int[] factX = {1, 1, 2/2, 6/2, 24/2, 120/2, 720/2, 5040/2, 40320/2, 362880/2, 3628800/2, 39916800/2, 479001600/2};

	static void initMvrot() {
		Edge3 e = new Edge3();
		for (int m=0; m<20; m++) {
			for (int r=0; r<8; r++) {
				e.set(0);
				e.move(m);
				e.rotate(r);
				System.arraycopy(e.edge, 0, mvrot[m<<3|r], 0, 12);
				e.std();
				System.arraycopy(e.temp, 0, mvroto[m<<3|r], 0, 12);
			}
		}
	}

	static void initRaw2Sym() {
		Edge3 e = new Edge3();		
		byte[] occ = new byte[11880/8];
		int count = 0;
		for (int i=0; i<11880; i++) {
			if ((occ[i>>>3]&(1<<(i&7))) == 0) {
				e.set(i * factX[8]);
				for (int j=0; j<8; j++) {
					int idx = e.get(4);
					if (idx == i) {
						symstate[count] |= 1 << j;
					}
					occ[idx>>3] |= (1<<(idx&7));
					raw2sym[idx] = count << 3 | syminv[j];
					e.rot(0);
					if ((j&1)==1) {
						e.rot(1);
						e.rot(2);
					}
				}
				sym2raw[count++] = i;
			}
		}
		// System.out.println(count);
	}
	
	public static void createPrun(Handler handler) {
		//long t = System.currentTimeMillis();
		Edge3 e = new Edge3();
		Edge3 f = new Edge3();
		Edge3 g = new Edge3();
		//eprun = new int[N_EPRUN / 16];

		Arrays.fill(eprun, -1);
		int depth = 0;
		c4prog = 1;
		setPruning(eprun, 0, 0);

		while (c4prog != N_EPRUN) {
			boolean inv = depth > 9;
			int depm3 = depth % 3;
			int dep1m3 = (depth + 1) % 3;
			int find = inv ? 0x3 : depm3;
			int chk = inv ? depm3 : 0x3;

			if (depth >= MAX_DEPTH - 1) {
				break;
			}

			for (int i_=0; i_<N_EPRUN; i_+=16) {
				int val = eprun[i_ >> 4];
				if (!inv && val == -1) {
					continue;
				}
				for (int i=i_, end=i_+16; i<end; i++, val>>=2) {
					if ((val & 3) != find) {
						continue;
					}
					int symcord1 = i / N_RAW;
					int cord1 = sym2raw[symcord1];
					int cord2 = i % N_RAW;
					e.set(cord1 * N_RAW + cord2);

					for (int m=0; m<17; m++) {
						int cord1x = getmvrot(e.edge, m<<3, 4);
						int symcord1x = raw2sym[cord1x];
						int symx = symcord1x & 0x7;
						symcord1x >>= 3;
						int cord2x = getmvrot(e.edge, m<<3|symx, 10) % N_RAW;
						int idx = symcord1x * N_RAW + cord2x;
						if (getPruning(eprun, idx) != chk) {
							continue;
						}
						setPruning(eprun, inv ? i : idx, dep1m3);
						c4prog++;
						if ((c4prog % 448090) == 179236) {
							handler.sendEmptyMessage(22);
						}
						if (inv) {
							break;
						}
						char symState = symstate[symcord1x];
						if (symState == 1){
							continue;
						}
						f.set(e);
						f.move(m);
						f.rotate(symx);
						for (int j=1; (symState >>= 1) != 0; j++) {
							if ((symState & 1) == 1) {
								g.set(f);
								g.rotate(j);
								int idxx = symcord1x * N_RAW + g.get(10) % N_RAW;
								if (getPruning(eprun, idxx) == chk) {
									setPruning(eprun, idxx, dep1m3);
									c4prog++;
									if ((c4prog % 448090) == 179236) {
										handler.sendEmptyMessage(22);
									}
								}
							}
						}
					}
				}
			}
			depth++;
			//System.out.println(depth+" "+prog);
		}
	}
	
	static void setPruning(int[] table, int index, int value) {
		table[index >> 4] ^= (0x3 ^ value) << ((index & 0xf) << 1);
	}

	static int getPruning(int[] table, int index) {
		return (table[index >> 4] >> ((index & 0xf) << 1)) & 0x3;
	}

	static int getprun(int edge, int prun) {
		int depm3 = getPruning(eprun, edge);
		if (depm3 == 0x3) {
			return MAX_DEPTH;
		}
		return (depm3 - prun + 16) % 3 + prun - 1;
	}

	static int getprun(int edge) {
		Edge3 e = new Edge3();
		int depth = 0;
		int depm3 = getPruning(eprun, edge);
		if (depm3 == 0x3) {
			return MAX_DEPTH;
		}
		while (edge!=0) {
			if (depm3 == 0) {
				depm3 = 2;
			} else {
				depm3--;
			}

			int symcord1 = edge / N_RAW;
			int cord1 = sym2raw[symcord1];
			int cord2 = edge % N_RAW;
			e.set(cord1 * N_RAW + cord2);

			for (int m=0; m<17; m++) {
				int cord1x = getmvrot(e.edge, m<<3, 4);
				int symcord1x = raw2sym[cord1x];
				int symx = symcord1x & 0x7;
				symcord1x >>= 3;
				int cord2x = getmvrot(e.edge, m<<3|symx, 10) % N_RAW;
				int idx = symcord1x * N_RAW + cord2x;
				if (getPruning(eprun, idx) == depm3) {
					depth++;
					edge = idx;
					break;
				}
			}
		}
		return depth;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<12; i++) {
			sb.append(String.format("%2c", "0123456789AB".charAt(edge[i])));
		}
		sb.append('\n');
		for (int i=0; i<12; i++) {
			sb.append(String.format("%2c", "0123456789AB".charAt(edgeo[i])));
		}
		return sb.toString();
	}

	static int[] FullEdgeMap = {0, 2, 4, 6, 1, 3, 7, 5, 8, 9, 10, 11};

	int getsym() {
		int cord1x = get(4);
		int symcord1x = raw2sym[cord1x];
		int symx = symcord1x & 0x7;
		symcord1x >>= 3;
		rotate(symx);
		int cord2x = get(10) % N_RAW;
		return symcord1x * N_RAW + cord2x;
	}

	int set(EdgeCube c) {
		if (temp == null) {
			temp = new int[12];
		}
		for (int i=0; i<12; i++) {
			temp[i] = i;
			edge[i] = c.ep[FullEdgeMap[i]+12]%12;
		}
		int parity = 1;	//because of FullEdgeMap
		for (int i=0; i<12; i++) {
			while (edge[i] != i) {
				int t = edge[i];
				edge[i] = edge[t];
				edge[t] = t;
				int s = temp[i];
				temp[i] = temp[t];
				temp[t] = s;
				parity ^= 1;
			}
		}
		for (int i=0; i<12; i++) {
			edge[i] = temp[c.ep[FullEdgeMap[i]]%12];
		}
		return parity;
	}

	void set(Edge3 e) {
		System.arraycopy(e.edge, 0, this.edge, 0, 12);
		System.arraycopy(e.edgeo, 0, this.edgeo, 0, 12);
		isStd = e.isStd;
	}

	static int getmvrot(int[] ep, int mrIdx, int end) {
		int[] movo = mvroto[mrIdx];
		int[] mov = mvrot[mrIdx];
		int idx = 0;

//		long val = 0xba9876543210L;
//		for (int i=0; i<end; i++) {
//			int v = movo[ep[mov[i]]] << 2;
//			idx *= 12 - i;
//			idx += (val >> v) & 0xf;
//			val -= 0x111111111110L << v;
//		}
		
		int vall = 0x76543210;
		int valh = 0xba98;
		for (int i=0; i<end; i++) {
			int v = movo[ep[mov[i]]] << 2;
			idx *= 12 - i;
			if (v >= 32) {
				idx += (valh >> (v - 32)) & 0xf;
				valh -= 0x1110 << (v - 32);
			} else {
				idx += (vall >> v) & 0xf;
				valh -= 0x1111;
				vall -= 0x11111110 << v;
			}
		}
		return idx;	
	}
	
	void std() {
		if (temp == null) {
			temp = new int[12];
		}
		for (int i=0; i<12; i++) {
			temp[edgeo[i]] = i;
		}

		for (int i=0; i<12; i++) {
			edge[i] = temp[edge[i]];
			edgeo[i] = i;
		}		
		isStd = true;
	}
	
	int get(int end) {
		if (!isStd) {
			std();
		}
		int idx = 0;
		
		long val = 0xba9876543210L;
		for (int i=0; i<end; i++) {
			int v = edge[i] << 2;
			idx *= 12 - i;
			idx += (val >> v) & 0xf;
			val -= 0x111111111110L << v;
		}
		
//		int vall = 0x76543210;
//		int valh = 0xba98;
//		for (int i=0; i<end; i++) {
//			int v = edge[i] << 2;
//			idx *= 12 - i;
//			if (v >= 32) {
//				idx += (valh >> (v - 32)) & 0xf;
//				valh -= 0x1110 << (v - 32);
//			} else {
//				idx += (vall >> v) & 0xf;
//				valh -= 0x1111;
//				vall -= 0x11111110 << v;
//			}
//		}
		return idx;		
	}
	
	void set(int idx) {
		long val = 0xba9876543210L;
		int parity = 0;
		for (int i=0; i<11; i++) {
			int p = factX[11-i];
			int v = idx / p;
			idx = idx % p;
			parity ^= v;
			v <<= 2;
			edge[i] = (int) ((val >> v) & 0xf);
			long m = (1L << v) - 1;
			val = (val & m) + ((val >> 4) & ~m);
		}
		if ((parity & 1) == 0) {
			edge[11] = (int) val;
		} else {
			edge[11] = edge[10];
			edge[10] = (int) val;
		}
		
		for (int i=0; i<12; i++) {
			edgeo[i] = i;
		}
		isStd = true;
	}

	void move(int i) {
		isStd = false;
		switch (i) {
		case 0:		//U
			circle(edge, 0, 4, 1, 5);
			circle(edgeo, 0, 4, 1, 5);
			break;
		case 1:		//U2
			swap(edge, 0, 4, 1, 5);
			swap(edgeo, 0, 4, 1, 5);
			break;
		case 2:		//U'
			circle(edge, 0, 5, 1, 4);
			circle(edgeo, 0, 5, 1, 4);
			break;
		case 3:		//R2
			swap(edge, 5, 10, 6, 11);
			swap(edgeo, 5, 10, 6, 11);
			break;
		case 4:		//F
			circle(edge, 0, 11, 3, 8);
			circle(edgeo, 0, 11, 3, 8);
			break;
		case 5:		//F2
			swap(edge, 0, 11, 3, 8);
			swap(edgeo, 0, 11, 3, 8);
			break;
		case 6:		//F'
			circle(edge, 0, 8, 3, 11);
			circle(edgeo, 0, 8, 3, 11);
			break;
		case 7:		//D
			circle(edge, 2, 7, 3, 6);
			circle(edgeo, 2, 7, 3, 6);
			break;
		case 8:		//D2
			swap(edge, 2, 7, 3, 6);
			swap(edgeo, 2, 7, 3, 6);
			break;
		case 9:		//D'
			circle(edge, 2, 6, 3, 7);
			circle(edgeo, 2, 6, 3, 7);
			break;
		case 10:	//L2
			swap(edge, 4, 8, 7, 9);
			swap(edgeo, 4, 8, 7, 9);
			break;
		case 11:	//B
			circle(edge, 1, 9, 2, 10);
			circle(edgeo, 1, 9, 2, 10);
			break;
		case 12:	//B2
			swap(edge, 1, 9, 2, 10);
			swap(edgeo, 1, 9, 2, 10);
			break;
		case 13:	//B'
			circle(edge, 1, 10, 2, 9);
			circle(edgeo, 1, 10, 2, 9);
			break;
		case 14:	//u2
			swap(edge, 0, 4, 1, 5);
			swap(edgeo, 0, 4, 1, 5);
			swap(edge, 9, 11);
			swap(edgeo, 8, 10);
			break;
		case 15:	//r2
			swap(edge, 5, 10, 6, 11);
			swap(edgeo, 5, 10, 6, 11);
			swap(edge, 1, 3);
			swap(edgeo, 0, 2);
			break;
		case 16:	//f2
			swap(edge, 0, 11, 3, 8);
			swap(edgeo, 0, 11, 3, 8);
			swap(edge, 5, 7);
			swap(edgeo, 4, 6);
			break;
		case 17:	//d2
			swap(edge, 2, 7, 3, 6);
			swap(edgeo, 2, 7, 3, 6);
			swap(edge, 8, 10);
			swap(edgeo, 9, 11);
			break;
		case 18:	//l2
			swap(edge, 4, 8, 7, 9);
			swap(edgeo, 4, 8, 7, 9);
			swap(edge, 0, 2);
			swap(edgeo, 1, 3);
			break;
		case 19:	//b2
			swap(edge, 1, 9, 2, 10);
			swap(edgeo, 1, 9, 2, 10);
			swap(edge, 4, 6);
			swap(edgeo, 5, 7);
			break;		
		}
	}

	void rot(int r) {
		isStd = false;
		switch (r) {
		case 0:
			move(14);
			move(17);
			break;
		case 1:
			circlex(11, 5, 10, 6);//r
			circlex(5, 10, 6, 11);				
			circlex(1, 2, 3, 0);
			circlex(4, 9, 7, 8);//l'
			circlex(8, 4, 9, 7);				
			circlex(0, 1, 2, 3);
			break;
		case 2:
			swapx(4, 5); swapx(5, 4);
			swapx(11, 8); swapx(8, 11);
			swapx(7, 6); swapx(6, 7);
			swapx(9, 10); swapx(10, 9);
			swapx(1, 1); swapx(0, 0);
			swapx(3, 3); swapx(2, 2);
			break;
		}	
	}

	void rotate(int r) {
		while (r >= 2) {
			r -= 2;
			rot(1);
			rot(2);
		}
		if (r != 0) {
			rot(0);
		}
	}

	static int tempi;
	void circle(int[] arr, int a, int b, int c, int d) {
		tempi = arr[d];
		arr[d] = arr[c];
		arr[c] = arr[b];
		arr[b] = arr[a];
		arr[a] = tempi;
	}

	void swap(int[] arr, int a, int b, int c, int d) {
		tempi = arr[a];
		arr[a] = arr[c];
		arr[c] = tempi;
		tempi = arr[b];
		arr[b] = arr[d];
		arr[d] = tempi;
	}

	void swap(int[] arr, int x, int y) {
		tempi = arr[x];
		arr[x] = arr[y];
		arr[y] = tempi;
	}

	void swapx(int x, int y) {
		tempi = edge[x];
		edge[x] = edgeo[y];
		edgeo[y] = tempi;
	}

	void circlex(int a, int b, int c, int d) {
		tempi = edgeo[d];
		edgeo[d] = edge[c];
		edge[c] = edgeo[b];
		edgeo[b] = edge[a];
		edge[a] = tempi;
	}
}
