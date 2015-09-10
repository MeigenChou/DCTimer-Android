package solver;

public class PetrusxRoux {
	private static short[][] epm = new short[1320][6];
	private static short[][] eom = new short[1760][6];
	private static byte[][] com = new byte[24][6];
	
	private static byte[][] rcpm = new byte[56][6];
	private static short[][] rcom = new short[252][6];
	
	private static byte[] epd = new byte[1320];
	private static byte[] eod = new byte[1760];

	private static byte[][] ed = new byte[220][48];
	private static byte[][] cd = new byte[28][18];
	
	private static int edgesmv(int c,int p, int o,int f){
		int[] n=new int[12], s=new int[3];
		int q,t,v;
		for(q=1;q<=3;q++){
			t=p%q;
			p=p/q;
			for(v=q-2;v>=t;v--)
				s[v+1]=s[v];
			s[t]=3-q;
		}
		q=3;
		for(t=0;t<12;t++)
			if(c>=Mapping.Cnk[11-t][q]){
				c-=Mapping.Cnk[11-t][q--];
				n[t]=s[q]<<1|o&1;
				o>>=1;
			}
			else n[t]=-1;
		switch(f){
		case 0:
			Cross.circle(n,0,1,2,3,0);break;
		case 1:
			Cross.circle(n,11,10,9,8,0);break;
		case 2:
			Cross.circle(n,1,4,9,5,0);break;
		case 3:
			Cross.circle(n,3,6,11,7,0);break;
		case 4:
			Cross.circle(n,0,7,8,4,1);break;
		case 5:
			Cross.circle(n,2,5,10,6,1);break;
		}
		c=0;q=3;
		for(t=0;12>t;t++)
			if(0<=n[t]){
				c+=Mapping.Cnk[11-t][q--];
				s[q]=n[t]>>1;
				o|=(n[t]&1)<<2-q;
			}
		p=0;
		for(q=0;q<3;q++){
			for(v=t=0;3>v&&!(s[v]==q);v++)if(s[v]>q)t++;
			p=p*(3-q)+t;
		}
		return 6*c+p<<3|o;
	}
	private static void cir(int[] d,int[] s,int f,int h,int l,int n){
		int q=d[f]; d[f]=d[h]; d[h]=d[l]; d[l]=d[n]; d[n]=q;
		int t=s[f]; s[f]=s[h]; s[h]=s[l]; s[l]=s[n]; s[n]=t;
	}
	private static int cornersmv(int c,int p,int o,int f){
		int[] n=new int[8], s=new int[4], u=new int[8];
		int q,t;
		s[0]=p%2; s[1]=1-s[0]; s[2]=o/3; s[3]=o%3;
		q=2;
		for(t=7;t>=0;t--)
			if(c>=Mapping.Cnk[t][q]){
				c-=Mapping.Cnk[t][q--];
				n[t]=s[q];
				u[t]=s[q+2];
			}
			else n[t]=-1;
		switch(f){
		case 0:
			cir(n,u,0,3,2,1);break;
		case 1:
			cir(n,u,4,5,6,7);break;
		case 2:
			cir(n,u,0,4,7,3);
			u[0]+=2;u[3]++;u[4]++;u[7]+=2;
			break;
		case 3:
			cir(n,u,1,2,6,5);
			u[1]++;u[2]+=2;u[5]+=2;u[6]++;
			break;
		case 4:
			cir(n,u,2,3,7,6);
			u[2]++;u[3]+=2;u[6]+=2;u[7]++;
			break;
		case 5:
			cir(n,u,0,1,5,4);
			u[0]++;u[1]+=2;u[4]+=2;u[5]++;
			break;
		}
		c=0;q=2;
		for(t=7;t>=0;t--)
			if(0<=n[t]){
				c+=Mapping.Cnk[t][q--];
				s[q]=n[t];
				s[q+2]=u[t]%3;
			}
		return (c*2+s[0])*9+s[2]*3+s[3];
	}
	
