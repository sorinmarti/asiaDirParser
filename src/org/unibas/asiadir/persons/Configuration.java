package org.unibas.asiadir.persons;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Configuration {

	private Map<String, String> confs; 
	
	protected Configuration() {
		confs = new HashMap<>();
	}
	
	protected void addConf(String name, String value) {
		confs.put(name, value);
	}

	public boolean nameExists(String name) {
		return confs.containsKey(name);
	}

	public void print() {
		 Iterator<Entry<String, String>> it = confs.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = it.next();
		        System.out.println(pair.getKey() + "=" + pair.getValue());
		    }
	}

	public int getTranslatedValue(String key) {
		if(!confs.containsKey(key.toLowerCase())) {
			AsiaDirParser.print("Configuration: key does not exist ("+key+")");
			return -1;
		}
		
		String value = confs.get(key.toLowerCase());
		if(key.equals("log.level")) {
			switch(value) {
			case "info":
				return AsiaDirParser.INFO;
			case "warn":
				return AsiaDirParser.WARN;
			case "error":
				return AsiaDirParser.ERROR;
			case "debug":
				return AsiaDirParser.DEBUG;
			default:
				return -1;
			}
		}
		else if(key.equals("updatePlaces.output.type")) {
			switch(value) {
			case "tsv":
				return PlaceCreator.TSV;
			case "json":
				return PlaceCreator.JSON;
			case "all":
				return PlaceCreator.ALL;
			default:
				return -1;
			}
		}
		else {
			try {
				return Integer.valueOf(value);
			} catch(NumberFormatException e) {
				return -1;
			}
		}
	}

	public String getValue(String key) {
		if(!confs.containsKey(key.toLowerCase())) {
			AsiaDirParser.print("Configuration: key does not exist ("+key+")");
			return null;
		}
		return confs.get(key.toLowerCase());
	}

	public boolean getBooleanValue(String key) {
		if(!confs.containsKey(key.toLowerCase())) {
			AsiaDirParser.print("Configuration: key does not exist ("+key+")");
			return false;
		}
		return Boolean.valueOf(confs.get(key.toLowerCase()));
	}
}
