package solver;

import java.util.Random;

public class Gear {
	private static byte[][] cpm = new byte[24][3], epm = new byte[24][3], eom = new byte[27][3];
	private static byte[][] pd = new byte[3][576];
	private static String[] turn = {"U","R","F"};
	private static String[] suff = {"'","2'","3'","4'","5'","6","5","4","3","2",""};
	private static StringBuffer sb;
	private static boolean ini = false;
	
	private static void init() {
		if(ini)return;
		int[] arr = new int[4];
		for(int i = 0; i < 24; i++){
			for(int j = 0; j < 3; j++){
				Mapping.idxToPerm(arr, i, 4);
				Mapping.cir(arr, 3, j);
				cpm[i][j] = (byte) Mapping.permToIdx(arr, 4);
			}
		}
		for(int i = 0; i < 24; i++){
			for(int j = 0; j < 3; j++){
				Mapping.idxToPerm(arr, i, 4);
				switch(j){
				case 0: Mapping.cir(arr, 0, 3, 2, 1); break;
				case 1: Mapping.cir(arr, 0, 1); break;
				case 2: Mapping.cir(arr, 1, 2); break;
				}
				epm[i][j] = (byte) Mapping.permToIdx(arr, 4);
			}
		}
		arr = new int[3];
		for(int i = 0; i < 27; i++){
			for(int j = 0; j < 3; j++){
				Mapping.idxToOri(arr, i, 3, 3);
				arr[j] = (arr[j] + 1) % 3;
				eom[i][j] = (byte) Mapping.oriToIdx(arr, 3, 3);
			}
		}
		//int n;
		for (int i = 0; i < 3; i++) {
			for(int j = 1; j < 576; j++)pd[i][j] = -1;
			pd[i][0] = 0;
			for(int d = 0; d < 5; d++) {
				//n = 0;
				for(int j = 0; j < 576; j++)
					if(pd[i][j] == d)
						for (int k = 0; k < 3; k++) {
							int p = j;
							for (int m = 0; m < 11; m++) {
								int e = p % 24;
								p = p / 24;
								p = cpm[p][k];
								e = epm[e][(k + i) % 3];
								p = 24 * p + e;
								if(pd[i][p] == -1){
									pd[i][p] = (byte) (d + 1);
									//n++;
								}
							}
						}
				//System.out.println(d+" "+n);
			}
		}
		ini = true;
	}
	private static boolean search(int cp, int ep1, int ep2, int ep3, int eo, int d, int l) {
		if (d == 0) return cp == 0 && ep1 == 0 && ep2 == 0 && ep3 == 0 && eo == 0;
		if (Math.max(Math.max(pd[0][24 * cp + ep1], pd[1][24 * cp + ep2]), pd[2][24 * cp + ep3]) > d) return false;
		for (int n = 0; n < 3; n++)
			if (n != l) {
				int cn = cp, e1n = ep1, e2n = ep2, e3n = ep3, en = eo;
				for (int m = 0; 11 > m; m++){
					cn = cpm[cn][n]; e1n = epm[e1n][n]; e2n = epm[e2n][(n + 1) % 3];
					e3n = epm[e3n][(n + 2) % 3]; en = eom[en][n];
					if (search(cn, e1n, e2n, e3n, en, d - 1, n)){
						sb.insert(0, turn[n] + suff[m] + " ");
						return true;
					}
				}
			}
		return false;
	}
	
	public static String solve(Random r) {
		init();
		int cp = r.nextInt(24);
		int[] ep = new int[3];
		for(int i = 0; i < 3; i++){
			do ep[i] = r.nextInt(24);
			while (pd[i][24 * cp + ep[i]] < 0);
		}
		int eo = r.nextInt(27);
		sb = new StringBuffer();
		for (int d = 3;!search(cp, ep[0], ep[1], ep[2], eo, d, -1); d++);
		return sb.toString();
	}
}
