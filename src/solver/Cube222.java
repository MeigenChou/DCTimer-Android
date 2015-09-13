package solver;

import java.util.Random;

public class Cube222 {
	private int[][] state = new int[2][8];
	private static byte[] perm = new byte[5040];
	private static byte[] twst = new byte[729];
	private static short[][] permmv = new short[5040][3];
	private static short[][] twstmv = new short[729][3];
	
	private static String[] turn = {"U", "R", "F"};
	private static String[] suff = {"'", "2", ""};
	private static boolean ini = false;
	private StringBuffer sol;
	
	private static Random r = new Random();
	
	private static int[] fact = {1, 1, 2, 6, 24, 120, 720};
	private static void idxToPrm(int[] ps, int idx) {
		int val = 0x6543210;
		for (int i=0; i<6; i++) {
			int p = fact[6-i];
			int v = idx / p;
			idx -= v*p;
			v <<= 2;
			ps[i] = (val >> v) & 07;
			int m = (1 << v) - 1;
			val = (val & m) + ((val >> 4) & ~m);
		}
		ps[6] = val;
	}
	
	private static int prmToIdx(int[] ps) {
		int idx = 0;
		int val = 0x6543210;
		for (int i=0; i<7; i++) {
			int v = ps[i] << 2;
			idx = (7 - i) * idx + ((val >> v) & 07);
			val -= 0x1111110 << v;
		}
		return idx;
	}
	
	public Cube222() {
		if(!ini) {
			calcperm();
			ini = true;
		}
	}
	
	private void permMove(int[]ps, int m) {
		switch (m) {
		case 0:	//U
			Mapping.cir(ps, 0, 1, 3, 2); break;
		case 1:	//R
			Mapping.cir(ps, 0, 4, 5, 1); break;
		case 2:	//F
			Mapping.cir(ps, 0, 2, 6, 4); break;
		case 3:	//D
			Mapping.cir(ps, 4, 6, 7, 5); break;
		case 4:	//L
			Mapping.cir(ps, 2, 3, 7, 6); break;
		case 5:	//B
			Mapping.cir(ps, 1, 5, 7, 3); break;
		}
	}
	
	private void twistMove(int[] ps, int m) {
		int c;
		switch (m) {
		case 0:
			Mapping.cir(ps, 0, 1, 3, 2);	//U
			break;
		case 1:
			c=ps[0]; ps[0]=ps[4]+2; ps[4]=ps[5]+1; ps[5]=ps[1]+2; ps[1]=c+1;//R
			break;
		case 2:
			c=ps[0]; ps[0]=ps[2]+1; ps[2]=ps[6]+2; ps[6]=ps[4]+1; ps[4]=c+2;//F
			break;
		case 3:
			Mapping.cir(ps, 4, 6, 7, 5);	//D
			break;
		case 4:
			c=ps[2]; ps[2]=ps[3]+1; ps[3]=ps[7]+2; ps[7]=ps[6]+1; ps[6]=c+2;//L
			break;
		case 5:
			c=ps[1]; ps[1]=ps[5]+2; ps[5]=ps[7]+1; ps[7]=ps[3]+2; ps[3]=c+1;//B
			break;
		}
	}
	
	private void doMove(int m, int n) {
		n %= 4;
		if(n>0) {
			switch (m) {
			case 0:	//U
			case 1:	//R
			case 2:	//F
			case 3:	//D
			case 4:	//L
			case 5:	//B
				for(int i=0; i<n; i++) {
					permMove(state[0], m);
					twistMove(state[1], m);
				}
				break;
			case 6:	//y
			case 7:	//x
			case 8:	//z
				for(int i=0; i<n; i++) {
					permMove(state[0], m-6);
					twistMove(state[1], m-6);
				}
				for(int i=0; i<4-n; i++) {
					permMove(state[0], m-3);
					twistMove(state[1], m-3);
				}
				break;
			}
		}
	}
	
	private void swap(int first, int second) {
		if (first<0 || second<0 || first>7 || second>7 || first==second) {
			return;
		}
		//位置交换
		int tmp = state[0][first];
		state[0][first] = state[0][second];
		state[0][second] = tmp;
		//色相交换
		tmp = state[1][first];
		state[1][first] = state[1][second];
		state[1][second] = tmp;
	}
	
	private void twist(int corner, int value) {
		if (value < 0) return;
		state[1][corner] += value;
	}

	private void reset() {
		for(int i=0; i<8; i++) {
			state[0][i] = i;
			state[1][i] = 0;
		}
	}

	public String randomState() {
		int p, o;
		do {
			p = r.nextInt(5040);
			o = r.nextInt(729);
		} while(p==0 && o==0);
		return scramble(p, o);
	}
	
