package scramblers;

public class Clock {
	private static String[] turns={"UR","DR","DL","UL","U","R","D","L","ALL"};
	private static int[][] moves = { 
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
	private static byte[] idx = {1, 3, 2, 0};
	private static byte[] posit = new byte[18];
	private static byte[] pegs = new byte[4];
	public static String scramble() {
		for(int i=0; i<18; i++)posit[i]=0;
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
	public static byte[] posit(){
		return posit;
	}
	public static byte[] pegs(){
		return pegs;
	}
}
