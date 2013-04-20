package scramblers;

import com.dctimer.Mi;

public class OtherScr {
	public static String rndEl(String[] x){
		return x[(int)(Math.random()*x.length)];
	}

	public static String edgescramble(String start, String[] end, String[] moves) {
		int u=0,d=0;
		int[] movemis=new int[moves.length];
		String[][] triggers={{"R","R'"},{"R'","R"},{"L","L'"},{"L'","L"},{"F'","F"},{"F","F'"},{"B","B'"},{"B'","B"}};
		String[] ud={"U","D"},cubesuff={"","2","'"};
		String ss = start;
		String v="";
		// initialize move misalignments
		for (int i=0; i<moves.length; i++) {
			movemis[i] = 0;
		}

		for (int i=0; i<Mi.scrLen; i++) {
			// apply random moves
			boolean done = false;
			while (!done) {
				v = "";
				for (int j=0; j<moves.length; j++) {
					int x = (int)(Math.random()*4);
					movemis[j] += x;
					if (x!=0) {
						done = true;
						v += " " + moves[j] + cubesuff[x-1];
					}
				}
			}
			ss += v;

			// apply random trigger, update U/D
			int trigger = (int)(Math.random()*8);
			int layer = (int)(Math.random()*2);
			int turn = (int)(Math.random()*3);
			ss += " " + triggers[trigger][0] + " " + ud[layer] + cubesuff[turn] + " " + triggers[trigger][1];
			if (layer==0) {u += turn+1;}
			if (layer==1) {d += turn+1;}
		}

		// fix everything
		for (int i=0; i<moves.length; i++) {
			int x = 4-(movemis[i]%4);
			if (x<4) {
				ss += " " + moves[i] + cubesuff[x-1];
			}
		}
		u = 4-(u%4); d = 4-(d%4);
		if (u<4) {
			ss += " U" + cubesuff[u-1];
		}
		if (d<4) {
			ss += " D" + cubesuff[d-1];
		}
		ss += " " + rndEl(end);
		return ss;
	}
	public static String megascramble(String[][] turns, String[] suffixes){
		int[] donemoves=new int[turns[0].length];
		int lastaxis;
		int j,k;
		String s="";

		lastaxis=-1;
		for(j=0;j<Mi.scrLen;j++){
			int done=0;
			do{
				int first=(int)(Math.random()*turns.length);
				int second=(int)(Math.random()*turns[first].length);
				if(first!=lastaxis||donemoves[second]==0){
					//donemoves=new int[turns[first].length];
					if(first==lastaxis){
						donemoves[second]=1;
						s+=turns[first][second]+rndEl(suffixes)+" ";
					}else{
						for(k=0;k<turns[first].length;k++){donemoves[k]=0;}
						lastaxis=first;
						donemoves[second]=1;
						s+=turns[first][second]+rndEl(suffixes)+" ";
					}
					done=1;
				}
			}while(done==0);
		}
		return s;
	}
	public static String megascramble(String[][] turns, String[] suffixes, int len){
		int l = Mi.scrLen;
		Mi.scrLen = len;
		String s = megascramble(turns, suffixes);
		Mi.scrLen = l;
		return s;
	}
	public static String megascramble(String[][][] turns, String[] suffixes){
		int[] donemoves=new int[turns[0].length];
		int lastaxis;
		int j,k;

		String s="";
		lastaxis=-1;
		for(j=0;j<Mi.scrLen;j++){
			int done=0;
			do{
				int first=(int)(Math.random()*turns.length);
				int second=(int)(Math.random()*turns[first].length);
				if (first!=lastaxis) {
					//donemoves=new int[turns[first].length];
					for(k=0;k<turns[first].length;k++){donemoves[k]=0;}
					lastaxis=first;
				}
				if (donemoves[second]==0) {
					donemoves[second]=1;
					//if(isArray(turns[first][second])){
					s+=rndEl(turns[first][second])+rndEl(suffixes)+" ";
					//}else{
					// s+=turns[first][second]+rndEl(suffixes)+" ";
					//}
					done=1;
				}
			}while(done==0);
		}
		return s;
	}
	public static String helicubescramble(){
		int j,k;
		String[] faces = {"UF", "UR", "UB", "UL", "FR", "BR", "BL", "FL", "DF", "DR", "DB", "DL"};
		int[] used = new int[12];
		// adjacency table
		String[] adj = {"010110010000","101011000000","010101100000","101000110000","110000001100","011000000110","001100000011","100100001001","000010010101","000011001010","000001100101","000000111010"};
		// now generate the scramble(s)

		String s="";
		for(j=0;j<12;j++){
			used[j] = 0;
		}
		for(j=0;j<Mi.scrLen;j++){
			boolean done = false;
			do {
				int face = (int)(Math.random()*12);
				if (used[face] == 0) {
					s += faces[face] + " ";
					for(k=0;k<12;k++){
						if (adj[face].charAt(k)=='1') {used[k] = 0;}
					}
					used[face] = 1;
					done = true;
				}
			} while (!done);
		}
		return s;
	}

//	public static String gigascramble(){
//		int i,j;
//		String s="";
//		String[] El={"+","++","-","--"};
//		String[] minxsuff={"","2","'","2'"};
//		for(i=0;i<Math.ceil(30);i++){
//			for(j=0;j<10;j++){
//				s+=(j%2==0?(Math.random()>0.5?"R":"r"):(Math.random()>0.5?"D":"d"))+rndEl(El)+" ";
//			}
//			s+="y"+rndEl(minxsuff)+" ";
//		}
//		return s;
//	}
	public static String ssq1t_scramble(){
		byte[][][] seq=new byte[2][Mi.scrLen*2][2];
		int i;
		sq1_getseq(seq, 0);
		byte[][] s=seq[0],t=seq[1];
		StringBuffer u=new StringBuffer();
		//int[][] temp={{0,0}};
		if (s[0][0]==7) {
			for(i=0;i<Mi.scrLen;i++) {
				s[i*2][0]=s[i*2+1][0];
				s[i*2][1]=s[i*2+1][1];
			}
		}
		if (t[0][0]==7) {
			for(i=0;i<Mi.scrLen;i++) {
				t[i*2][0]=t[i*2+1][0];
				t[i*2][1]=t[i*2+1][1];
			}
		}
		for(i=0;i<Mi.scrLen;i++){
			u.append("(" + s[2*i][0] + "," + t[2*i][0] + "," + t[2*i][1] + "," + s[2*i][1] + ") / ");
		}
		return u.toString();
	}
	private static void sq1_getseq(byte[][][] seq, int type){
		for(int n=0; n<seq.length; n++){
			byte[] p = {1,0,0,1,0,0,1,0,0,1,0,0,0,1,0,0,1,0,0,1,0,0,1,0};
			int cnt = 0;
			int seql=0;
			while (cnt < Mi.scrLen) {
				byte x = (byte)(Math.random() * 12 - 5);
				byte y = (type==2) ? 0 : (byte)(Math.random() * 12 - 5);
				int size = (x==0?0:1) + (y==0?0:1);
				if ((cnt + size <= Mi.scrLen || type != 1) && (size > 0 || cnt == 0)) {
					if (sq1_domove(p, x, y)) {
						if (type == 1) cnt += size;
						if (size > 0) seq[n][seql++] = new byte[]{x,y};
						if (cnt < Mi.scrLen || type != 1) {
							cnt++;
							seq[n][seql++] = new byte[]{7,0};
							sq1_domove(p, 7, 0);
						}
					}
				}
			}
		}
	}
	private static boolean sq1_domove(byte[] p, int x, int y){
		int i, temp;
		byte[] px, py;
		if (x == 7) {
			for (i=0; i<6; i++) {
				temp = p[i+6];
				p[i+6] = p[i+12];
				p[i+12] = (byte)temp;
			}
			return true;
		} else {
			if (p[(17-x)%12]!=0 || p[(11-x)%12]!=0 || p[12+(17-y)%12]!=0 || p[12+(11-y)%12]!=0) {
				return false;
			} else {
				// do the move itself
				px=new byte[12];
				py=new byte[12];
				for(int j=0;j<12;j++)px[j]=p[j];
				for(int j=12;j<24;j++)py[j-12]=p[j];
				for (i=0; i<12; i++) {
					p[i] = px[(12+i-x)%12];
					p[i+12] = py[(12+i-y)%12];
				}
				return true;
			}
		}
	}
	public static byte[] imagestr(String scr) {
		String[] s=scr.replace("3R", "3r r'").replace("M'", "r R'").replace("M2", "r2 R2").replace("M", "r' R")
				.replace("x'", "r' L").replace("x2", "r2 L2").replace("x", "r L'").split(" ");
		int k, len = s.length;
		if(len>0) {
			char[] seq = new char[len];
			int count = 0;
			for(int i=0;i<len;i++) {
				k=0;
				if(s[i].length()>0){
					switch(s[i].charAt(0)){
					case '5':k=4;break;
					case '4':k=3;break;
					case '3':k=2;break;
					case '2':k=1;break;
					case 'R':seq[count]=16;break;
					case 'L':seq[count]=4;break;
					case 'U':seq[count]=12;break;
					case 'D':seq[count]=0;break;
					case 'F':seq[count]=20;break;
					case 'B':seq[count]=8;break;
					case 'r':seq[count]=16;k=1;break;
					case 'l':seq[count]=4;k=1;break;
					case 'u':seq[count]=12;k=1;break;
					case 'd':seq[count]=0;k=1;break;
					case 'f':seq[count]=20;k=1;break;
					case 'b':seq[count]=8;k=1;break;
					}
					if(s[i].length()>1){
						switch(s[i].charAt(1)){
						case '\'':seq[count]+=2;break;
						case '2':seq[count]++;break;
						case '³':k=2;break;
						case '²':k=1;break;
						case 'w':seq[count]+=24;break;
						case 'r':seq[count]=16;break;
						case 'l':seq[count]=4;break;
						case 'u':seq[count]=12;break;
						case 'd':seq[count]=0;break;
						case 'f':seq[count]=20;break;
						case 'b':seq[count]=8;break;
						}
						if(s[i].length()>2){
							switch(s[i].charAt(2)){
							case '\'':seq[count]+=2;break;
							case '2':seq[count]++;break;
							}
						}
					}
					seq[count++]+=k*24;
				}
			}
			if(count>0) {
				Cube.seq=new char[count];
				for(int i=0; i<count; i++) Cube.seq[i] = seq[i];
			}
		}
		return Cube.imagestring();
	}
	public static String yj4x4(){
		// the idea is to keep the fixed center on U and do Rw or Lw, Fw or Bw, to not disturb it
		String[][] turns = {{"U","D"},{"R","L","r"},{"F","B","f"}};
		String[] cubesuff={"","2","'"};
		int[] donemoves=new int[3];
		int lastaxis,fpos = 0, // 0 = Ufr, 1 = Ufl, 2 = Ubl, 3 = Ubr
				j,k;
		StringBuffer s=new StringBuffer();
		lastaxis=-1;
		for(j=0;j<Mi.scrLen;j++){
			int done=0;
			do{
				int first=(int)(Math.random()*turns.length);
				int second=(int)(Math.random()*turns[first].length);
				if(first!=lastaxis||donemoves[second]==0){
					if(first==lastaxis){
						donemoves[second]=1;
						int rs = (int)(Math.random()*3);
						if(first==0&&second==0){fpos = (fpos + 4 + rs)%4;}
						if(first==1&&second==2){ // r or l
							if(fpos==0||fpos==3) s.append("l"+cubesuff[rs]+" ");
							else s.append("r"+cubesuff[rs]+" ");
						} else if(first==2&&second==2){ // f or b
							if(fpos==0||fpos==1) s.append("b"+cubesuff[rs]+" ");
							else s.append("f"+cubesuff[rs]+" ");
						} else {
							s.append(turns[first][second]+cubesuff[rs]+" ");
						}
					}else{
						for(k=0;k<turns[first].length;k++){donemoves[k]=0;}
						lastaxis=first;
						donemoves[second]=1;
						int rs = (int)(Math.random()*cubesuff.length);
						if(first==0&&second==0){fpos = (fpos + 4 + rs)%4;}
						if(first==1&&second==2){ // r or l
							if(fpos==0||fpos==3) s.append("l"+cubesuff[rs]+" ");
							else s.append("r"+cubesuff[rs]+" ");
						} else if(first==2&&second==2){ // f or b
							if(fpos==0||fpos==1) s.append("b"+cubesuff[rs]+" ");
							else s.append("f"+cubesuff[rs]+" ");
						} else {
							s.append(turns[first][second]+cubesuff[rs]+" ");
						}
					}
					done=1;
				}
			}while(done==0);
		}
		return s.toString();
	}
	public static String oldminxscramble(){
		int j,k;
		String[] minxsuff={"","2","'","2'"};
		String[] faces = {"F","B","U","D","L","DBR","DL","BR","DR","BL","R","DBL"};
		int[] used=new int[12];
		// adjacency table
		String[] adj = {"001010101010","000101010101","100010010110","010001101001","101000100101","010100011010","100110001001","011001000110","100101100010","011010010001","101001011000","010110100100"};
		// now generate the scramble(s)
		StringBuffer s=new StringBuffer();
		for(j=0;j<12;j++){
			used[j] = 0;
		}
		for(j=0;j<Mi.scrLen;j++){
			boolean done = false;
			do {
				int face = (int)(Math.random()*12);
				if (used[face] == 0) {
					s.append(faces[face] + rndEl(minxsuff) + " ");
					for(k=0;k<12;k++){
						if (adj[face].charAt(k)=='1') {used[k] = 0;}
					}
					used[face] = 1;
					done = true;
				}
			} while (!done);
		}
		return s.toString();
	}
	public static String sq1_scramble(int type){
		byte[][][] seq=new byte[1][Mi.scrLen*2][2];
		int i;
		byte[] k;
		sq1_getseq(seq, type);
		StringBuffer s=new StringBuffer();
		for(i=0; i<seq[0].length; i++){
			k=seq[0][i];
			if(k[0] == 7) {
				s.append("/ ");
			} else {
				s.append("(" + k[0] + "," + k[1] + ") ");
			}
		}
		return s.toString();
	}
	public static String do15puzzle(boolean mirrored){
		String[] moves;
		if(mirrored)moves=new String[]{"U","L","R","D"};
		else moves=new String[]{"D","R","L","U"};
		int[][] effect = {{0,-1},{1,0},{-1,0},{0,1}};
		int x=0,y=3,k,r,lastr=5;
		boolean done;
		StringBuffer s=new StringBuffer();
		for(k=0;k<Mi.scrLen;k++){
			done=false;
			while(!done){
				r=(int)(Math.random()*4);
				if (x+effect[r][0]>=0 && x+effect[r][0]<=3 && y+effect[r][1]>=0 && y+effect[r][1]<=3 && r+lastr != 3) {
					done=true;
					x+=effect[r][0];
					y+=effect[r][1];
					s.append(moves[r]+" ");
					lastr=r;
				}
			}
		}
		return s.toString();
	}
	private static int[][] d = {{0,1,2,5,8,7,6,3,4},{6,7,8,13,20,19,18,11,12},{0,3,6,11,18,17,16,9,10},{8,5,2,15,22,21,20,13,14}};
	private static int[] start={1,1,2,3,3,2,4,4,0,5,6,7,8,9,10,10,5,6,7,8,9,11,11};
	private static String[] move={"U","F","L","R"};
	private static String[] cubesuff={"","2","'"};

