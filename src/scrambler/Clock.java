package scrambler;

public class Clock {
	private static String[] turns = {"UR", "DR", "DL", "UL", "U", "R", "D", "L", "ALL"};
	private static byte[][] moves = { 
		{0,1,1,0,1,1,0,0,0,  -1, 0, 0, 0, 0, 0, 0, 0, 0},// UR
		{0,0,0,0,1,1,0,1,1,   0, 0, 0, 0, 0, 0,-1, 0, 0},// DR
		{0,0,0,1,1,0,1,1,0,   0, 0, 0, 0, 0, 0, 0, 0,-1},// DL
		{1,1,0,1,1,0,0,0,0,   0, 0,-1, 0, 0, 0, 0, 0, 0},// UL
		{1,1,1,1,1,1,0,0,0,  -1, 0,-1, 0, 0, 0, 0, 0, 0},// U
		{0,1,1,0,1,1,0,1,1,  -1, 0, 0, 0, 0, 0,-1, 0, 0},// R
		{0,0,0,1,1,1,1,1,1,   0, 0, 0, 0, 0, 0,-1, 0,-1},// D
		{1,1,0,1,1,0,1,1,0,   0, 0,-1, 0, 0, 0, 0, 0,-1},// L
		{1,1,1,1,1,1,1,1,1,  -1, 0,-1, 0, 0, 0,-1, 0,-1},// A
	};
	private static byte[][] movesOld = {
		{1,1,1,1,1,1,0,0,0,  -1, 0,-1, 0, 0, 0, 0, 0, 0},//UUdd
		{0,1,1,0,1,1,0,1,1,  -1, 0, 0, 0, 0, 0,-1, 0, 0},//dUdU
		{0,0,0,1,1,1,1,1,1,   0, 0, 0, 0, 0, 0,-1, 0,-1},//ddUU
		{1,1,0,1,1,0,1,1,0,   0, 0,-1, 0, 0, 0, 0, 0,-1},//UdUd
		{0,0,0,0,0,0,1,0,1,   0, 0, 0,-1,-1,-1,-1,-1,-1},
		{1,0,0,0,0,0,1,0,0,   0,-1,-1, 0,-1,-1, 0,-1,-1},
		{1,0,1,0,0,0,0,0,0,  -1,-1,-1,-1,-1,-1, 0, 0, 0},
		{0,0,1,0,0,0,0,0,1,  -1,-1, 0,-1,-1, 0,-1,-1, 0},
		{0,1,1,1,1,1,1,1,1,  -1, 0, 0, 0, 0, 0,-1, 0,-1},//dUUU
		{1,1,0,1,1,1,1,1,1,   0, 0,-1, 0, 0, 0,-1, 0,-1},//UdUU
		{1,1,1,1,1,1,1,1,0,  -1, 0,-1, 0, 0, 0, 0, 0,-1},//UUUd
		{1,1,1,1,1,1,0,1,1,  -1, 0,-1, 0, 0, 0,-1, 0, 0},//UUdU
		{1,1,1,1,1,1,1,1,1,  -1, 0,-1, 0, 0, 0,-1, 0,-1},//UUUU
		{1,0,1,0,0,0,1,0,1,  -1,-1,-1,-1,-1,-1,-1,-1,-1},//dddd
	};
	private static byte[] idx = {1, 3, 2, 0};
	private static byte[] epoIdx = {12, 8, 1, 5, 11, 0, 4, 10, 3, 7, 9, 2, 6, 13};
	public static byte[] posit = new byte[18];
	public static byte[] pegs = new byte[4];
	
