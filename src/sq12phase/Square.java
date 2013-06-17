package sq12phase;

import solvers.Im;

public class Square {
	int edgeperm;		//number encoding the edge permutation 0-40319
	int cornperm;		//number encoding the corner permutation 0-40319
	boolean topEdgeFirst;	//true if top layer starts with edge left of seam
	boolean botEdgeFirst;	//true if bottom layer starts with edge right of seam
	int ml;			//shape of middle layer (+/-1, or 0 if ignored)

	public static byte SquarePrun[] = new byte[40320 * 2];			//pruning table; #twists to solve corner|edge permutation

	public static char TwistMove[] = new char[40320];			//transition table for twists
	public static char TopMove[] = new char[40320];			//transition table for top layer turns
	public static char BottomMove[] = new char[40320];			//transition table for bottom layer turns

//	static int get8Comb(byte[] arr) {
//		int idx = 0, r = 4;
//		for (int i=0; i<8; i++) {
//			if (arr[i] >= 4) {
//				idx += Im.Cnk[7-i][r--];
//			}
//		}
//		return idx;
//	}

	static boolean inited = false;

	public static void init() {
		if (inited) {
			return;
		}
		int[] pos = new int[8];
		int temp;

		for(int i=0;i<40320;i++){
			//twist
			Im.set8Perm(pos, i);

			temp=pos[2];pos[2]=pos[4];pos[4]=temp;
			temp=pos[3];pos[3]=pos[5];pos[5]=temp;
			TwistMove[i]=(char) Im.get8Perm(pos);

			//top layer turn
			Im.set8Perm(pos, i);
			temp=pos[0]; pos[0]=pos[1]; pos[1]=pos[2]; pos[2]=pos[3]; pos[3]=temp;
			TopMove[i]=(char) Im.get8Perm(pos);

			//bottom layer turn
			Im.set8Perm(pos, i);
			temp=pos[4]; pos[4]=pos[5]; pos[5]=pos[6]; pos[6]=pos[7]; pos[7]=temp;
			BottomMove[i]=(char) Im.get8Perm(pos);
		}	

		for (int i=0; i<40320*2; i++) {
			SquarePrun[i] = -1;
		}
		SquarePrun[0] = 0;
		int depth = 0;
		int done = 1;
		while (done < 40320 * 2) {
			boolean inv = depth >= 11;
			int find = inv ? -1 : depth;
			int check = inv ? depth : -1;
			++depth; 
			OUT:
			for (int i=0; i<40320*2; i++) {
				if (SquarePrun[i] == find) {
					int idx = i >> 1;
					int ml = i & 1;

					//try twist
					int idxx = TwistMove[idx]<<1 | (1-ml);
					if(SquarePrun[idxx] == check) {
						++done;
						SquarePrun[inv ? i : idxx] = (byte) (depth);
						if (inv) continue OUT;
					}

					//try turning top layer
					idxx = idx;
					for(int m=0; m<4; m++) {
						idxx = TopMove[idxx];
						if(SquarePrun[idxx<<1|ml] == check){
							++done;
							SquarePrun[inv ? i : (idxx<<1|ml)] = (byte) (depth);
							if (inv) continue OUT;
						}
					}
					assert idxx == idx;
					//try turning bottom layer
					for(int m=0; m<4; m++) {
						idxx = BottomMove[idxx];
						if(SquarePrun[idxx<<1|ml] == check){
							++done;
							SquarePrun[inv ? i : (idxx<<1|ml)] = (byte) (depth);
							if (inv) continue OUT;
						}
					}

				}
			}
			//System.out.print(depth);
			//System.out.print('\t');
			//System.out.println(done);
		}
		inited = true;
	}

}

