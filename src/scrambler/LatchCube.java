package scrambler;

import java.util.Random;

public class LatchCube {
//	private static final int u = 0;
//	private static final int d = 1;
//	private static final int l = 2;
//	private static final int r = 3;
//	private static final int f = 4;
//	private static final int b = 5;
	private static int[] moveFaces;
	private static int[] moveTimes;
	private static String[] moveNames;

	private static int moveCount = 0;
	private static int currentMove = -1;
	private static int currentFace = -1;
	private static int currentTime = 0;
	private static int[] movableFaces = new int[6];
	private static int[] specialForbiddenFaces = new int[6];
	private static int movableFaceCount = 0;
	private static boolean bothDirections = false;
	private static StringBuffer sequence = new StringBuffer();

	private static int[][] label = new int[6][4];
	
	private static boolean check(int face) {
		int sum = 0;
		int count = 0;
		for (int i = 0; i < 4; ++i) {
			sum += label[face][i];
			count += Math.abs(label[face][i]);
		}
		sum = Math.abs(sum);

		return sum == count;
	}

	private static boolean isCounterClockwise(int face) {
		int sum = 0;
		for (int i = 0; i < 4; ++i) {
			sum += label[face][i];
		}
		bothDirections = sum == 0;

		return sum < 0;
	}

	private static void determineMovableFaces() {
		movableFaceCount = 0;
		for (int i = 0; i < 6; ++i) {
			movableFaces[i] = ((check(i)) ? i : -1);
		}
		for (int i = 0; i < 6; ++i) {
			if (specialForbiddenFaces[i] == i) {
				movableFaces[i] = -1;
			}
		}
		for (int i = 0; i < 6; ++i)
			movableFaceCount += ((movableFaces[i] >= 0) ? 1 : 0);
	}

	private static void moveU(int times) {
		int i = 0;
		for (i = 0; i < times; ++i) {
			int tmp = label[0][0];
			label[0][0] = label[0][1];
			label[0][1] = label[0][3];
			label[0][3] = label[0][2];
			label[0][2] = tmp;

			tmp = label[4][0];
			label[4][0] = label[3][0];
			label[3][0] = label[5][0];
			label[5][0] = label[2][0];
			label[2][0] = tmp;
		}
	}
	
	private static void moveD(int times) {
		int i = 0;
		for (i = 0; i < times; ++i) {
			int tmp = label[1][0];
			label[1][0] = label[1][1];
			label[1][1] = label[1][3];
			label[1][3] = label[1][2];
			label[1][2] = tmp;

			tmp = label[4][3];
			label[4][3] = label[2][3];
			label[2][3] = label[5][3];
			label[5][3] = label[3][3];
			label[3][3] = tmp;
		}
	}

	private static void moveL(int times) {
		int i = 0;
		for (i = 0; i < times; ++i) {
			int tmp = label[2][0];
			label[2][0] = label[2][1];
			label[2][1] = label[2][3];
			label[2][3] = label[2][2];
			label[2][2] = tmp;

			tmp = label[0][1];
			label[0][1] = label[5][2];
			label[5][2] = label[1][1];
			label[1][1] = label[4][1];
			label[4][1] = tmp;
		}
	}

	private static void moveR(int times) {
		int i = 0;
		for (i = 0; i < times; ++i) {
			int tmp = label[3][0];
			label[3][0] = label[3][1];
			label[3][1] = label[3][3];
			label[3][3] = label[3][2];
			label[3][2] = tmp;

			tmp = label[0][2];
			label[0][2] = label[4][2];
			label[4][2] = label[1][2];
			label[1][2] = label[5][1];
			label[5][1] = tmp;
		}
	}

	private static void moveF(int times) {
		int i = 0;
		for (i = 0; i < times; ++i) {
			int tmp = label[4][0];
			label[4][0] = label[4][1];
			label[4][1] = label[4][3];
			label[4][3] = label[4][2];
			label[4][2] = tmp;

			tmp = label[0][3];
			label[0][3] = label[2][2];
			label[2][2] = label[1][0];
			label[1][0] = label[3][1];
			label[3][1] = tmp;
		}
	}

	private static void moveB(int times) {
		int i = 0;
		for (i = 0; i < times; ++i) {
			int tmp = label[5][0];
			label[5][0] = label[5][1];
			label[5][1] = label[5][3];
			label[5][3] = label[5][2];
			label[5][2] = tmp;

			tmp = label[0][0];
			label[0][0] = label[3][2];
			label[3][2] = label[1][3];
			label[1][3] = label[2][1];
			label[2][1] = tmp;
		}
	}

