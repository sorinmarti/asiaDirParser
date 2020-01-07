package org.unibas.asiadir.persons;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ConfigurationReader {

	public static Configuration read(File configurationFile) {
		
		Configuration conf = new Configuration();
		String error = null;
		
		try {
			List<String> allLines = Files.readAllLines(configurationFile.toPath());
			for (String line : allLines) {
				
				line = line.trim();
				
				if(!line.startsWith("#") && !line.isEmpty()) {
					String name = null, value = null;
					String[] lineParse = line.split("=");
					if(lineParse.length==2) {
						name = lineParse[0].trim().toLowerCase();
						value = lineParse[1].trim().toLowerCase();
						
						if(conf.nameExists(name)) {
							error = "Option already set: "+name;
							break;
						}
						else {
							// Name does not exist, line is well formed
							conf.addConf(name, value);
						}
						
					}
					else {
						error = "Line malformed: "+line;
						break;
					}
					
					if(name!=null && value!=null) {
						conf.addConf(name, value);
					}
					else {
						error = "Name or Value are not specified";
						break;
					}
				}
				
				if(error!=null) {
					System.err.println(error);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return conf;
	}

}
