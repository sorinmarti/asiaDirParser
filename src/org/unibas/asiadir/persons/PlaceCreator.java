package org.unibas.asiadir.persons;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PlaceCreator extends AbstractCreator {

	protected static final int ALL  = 0;
	protected static final int TSV  = 1;
	protected static final int JSON = 2;
	
	private static final int idxName             = 0;
	private static final int idxTgn              = 1;
	private static final int idxLatitude	     = 2;
	private static final int idxLongitude        = 3;
	private static final int idxPlaceType        = 4;
	private static final int idxPlaceCategory    = 5;
	private static final int idxGroupMemberships = 6;
	private static final int mapsLink            = 7;
	private static final int idxComment          = 8;
	private static final int idxUncertainty      = 9;
	private static final int idxVariationStart   = 10;
	
	//Control	Preferred Shipname	Computed shipname	Assumed nationality	Name var 1
	private static final int idxShipControl          = 100;
	private static final int idxShipNamePreferred    = 101;
	private static final int idxShipNameComputed     = 102;
	private static final int idxShipNationality      = 103;
	private static final int idxShipVariationStart   = 104;
	
	
	/**
	 * 
	 * @param file
	 * @param hasHeader
	 * @param hasIdColumn
	 * @return
	 * @throws IOException
	 */
	public static List<Place> getPlacesWithVariationsFromTSV(File file, boolean hasHeader, boolean hasIdColumn) throws IOException {
		AsiaDirParser.print("readPlacesFromFile: START");
		List<Place> places = new ArrayList<Place>();
		
		int idxOffset = 0;
		if(hasIdColumn) {
			idxOffset=1;
		}
		
		String row;
		int line = 0;
		BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
		//BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FileHandler.getFileFromResources("/normalized_placenames.tsv")), StandardCharsets.UTF_8));
		//BufferedReader csvReader = new BufferedReader(new FileReader(FileHandler.getFileFromResources("/normalized_placenames.tsv")));
		while ((row = csvReader.readLine()) != null) {
		    if((hasHeader && line>0) || (!hasHeader)) {
		    	String[] data = row.split("\t");
		    	// If at least a preferred label is set:
		    	// we compute the entry
		    	if(data.length>=5) {
		    		Place place = new Place(data[idxName+idxOffset]);
		    		place.setTgnNumber(data[idxTgn+idxOffset]);
		    		place.setLatitude(data[idxLatitude+idxOffset]);
		    		place.setLongitude(data[idxLongitude+idxOffset]);
		    		place.setPlaceType(data[idxPlaceType+idxOffset]);
		    		place.setPlaceCategory(data[idxPlaceCategory+idxOffset]);
		    		place.setReviewComment(data[idxComment+idxOffset]);
		    		if(!data[idxUncertainty+idxOffset].isEmpty()) {
		    			if(data[idxUncertainty+idxOffset].toLowerCase().contains("true")) {
		    				place.setUncertain(true);
		    			}
		    			if(data[idxUncertainty+idxOffset].toLowerCase().contains("false")) {
		    				place.setUncertain(false);
		    			}
		    		}
		    		
		    		String[] groups = data[idxGroupMemberships].split(",");
		    		for(String s : groups) {
		    			if(!s.trim().isEmpty()) {
		    				place.addGroupMemberships(s.trim());
		    			}
		    		}
		    		
		    		for(int i=idxVariationStart+idxOffset;i<40;i++) {
		    			if(i<(data.length)) {
		    				if(!data[i].trim().isEmpty()) {
		    					place.addNameVariation(data[i]);
		    				}
		    			}
		    		}
		    		places.add(place);
		    		AsiaDirParser.printInfo("--> readPlacesFromFile: FOUND [place] '"+place.getPreferredLabel()+"'");
		    	}
		    	else {
		    		AsiaDirParser.printWarning("--> readPlacesFromFile: IGNORED [place] b/c input line was malformed: '"+row+"'");
		    	}
		    }
		    
		    line++;
		}
		csvReader.close();
		
		AsiaDirParser.print("--! readPlacesFromFile: PARSED [places] ("+places.size()+" places found.)");	
		AsiaDirParser.print("readPlacesFromFile: END");
		return places;
	}
	
	
	public static List<Place> getShipsWithVariationsFromTSV(File file, boolean hasHeader, boolean hasIdColumn) throws IOException {
		AsiaDirParser.print("readShipsFromFile: START");
		List<Place> places = new ArrayList<Place>();
		
		int idxOffset = -100;
		if(hasIdColumn) {
			idxOffset=1;
		}
		
		String row;
		int line = 0;
		BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
		//BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(FileHandler.getFileFromResources("/normalized_placenames.tsv")), StandardCharsets.UTF_8));
		//BufferedReader csvReader = new BufferedReader(new FileReader(FileHandler.getFileFromResources("/normalized_placenames.tsv")));
		while ((row = csvReader.readLine()) != null) {
		    if((hasHeader && line>0) || (!hasHeader)) {
		    	String[] data = row.split("\t");
		    	// If at least a preferred label is set:
		    	// we compute the entry
		    	if(data.length>=4) {
		    		System.out.println("> "+(idxShipNamePreferred+idxOffset)+": "+data[idxShipNamePreferred+idxOffset]);
		    		Place place = new Place(data[idxShipNamePreferred+idxOffset]);
		    		place.setTgnNumber(data[idxShipNameComputed+idxOffset]);
		    		place.setPlaceCategory(data[idxShipNationality+idxOffset]);
		    		place.setReviewComment(data[idxShipControl+idxOffset]);
		    		
		    		for(int i=idxShipVariationStart+idxOffset;i<40;i++) {
		    			if(i<(data.length)) {
		    				if(!data[i].trim().isEmpty()) {
		    					place.addNameVariation(data[i]);
		    				}
		    			}
		    		}
		    		places.add(place);
		    		AsiaDirParser.printInfo("--> readShipsFromFile: FOUND [ship] '"+place.getPreferredLabel()+"'");
		    	}
		    	else {
		    		AsiaDirParser.printWarning("--> readShipsFromFile: IGNORED [ship] b/c input line was malformed: '"+row+"'");
		    	}
		    }
		    
		    line++;
		}
		csvReader.close();
		
		AsiaDirParser.print("--! readShipsFromFile: PARSED [places] ("+places.size()+" ships found.)");	
		AsiaDirParser.print("readShipsFromFile: END");
		return places;
	}
	
	public static Place searchForPlace(List<Place> places, String variation) {
		for(Place p : places) {
			if(p.getNameVariations().contains(variation)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param places
	 * @param forceTGNReload
	 */
	public static void updateGeoData(List<Place> places, boolean forceTGNReload) {
		AsiaDirParser.print("updateGeoData: START");
		int updated = 0, ignored = 0, skipped = 0;
		for(Place p : places) {
			if((!p.getTgnNumber().isEmpty() && (p.getLatitude().isEmpty() || p.getLongitude().isEmpty())) || (!p.getTgnNumber().isEmpty() && forceTGNReload)) {
				try {
					AsiaDirParser.printInfo("--> updateGeoData: FETCH [geodata] for '"+p.getPreferredLabel() + "' (TGN "+p.getTgnNumber()+")");
					String[] latLong = TGNParser.getCoordinatesFromTGN(p.getTgnNumber());
					p.setLatitude(latLong[0]);
					p.setLongitude(latLong[1]);
					p.setPlaceType(latLong[2]);
					p.setPlaceCategory(latLong[3]);
					AsiaDirParser.printInfo("--> updateGeoData: FETCHED [geodata] for '"+p.getPreferredLabel()+"' (LAT "+p.getLatitude()+" LON "+p.getLongitude()+")");
				} catch (URISyntaxException | IOException e) {
					AsiaDirParser.printWarning("--> updateGeoData: FETCH FAILED [geodata] for '"+p.getPreferredLabel()+"' (error: "+e.getMessage()+")");
				}
				updated++;
			}
			else {
				if(p.getTgnNumber().isEmpty() && (p.getLatitude().isEmpty() || p.getLongitude().isEmpty())) {
					AsiaDirParser.printWarning("--> updateGeoData: IGNORED [place] '"+p.getPreferredLabel()+"' (neither TGN nor coordinates supplied)");
					ignored++;
				}
				else {
					skipped++;
				}
			}
		}
		AsiaDirParser.print("--! updateGeoData: UPDATED [geodata] (Updated: "+updated+" / Skipped: "+skipped+" / Ignored: "+ignored+")");	
		AsiaDirParser.print("updateGeoData: END");
	}

	protected static void savePlaceList_All(File file, List<Place> places, int outputType) throws IOException {
		savePlaceList(file, places, outputType);
	}
	
	protected static void savePlaceList_Uncertain(File file, List<Place> places, int outputType) throws IOException {
		List<Place> filteredPlaces = new ArrayList<>();
		for(Place p : places) {
			if(p.isUncertain()) {
				filteredPlaces.add(p);
			}
		}
		savePlaceList(file, filteredPlaces, outputType);
	}
	
	protected static void savePlaceList_NoCoordinates(File file, List<Place> places, int outputType) throws IOException {
		List<Place> filteredPlaces = new ArrayList<>();
		for(Place p : places) {
			if(!p.hasValidCoordinates()) {
				filteredPlaces.add(p);
			}
		}
		savePlaceList(file, filteredPlaces, outputType);
	}
	
	protected static void savePlaceList_Complete(File file, List<Place> places, int outputType) throws IOException {
		List<Place> filteredPlaces = new ArrayList<>();
		for(Place p : places) {
			if(p.hasValidCoordinates() && !p.isUncertain()) {
				filteredPlaces.add(p);
			}
		}
		savePlaceList(file, filteredPlaces, outputType);
	}
	
	private static void savePlaceList(File file, List<Place> places, int outputType) throws IOException {
		switch(outputType) {
		case TSV:
			savePlaceListCSV(file, places);
			return;
		case JSON:
			savePlaceListJSON(file, places);
			return;
		default:
			AsiaDirParser.printError("savePlaceList: INVALID FILE TYPE.");
		}
	}
	
	private static void savePlaceListCSV(File file, List<Place> places) throws IOException {
		AsiaDirParser.print("--> savePlaceListCSV: START (FILE "+file.getAbsolutePath()+")");
		String csvString = "";
		for(Place p : places) {
			csvString += p.getCSVLine() + NL;
		}
		
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			writer.write(csvString);
			writer.close();
		}
		AsiaDirParser.print("--! savePlaceListCSV: SAVED "+places.size()+" [places]");
		AsiaDirParser.print("--> savePlaceListCSV: END");
	}
	
	/**
	 * 
	 * @param file
	 * @param places
	 * @throws IOException
	 */
	private static void savePlaceListJSON(File file, List<Place> places) throws IOException {
		AsiaDirParser.print("--> savePlaceListJSON: START (FILE "+file.getAbsolutePath()+")");
		String jsonString = "{" + NL +
							" \"type\": \"FeatureCollection\", "+ NL +
							" \"features\": [" + NL;
		for(Place p : places) {
			jsonString += p.getJSONLine() + ", "+NL;
		}
		jsonString = jsonString.substring(0, jsonString.length() - (", "+NL).length()); 
		jsonString += " ]" + NL + "}";
		
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			writer.write(jsonString);
			writer.close();
		}
		AsiaDirParser.print("--! savePlaceListJSON: SAVED "+places.size()+" [places]");
		AsiaDirParser.print("--> savePlaceListJSON: END");
	}

	public static String getDataInformation(List<Place>... places) {
		int largestValue = 0;
		int smallestValue = Integer.MAX_VALUE;
		String output = "Data information" + NL + "----------------";
		for(List<Place> placeList : places) {
			for(Place p : placeList) {
				if(p.getPersons().size()>largestValue) {
					largestValue = p.getPersons().size();
				}
				if(p.getPersons().size()<smallestValue) {
					smallestValue = p.getPersons().size();
				}
			}
		}
		
		output += "Smallest/Largest values are: "+smallestValue + " / " +largestValue;
		return output;
	}

	public static void savePlaceListSQL(File file, List<Place> places) throws IOException {
		AsiaDirParser.print("--> savePlaceListSQL: START");
		String sql = "";
		for(Place p : places) {
			int uncertainty = 0;
			if(p.isUncertain()) {
				uncertainty = 1;
			}
			sql += "INSERT INTO `normplace` (`placename`, `tgn`, `latitude`, `longitude`, `placetype`, `category`, `comment`, `uncertainty`) "
					+ "VALUES ('"+escapeString(p.getPreferredLabel())+"', '"+p.getTgnNumber()+"', '"+p.getLatitude()+"', '"+p.getLongitude()+"', '"+TGNParser.translateBackPlaceType(p.getPlaceType())+"', '"+TGNParser.translateBackCategory(p.getPlaceCategory())+"', '"+escapeString(p.getReviewComment())+"', '"+uncertainty+"');" + NL;
			
			for(String var : p.getNameVariations()) {
				sql += "  INSERT INTO `variation` (`name`, `belongsTo`, `comment`, `uncertainty`) VALUES ('"+escapeString( var )+"', '"+escapeString(p.getPreferredLabel())+"', '', '0');" + NL;
			}
			
		}
		
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			writer.write(sql);
			writer.close();
		}
		AsiaDirParser.print("--! savePlaceListSQL: SAVED "+places.size()+" [places]");
		AsiaDirParser.print("--> savePlaceListSQL: END");
	}

	private static String escapeString(String reviewComment) {
		return reviewComment.replaceAll("\"", "\\\\\"").replaceAll("'", "\\\\\'").replaceAll("’", "\\’");
	}

	protected static void printDuplicateVariations(List<Place> places) {
		List<String> allStrings = new ArrayList<>();
		
		for(Place p : places) {
			for(String var : p.getNameVariations()) {
				allStrings.add(var);
			}
		}
		
		for (int i = 0; i < allStrings.size(); i++) {
		     for (int j = i + 1 ; j < allStrings.size(); j++) {
		          if (allStrings.get(i).equals(allStrings.get(j))) {
		        	  AsiaDirParser.print("Duplicate variation: "+allStrings.get(i));
		          }
		     }
		 }
	}

	public static void printDuplicatePlacenames(List<Place> places) {
		List<String> allStrings = new ArrayList<>();
		
		for(Place p : places) {
			allStrings.add(p.getPreferredLabel());
		}
		
		for (int i = 0; i < allStrings.size(); i++) {
		     for (int j = i + 1 ; j < allStrings.size(); j++) {
		          if (allStrings.get(i).equals(allStrings.get(j))) {
		        	  AsiaDirParser.print("Duplicate placename: "+allStrings.get(i));
		          }
		     }
		 }
	}
}
