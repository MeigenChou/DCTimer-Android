package solver;

public class EOline {
	private static short[][] eom = new short[2048][6];
	private static short[][] epm = new short[132][6];
	private static byte[] eod = new byte[2048];
	private static byte[] epd = new byte[132];
	
	private static boolean ini=false;
	public static void init() {
		if(ini) return;
		int[] arr=new int[12];
		for(int i=0; i<2048; i++){
			for(int j=0; j<6; j++) {
				Mapping.idxToZori(arr, i, 2, 12);
				switch(j){
				case 0: Mapping.cir(arr, 4, 7, 6, 5); break;
				case 1: Mapping.cir(arr, 8, 9, 10, 11); break;
				case 2: Mapping.cir(arr, 7, 3, 11, 2); break;
				case 3: Mapping.cir(arr, 5, 1, 9, 0); break;
				case 4: Mapping.cir(arr, 6, 2, 10, 1);
					arr[6]^=1; arr[2]^=1; arr[10]^=1; arr[1]^=1; break;
				case 5: Mapping.cir(arr, 4, 0, 8, 3);
					arr[4]^=1; arr[0]^=1; arr[8]^=1; arr[3]^=1; break;
				}
				eom[i][j] = (short) Mapping.zoriToIdx(arr, 2, 12);
			}
		}
		for(int i=0; i<66; i++){
			for(int j=0; j<2; j++){
				for(int k=0; k<6; k++){
				    epm[i*2+j][k] = (short) getEpm(i, j, k);
				}
			}
			
		}
		for(int i=1; i<2048; i++) eod[i] = -1;
		eod[0] = 0;
		int d = 0;
		//int n = 1;
		for(d=0; d<7; d++){
			//n=0;
			for(int i=0; i<2048; i++)
				if(eod[i] == d)
					for(int j=0; j<6; j++)
						for(int y=i,m=0; m<3; m++){
							y=eom[y][j];
							if(eod[y]==-1){
								eod[y]=(byte) (d+1);
								//n++;
							}
						}
			//System.out.println(d+" "+n);
		}
		
		for(int i=0; i<132; i++) epd[i] = -1;
		epd[106] = 0;
		for(d=0; d<4; d++){
			//n=0;
			for(int i=0; i<132; i++)
				if(epd[i] == d)
					for(int j=0; j<6; j++){
						int y=i;
						for(int m=0; m<3; m++){
							y=epm[y][j];
							if(epd[y]==-1){
								epd[y]=(byte) (d+1);
								//n++;
							}
						}
					}
			//System.out.println(d+" "+n);
		}
		ini=true;
	}
	
	private static int getEpm(int eci, int epi, int k){
		boolean[] combination = new boolean[12];
		Mapping.idxToComb(combination, eci, 2, 12);
		int[] permutation = new int[2];
		Mapping.idxToPerm(permutation, epi, 2);
		byte[] selectedEdges = {8, 10};
		int next = 0;
		int[] ep = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		for (int i = 0; i < 12; i++)
			if (combination[i]) ep[i] = selectedEdges[permutation[next++]];
		switch(k){
		case 0: Mapping.cir(ep, 4, 7, 6, 5); break;
		case 1: Mapping.cir(ep, 8, 9, 10, 11); break;
		case 2: Mapping.cir(ep, 7, 3, 11, 2); break;
		case 3: Mapping.cir(ep, 5, 1, 9, 0); break;
		case 4: Mapping.cir(ep, 6, 2, 10, 1); break;
		case 5: Mapping.cir(ep, 4, 0, 8, 3); break;
		}
		byte[] edgesMapping = {0, 1, 2, 3};
		boolean[] ec = new boolean[12];
		for (int i = 0; i < 12; i++)
			ec[i] = ep[i] > 0;
		eci = Mapping.combToIdx(ec, 2);
		int[] edgesPermutation = new int[2];
		next = 0;
		for (int i = 0; i < 12; i++)
			if (ec[i]) edgesPermutation[next++] = ep[i] > -1 ? edgesMapping[ep[i]-8] : -1;
		epi = Mapping.permToIdx(edgesPermutation, 2);
		return eci * 2 + epi;
	}
	
	private static String[] sideStr = {"D(LR)", "D(FB)", "U(LR)", "U(FB)",
		"L(UD)", "L(FB)", "R(UD)", "R(FB)", "F(LR)", "F(UD)", "B(LR)", "B(UD)"};
	private static String[] moveIdx = {"UDLRFB", "UDFBRL", "DURLFB", "DUFBLR",
		"RLUDFB", "RLFBDU", "LRDUFB", "LRFBUD", "BFLRUD", "BFUDRL", "FBLRDU", "FBDURL"};
	private static String[] rotIdx = {"", "y", "z2", "z2 y",
		"z'", "z' y", "z", "z y", "x'", "x' y", "x", "x y"};
	private static String[] turn={"U", "D", "L", "R", "F", "B"};
	private static String[] suff={"", "2", "'"};
	private static StringBuffer sb;
	
	private static boolean search(int eo, int ep, int depth, int l) {
		if(depth==0) return eo==0 && ep==106;
		if(eod[eo]>depth || epd[ep]>depth) return false;
		for (int i = 0; i < 6; i++)
			if (i != l) {
				int w = eo, y = ep;
				for (int j = 0; j < 3; j++) {
					y = epm[y][i];
					w = eom[w][i];
					if (search(w, y, depth - 1, i)) {
						sb.insert(0, " " + turn[i] + suff[j]);
						return true;
					}
				}
			}
		return false;
	}
	
	private static String solve(String s, int face) {
		String[] scr = s.split(" ");
		int ep = 106, eo = 0;
		for (int d = 0; d < scr.length; d++) {
			if (0 != scr[d].length()) {
				int o = moveIdx[face].indexOf(scr[d].charAt(0));
				ep = epm[ep][o]; eo = eom[eo][o];
				if (1 < scr[d].length()) {
					if (scr[d].charAt(1) == '2') {
						eo = eom[eo][o]; ep = epm[ep][o];
					} else {
						eo = eom[eom[eo][o]][o]; ep = epm[epm[ep][o]][o];
					}
				}
			}
		}
		sb = new StringBuffer();
		for (int d = 0; !search(eo, ep, d, face); d++);
		return "\n" + sideStr[face] + ": " + rotIdx[face] + sb.toString();
	}
	
	public static String eoLine(String scr, int face){
		init();
		if(face == 6) {
			StringBuffer s = new StringBuffer();
			for(int i=0; i<12; i++) s.append(solve(scr, i));
			return s.toString();
		}
		return solve(scr, face<<1) + solve(scr, face<<1|1);
	}
}
