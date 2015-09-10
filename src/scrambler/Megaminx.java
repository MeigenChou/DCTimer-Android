package scrambler;

public class Megaminx {
	private static byte linelen = 10;
	private static byte linenbr;
	private static short[] permU = {4, 0, 1, 2, 3, 9, 5, 6, 7, 8, 10, 11, 12, 13, 58, 59, 16, 17, 18, 63, 20, 21, 22, 23, 24, 14, 15, 27, 28, 29, 19, 31, 32, 33, 34, 35, 25, 26, 38, 39, 40, 30, 42, 43, 44, 45, 46, 36, 37, 49, 50, 51, 41, 53, 54, 55, 56, 57, 47, 48, 60, 61, 62, 52, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131};
	private static short[] permUi = {1, 2, 3, 4, 0, 6, 7, 8, 9, 5, 10, 11, 12, 13, 25, 26, 16, 17, 18, 30, 20, 21, 22, 23, 24, 36, 37, 27, 28, 29, 41, 31, 32, 33, 34, 35, 47, 48, 38, 39, 40, 52, 42, 43, 44, 45, 46, 58, 59, 49, 50, 51, 63, 53, 54, 55, 56, 57, 14, 15, 60, 61, 62, 19, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131};
	private static short[] permD2 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 33, 34, 35, 14, 15, 38, 39, 40, 19, 42, 43, 44, 45, 46, 25, 26, 49, 50, 51, 30, 53, 54, 55, 56, 57, 36, 37, 60, 61, 62, 41, 64, 65, 11, 12, 13, 47, 48, 16, 17, 18, 52, 20, 21, 22, 23, 24, 58, 59, 27, 28, 29, 63, 31, 32, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 124, 125, 121, 122, 123, 129, 130, 126, 127, 128, 131};
	private static short[] permD2i = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 44, 45, 46, 14, 15, 49, 50, 51, 19, 53, 54, 55, 56, 57, 25, 26, 60, 61, 62, 30, 64, 65, 11, 12, 13, 36, 37, 16, 17, 18, 41, 20, 21, 22, 23, 24, 47, 48, 27, 28, 29, 52, 31, 32, 33, 34, 35, 58, 59, 38, 39, 40, 63, 42, 43, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 123, 124, 125, 121, 122, 128, 129, 130, 126, 127, 131};
	private static short[] permR2 = {81, 77, 78, 3, 4, 86, 82, 83, 8, 85, 87, 122, 123, 124, 125, 121, 127, 128, 129, 130, 126, 131, 89, 90, 24, 25, 88, 94, 95, 29, 97, 93, 98, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 26, 22, 23, 48, 30, 31, 27, 28, 53, 32, 69, 70, 66, 67, 68, 74, 75, 71, 72, 73, 76, 101, 102, 103, 99, 100, 106, 107, 108, 104, 105, 109, 46, 47, 79, 80, 45, 51, 52, 84, 49, 50, 54, 0, 1, 2, 91, 92, 5, 6, 7, 96, 9, 10, 15, 11, 12, 13, 14, 20, 16, 17, 18, 19, 21, 113, 114, 110, 111, 112, 118, 119, 115, 116, 117, 120, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65};
	private static short[] permR2i = {88, 89, 90, 3, 4, 93, 94, 95, 8, 97, 98, 100, 101, 102, 103, 99, 105, 106, 107, 108, 104, 109, 46, 47, 24, 25, 45, 51, 52, 29, 49, 50, 54, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 81, 77, 78, 48, 85, 86, 82, 83, 53, 87, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 57, 58, 59, 55, 56, 62, 63, 64, 60, 61, 65, 1, 2, 79, 80, 0, 6, 7, 84, 9, 5, 10, 26, 22, 23, 91, 92, 31, 27, 28, 96, 30, 32, 69, 70, 66, 67, 68, 74, 75, 71, 72, 73, 76, 112, 113, 114, 110, 111, 117, 118, 119, 115, 116, 120, 15, 11, 12, 13, 14, 20, 16, 17, 18, 19, 21};
	public static byte[] state = new byte[11*12];
	private static byte[] seq;	// move sequences
	private static StringBuffer sb;

	private static void initState() {
		for (byte i=0; i<12; i++)
			for (int j=0; j<11; j++)
				state[i*11+j] = i;
	}

	private static byte[] applyMove(byte[] state, short[] movePerm) {
		byte[] stateNew = new byte[11*12];
		for (int i=0; i<11*12; i++) {
			stateNew[i] = state[movePerm[i]];
		}
		return stateNew;
	}

	private static void scramble(){
		for(int i=0; i<linenbr*linelen; i++){
			seq[i] = (byte)(Math.random()*2);
		}
	}

	public static String scramblestring(){
		linenbr = (byte) Math.ceil(Scrambler.scrLen/10.0);
		seq = new byte[linelen*linenbr];
		scramble();
		sb=new StringBuffer();
		byte i,j;
		initState();
		for(j=0; j<linenbr; j++) {
			for(i=0; i<linelen; i++) {
				if (i%2!=0) {
					if (seq[j*linelen + i]!=0) {
						sb.append("D++ ");
						state = applyMove(state, permD2);
					} else {
						sb.append("D-- ");
						state = applyMove(state, permD2i);
					}
				} else {
					if (seq[j*linelen + i]!=0) {
						sb.append("R++ ");
						state = applyMove(state, permR2);
					} else {
						sb.append("R-- ");
						state = applyMove(state, permR2i);
					}
				}
			}
			if (seq[(j+1)*linelen - 1]!=0) {
				sb.append("U ");
				state = applyMove(state, permU);
			} else {
				sb.append("U' ");
				state = applyMove(state, permUi);
			}
		}
		return sb.toString();
	}

	public static byte[] image(String scr) {
		initState();
		String[] s = scr.split(" ");
		for(int i=0; i<s.length; i++) {
			if(s[i].length()>0) {
				if(s[i].equals("D++")) state = applyMove(state, permD2);
				else if(s[i].equals("D--")) state = applyMove(state, permD2i);
				else if(s[i].equals("R++")) state = applyMove(state, permR2);
				else if(s[i].equals("R--")) state = applyMove(state, permR2i);
				else if(s[i].equals("U")) state = applyMove(state, permU);
				else if(s[i].equals("U'")) state = applyMove(state, permUi);
			}
		}
		return state;
	}
}
