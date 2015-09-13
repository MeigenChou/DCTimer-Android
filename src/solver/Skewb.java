package solver;

import java.util.Random;

public class Skewb {
	private static short[][] ctm = new short[360][4];
	private static byte[][] cpm = new byte[36][4];
	private static short[][] com = new short[2187][4];
	private static byte[] ctd = new byte[360];
	private static byte[][] cd = new byte[2187][36];
	private static Random r = new Random();
	
	private static boolean ini = false;
	public Skewb() {
		if(!ini) {
			init();
			ini = true;
		}
	}
	
	private void init() {
		// move tables
		/* center
		 * 		  0
		 *	4	1	2	3
		 * 		  5
		 */
		int[] arr = new int[7];
		for (int i=0; i<360; i++)
			for (int j=0; j<4; j++) {
				Mapping.idxToEperm(arr, i, 6);
				switch(j) {
				case 0: Mapping.cir(arr, 2, 5, 3); break;	//R
				case 1: Mapping.cir(arr, 0, 3, 4); break;	//U
				case 2: Mapping.cir(arr, 1, 4, 5); break;	//L
				case 3: Mapping.cir(arr, 3, 5, 4); break;	//B
				}
				ctm[i][j] = (short) Mapping.epermToIdx(arr, 6);
			}
		/* corner permutation
		 *	0		1
		 *		3
		 *		2
		 *
		 *		0
		 *	1		2
		 */
		int[] arr2 = new int[3];
		for (int i=0; i<12; i++)
			for (int j=0; j<3; j++) {
				for (int k=0; k<4; k++) {
					Mapping.idxToEperm(arr, i, 4);
					Mapping.idxToEperm(arr2, j, 3);
					switch (k) {
					case 0: Mapping.cir(arr, 1, 2, 3); break;	//R
					case 1: Mapping.cir(arr, 0, 1, 3); break;	//U
					case 2: Mapping.cir(arr, 2, 0, 3); break;	//L
					case 3: Mapping.cir(arr2, 0, 2, 1); break;	//B
					}
					cpm[i*3+j][k] = (byte) (Mapping.epermToIdx(arr, 4)*3+Mapping.epermToIdx(arr2, 3));
				}
			}
		//corner orientation
		/*
		 *		0
		 *	1		2
		 *		3
		 *	4		5
		 *		6
		 */
		for(int i=0; i<2187; i++) {
			for(int j=0; j<4; j++) {
				Mapping.idxToOri(arr, i, 3, 7);
				switch (j) {
				case 0:
					Mapping.cir(arr, 2, 6, 3); arr[2]+=2; arr[3]+=2; arr[5]++; arr[6]+=2; break;
				case 1:
					Mapping.cir(arr, 1, 2, 3); arr[0]++; arr[1]+=2; arr[2]+=2; arr[3]+=2; break;
				case 2:
					Mapping.cir(arr, 1, 3, 6); arr[1]+=2; arr[3]+=2; arr[4]++; arr[6]+=2; break;
				case 3:
					Mapping.cir(arr, 0, 5, 4); arr[0]+=2; arr[3]++; arr[4]+=2; arr[5]+=2; break;
				}
				com[i][j] = (short) Mapping.oriToIdx(arr, 3, 7);
			}
		}
		
		// distance table
		for (int i = 0; i < 360; i++)
			ctd[i] = -1;
		ctd[0] = 0;
		//int c = 1;
		for(int depth = 0; depth < 5; depth++) {
			for (int i = 0; i < 360; i++)
					if (ctd[i] == depth)
						for (int m = 0; m < 4; m++) {
							int p = i;
							for(int n = 0; n < 2; n++){
								p = ctm[p][m];
								if (ctd[p] == -1) {
									ctd[p] = (byte) (depth + 1);
									//c++;
								}
							}
						}
			//System.out.println(depth+1+" "+c);
		}
		for(int i=0; i<2187; i++)
			for(int j=0; j<36; j++) cd[i][j] = -1;
		cd[0][0] = 0;
		//c=1;
		for(int d=0; d<7; d++) {
			for(int i=0; i<2187; i++)
				for(int j=0; j<36; j++)
					if(cd[i][j] == d) 
						for(int k=0; k<4; k++) {
							int p = i, q = j;
							for(int l=0; l<2; l++) {
								p = com[p][k]; q = cpm[q][k];
								if(cd[p][q] == -1) {
									cd[p][q] = (byte)(d+1);
									//c++;
								}
							}
						}
			//System.out.println(d+1+" "+c);
		}
	}
	