	public void randomEG(int type, String olls) {
		reset();
		//整体转动
		for(int i=0; i<3; i++)
			doMove(i+6, r.nextInt(4));
		//交换底层2块
		//doMove(3, r.nextInt(4));
		switch (type) {
		case 4:	//不交换
			break;
		case 2:	//交换相邻两块
			swap(4, 6);
			break;
		case 1:	//交换相对两块
			swap(5, 6);
			break;
		case 6:	//不交换或交换相邻块
			if(r.nextInt(2)==1) 
				swap(4, 5);
			break;
		case 5:	//不交换或交换相对块
			if(r.nextInt(2)==2)
				swap(5, 6);
			break;
		case 3:	//交换任意两块
			swap(4+r.nextInt(2), 6);
			break;
		default:
			switch (r.nextInt(3)) {
			case 0:
				break;
			case 1:
				swap(4, 6);
				break;
			case 2:
				swap(5, 6);
				break;
			}
			break;
		}
		//随机顶层
		for(int i=0; i<4; i++) {
			swap(i, i+r.nextInt(4-i));
		}
		if(olls.equals(""))
			Mapping.idxToZori(state[1], r.nextInt(27), 3, 4);
		else if(olls.equals("X") || olls.equals("PHUTLSA")) {
			Mapping.idxToZori(state[1], r.nextInt(26)+1, 3, 4);
		}
		else {
			char oll = olls.charAt(r.nextInt(olls.length()));
			switch (oll) {
			case 'P':
				twist(0, 2); twist(1, 1); twist(2, 2); twist(3, 1);
				break;
			case 'H':
				twist(0, 2); twist(1, 1); twist(2, 1); twist(3, 2);
				break;
			case 'U':
				twist(2, 2); twist(3, 1);
				break;
			case 'T':
				twist(2, 1); twist(3, 2);
				break;
			case 'L':
				twist(0, 2); twist(3, 1);
				break;
			case 'S':
				twist(0, 2); twist(1, 2); twist(3, 2);
				break;
			case 'A':
				twist(0, 1); twist(1, 1); twist(2, 1);
				break;
			case 'N':
				break;
			}
		}
		doMove(0, r.nextInt(4));
		//将DBL块放到D层
		while (state[0][4]!=7 && state[0][5]!=7 && state[0][6]!=7
				&& state[0][7]!=7) {
			doMove(7, 1);
		}
		//将DBL块放回原位
		while (state[0][7] != 7) {
			doMove(6, 1);
		}
		//调整DBL块色向
		while (state[1][7]%3 != 0) {
			doMove(7, 1);
			doMove(6, 1);
		}
	}
	
	public void randomTEG(int type, int twist) {
		reset();
		//整体转动
		for(int i=0; i<3; i++)
			doMove(i+6, r.nextInt(4));
		//交换底层2块
		//doMove(3, r.nextInt(4));
		switch (type) {
		case 4:	//不交换
			break;
		case 2:	//交换相邻两块
			swap(4, 6);
			break;
		case 1:	//交换相对两块
			swap(5, 6);
			break;
		case 6:	//不交换或交换相邻块
			if(r.nextInt(2)==1) 
				swap(4, 5);
			break;
		case 5:	//不交换或交换相对块
			if(r.nextInt(2)==1)
				swap(5, 6);
			break;
		case 3:	//交换任意两块
			swap(4+r.nextInt(2), 6);
			break;
		default:
			switch (r.nextInt(3)) {
			case 0:
				break;
			case 1:
				swap(4, 6);
				break;
			case 2:
				swap(5, 6);
				break;
			}
			break;
		}
		//随机顶层
		for(int i=0; i<4; i++) {
			swap(i, i+r.nextInt(4-i));
		}
		Mapping.idxToZori(state[1], r.nextInt(27), 3, 4);
		//一个底角翻转
		twist(4, twist);
		//随机一个顶角翻转
		twist(r.nextInt(4), 3-twist);
		doMove(0, r.nextInt(4));
		//将DBL块放到D层
		while (state[0][4]!=7 && state[0][5]!=7 && state[0][6]!=7
				&& state[0][7]!=7) {
			doMove(7, 1);
		}
		//将DBL块放回原位
		while (state[0][7] != 7) {
			doMove(6, 1);
		}
		//调整DBL块色向
		while (state[1][7]%3 != 0) {
			doMove(7, 1);
			doMove(6, 1);
		}
	}

	public String randomCLL() {
		int p, o;
		do {
			randomEG(4, "X");
			p = prmToIdx(state[0]);
			o = Mapping.zoriToIdx(state[1], 3, 7);
		} while (p==0 && o==0);
		return scramble(p, o);
	}
	
	public String randomEG1() {
		int p, o;
		do {
			randomEG(2, "X");
			p = prmToIdx(state[0]);
			o = Mapping.zoriToIdx(state[1], 3, 7);
		} while (p==0 && o==0);
		return scramble(p, o);
	}
	