	private static void reset() {
		label = new int[][]{ { 0, -1, -1, 0 }, { 0, 1, 1, 0 }, { 0, -1, -1, 0 },
				 { 0, 1, 1, 0 }, { -1, 0, 0, -1 }, { 1, 0, 0, 1 } };
		moveFaces = new int[Scrambler.scrLen];
		moveTimes = new int[Scrambler.scrLen];
		moveNames = new String[Scrambler.scrLen];
		for (int i = 0; i < Scrambler.scrLen; ++i) {
			moveFaces[i] = -1;
			moveTimes[i] = 0;
			moveNames[i] = "";
		}
		currentMove = -1;
		currentFace = -1;
		currentTime = 0;
		movableFaceCount = 0;
		for (int i = 0; i < 6; ++i) {
			movableFaces[i] = i;
			specialForbiddenFaces[i] = -1;
		}
		moveCount = 0;
		sequence.delete(0, sequence.length());
	}
	
	private static void undoLastMove() {
		switch (moveFaces[(moveCount - 1)]) {
		case 0:
			moveU((4 - moveTimes[moveCount - 1]) % 4);
			break;
		case 1:
			moveD((4 - moveTimes[moveCount - 1]) % 4);
			break;
		case 2:
			moveL((4 - moveTimes[moveCount - 1]) % 4);
			break;
		case 3:
			moveR((4 - moveTimes[moveCount - 1]) % 4);
			break;
		case 4:
			moveF((4 - moveTimes[moveCount - 1]) % 4);
			break;
		case 5:
			moveB((4 - moveTimes[moveCount - 1]) % 4);
		}

		moveFaces[(moveCount - 1)] = -1;
		moveTimes[(moveCount - 1)] = 0;
		moveNames[(moveCount - 1)] = "";
		moveCount--;
	}

	public static String scramble() {
		reset();
		Random rand = new Random(System.currentTimeMillis());

		for (int i = 0; i < Scrambler.scrLen; moveCount++) {
			determineMovableFaces();
			currentMove = rand.nextInt(18);
			currentFace = currentMove / 3;
			currentTime = currentMove % 3 + 1;
			if (movableFaces[currentFace] == -1) {
				--i;
				moveCount -= 1;
			}
			else if (movableFaceCount < 2) {
				undoLastMove();
				--i;
				--i;
				moveCount -= 1;
			}
			else if ((movableFaceCount < 3) && 
					(moveCount > 1) && 
					(moveFaces[moveCount - 1] / 2 == moveFaces[moveCount - 2] / 2)) {
				undoLastMove();
				undoLastMove();
				i -= 3;
				moveCount -= 1;
			}
			else if ((moveCount > 0) && (currentFace == moveFaces[moveCount - 1])) {
				--i;
				moveCount -= 1;
			}
			else if ((moveCount > 1) && 
					(currentFace / 2 == moveFaces[moveCount - 1] / 2) && 
					(currentFace / 2 == moveFaces[moveCount - 2] / 2)) {
				--i;
				moveCount -= 1;
			}
			else {
				addMove(currentFace, currentTime);
			}
			++i;
		}

		for (int i = 0; i < Scrambler.scrLen; ++i) {
			sequence.append(moveNames[i]);
		}
		return sequence.toString();
	}

	private static void addMove(int face, int time) {
		switch (face) {
		case 0:
			moveU(time);
			moveNames[moveCount] = "U";
			break;
		case 1:
			moveD(time);
			moveNames[moveCount] = "D";
			break;
		case 2:
			moveL(time);
			moveNames[moveCount] = "L";
			break;
		case 3:
			moveR(time);
			moveNames[moveCount] = "R";
			break;
		case 4:
			moveF(time);
			moveNames[moveCount] = "F";
			break;
		case 5:
			moveB(time);
			moveNames[moveCount] = "B";
		}

		if (isCounterClockwise(face))
			time -= 4;
		moveFaces[moveCount] = face;
		moveTimes[moveCount] = time;
		switch (time) {
		case -3:
			moveNames[moveCount] = (moveNames[moveCount] + "3' ");
			break;
		case -2:
			moveNames[moveCount] = (moveNames[moveCount] + "2' ");
			break;
		case -1:
			moveNames[moveCount] = (moveNames[moveCount] + "' ");
			break;
		case 1:
			moveNames[moveCount] = (moveNames[moveCount] + " ");
			break;
		case 2:
			moveNames[moveCount] = (moveNames[moveCount] + "2 ");
			break;
		case 3:
			if (bothDirections) {
				moveNames[moveCount] = (moveNames[moveCount] + "' ");
			}
			else moveNames[moveCount] = (moveNames[moveCount] + "3 ");
		}
	}
}
