package solvers;

public class Tl {
	private static int[] fact={1,1,2,6,24,120,720,5040};
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
	
	public static void cir(int[] arr, int a, int b, int c, int d){
    	int temp=arr[a]; arr[a]=arr[b]; arr[b]=arr[c]; arr[c]=arr[d]; arr[d]=temp;
    }
	
	public static void cir(byte[] arr, int a, int b, int c, int d){
    	byte temp=arr[a]; arr[a]=arr[b]; arr[b]=arr[c]; arr[c]=arr[d]; arr[d]=temp;
    }
	
	public static void cir2(int[] arr, int a, int b, int c, int d){
    	int temp=arr[a]; arr[a]=arr[b]; arr[b]=temp;
    	temp=arr[c]; arr[c]=arr[d]; arr[d]=temp;
    }
	
	public static void cir2(byte[] arr, int a, int b, int c, int d){
		byte temp=arr[a]; arr[a]=arr[b]; arr[b]=temp;
    	temp=arr[c]; arr[c]=arr[d]; arr[d]=temp;
    }
	
	public static void cir(int[] arr, int a, int b){
    	int temp=arr[a]; arr[a]=arr[b]; arr[b]=temp;
    }
	
	public static void cir(byte[] arr, int a, int b){
		byte temp=arr[a]; arr[a]=arr[b]; arr[b]=temp;
    }
}
