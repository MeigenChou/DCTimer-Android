package solver;

//import java.io.*;

public class Cube2bl {
	private static byte[][] Cnk=new byte[8][8];
	
	private static short[][] cpm = new short[1680][6];
	private static short[][] com = new short[5670][6];
	private static byte[] cpd = new byte[1680];
	private static byte[] cod = new byte[5670];
	
	private static boolean ini = false;
	private static void init() {
		if(ini) return;
		for(int i=0; 8>i; ++i) {
			Cnk[i][0] = 1;
			for(int j=Cnk[i][i]=1; j<i; ++j) {
				Cnk[i][j] = (byte) (Cnk[i-1][j-1] + Cnk[i-1][j]);
			}
		}
		int d;
		for(int i=0; i<70; i++) {
			for(int j=0; j<81; j++) {
				for(int k=0; k<6; k++) {
					d = getmv(i, j, k);
					com[i*81+j][k] = (short) (((d>>7)/24)*81+(d&127));
					if(j<24) cpm[i*24+j][k] = (short) (d>>7);
				}
			}
		}
		for(int i = 0; i< 1680; i++) cpd[i]=-1;
		cpd[0] = cpd[18] = cpd[16] = cpd[9] = 0;	//1656, 1665, 1672
		//int c;
		for(d=0; d<4; d++) {
			//c=0;
			for (int i=0; i<1680; i++)
				if (cpd[i] == d)
					for (int j=0; j<6; j++)
						for(int y=i, k=0; k<3; k++){
							y = cpm[y][j];
							if (cpd[y] < 0) {
								cpd[y] = (byte) (d + 1);
								//c++;
							}
						}
			//System.out.println(d+" "+c);
		}
		for(int i=0; i<5670; i++) cod[i]=-1;
		cod[0] = 0;
		for(d=0; d<5; d++) {
			//c=0;
			for (int i=0; i<5670; i++)
				if (cod[i] == d)
					for (int j=0; j<6; j++)
						for(int y=i,k=0; k<3; k++) {
							y = com[y][j];
							if (cod[y] < 0) {
								cod[y] = (byte) (d + 1);
								//c++;
							}
						}
			//System.out.println(d+" "+c);
		}
		ini = true;
	}
	
	private static int getmv(int c, int po, int v) {
		int[] n = new int[8], s = new int[4], y = new int[4];
		Cross.idxToPerm(s, po);
		Mapping.idxToOri(y, po, 3, 4);
		int q=4, t;
		for(t=0; 8>t; t++)
			if(c >= Cnk[7-t][q]) {
				c -= Cnk[7-t][q--];
				n[t] = s[q] << 3 | y[3-q];
			}
			else n[t] = -3;
		switch(v) {
		case 0:
			Mapping.cir(n, 0, 3, 2, 1);//U
			break;
		case 1:
			Mapping.cir(n, 4, 5, 6, 7);//D
			break;
		case 2:
			c=n[0]; n[0]=n[4]+1; n[4]=n[7]+2; n[7]=n[3]+1; n[3]=c+2;//L
			break;
		case 3:
			c=n[1]; n[1]=n[2]+2; n[2]=n[6]+1; n[6]=n[5]+2; n[5]=c+1;//R
			break;
		case 4:
			c=n[2]; n[2]=n[3]+2; n[3]=n[7]+1; n[7]=n[6]+2; n[6]=c+1;//F
			break;
		case 5:
			c=n[0]; n[0]=n[1]+2; n[1]=n[5]+1; n[5]=n[4]+2; n[4]=c+1;//B
			break;
		}
		c=po=0; q=4;
		for(t=0; 8>t; t++)
			if(0 <= n[t]) {
				c += Cnk[7-t][q--];
				s[q] = n[t]>>3;
				po += (n[t]&7)%3;
				po *= 3;
			}
		int i = Cross.permToIdx(s);
		return 24*c+i<<7|po/3;
	}
	
	private static String[] turn = {"U","D","L","R","F","B"};
	private static String[] suff = {"","2","'"};
	private static StringBuffer sb;
	private static boolean search(int cp, int co, int depth, int lm) {
		if (depth == 0) return (cp==0||cp==18||cp==16||cp==9) && co==0;
		if (cpd[cp] > depth || cod[co] > depth) return false;
		for (int i = 0; i < 6; i++)
			if(i!=lm){
				int y=cp, s=co;
				for(int j=0;j<3;j++){
					y=cpm[y][i];s=com[s][i];
					if(search(y, s, depth-1, i)){
						sb.insert(0, " "+turn[i]+suff[j]);
						return true;
					}
				}
			}
		return false;
	}
	
	private static String[] moveIdx={"UDLRFB","DURLFB","RLUDFB","LRDUFB","BFLRUD","FBLRDU"};
	private static String[] color={"D: ","U: ","L: ","R: ","F: ","B: "};
	private static String[] rotIdx={"","z2","z'","z","x'","x"};
	private static short[] scp={0, 1679, 665, 1030, 1446, 233}, sco={0, 5589, 2239, 3470, 4912, 781};
	private static byte[][] oriIdx = {{0,1,2,3,4,5},{1,0,3,2,5,4},
		{3,2,0,1,3,3},{2,3,1,0,2,2},{5,5,5,5,0,1},{4,4,4,4,1,0}};
	public static String solve(String s, int face) {
		init();
		String[] scr=s.split(" ");
		int[] cp=new int[6], co=new int[6];
		for(int y=0; y<6; y++) {
			cp[y]=scp[oriIdx[face][y]]; co[y]=sco[oriIdx[face][y]];
			for(int d=0;d<scr.length;d++)
				if(0!=scr[d].length()){
					int o=moveIdx[y].indexOf(scr[d].charAt(0));
					cp[y]=cpm[cp[y]][o]; co[y]=com[co[y]][o];
					if(1<scr[d].length()) {
						co[y]=com[co[y]][o]; cp[y]=cpm[cp[y]][o];
						if(scr[d].charAt(1)=='\''){
							co[y]=com[co[y]][o]; cp[y]=cpm[cp[y]][o];
						}
					}
				}
		}
		sb = new StringBuffer();
		for(int d=0; ;d++)
			for(int idx=0; idx<6; idx++)
				if(search(cp[idx],co[idx],d,-1))
					return("\n"+color[face]+rotIdx[idx]+sb.toString());
	}
	
	public static String cube2layer(String scr, int face) {
		if(face>0 && face<7)return solve(scr, face-1);
		StringBuffer s = new StringBuffer();
		for(int i=0; i<6; i++)s.append(solve(scr, i));
		return s.toString();
	}
}
