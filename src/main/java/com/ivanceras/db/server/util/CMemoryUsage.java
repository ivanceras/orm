package com.ivanceras.db.server.util;

public class CMemoryUsage {

	private static Runtime runtime;
	static int mb = 1024 * 1024;
	static int gb = 1024 * 1024 * 1024;
	public static String MB = "mb";
	public static String GB = "gb";

	private static void init(){
		if(runtime == null){
			runtime = Runtime.getRuntime();
		}
	}
	
	public static float getMax(String unit){
		init();
		return convert(runtime.maxMemory(), unit);
	}

	public static float getUsage(String unit){
		init();
		long usage = runtime.totalMemory() - runtime.freeMemory();
		return convert(usage, unit);
	}
	
	private static float convert(long magnitude, String unit){
		if(unit != null){
			if(unit.equals(MB)){
				return (float)magnitude/mb;
			}
			else if(unit.equals(GB)){
				return (float)magnitude/gb;
			}
		}
		return magnitude;
	}
	
	public static void main(String[] args) {
		float spec = CMemoryUsage.getMax(MB);
		float show = CMemoryUsage.getUsage(MB);
		System.out.println("spec: "+spec);
		System.out.println("show: "+show);
	}

}
