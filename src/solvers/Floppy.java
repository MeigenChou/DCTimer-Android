package solvers;

import java.util.Random;

public class Floppy {
	private static byte[][] distance = new byte[24][16];
	private static String[] turn={"U", "R", "D", "L"};

	public static boolean ini=false;
	public static void init(){
		if(ini)return;
		for (int i = 0; i < 24; i++)
			for (int j = 0; j < 16; j++)
				distance[i][j] = -1;
		distance[0][0] = 0;
		int nVisited = 1;
		int depth = 0;
		int[] cp = new int[4];
		int[] eo = new int[4];
		while (nVisited > 0) {
			nVisited = 0;
			for (int i = 0; i < 24; i++)
				for (int j = 0; j < 16; j++)
					if (distance[i][j] == depth)
						for(int k=0; k<4; k++){
							Im.indexToPermutation(cp, i, 4);
							Im.indexToOrientation(eo, j, 2, 4);
							switch(k){
							case 0:Tl.cir(cp, 0, 1);break;	//U2
							case 1:Tl.cir(cp, 1, 2);break;	//R2
							case 2:Tl.cir(cp, 2, 3);break;	//D2
							case 3:Tl.cir(cp, 0, 3);break;	//L2
							}
							eo[k]=1-eo[k];
							int cpi=Im.permutationToIndex(cp, 4);
							int eoi=Im.orientationToIndex(eo, 2, 4);
							if (distance[cpi][eoi] == -1) {
								distance[cpi][eoi] = (byte) (depth + 1);
								nVisited++;
							}
						}
			depth++;
			//System.out.println(depth+" "+nVisited);
		}
		ini=true;
	}
	
	public static String solve(Random r) {
		//init();
		for (;;) {
			int cpi = r.nextInt(24);
			int eoi = r.nextInt(16);
			if (distance[cpi][eoi] > 0) {
				StringBuffer sb=new StringBuffer();
				while(distance[cpi][eoi] != 0){
					int[] cp = new int[4];
					int[] eo = new int[4];
					for (int i=0; i<4; i++) {
						Im.indexToPermutation(cp, cpi, 4);
						Im.indexToOrientation(eo, eoi, 2, 4);
						switch(i){
						case 0:Tl.cir(cp, 0, 1);break;
						case 1:Tl.cir(cp, 1, 2);break;
						case 2:Tl.cir(cp, 2, 3);break;
						case 3:Tl.cir(cp, 0, 3);break;
						}
						eo[i]=1-eo[i];
						int nextCpi = Im.permutationToIndex(cp, 4);
						int nextEoi = Im.orientationToIndex(eo, 2, 4);
						if (distance[nextCpi][nextEoi] == distance[cpi][eoi] - 1) {
							sb.insert(0, turn[i]+" ");
							cpi = nextCpi;
							eoi = nextEoi;
							break;
						}
					}
				}
				return sb.toString();
			}
		}
	}
	
	private static byte[] img=new byte[30];
	private static void initColor(){
		img=new byte[]{
			  3,3,3,
			5,4,4,4,2,1,1,1,
			5,4,4,4,2,1,1,1,
			5,4,4,4,2,1,1,1,
			  0,0,0
		};
	}
	
	private static void move(int turn){
		switch(turn){
		case 0:	//U
			Tl.cir2(img, 0, 2, 3, 7);
			Tl.cir2(img, 4, 8, 6, 10);
			Tl.cir(img, 5, 9); break;
		case 1:	//R
			Tl.cir2(img, 7, 23, 2, 29);
			Tl.cir2(img, 6, 24, 22, 8);
			Tl.cir(img, 14, 16); break;
		case 2:	//D
			Tl.cir2(img, 27, 29, 19, 23);
			Tl.cir2(img, 20, 24, 22, 26);
			Tl.cir(img, 21, 25); break;
		case 3:	//L
			Tl.cir2(img, 3, 19, 0, 27);
			Tl.cir2(img, 4, 26, 20, 10);
			Tl.cir(img, 12, 18); break;
		}
	}
	private static String moveIdx="URDL";
	public static byte[] image(String scr){
		initColor();
		String[] s=scr.split(" ");
		for(int i=0; i<s.length; i++){
			if(s[i].length()>0)
				move(moveIdx.indexOf(s[i].charAt(0)));
		}
		return img;
	}
}