	public static String scramble() {
		for(int i=0; i<18; i++) posit[i] = 0;
		StringBuilder scramble = new StringBuilder();
		byte[] positCopy = new byte[18];
		for(int x=0; x<9; x++) {
			int turn = (int) (Math.random()*12-5);
			for(int j=0; j<18; j++){
				positCopy[j]+=turn*moves[x][j];
			}
			boolean clockwise = ( turn >= 0 );
			turn = Math.abs(turn);
			scramble.append( turns[x] + turn + (clockwise?"+":"-") + " ");
		}
		scramble.append( "y2 ");
		for(int i=0; i<9; i++){
			posit[i] = positCopy[i+9];
			posit[i+9] = positCopy[i];
		}
		for(int x=4; x<9; x++) {
			int turn = (int) (Math.random()*12-5);
			for(int j=0; j<18; j++){
				posit[j]+=turn*moves[x][j];
			}
			boolean clockwise = ( turn >= 0 );
			turn = Math.abs(turn);
			scramble.append( turns[x] + turn + (clockwise?"+":"-") + " ");
		}
		for(int j=0; j<18; j++){
			posit[j]%=12;
			while( posit[j]<=0 ) posit[j]+=12;
		}
		boolean isFirst = true;
		for(int x=0; x<4; x++) {
			pegs[idx[x]] = (byte)(Math.random()*2);
			if (pegs[idx[x]] == 0) {
				scramble.append((isFirst?"":" ")+turns[x]);
				isFirst = false;
			}
		}
		return scramble.toString();
	}
	
	public static String scrambleOld(boolean concise){
		byte[] seq=new byte[14];
		byte i,j;
		for(i=0;i<18;i++)posit[i]=0;
		for(i=0; i<14; i++){
			seq[i] = (byte)((Math.random()*12)-5);
		}
		for( i=0; i<14; i++){
			for( j=0; j<18; j++){
				posit[j]+=seq[i]*movesOld[i][j];
			}
		}
		for( j=0; j<18; j++){
			posit[j]%=12;
			while( posit[j]<=0 ) posit[j]+=12;
		}
		StringBuilder sb=new StringBuilder();
		if(concise) {
			for(i=0; i<4; i++)
				sb.append("("+seq[i]+", "+seq[i+4]+") / ");
			for(i=8; i<14; i++)
				sb.append("("+seq[i]+") / ");
		} else {
			sb.append("UUdd u="+seq[0]+",d="+seq[4]+" / ");
			sb.append("dUdU u="+seq[1]+",d="+seq[5]+" / ");
			sb.append("ddUU u="+seq[2]+",d="+seq[6]+" / ");
			sb.append("UdUd u="+seq[3]+",d="+seq[7]+" / ");
			sb.append("dUUU u="+seq[8]+" / ");
			sb.append("UdUU u="+seq[9]+" / ");
			sb.append("UUUd u="+seq[10]+" / ");
			sb.append("UUdU u="+seq[11]+" / ");
			sb.append("UUUU u="+seq[12]+" / ");
			sb.append("dddd d="+seq[13]+" / ");
		}
		for( i=0; i<4; i++){
			pegs[i] = (byte)(Math.random()*2);
			if(pegs[i]==0)sb.append("U");else sb.append("d");
		}
		return sb.toString();
	}
	
	public static String scrambleEpo(){
		byte[] seq = new byte[14];
		byte i,j;
		for(i=0;i<18;i++)posit[i]=0;
		for(i=0; i<14; i++){
			seq[i] = (byte)((Math.random()*12)-5);
		}
		for( i=0; i<14; i++){
			for( j=0; j<18; j++){
				posit[j]+=seq[i]*movesOld[epoIdx[i]][j];
			}
		}
		for( j=0; j<18; j++){
			posit[j]%=12;
			while( posit[j]<=0 ) posit[j]+=12;
		}
		StringBuilder sb=new StringBuilder();
		sb.append("UUUU u="+seq[0]+" / ");
		sb.append("dUUU u="+seq[1]+" / ");
		sb.append("dUdU u="+seq[2]+",d="+seq[3]+" / ");
		sb.append("UUdU u="+seq[4]+" / ");
		sb.append("UUdd u="+seq[5]+",d="+seq[6]+" / ");
		sb.append("UUUd u="+seq[7]+" / ");
		sb.append("UdUd u="+seq[8]+",d="+seq[9]+" / ");
		sb.append("UdUU u="+seq[10]+" / ");
		sb.append("ddUU u="+seq[11]+",d="+seq[12]+" / ");
		sb.append("dddd d="+seq[13]+" / ");
		for( i=0; i<4; i++){
			pegs[i] = (byte)(Math.random()*2);
			if(pegs[i]==0)sb.append("U");else sb.append("d");
		}
		return sb.toString();
	}
}
