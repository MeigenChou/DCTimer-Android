package solver;

import java.util.Random;

public class Pyraminx {
	private static byte[] colmap = new byte[91];
	private static byte[] perm = new byte[360];	// pruning table for edge permutation
	private static byte[] twst = new byte[2592];	// pruning table for edge orientation+twist
	private static short[][] permmv = new short[360][4];	// transition table for edge permutation
	private static byte[][] twstmv = new byte[81][4];	// transition table for corner orientation
	private static byte[][] flipmv = new byte[32][4];	// transition table for edge orientation
	private static String[] turn = {"L", "R", "B", "U"};
	private static String[] suff = {"'", ""};
	private static String[] tip = {"l", "r", "b", "u"};
	private StringBuffer sol;
	private static boolean ini = false;
	private static byte[] img = new byte[91];
	private static Random r = new Random();
	
	public Pyraminx() {
		if(!ini) {
			calcperm();
			ini = true;
		}
	}
	
	private static void init_colors() {
		colmap = new byte[] {
				1, 1, 1, 1, 1, 0, 2, 0, 3, 3, 3, 3, 3,
				0, 1, 1, 1, 0, 2, 2, 2, 0, 3, 3, 3, 0,
				0, 0, 1, 0, 2, 2, 2, 2, 2, 0, 3, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 4, 4, 4, 4, 4, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 4, 4, 4, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0 };
	}
	
	public String scramble() {
		int i, j;
		int t = r.nextInt(2592), q = r.nextInt(360);
		sol = new StringBuffer();
		for(int l=4; !search(q,t,l,-1); l++);
		init_colors();
		for (i=0; i<4; i++) {
			j = r.nextInt(3);
			if (j < 2) {
				sol.append(tip[i] + suff[j] + " ");
			}
		}
		return sol.toString();
	}
	
	public String scramble(int minLen) {
		int i;
		if(!ini) {
			calcperm();
			ini = true;
		}
		int[] tips = new int[4];
		for(i=0; i<4; i++) {
			tips[i] = r.nextInt(3);
			if(tips[i] < 2)
				minLen--;
		}
		int t = r.nextInt(2592), q = r.nextInt(360);
		sol = new StringBuffer();
		for(int l=0; ; l++) {
			if(search(q, t, l, -1)) {
				//System.out.println("len "+l);
				if (l < minLen) return scramble(minLen);
				sol = new StringBuffer();
				search(q, t, 11, -1);
				break;
			}
		}
		for (i=0; i<4; i++) {
			if (tips[i] < 2) {
				sol.append(tip[i] + suff[tips[i]] + " ");
			}
		}
		return sol.toString();
	}