	private static boolean ini=false;
	public static void init0() {
		if(ini)return;
		int i,j;
		for(i=0; i<220; i++){
			for(j=0; j<8; j++){
				for(int k=0; k<6; k++){
					int d=edgesmv(i, j, j, k);
					if(j<6)epm[i*6+j][k]=(short) (d>>3);
					eom[i*8+j][k]=(short) (((d>>3)/6)<<3|d&7);
				}
			}
		}
		ini=true;
	}
	private static boolean inip=false;
	private static void initp(){
		if(inip)return;
		init0();
		int i,j;
		byte[][] p = {
				{1,0,3,0,0,4},{2,1,1,5,1,0},{3,2,2,1,6,2},{0,3,7,3,2,3},
				{4,7,0,4,4,5},{5,4,5,6,5,1},{6,5,6,2,7,6},{7,6,4,7,3,7}
		};
		byte[][] o = {
				{0,0,1,0,0,2},{0,0,0,2,0,1},{0,0,0,1,2,0},{0,0,2,0,1,0},
				{0,0,2,0,0,1},{0,0,0,1,0,2},{0,0,0,2,1,0},{0,0,1,0,2,0}
		};
		for(i=0; i<8; i++)
			for(j=0; j<3; j++)
				for(int k=0; k<6; k++)
					com[i*3+j][k] = (byte) (p[i][k]*3+(o[i][k]+j)%3);
		for(i=0; i<1320; i++) epd[i]=-1;
		epd[132]=0;
		for(int d=0; d<5; d++) {
			//c=0;
			for(i=0; i<1320; i++)
				if (epd[i] == d)
					for (j = 0; j < 6; j++)
						for(int y = i, k = 0; k < 3; k++) {
							y = epm[y][j];
							if (epd[y] < 0) {
								epd[y] = (byte) (d + 1);
								//c++;
							}
						}
			//System.out.println(d+" "+c);
		}
		for(i=0; i<1760; i++) eod[i]=-1;
		eod[176]=0;
		for(int d=0; d<5; d++) {
			//c=0;
			for(i=0; i<1760; i++)
				if (eod[i] == d)
					for (j=0; j<6; j++)
						for(int y=i, k=0; k<3; k++) {
							y = eom[y][j];
							if (eod[y] < 0) {
								eod[y] = (byte) (d + 1);
								//c++;
							}
						}
			//System.out.println(d+" "+c);
		}
		inip=true;
	}
	
	private static boolean inir=false;
	private static void initr(){
		if(inir)return;
		init0();
		int i,j;
		for(i=0; i<28; i++)
			for (j = 0; j < 9; j++)
				for (int k = 0; k < 6; k++) {
					int d = cornersmv(i, j, j, k);
					if(j < 2)rcpm[(i<<1)+j][k] = (byte) (d/9);
					rcom[i*9+j][k] = (short)(d/18*9+d%9);
				}
		for(i=0; i<220; i++)
			for(j=0; j<48; j++)
				ed[i][j]=-1;
		ed[52][0]=0;
		for(int d=0; d<7; d++) {
			//c=0;
			for(i=0; i<220; i++)
				for(j=0; j<48; j++)
					if(ed[i][j] == d)
						for(int l=0; l<6; l++){
							int x=i, y=j;
							for(int m=0; m<3; m++){
								y=epm[x*6+(y>>3)][l]%6<<3|eom[x<<3|y&7][l]&7;
								x=epm[x*6+(y>>3)][l]/6;
								if(ed[x][y]<0){
									ed[x][y]=(byte) (d+1);
									//c++;
								}
							}
						}
			//System.out.println(d+" "+c);
		}
		for(i=0; i<28; i++)
			for(j=0; j<18; j++)
				cd[i][j]=-1;
		cd[25][0]=0;
		for(int d=0; d<4; d++){
			//c=0;
			for(i=0; i<28; i++)
				for(j=0; j<18; j++)
					if(cd[i][j] == d)
						for(int l=0; l<6; l++){
							int x=i, y=j;
							for(int m=0; m<3; m++){
								y=(rcpm[(x<<1)+y/9][l]&1)*9+rcom[x*9+y%9][l]%9;
								x=rcpm[(x<<1)+y/9][l]>>1;
								if(cd[x][y]<0){
									cd[x][y]=(byte) (d+1);
									//c++;
								}
							}
						}
			//System.out.println(d+" "+c);
		}
		inir=true;
	}
	