	private static StringBuffer sol;
	private static String[] turn = {"R", "U", "L", "B"};
	private static String[] suff = {"'", ""};
	private boolean search(int ct, int cp, int co, int d, int l) {
		if (d==0) return ctd[ct] == 0 && cd[co][cp] == 0;
		if (ctd[ct] > d || cd[co][cp] > d) return false;
		for (int k=0; k<4; k++)
			if (k != l) {
				int p = ct, q = cp, r = co;
				for(int m=0; m<2; m++) {
					p = ctm[p][k]; q = cpm[q][k]; r = com[r][k];
					if(search(p, q, r, d-1, k)) {
						sol.append(turn[k] + suff[m] + " ");
						return true;
					}
				}
			}
		return false;
	}
	public String scramble() {
		init();
		int ct = r.nextInt(360), cp, co;
		do{
			cp = r.nextInt(36);
			co = r.nextInt(2187);
		} while (cd[co][cp] < 0);
		sol = new StringBuffer();
		for(int d=0; d<13; d++) 
			if(search(ct, cp, co, d, -1)) 
				break;
		return sol.toString();
	}
	
	public String scramble(int minLen) {
		init();
		int ct = r.nextInt(360), cp, co;
		do{
			cp = r.nextInt(36);
			co = r.nextInt(2187);
		} while (cd[co][cp] < 0);
		sol = new StringBuffer();
		for(int d=0; ; d++) 
			if(search(ct, cp, co, d, -1)) {
				//System.out.println("len: "+d);
				if(d < minLen) return scramble(minLen);
				sol = new StringBuffer();
				search(ct, cp, co, 11, -1);
				return sol.toString();
			}
	}

	/*
	 *				0		1
	 *					2
	 *				3		4
	 *	5		6	10		11	15		16	20		21
	 *		7			12			17			22
	 *	8		9	13		14	18		19	23		24
	 *				25		26
	 *					27
	 *				28		29
	 */
	private static byte[] img = new byte[30];
	private void initColor() {
		for (int i=0; i<5; i++)
			for (byte j=0; j<6; j++) img[j*5+i] = j;
	}
	private static void cir3(byte[] arr, int a, int b, int c) {
		byte temp = arr[a];
		arr[a] = arr[b];
		arr[b] = arr[c];
		arr[c] = temp;
	}
	private void move(int turn) {
		switch(turn) {
		case 0:	//R
			cir3(img, 17, 27, 22);
			cir3(img, 19, 29, 23);
			cir3(img, 1, 14, 8);
			cir3(img, 20, 18, 28);
			cir3(img, 16, 26, 24);
			break;
		case 1:	//U
			cir3(img, 2, 22, 7);
			cir3(img, 0, 21, 5);
			cir3(img, 3, 20, 8);
			cir3(img, 10, 16, 28);
			cir3(img, 6, 1, 24);
			break;
		case 2:	//L
			cir3(img, 12, 7, 27);
			cir3(img, 13, 9, 25);
			cir3(img, 3, 24, 18);
			cir3(img, 10, 8, 26);
			cir3(img, 6, 28, 14);
			break;
		case 3:	//B
			cir3(img, 22, 27, 7);
			cir3(img, 24, 28, 8);
			cir3(img, 0, 19, 13);
			cir3(img, 5, 23, 25);
			cir3(img, 21, 29, 9);
			break;
		}
	}
	private static String moveIdx="RULB";
	public byte[] image(String scr) {
    	initColor();
    	String[] s = scr.split(" ");
    	for (int i=0; i<s.length; i++)
    		if (s[i].length() > 0) {
    			int mov = moveIdx.indexOf(s[i].charAt(0));
    			move(mov);
    			if(s[i].length() > 1)
    				move(mov);
    		}
    	return img;
    }
}
