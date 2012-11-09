package solvers;

import java.util.Random;

public class Skewb {
	private static short[][] fpm = new short[360][4];
	private static byte[][] cpm = new byte[12][4];
	private static byte[][] com = new byte[27][4];
	private static byte[][] fcm = new byte[81][4];
	private static byte[] fd = new byte[360];
	private static byte[][][] cd = new byte[12][27][81];
	
	private static void cir(int[] arr, int a, int b, int c){
		int temp = arr[a];
		arr[a] = arr[b];
		arr[b] = arr[c];
		arr[c] = temp;
	}
	
	private static boolean ini=false;
	private static void init() {
		if(ini)return;
		
		int[] arr = new int[6];
		// move tables
		for (int i = 0; i < 360; i++)
			for (int j = 0; j < 4; j++) {
				Im.indexToEvenPermutation(arr, i, 6);
				switch(j){
				case 0: cir(arr, 0, 1, 4); break;
				case 1: cir(arr, 0, 3, 2); break;
				case 2: cir(arr, 3, 4, 5); break;
				case 3: cir(arr, 1, 2, 5); break;
				}
				fpm[i][j] = (short)Im.evenPermutationToIndex(arr, 6);
			}
		
		arr = new int[4];
		for (int i = 0; i < 12; i++)
			for (int j = 0; j < 4; j++) {
				Im.indexToEvenPermutation(arr, i, 4);
				switch(j){
				case 0: cir(arr, 0, 2, 1); break;
				case 1: cir(arr, 0, 1, 3); break;
				case 2: cir(arr, 1, 2, 3); break;
				case 3: cir(arr, 0, 3, 2); break;
				}
				cpm[i][j] = (byte)Im.evenPermutationToIndex(arr, 4);
			}

		for (int i = 0; i < 27; i++)
			for (int j = 0; j < 4; j++) {
				Im.indexToZeroSumOrientation(arr, i, 3, 4);
				switch(j){
				case 0: cir(arr, 0, 2, 1); arr[0] = (arr[0] + 2) % 3;
				arr[1] = (arr[1] + 2) % 3; arr[2] = (arr[2] + 2) % 3; break;
				case 1: cir(arr, 0, 1, 3); arr[0] = (arr[0] + 2) % 3;
				arr[1] = (arr[1] + 2) % 3; arr[3] = (arr[3] + 2) % 3; break;
				case 2: cir(arr, 1, 2, 3); arr[3] = (arr[3] + 2) % 3;
				arr[1] = (arr[1] + 2) % 3; arr[2] = (arr[2] + 2) % 3; break;
				case 3: cir(arr, 0, 3, 2); arr[0] = (arr[0] + 2) % 3;
				arr[3] = (arr[3] + 2) % 3; arr[2] = (arr[2] + 2) % 3; break;
				}
				com[i][j] = (byte)Im.zeroSumOrientationToIndex(arr, 3, 4);
			}

		int[] ch = {0, 1, 3, 2};
		for (int i = 0; i < 81; i++)
			for (int j = 0; j < 4; j++) {
				Im.indexToOrientation(arr, i, 3, 4);
				arr[ch[j]] = (arr[ch[j]] + 1) % 3;
				fcm[i][j] = (byte)Im.orientationToIndex(arr, 3, 4);
			}
		
		// distance table
		for (int i = 0; i < 360; i++)
			fd[i]=-1;
		for (int j = 0; j < 12; j++)
			for(int k = 0; k < 27; k++)
				for(int l = 0; l < 81; l++)
					cd[j][k][l] = -1;
		fd[0] = 0; cd[0][0][0] = 0;
		for(int depth = 0; depth < 5; depth++) {
			//nVisited = 0;
			for (int i = 0; i < 360; i++)
				if (fd[i] == depth)
					for (int m = 0; m < 4; m++) {
						int p = i;
						for(int n = 0; n < 2; n++){
							p = fpm[p][m];
							if (fd[p] == -1) {
								fd[p] = (byte) (depth + 1);
								//nVisited++;
							}
						}
					}
			//System.out.println(depth+" "+nVisited);
		}
		
		for(int depth = 0; depth < 7; depth++) {
			//nVisited = 0;
			for (int j = 0; j < 12; j++)
				for (int k = 0; k < 27; k++)
					for (int l = 0; l< 81; l++)
						if (cd[j][k][l] == depth)
							for (int m = 0; m < 4; m++) {
								int p = j, q = k, r = l;
								for(int n = 0; n < 2; n++){
									p = cpm[p][m];
									q = com[q][m];
									r = fcm[r][m];
									if (cd[p][q][r] == -1) {
										cd[p][q][r] = (byte) (depth + 1);
										//nVisited++;
									}
								}
							}
			//System.out.println(depth+" "+nVisited);
		}
		ini = true;
	}
	private static StringBuffer sb;
	private static String[] turn = {"L", "R", "D", "B"};
	private static String[] suff = {"'", ""};
	private static boolean search(int fp, int cp, int co, int fco, int d, int l) {
		if(d==0)return fd[fp] == 0 && cd[cp][co][fco] == 0;
		if(fd[fp] > d || cd[cp][co][fco] > d)return false;
		for(int k = 0; k < 4; k++)
			if(k != l){
				int p=fp, q=cp, r=co, s=fco;
				for(int m=0; m<2; m++){
					p=fpm[p][k]; q=cpm[q][k]; r=com[r][k]; s=fcm[s][k];
					if(search(p, q, r, s, d-1, k)) {
						sb.insert(0, turn[k]+suff[m]+" ");
						return true;
					}
				}
			}
		return false;
	}
	public static String solve(Random r) {
		init();
		
		int fp = r.nextInt(360);
		int cp, co, fco;
		do{
			cp = r.nextInt(12);
			co = r.nextInt(27);
			fco = r.nextInt(81);
		}
		while (cd[cp][co][fco] < 0);
		
		sb = new StringBuffer();
		for (int depth = 0;!search(fp, cp, co, fco, depth, -1); depth++);
		return sb.toString();
	}