	private static String[][] turn={{"D","U","L","R","B","F"},
		{"F","B","L","R","D","U"},{"D","U","F","B","L","R"},
		{"D","U","R","L","F","B"},{"U","D","F","B","R","L"},
		{"U","D","L","R","F","B"},{"U","D","R","L","B","F"},
		{"U","D","B","F","L","R"}};
	private static String[] suff={"","2","'"};
	private static StringBuffer sb=new StringBuffer();
	private static boolean idaPetrus(int co, int ep, int eo, int depth, int lm, int face) {
		if (depth == 0) return co==12 && ep==132 && eo==176;
		if (epd[ep] > depth || eod[eo] > depth) return false;
		for (int i = 0; i < 6; i++)
			if(i!=lm){
				int w=co, y=ep, s=eo;
				for(int j=0;j<3;j++){
					w=com[w][i];
					y=epm[y][i];s=eom[s][i];
					if(idaPetrus(w, y, s, depth-1, i, face)){
						sb.insert(0, " "+turn[face][i]+suff[j]);
						return true;
					}
				}
			}
		return false;
	}
	private static String[] moveIdx={"DULRBF","FBLRDU","DUFBLR","DURLFB",
		"UDFBRL","UDLRFB","UDRLBF","UDBFLR"};
	private static String[] colorp={"ULF:","ULB:","URF:","URB:","DLF:","DLB:","DRF:","DRB:"};
	private static String solve(String s, int face){
		String[] scr=s.split(" ");
		int co=12, ep=132, eo=176;
		for(int d=0;d<scr.length;d++)
			if(0!=scr[d].length()){
				int o=moveIdx[face].indexOf(scr[d].charAt(0));
				co=com[co][o];
				ep=epm[ep][o];eo=eom[eo][o];
				if(1<scr[d].length()) {
					if(scr[d].charAt(1)=='2'){
						co=com[co][o];eo=eom[eo][o];ep=epm[ep][o];
					} else {
						co=com[com[co][o]][o];eo=eom[eom[eo][o]][o];ep=epm[epm[ep][o]][o];
					}
				}
			}
		sb=new StringBuffer();
		for(int d=0;!idaPetrus(co,ep,eo,d,-1,face);d++);
		return "\n"+colorp[face]+sb;
	}
	private static byte[][] faceIdx={{4,5,6,7},{0,1,2,3},{0,1,4,5},
		{2,3,6,7},{0,2,4,6},{1,3,5,7}};
	public static String petrus(String scr, int face) {
		initp();
		StringBuffer s = new StringBuffer();
		if (face < 6) for (int i = 0; i < 4; i++) s.append(solve(scr, faceIdx[face][i]));
		else for (int i = 0; i < 8; i++) s.append(solve(scr, i));
		return s.toString();
	}
	
	private static boolean idaRoux(int cp, int co, int ep, int eo, int depth, int lm) {
		if (depth == 0) return (cp==50 && co==225 && ep==312 && eo==416);
		if (ed[ep/6][(ep%6)<<3|eo&7] > depth || cd[cp>>1][(cp&1)*9+co%9] > depth) return false;
		//if(pd[ep][cp]>depth || od[eo][co]>depth)return false;
		for (int i = 0; i < 6; i++)
			if(i!=lm)
				for(int d=cp, w=co, y=ep, s=eo, j=0;j<3;j++){
					d=rcpm[d][i];w=rcom[w][i];
					y=epm[y][i];s=eom[s][i];
					if(idaRoux(d, w, y, s, depth-1, i)){
						sb.insert(0, " "+turn[5][i]+suff[j]);
						return true;
					}
				}
		return false;
	}
	
