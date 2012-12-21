package solvers;

public class Tl {
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
