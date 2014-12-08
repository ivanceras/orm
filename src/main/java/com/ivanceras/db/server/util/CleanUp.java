package com.ivanceras.db.server.util;

import java.io.File;
import java.io.IOException;

import com.ivanceras.commons.conf.Configuration;

public class CleanUp{

	public static void doCleanUp(String directoryName){
		File directory = new File(directoryName);

		File[] files = directory.listFiles();
		try{
			if(files != null){
				for (File file : files){
					if (!file.delete()){
						System.out.println("Failed to delete "+file);
					}else{
						System.out.println("Deleting "+file+" ...");
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void start(Configuration conf){
		doCleanUp(conf.daodirectory);
		doCleanUp(conf.mapperdirectory);
		doCleanUp(conf.bodirectory);
		doCleanUp(conf.metaDataDirectory);
	}
}
