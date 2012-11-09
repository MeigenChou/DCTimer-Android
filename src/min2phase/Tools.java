package min2phase;

import java.util.Random;

/**
 * Some useful functions.
 */
public class Tools implements Runnable {
	
	protected static final boolean USE_TWIST_FLIP_PRUN = false;

	public static boolean inited = false;
	
	private static Random gen = new Random();
	
	private static int[] initState = new int[2];
	private static int[] require = {0x0, 0x1, 0x2, 0x2,  0x2, 0x7, 0xa, 0x3,  0x13, 0x13, 0x3, 0x6e,  0xca, 0xa6, 0x612, 0x512};
	
	public static void initIdx(int idx) {
		switch (idx) {
			case 0 : CubieCube.initMove(); break;//-
			case 1 : CubieCube.initSym(); break;//0
			case 2 : CubieCube.initFlipSym2Raw(); break;//1
			case 3 : CubieCube.initTwistSym2Raw(); break;//1
			
			case 4 : CubieCube.initPermSym2Raw(); break;//1
			case 5 : CoordCube.initFlipMove(); break;//0, 1, 2
			case 6 : CoordCube.initTwistMove(); break;//0, 1, 3
			case 7 : CoordCube.initUDSliceMoveConj(); break;//0, 1
			
			case 8 : CoordCube.initCPermMove(); break;//0, 1, 4
			case 9 : CoordCube.initEPermMove(); break;//0, 1, 4
			case 10 : CoordCube.initMPermMoveConj(); break;//0, 1
			case 11 : if (USE_TWIST_FLIP_PRUN) {CoordCube.initTwistFlipPrun();}	break;//1, 2, 3, 5, 6
			
			case 12 : CoordCube.initSliceTwistPrun(); break;//1, 3, 6, 7
			case 13 : CoordCube.initSliceFlipPrun(); break;//1, 2, 5, 7
			case 14 : CoordCube.initMEPermPrun(); break;//1, 4, 9, 10
			case 15 : CoordCube.initMCPermPrun(); break;//1, 4, 8, 10
		}
	}
	
	protected Tools() {}
	
