package cs.sq12phase;

import solver.Mapping;

class Square {
	int edgeperm;		//number encoding the edge permutation 0-40319
	int cornperm;		//number encoding the corner permutation 0-40319
	boolean topEdgeFirst;	//true if top layer starts with edge left of seam
	boolean botEdgeFirst;	//true if bottom layer starts with edge right of seam
	int ml;			//shape of middle layer (+/-1, or 0 if ignored)

	static byte SquarePrun[] = new byte[40320 * 2];			//pruning table; #twists to solve corner|edge permutation

	static char sqTwistMove[] = new char[40320];			//transition table for twists
	static char sqTopMove[] = new char[40320];			//transition table for top layer turns
	static char sqBottomMove[] = new char[40320];			//transition table for bottom layer turns

//	static int get8Comb(byte[] arr) {
//		int idx = 0, r = 4;
//		for (int i=0; i<8; i++) {
//			if (arr[i] >= 4) {
//				idx += Im.Cnk[7-i][r--];
//			}
//		}
//		return idx;
//	}

	static boolean ini = false;

	public static void init() {
		if (ini) {
			return;
		}
		int[] pos = new int[8];
		int temp;

		for(int i=0;i<40320;i++){
			//twist
			Mapping.set8Perm(pos, i);

			temp=pos[2];pos[2]=pos[4];pos[4]=temp;
			temp=pos[3];pos[3]=pos[5];pos[5]=temp;
			sqTwistMove[i]=(char) Mapping.get8Perm(pos);

			//top layer turn
			Mapping.set8Perm(pos, i);
			temp=pos[0]; pos[0]=pos[1]; pos[1]=pos[2]; pos[2]=pos[3]; pos[3]=temp;
			sqTopMove[i]=(char) Mapping.get8Perm(pos);

			//bottom layer turn
			Mapping.set8Perm(pos, i);
			temp=pos[4]; pos[4]=pos[5]; pos[5]=pos[6]; pos[6]=pos[7]; pos[7]=temp;
			sqBottomMove[i]=(char) Mapping.get8Perm(pos);
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
					int idxx = sqTwistMove[idx]<<1 | (1-ml);
					if(SquarePrun[idxx] == check) {
						++done;
						SquarePrun[inv ? i : idxx] = (byte) (depth);
						if (inv) continue OUT;
					}

					//try turning top layer
					idxx = idx;
					for(int m=0; m<4; m++) {
						idxx = sqTopMove[idxx];
						if(SquarePrun[idxx<<1|ml] == check){
							++done;
							SquarePrun[inv ? i : (idxx<<1|ml)] = (byte) (depth);
							if (inv) continue OUT;
						}
					}
					//try turning bottom layer
					for(int m=0; m<4; m++) {
						idxx = sqBottomMove[idxx];
						if(SquarePrun[idxx<<1|ml] == check){
							++done;
							SquarePrun[inv ? i : (idxx<<1|ml)] = (byte) (depth);
							if (inv) continue OUT;
						}
					}
				}
			}
			//System.out.println(depth+'\t'+done);
		}
		ini = true;
	}

}