	private static String[][] moveRIdx={
		{"LRDUFB","RLDUBF","BFDULR","FBDURL"},
		{"RLUDFB","LRUDBF","BFUDRL","FBUDLR"},
		{"UDLRFB","DULRBF","BFLRUD","FBLRDU"},
		{"DURLFB","UDRLBF","BFRLDU","FBRLUD"},
		{"UDFBRL","DUFBLR","LRFBUD","RLFBDU"},
		{"UDBFLR","DUBFRL","RLBFUD","LRBFDU"}};
	private static String[][] colorr={
		{"DL","DF","DR","DB"},{"UR","UF","UL","UB"},
		{"LU","LF","LD","LB"},{"RD","RF","RU","RB"},
		{"FU","FR","FD","FL"},{"BU","BL","BD","BR"}};
	private static String[] rotIdx={"z", "z'", "", "z2", "y", "y'"};
	private static String[] rotIdx2={"", " x2", " x'", " x"};
	private static int[] scp={50, 7, 49, 12}, sco={225, 27, 221, 61};
	private static int[] sep={312, 887, 860, 825}, seo={416, 1176, 1144, 1096};
	private static int[][] oriIdx={{1,0,2,3},{2,3,0,1},{0,1,3,2},{3,2,1,0}};
	public static String solveRoux(String s, int face, int side){
		String[] scr=s.split(" ");
		int[] cp=new int[4], co=new int[4], ep=new int[4], eo=new int[4], o=new int[4];
		for(int i=0; i<4; i++){
			cp[i]=scp[oriIdx[side][i]];
			co[i]=sco[oriIdx[side][i]];
			ep[i]=sep[oriIdx[side][i]];
			eo[i]=seo[oriIdx[side][i]];
		}
		for(int d=0;d<scr.length;d++){
			if(0!=scr[d].length()){
				for(int i=0; i<4; i++)o[i]=moveRIdx[face][i].indexOf(scr[d].charAt(0));
				for(int idx=0; idx<4; idx++) {
					cp[idx]=rcpm[cp[idx]][o[idx]];co[idx]=rcom[co[idx]][o[idx]];
					ep[idx]=epm[ep[idx]][o[idx]];eo[idx]=eom[eo[idx]][o[idx]];
				}
				if(1<scr[d].length())
					if(scr[d].charAt(1)=='2')
						for(int idx=0; idx<4; idx++) {
							cp[idx]=rcpm[cp[idx]][o[idx]];co[idx]=rcom[co[idx]][o[idx]];
							ep[idx]=epm[ep[idx]][o[idx]];eo[idx]=eom[eo[idx]][o[idx]];
						}
					else
						for(int idx=0; idx<4; idx++) {
							cp[idx]=rcpm[rcpm[cp[idx]][o[idx]]][o[idx]];co[idx]=rcom[rcom[co[idx]][o[idx]]][o[idx]];
							eo[idx]=eom[eom[eo[idx]][o[idx]]][o[idx]];ep[idx]=epm[epm[ep[idx]][o[idx]]][o[idx]];
						}
			}
		}
		sb=new StringBuffer();
		for(int d=0; ; d++){
			//System.out.print(d+" ");
			for(int idx=0; idx<4; idx++)
				if(idaRoux(cp[idx], co[idx], ep[idx], eo[idx], d, -1))
					return "\n"+colorr[face][side]+": "+rotIdx[face]+rotIdx2[idx]+sb;
		}
	}
	public static String roux(String scr, int face){
		initr();
		StringBuffer s=new StringBuffer();
		if(face<6) for(int i=0; i<4; i++)s.append(solveRoux(scr, face, i));
		else for(int i=0; i<6; i++)s.append(solveRoux(scr, i, 0));
		return s.toString();
	}
}
