package solvers;

public class Im {
    // permutation
    public static int permutationToIndex(int[] permutation, int length) {
        int index = 0;
        for (int i = 0; i < length - 1; i++) {
            index *= length - i;
            for (int j = i + 1; j < length; j++)
                if (permutation[i] > permutation[j]) index++;
        }
        return index;
    }

    public static void indexToPermutation(int[] permutation, int index, int length) {
        permutation[length - 1] = 0;
        for (int i = length - 2; i >= 0; i--) {
            permutation[i] = index % (length - i);
            index /= length - i;
            for (int j = i + 1; j < length; j++)
                if (permutation[j] >= permutation[i]) permutation[j]++;
        }
    }

    // even permutation
    public static int evenPermutationToIndex(int[] permutation, int length) {
        int index = 0;
        for (int i = 0; i < length - 2; i++) {
            index *= length - i;
            for (int j = i + 1; j < length; j++)
                if (permutation[i] > permutation[j]) index++;
        }
        return index;
    }

    public static void indexToEvenPermutation(int[] permutation, int index, int length) {
        int sum = 0;
        permutation[length - 1] = 1;
        permutation[length - 2] = 0;
        for (int i = length - 3; i >= 0; i--) {
            permutation[i] = index % (length - i);
            sum += permutation[i];
            index /= length - i;
            for (int j = i + 1; j < length; j++)
                if (permutation[j] >= permutation[i]) permutation[j]++;
        }
        if (sum % 2 != 0) {
            int temp = permutation[length - 1];
            permutation[length - 1] = permutation[length - 2];
            permutation[length - 2] = temp;
        }
    }

    // orientation
    public static int orientationToIndex(int[] orientation, int nValues, int length) {
        int index = 0;
        for (int i = 0; i < length; i++)
            index = nValues * index + orientation[i];
        return index;
    }

    public static void indexToOrientation(int[] orientation, int index, int nValues, int length) {
        for (int i = length - 1; i >= 0; i--) {
            orientation[i] = index % nValues;
            index /= nValues;
        }
    }

    // zero sum orientation
    public static int zeroSumOrientationToIndex(int[] orientation, int nValues, int length) {
        int index = 0;
        for (int i = 0; i < length - 1; i++)
            index = nValues * index + orientation[i];
        return index;
    }

    public static void indexToZeroSumOrientation(int[] orientation, int index, int nValues, int length) {
        orientation[length - 1] = 0;
        for (int i = length - 2; i >= 0; i--) {
            orientation[i] = index % nValues;
            index /= nValues;
            orientation[length - 1] += orientation[i];
        }
        orientation[length - 1] = (nValues - orientation[length - 1] % nValues) % nValues;
    }

    // combinations
    private static int nChooseK(int n, int k) {
        int value = 1;
        for (int i = 0; i < k; i++) {
            value *= n - i;
        }
        for (int i = 0; i < k; i++) {
            value /= k - i;
        }
        return value;
    }

    public static int combinationToIndex(boolean[] combination, int k) {
        int index = 0;
        for (int i = combination.length - 1; i >= 0 && k > 0; i--) {
            if (combination[i]) {
                index += nChooseK(i, k--);
            }
        }
        return index;
    }

    public static void indexToCombination(boolean[] combination, int index, int k, int length) {
        //boolean[] combination = new boolean[length];
        for (int i = length - 1; i >= 0 && k >= 0; i--) {
            if (index >= nChooseK(i, k)) {
                combination[i] = true;
                index -= nChooseK(i, k--);
            }
        }
    }
}