	private static boolean canMove(int face) {
		int[] u=new int[8];
		int ulen=0, i, j, done, z=0;
		for (i=0; i<9; i++) {
			done = 0;
			for (j=0; j<ulen; j++) {
				if (u[j]==start[d[face][i]]) done = 1;
			}
			if (done==0) {
				u[ulen++] = start[d[face][i]];
				if (start[d[face][i]] == 0) z = 1;
			}
		}
		return (ulen==5 && z==1);
	}
	private static void doMove(int face, int amount) {
		for (int i=0; i<amount; i++) {
			int t = start[d[face][0]];
			start[d[face][0]] = start[d[face][6]];
			start[d[face][6]] = start[d[face][4]];
			start[d[face][4]] = start[d[face][2]];
			start[d[face][2]] = t;
			t = start[d[face][7]];
			start[d[face][7]] = start[d[face][5]];
			start[d[face][5]] = start[d[face][3]];
			start[d[face][3]] = start[d[face][1]];
			start[d[face][1]] = t;
		}
	}
	public static String bicube(){
		StringBuffer sb=new StringBuffer();
		int[][] arr=new int[Mi.scrLen][];
		int[] poss;
		int arrlen=0, done, i, j, x=0, y=0;
		while (arrlen < Mi.scrLen) {
			poss = new int[]{1,1,1,1};
			for (j=0; j<4; j++) {
				if (poss[j]==1 && !canMove(j))
					poss[j]=0;
			}
			done = 0;
			while (done==0) {
				x = (int) (Math.random()*4);
				if (poss[x]==1) {
					y = (int) (Math.random()*3)+1;
					doMove(x, y);
					done = 1;
				}
			}
			arr[arrlen++] = new int[]{x,y};
			if (arrlen >= 2) {
				if (arr[arrlen-1][0] == arr[arrlen-2][0]) {
					arr[arrlen-2][1] = (arr[arrlen-2][1] + arr[arrlen-1][1])%4;
					arrlen--;//arr = arr.slice(0,arr.length - 1);
				}
			}
			if (arrlen >= 1) {
				if (arr[arrlen-1][1] == 0) {
					arrlen--;//arr = arr.slice(0,arr.length - 1);
				}
			}
		}
		for (i=0; i<Mi.scrLen; i++) {
			sb.append( move[arr[i][0]] + cubesuff[arr[i][1]-1] + " ");
		}
		return sb.toString();
	}
	
	public static String c(String s) {
		int i = (int) (Math.random()*12);
		return s+"="+(i-5);
	}
	public static String c() {
		return rndEl(new String[]{"d","U"})+rndEl(new String[]{"d","U"})
				+rndEl(new String[]{"d","U"})+rndEl(new String[]{"d","U"});
	}
}