	private static byte[] img=new byte[30];
	private static void initColor(){
		for(int i=0; i<5; i++)
			for(byte j=0; j<6; j++)img[j*5+i]=j;
	}
	private static void cir3(byte[] arr, int a, int b, int c){
		byte temp = arr[a];
		arr[a] = arr[b];
		arr[b] = arr[c];
		arr[c] = temp;
	}
	private static void move(int turn){
		switch(turn){
		case 0:	//L
			cir3(img,19,29,24);
			cir3(img,15,27,21);
			cir3(img,6,0,10);
			cir3(img,25,23,17);
			cir3(img,18,26,20);
			break;
		case 1:	//R
			cir3(img,19,14,9);
			cir3(img,15,10,8);
			cir3(img,25,21,2);
			cir3(img,6,17,12);
			cir3(img,5,16,11);
			break;
		case 2:	//D
			cir3(img,14,24,4);
			cir3(img,12,21,0);
			cir3(img,8,17,27);
			cir3(img,2,10,23);
			cir3(img,13,22,1);
			break;
		case 3:	//B
			cir3(img,29,9,4);
			cir3(img,25,8,0);
			cir3(img,15,12,23);
			cir3(img,6,2,27);
			cir3(img,28,7,3);
			break;
		}
	}
	private static String moveIdx="LRDB";
	public static byte[] image(String scr){
    	initColor();
    	String[] s=scr.split(" ");
    	for(int i=0; i<s.length; i++)
    		if(s[i].length()>0){
    			int mov = moveIdx.indexOf(s[i].charAt(0));
    			move(mov);
    			if(s[i].length()>1)
    				move(mov);
    		}
    	return img;
    }
}