	public String randomEG2() {
		int p, o;
		do {
			randomEG(1, "X");
			p = prmToIdx(state[0]);
			o = Mapping.zoriToIdx(state[1], 3, 7);
		} while (p==0 && o==0);
		return scramble(p, o);
	}
	
	public String randomXLL() {
		int p, o;
		do {
			randomEG(0, "N");
			p = prmToIdx(state[0]);
			o = Mapping.zoriToIdx(state[1], 3, 7);
		} while (p==0 && o==0);
		return scramble(p, o);
	}
	
	public String randomTCLL(int twist) {
		int p, o;
		do {
			randomTEG(4, twist);
			p = prmToIdx(state[0]);
			o = Mapping.zoriToIdx(state[1], 3, 7);
		} while (p==0 && o==0);
		return scramble(p, o);
	}
	
	public String randomTEG1(int twist) {
		int p, o;
		do {
			randomTEG(2, twist);
			p = prmToIdx(state[0]);
			o = Mapping.zoriToIdx(state[1], 3, 7);
		} while (p==0 && o==0);
		return scramble(p, o);
	}
	
	public String randomTEG2(int twist) {
		int p, o;
		do {
			randomTEG(1, twist);
			p = prmToIdx(state[0]);
			o = Mapping.zoriToIdx(state[1], 3, 7);
		} while (p==0 && o==0);
		return scramble(p, o);
	}
	
	public String egScr(int type, String olls) {
		int p, o;
		do {
			randomEG(type, olls);
			p = prmToIdx(state[0]);
			o = Mapping.zoriToIdx(state[1], 3, 7);
		} while (p==0 && o==0);
		return scramble(p, o);
	}
	
	private int getprmmv(int p, int m) {
		//given position p<5040 and move m<3, return new position number
		//convert number into array;
		int[] ps=new int[8];
		idxToPrm(ps, p);
		//perform move on array
		permMove(ps, m);
		//convert array back to number
		return prmToIdx(ps);
	}
	
	private int gettwsmv(int p, int m) {
		//given orientation p<729 and move m<3, return new orientation number
		//convert number into array;
		int[] ps=new int[7];
		Mapping.idxToZori(ps, p, 3, 7);
		//perform move on array
		twistMove(ps, m);
		//convert array back to number
		return Mapping.zoriToIdx(ps, 3, 7);
	}
	
	private void calcperm(){
		//calculate solving arrays
		//first permutation
		for(int p=0; p<5040; p++) {
			perm[p] = -1;
			for(int m=0; m<3; m++)
				permmv[p][m] = (short) getprmmv(p, m);
		}

		perm[0] = 0;
		for(int l=0; l<=6; l++) {
			//n=0;
			for(int p=0; p<5040; p++)
				if(perm[p] == l)
					for(int m=0; m<3; m++){
						int q = p;
						for(int c=0; c<3; c++){
							q = permmv[q][m];
							if(perm[q] < 0) {
								perm[q] = (byte) (l+1);
								//n++;
							}
						}
					}
		}

		//then twist
		for(int p=0; p<729; p++){
			twst[p] = -1;
			for(int m=0; m<3; m++)
				twstmv[p][m] = (short) gettwsmv(p, m);
		}

		twst[0] = 0;
		for(int l=0; l<=5; l++){
			//n=0;
			for(int p=0; p<729; p++)
				if(twst[p] == l)
					for(int m=0; m<3; m++){
						int q = p;
						for(int c=0; c<3; c++){
							q = twstmv[q][m];
							if(twst[q] < 0) {
								twst[q] = (byte) (l+1);
								//n++;
							}
						}
					}
		}
		//remove wait sign
	}
	
	private boolean search(int q, int t, int l, int lm) {
		//searches for solution, from position q|t, in l moves exactly. last move was lm, current depth=d
		if(l==0)return q==0 && t==0;
		if(perm[q]>l || twst[t]>l) return(false);
		for(int m=0; m<3; m++) {
			if(m != lm) {
				int p=q, s=t;
				for(int a=0; a<3; a++) {
					p = permmv[p][m];
					s = twstmv[s][m];
					if(search(p, s, l-1, m)) {
						sol.append(turn[m]+suff[a]+" ");
						return(true);
					}
				}
			}
		}
		return(false);
	}
	
	private String scramble(int p, int o) {
		sol = new StringBuffer();
//		int p = prmToIdx(state[0]);
//		int o = Mapping.zeroSumOrientationToIndex(state[1], 3, 7);
		for(int l=4; l<13; l++) {
			if(search(p, o, l, -1)) break;
		}
		return sol.toString();
	}
	
	public String scramble(int minLen) {
		int p = r.nextInt(5040);
		int o = r.nextInt(729);
		sol = new StringBuffer();
		for(int l=0; ; l++) {
			if(search(p, o, l, -1)) {
				//System.out.println("len "+l);
				if(l < 4) return scramble(minLen);
				sol = new StringBuffer();
				search(p, o, 11, -1);
				return sol.toString();
			}
		}
	}
}
