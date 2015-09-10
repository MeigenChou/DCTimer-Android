package solver;

public class Mapping {
	public static short[][] Cnk = new short[12][12];
	static  {
		for(int i=0; i<12; ++i) {
			Cnk[i][0] = 1;
			for(int j=Cnk[i][i]=1; j<i; ++j)
				Cnk[i][j] = (short) (Cnk[i-1][j-1] + Cnk[i-1][j]);
		}
	}
	
	private static int[] fact = {1, 1, 2, 6, 24, 120, 720, 5040};
	public static void set8Perm(int[] arr, int idx) {
		int val = 0x76543210;
		for (int i=0; i<7; i++) {
			int p = fact[7-i];
			int v = idx / p;
			idx -= v*p;
			v <<= 2;
			arr[i] = (val >> v) & 07;
			int m = (1 << v) - 1;
			val = (val & m) + ((val >> 4) & ~m);
		}
		arr[7] = val;
	}
	
	public static int get8Perm(int[] arr) {
		int idx = 0;
		int val = 0x76543210;
		for (int i=0; i<7; i++) {
			int v = arr[i] << 2;
			idx = (8 - i) * idx + ((val >> v) & 07);
			val -= 0x11111110 << v;
		}
		return idx;
	}
	
	public static void set7Perm(int[] ps, int idx) {
		int val = 0x6543210;
		for (int i=0; i<6; i++) {
			int p = fact[6-i];
			int v = idx / p;
			idx -= v*p;
			v <<= 2;
			ps[i] = (val >> v) & 7;
			int m = (1 << v) - 1;
			val = (val & m) + ((val >> 4) & ~m);
		}
		ps[6] = val;
	}
	
	public static int get7Perm(int[] ps) {
		int idx = 0;
		int val = 0x6543210;
		for (int i=0; i<6; i++) {
			int v = ps[i] << 2;
			idx = (7 - i) * idx + ((val >> v) & 07);
			val -= 0x1111110 << v;
		}
		return idx;
	}
	
	public static void cir(int[] arr, int a, int b, int c, int d) {
    	int temp=arr[a]; arr[a]=arr[b]; arr[b]=arr[c]; arr[c]=arr[d]; arr[d]=temp;
    }
	
	public static void cir(byte[] arr, int a, int b, int c, int d) {
    	byte temp=arr[a]; arr[a]=arr[b]; arr[b]=arr[c]; arr[c]=arr[d]; arr[d]=temp;
    }
	
	public static void cir2(int[] arr, int a, int b, int c, int d) {
    	int temp=arr[a]; arr[a]=arr[b]; arr[b]=temp;
    	temp=arr[c]; arr[c]=arr[d]; arr[d]=temp;
    }
	
	public static void cir2(byte[] arr, int a, int b, int c, int d) {
		byte temp=arr[a]; arr[a]=arr[b]; arr[b]=temp;
    	temp=arr[c]; arr[c]=arr[d]; arr[d]=temp;
    }
	
	public static void cir(int[] arr, int a, int b) {
    	int temp=arr[a]; arr[a]=arr[b]; arr[b]=temp;
    }
	
	public static void cir(byte[] arr, int a, int b) {
		byte temp=arr[a]; arr[a]=arr[b]; arr[b]=temp;
    }
	
	public static void cir(int[] arr, int a, int b, int c) {
		int temp = arr[a]; arr[a] = arr[b]; arr[b] = arr[c]; arr[c] = temp;
	}
	
	public static void cir(byte[] arr, int a, int b, int c) {
		byte temp = arr[a]; arr[a] = arr[b]; arr[b] = arr[c]; arr[c] = temp;
	}
	
    // permutation
    public static int permToIdx(int[] permutation, int length) {
        int index = 0;
        for (int i = 0; i < length - 1; i++) {
            index *= length - i;
            for (int j = i + 1; j < length; j++)
                if (permutation[i] > permutation[j]) index++;
        }
        return index;
    }

    public static void idxToPerm(int[] permutation, int index, int length) {
        permutation[length - 1] = 0;
        for (int i = length - 2; i >= 0; i--) {
            permutation[i] = index % (length - i);
            index /= length - i;
            for (int j = i + 1; j < length; j++)
                if (permutation[j] >= permutation[i]) permutation[j]++;
        }
    }

    // even permutation
    public static int epermToIdx(int[] permutation, int length) {
        int index = 0;
        for (int i = 0; i < length - 2; i++) {
            index *= length - i;
            for (int j = i + 1; j < length; j++)
                if (permutation[i] > permutation[j]) index++;
        }
        return index;
    }

    public static void idxToEperm(int[] permutation, int index, int length) {
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
    public static int oriToIdx(int[] orientation, int nValues, int length) {
        int index = 0;
        for (int i = 0; i < length; i++)
            index = nValues * index + (orientation[i] % nValues);
        return index;
    }

    public static void idxToOri(int[] orientation, int index, int nValues, int length) {
        for (int i = length - 1; i >= 0; i--) {
            orientation[i] = index % nValues;
            index /= nValues;
        }
    }

    // zero sum orientation
    public static int zoriToIdx(int[] orientation, int nValues, int length) {
        int index = 0;
        for (int i = 0; i < length - 1; i++)
            index = nValues * index + (orientation[i] % nValues);
        return index;
    }

    public static void idxToZori(int[] orientation, int index, int nValues, int length) {
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

    public static int combToIdx(boolean[] combination, int k) {
        int index = 0;
        for (int i = combination.length - 1; i >= 0 && k > 0; i--) {
            if (combination[i]) {
                index += nChooseK(i, k--);
            }
        }
        return index;
    }

    public static void idxToComb(boolean[] combination, int index, int k, int length) {
        //boolean[] combination = new boolean[length];
        for (int i = length - 1; i >= 0 && k >= 0; i--) {
            if (index >= nChooseK(i, k)) {
                combination[i] = true;
                index -= nChooseK(i, k--);
            }
        }
    }
}