	/**
	 * Main Initialization Function, can be ignored.
	 */
	public void run() {
		while (true) {
			int choice = -1;
			synchronized (initState) {
				if (initState[0] == 0xffff) {
					return;
				}
				for (int i=0; i<16; i++) {
					if (((initState[0]>>i)&1)==0 && ((initState[1]&require[i])==require[i])) {
						choice = i;
						initState[0] |= 1 << choice;
						break;
					}
				}
				if (choice == -1) {
					try {
						initState.wait();
						continue;
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			}
			//long t = System.nanoTime();
			initIdx(choice);
			//System.out.println(choice + "\t" + (System.nanoTime() - t));
			synchronized (initState) {
				initState[1] |= 1 << choice;
				initState.notifyAll();
			}
		}	
	}
	
	private static void initParallel(int N_thread) {
		Thread[] initThreads = new Thread[N_thread-1];
		for (int i=0; i<N_thread-1; i++) {
			initThreads[i] = new Thread(new Tools());
			initThreads[i].start();
		}
		new Tools().run();
		try {
			for (int i=0; i<N_thread-1; i++) {
				initThreads[i].join();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Initialization of the Solver.<br>
	 * Always below 0.5 seconds with Multiple-Thread.<br>
	 * call it before first solving, or it will be called by {@link cs.min2phase.Search#solution(java.lang.String facelets, int maxDepth, long timeOut, long timeMin, int verbose)} at first solving.
	 */
	public static void init() {
		if (inited) {
			return;
		}
		/**
		 * Can be replaced by:
		 *     new Tools().run();
		 */
		//initParallel(Runtime.getRuntime().availableProcessors());
		initParallel(2);
		
		inited = true;
	}
	
	/**
	 * @return whether the package is initialized.
	 */
	public static boolean isInited() {
		return inited;
	}
	
	/**
	 * Set Random Source.
	 * @param gen new random source.
	 */
	public static void setRandomSource(Random gen) {
		Tools.gen = gen;
	}

	/**
	 * Generates a random cube.<br>
	 *
	 * The random source can be set by {@link Tl.min2phase.Tools#setRandomSource(java.util.Random)}
	 *
	 * @return A random cube in the string representation. Each cube of the cube space has almost (depends on randomSource) the same probability.
	 *
	 * @see Tl.min2phase.Tools#setRandomSource(java.util.Random)
	 * @see cs.min2phase.Search#solution(java.lang.String facelets, int maxDepth, long timeOut, long timeMin, int verbose)
	 */
	public static String randomCube() {
		return randomState(STATE_RANDOM, STATE_RANDOM, STATE_RANDOM, STATE_RANDOM);
	}
	
	private static int resolveOri(byte[] arr, int base) {
		int sum = 0, idx = 0, lastUnknown = -1;
		for (int i=0; i<arr.length; i++) {
			if (arr[i] == -1) {
				arr[i] = (byte) gen.nextInt(base);
				lastUnknown = i;
			}
			sum += arr[i];
		}
		if (sum % base != 0 && lastUnknown != -1) {
			arr[lastUnknown] = (byte) ((30 + arr[lastUnknown] - sum) % base);
		}
		for (int i=0; i<arr.length-1; i++) {
			idx *= base;
			idx += arr[i];
		}
		return idx;
	}
	
	private static int countUnknown(byte[] arr) {
		if (arr == STATE_SOLVED) {
			return 0;
		}
		int cnt = 0;
		for (int i=0; i<arr.length; i++) {
			if (arr[i] == -1) {
				cnt++;
			}
		}
		return cnt;
	}
	
	private static int resolvePerm(byte[] arr, int cntU, int parity) {
		if (arr == STATE_SOLVED) {
			return 0;
		} else if (arr == STATE_RANDOM) {
			return parity == -1 ? gen.nextInt(2) : parity;
		}
		byte[] val = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
		for (int i=0; i<arr.length; i++) {
			if (arr[i] != -1) {
				val[arr[i]] = -1;
			}
		}
		int idx = 0;
		for (int i=0; i<arr.length; i++) {
			if (val[i] != -1) {
				int j = gen.nextInt(idx + 1);
				byte temp = val[i];
				val[idx++] = val[j];
				val[j] = temp;
			}
		}
		int last = -1;
		for (idx=0; idx<arr.length && cntU>0; idx++) {
			if (arr[idx] == -1) {
				if (cntU == 2) {
					last = idx;
				}
				arr[idx] = val[--cntU];
			}
		}
		int p = Util.getNParity(Util.getNPerm(arr, arr.length), arr.length);
		if (p == 1-parity && last != -1) {
			byte temp = arr[idx-1];
			arr[idx-1] = arr[last];
			arr[last] = temp;
		}
		return p;
	}
	
	public static final byte[] STATE_RANDOM = null;
	public static final byte[] STATE_SOLVED = new byte[0];	
	
	public static String randomState(byte[] cp, byte[] co, byte[] ep, byte[] eo) {
		int parity;
		int cntUE = ep == STATE_RANDOM ? 12 : countUnknown(ep);
		int cntUC = cp == STATE_RANDOM ? 8 : countUnknown(cp);
		int cpVal, epVal;
		if (cntUE < 2) {	//ep != STATE_RANDOM
			if (ep == STATE_SOLVED) {
				epVal = parity = 0;
			} else {
				parity = resolvePerm(ep, cntUE, -1);
				epVal = Util.getNPerm(ep, 12);
			}
			if (cp == STATE_SOLVED) {
				cpVal = 0;
			} else if (cp == STATE_RANDOM) {
				do {
					cpVal = gen.nextInt(40320);
				} while (Util.getNParity(cpVal, 8) != parity);
			} else {
				resolvePerm(cp, cntUC, parity);
				cpVal = Util.getNPerm(cp, 8);			
			}
		} else {	//ep != STATE_SOLVED
			if (cp == STATE_SOLVED) {
				cpVal = parity = 0;
			} else if (cp == STATE_RANDOM) {
				cpVal = gen.nextInt(40320);
				parity = Util.getNParity(cpVal, 8);
			} else {
				parity = resolvePerm(cp, cntUC, -1);
				cpVal = Util.getNPerm(cp, 8);
			}
			if (ep == STATE_RANDOM) {
				do {
					epVal = gen.nextInt(479001600);
				} while (Util.getNParity(epVal, 12) != parity);
			} else {
				resolvePerm(ep, cntUE, parity);		
				epVal = Util.getNPerm(ep, 12);
			}
		}
		return Util.toFaceCube(new CubieCube(
			cpVal, 
			co == STATE_RANDOM ? gen.nextInt(2187) : (co == STATE_SOLVED ? 0 : resolveOri(co, 3)), 
			epVal, 
			eo == STATE_RANDOM ? gen.nextInt(2048) : (eo == STATE_SOLVED ? 0 : resolveOri(eo, 2))));
	}
	

	public static String randomLastLayer() {
		return randomState(
			new byte[]{-1, -1, -1, -1, 4, 5, 6, 7}, 
			new byte[]{-1, -1, -1, -1, 0, 0, 0, 0}, 
			new byte[]{-1, -1, -1, -1, 4, 5, 6, 7, 8, 9, 10, 11}, 
			new byte[]{-1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0});
	}
	
	public static String randomLastSlot() {
		return randomState(
			new byte[]{-1, -1, -1, -1, -1, 5, 6, 7}, 
			new byte[]{-1, -1, -1, -1, -1, 0, 0, 0}, 
			new byte[]{-1, -1, -1, -1, 4, 5, 6, 7, -1, 9, 10, 11}, 
			new byte[]{-1, -1, -1, -1, 0, 0, 0, 0, -1, 0, 0, 0});
	}

	public static String randomZBLastLayer() {
		return randomState(
			new byte[]{-1, -1, -1, -1, 4, 5, 6, 7}, 
			new byte[]{-1, -1, -1, -1, 0, 0, 0, 0}, 
			new byte[]{-1, -1, -1, -1, 4, 5, 6, 7, 8, 9, 10, 11}, 
			STATE_SOLVED);
	}

	public static String randomCornerOfLastLayer() {
		return randomState(
			new byte[]{-1, -1, -1, -1, 4, 5, 6, 7}, 
			new byte[]{-1, -1, -1, -1, 0, 0, 0, 0}, 
			STATE_SOLVED, 
			STATE_SOLVED);
	}

	public static String randomEdgeOfLastLayer() {
		return randomState(
			STATE_SOLVED, 
			STATE_SOLVED, 
			new byte[]{-1, -1, -1, -1, 4, 5, 6, 7, 8, 9, 10, 11}, 
			new byte[]{-1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0});
	}

	public static String randomCrossSolved() {
		return randomState(
			STATE_RANDOM, 
			STATE_RANDOM, 
			new byte[]{-1, -1, -1, -1, 4, 5, 6, 7, -1, -1, -1, -1}, 
			new byte[]{-1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1});
	}
	
	public static String randomEdgeSolved() {
		return randomState(
			STATE_RANDOM, 
			STATE_RANDOM, 
			STATE_SOLVED, 
			STATE_SOLVED);
	}
	
	public static String randomCornerSolved() {
		return randomState(
			STATE_SOLVED,
			STATE_SOLVED,
			STATE_RANDOM, 
			STATE_RANDOM);
	}
	
	public static String superFlip() {
		return Util.toFaceCube(new CubieCube(0, 0, 0, 2047));
	}
	
	/**
	 * Check whether the cube definition string s represents a solvable cube.
	 * 
	 * @param facelets is the cube definition string , see {@link cs.min2phase.Search#solution(java.lang.String facelets, int maxDepth, long timeOut, long timeMin, int verbose)}
	 * @return 0: Cube is solvable<br>
	 *         -1: There is not exactly one facelet of each colour<br>
	 *         -2: Not all 12 edges exist exactly once<br>
	 *         -3: Flip error: One edge has to be flipped<br>
	 *         -4: Not all 8 corners exist exactly once<br>
	 *         -5: Twist error: One corner has to be twisted<br>
	 *         -6: Parity error: Two corners or two edges have to be exchanged
	 */
	public static int verify(String facelets) {
		return new Search().verify(facelets);
	}		
}