	private boolean search(int q, int t, int l, int lm) {
		//searches for solution, from position q|t, in l moves exactly. last move was lm, current depth=d
		if(l==0) return q==0 && t==0;
		if(perm[q]>l || twst[t]>l) return false;
		int p, s, a, m;
		for(m=0; m<4; m++) {
			if(m != lm) {
				p = q; s = t;
				for(a=0; a<2; a++) {
					p = permmv[p][m];
					s = twstmv[s>>5][m] << 5 | flipmv[s&31][m];
					if(search(p, s, l-1, m)) {
						sol.append(turn[m]+suff[a]+" ");
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private void calcperm() {
		int c, q, l, m, p, r;
		//calculate solving arrays
		//first permutation
		//initialise arrays
		for(p=0; p<360; p++) {
			perm[p] = -1;
			for(m=0; m<4; m++)
				permmv[p][m] = (short) getprmmv(p, m);
		}
		//fill it
		perm[0] = 0;
		for(l=0; l<=4; l++)
			for(p=0; p<360; p++)
				if(perm[p] == l)
					for(m=0; m<4; m++) {
						q = p;
						for(c=0; c<2; c++) {
							q = permmv[q][m];
							if(perm[q] == -1) perm[q] = (byte) (l+1);
						}
					}
		//then twist && flip
		//initialise arrays
		for(p=0; p<81; p++)
			for(m=0; m<4; m++) {
				twstmv[p][m] = (byte) gettwsmv(p, m);
				if(p<32) flipmv[p][m] = (byte) getflpmv(p, m);
			}
		//fill it
		for(p=0; p<2592; p++) twst[p] = -1;
		twst[0] = 0;
		for(l=0; l<=6; l++)
			for(p=0; p<2592; p++)
				if(twst[p] == l)
					for(m=0; m<4; m++) {
						q = p>>5; r = p&31;
						for(c=0; c<2; c++) {
							q = twstmv[q][m]; r = flipmv[r][m];
							if(twst[q<<5|r] == -1) twst[q<<5|r] = (byte) (l+1);
						}
					}
	}
	
	private int getprmmv(int p, int m) {
		//given position p<360 and move m<4, return new position number
		//convert number into array
		int[] ps = new int[6];
		Mapping.idxToEperm(ps, p, 6);
		//perform move on array
		if(m == 0) {
			Mapping.cir(ps, 1, 5, 2);//L
		} else if(m == 1) {
			Mapping.cir(ps, 0, 2, 4);//R
		} else if(m == 2) {
			Mapping.cir(ps, 3, 4, 5);//B
		} else if(m == 3) {
			Mapping.cir(ps, 0, 3, 1);//U
		}
		//convert array back to number
		return(Mapping.epermToIdx(ps, 6));
	}
	
	private int getflpmv(int p, int m) {
		//given orientation p<32 and move m<4, return new position number
		//convert number into array;
		int a, d=0;
		int[] ps = new int[6];
		int q = p;
		//edge orientation
		for(a=0; a<=4; a++) {
			ps[a] = q & 1;
			q>>=1;
			d^=ps[a];
		}
		ps[5] = d;
		//perform move on array
		switch (m) {
		case 0:	//L
			Mapping.cir(ps, 1, 5, 2);
			ps[2]^=1; ps[5]^=1;
			break;
		case 1:	//R
			Mapping.cir(ps, 0, 2, 4);
			ps[0]^=1; ps[2]^=1;
			break;
		case 2:	//B
			Mapping.cir(ps, 3, 4, 5);
			ps[3]^=1; ps[4]^=1;
			break;
		case 3:	//U
			Mapping.cir(ps, 0, 3, 1);
			ps[1]^=1;ps[3]^=1;
			break;
		}
		//edge orientation
		for(a=4; a>=0; a--) {
			q=q*2+ps[a];
		}
		return q;
	}
	
	private int gettwsmv(int p, int m) {
		//given orientation p<81 and move m<4, return new position number
		//convert number into array;
		int[] ps = new int[4];
		//corner orientation
		Mapping.idxToOri(ps, p, 3, 4);
		//perform move on array
		switch (m) {
		case 0:	//L
			ps[1]++; if(ps[1]==3) ps[1]=0;
			break;
		case 1:	//R
			ps[2]++; if(ps[2]==3) ps[2]=0;
			break;
		case 2:	//B
			ps[3]++; if(ps[3]==3) ps[3]=0;
			break;
		case 3:	//U
			ps[0]++; if(ps[0]==3) ps[0]=0;
			break;
		}
		//convert array back to number
		//corner orientation
		return(Mapping.oriToIdx(ps, 3, 4));
	}
	
	private void picmove(int type, int direction) {
		switch(type) {
		case 0: // L
			rotate3(14,58,18, direction);
			rotate3(15,57,31, direction);
			rotate3(16,70,32, direction);
			rotate3(30,28,56, direction);
			break;
		case 1: // R
			rotate3(32,72,22, direction);
			rotate3(33,59,23, direction);
			rotate3(20,58,24, direction);
			rotate3(34,60,36, direction);
			break;
		case 2: // B
			rotate3(14,10,72, direction);
			rotate3( 1,11,71, direction);
			rotate3( 2,24,70, direction);
			rotate3( 0,12,84, direction);
			break;
		case 3: // U
			rotate3( 2,18,22, direction);
			rotate3( 3,19, 9, direction);
			rotate3(16,20,10, direction);
			rotate3( 4, 6, 8, direction);
			break;
		case 4: // l
			rotate3(30,28,56, direction);
			break;
		case 5: // r
			rotate3(34,60,36, direction);
			break;
		case 6: // b
			rotate3( 0,12,84, direction);
			break;
		case 7: // u
			rotate3( 4, 6, 8, direction);
			break;
		}
	}
	
	private static void rotate3(int v1, int v2, int v3, int clockwise) {
		if(clockwise == 2) {
			Mapping.cir(colmap, v3, v2, v1);
		} else {
			Mapping.cir(colmap, v1, v2, v3);
		}
	}
	
//	public static byte[] imageString() {
//		int d=0;
//		for(int x = 0; x < 91; x++)
//			img[d++] = (byte) (colmap[x] - 1);
//		return img;
//	}
	
	private static String moveIdx = "LRBUlrbu";
	public byte[] imageString(String scr) {
		String[] s=scr.split(" ");
		init_colors();
		int turn, suff;
		for(int i=0; i<s.length; i++) {
			suff = s[i].length();
			if(suff > 0) {
				turn = moveIdx.indexOf(s[i].charAt(0));
				picmove(turn, suff);
			}
		}
		int d=0;
		for(int x = 0; x < 91; x++)
			img[d++] = (byte) (colmap[x] - 1);
		return img;
	}
}
