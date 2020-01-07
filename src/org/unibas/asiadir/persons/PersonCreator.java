package org.unibas.asiadir.persons;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PersonCreator extends AbstractCreator {

	private static final int idxName       = 0;
	private static final int idxForename   = 1;
	private static final int idxPlace	   = 2;
	private static final int idxLink       = 3;
	
	protected static List<Place> addPersonsToPlaceList(File file, List<Place> places) throws IOException, CloneNotSupportedException {
		AsiaDirParser.print("addPersons: START (FILE "+file.getAbsolutePath()+")");
		String row;
		int numLines = 0;
		int notFoundOccurence = 0;
		BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
		
		List<Place> placeCopy = getPlacesCopy(places);
		
		// SEARCH THROUGH PERSONS AND ADD THEM TO PLACES
		while ((row = csvReader.readLine()) != null) {    
	    	String[] data = row.split("\t");
	    	// If at least a preferred label is set:
	    	// we compute the entry
	    	if(data.length>=4) {
	    		Person person = new Person();
	    		person.setForename(data[idxForename]);
	    		person.setName(data[idxName]);
	    		person.setImagePath(data[idxLink]);
	    		Place place = PlaceCreator.searchForPlace(placeCopy,data[idxPlace].trim());
	    		if(place!=null) {
	    			place.addPerson(person);
	    			AsiaDirParser.printInfo("--> addPersons: ADDED [person] '' to '"+place.getPreferredLabel()+"'");
	    		}
	    		else {
	    			notFoundOccurence++;
	    			AsiaDirParser.printWarning("--> addPersons: NOT FOUND [place] from variation '"+data[idxPlace]+"' ("+notFoundOccurence+")");
	    		}
	    	}
	    	else {
	    		AsiaDirParser.printWarning("--> addPersons: IGNORED [person] b/c input line was malformed: '"+row+"'");
	    	}
		    numLines++;
		}
		csvReader.close();
		AsiaDirParser.print("--! addPersons: PARSED [persons] (Total: "+numLines+" / Place not found: "+notFoundOccurence+")");
		AsiaDirParser.print("addPersons: END");
		
		return placeCopy;
	}

	protected static List<Place> addPersonsToShipList(File file, List<Place> places) throws IOException, CloneNotSupportedException {
		AsiaDirParser.print("addPersons: START (FILE "+file.getAbsolutePath()+")");
		String row;
		int numLines = 0;
		int notFoundOccurence = 0;
		BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
		
		List<Place> placeCopy = getPlacesCopy(places);
		
		// SEARCH THROUGH PERSONS AND ADD THEM TO PLACES
		while ((row = csvReader.readLine()) != null) {    
	    	String[] data = row.split("\t");
	    	// If at least a preferred label is set:
	    	// we compute the entry
	    	if(data.length>=4) {
	    		Person person = new Person();
	    		person.setForename(data[idxForename]);
	    		person.setName(data[idxName]);
	    		person.setImagePath(data[idxLink]);
	    		Place place = PlaceCreator.searchForPlace(placeCopy,data[idxPlace].trim());
	    		if(place!=null) {
	    			place.addPerson(person);
	    			AsiaDirParser.printInfo("--> addPersons: ADDED [person] '"+person.getName()+"' to '"+place.getPreferredLabel()+"'");
	    		}
	    		else {
	    			notFoundOccurence++;
	    			//AsiaDirParser.printWarning("--> addPersons: NOT FOUND [ship] from variation '"+data[idxPlace]+"' ("+notFoundOccurence+")");
	    		}
	    	}
	    	else {
	    		AsiaDirParser.printWarning("--> addPersons: IGNORED [person] b/c input line was malformed: '"+row+"'");
	    	}
		    numLines++;
		}
		csvReader.close();
		AsiaDirParser.print("--! addPersons: PARSED [persons] (Total: "+numLines+" / No Ship or Not found: "+notFoundOccurence+")");
		AsiaDirParser.print("addPersons: END");
		
		return placeCopy;
	}
	/**
	 * 
	 * @param file
	 * @param places
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected static void createPlaceCSVStringFromData(File file, List<Place> places) throws FileNotFoundException, IOException {
		AsiaDirParser.print("writePlaceWithNumPersons: START (FILE "+file.getAbsolutePath()+")");
		//String output = "id;name;lat;long;value";
		String output = "";
		int id = 1;
		for(Place p : places) {
			if(p.getLatitude()!=null && p.getLongitude()!=null && !p.getLatitude().isEmpty() && !p.getLongitude().isEmpty() && p.getPersons().size()>0 ) {
				output += id + ";" + p.getPreferredLabel() + ";" + p.getLatitude() + ";" + p.getLongitude() + ";" + p.getPersons().size() + "\n";
				id++;
			}
		}
		
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			 writer.write(output);
			 writer.close();
		}
		AsiaDirParser.print("--! writePlaceWithNumPersons: WROTE [places] (Places: "+id+")");
		AsiaDirParser.print("writePlaceWithNumPersons: END");
	}
	
	protected static void createShipCSVStringFromData(File file, List<Place> places) throws FileNotFoundException, IOException {
		AsiaDirParser.print("writeShipsWithNumPersons: START (FILE "+file.getAbsolutePath()+")");
		//String output = "id;name;lat;long;value";
		String output = "";
		int id = 1;
		int totalPersons = 0;
		for(Place p : places) {
			if(p.getPersons().size()>0 ) {
				output += id + ";" + p.getPreferredLabel() + ";0.0000;0.0000;" + p.getPersons().size() + "\n";
				id++;
				totalPersons += p.getPersons().size();
			}
		}
		output += id + ";TOTAL;0.0000;0.0000;" + totalPersons + "\n";
		
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			 writer.write(output);
			 writer.close();
		}
		AsiaDirParser.print("--! writeShipsWithNumPersons: WROTE [ships] (Ships: "+id+")");
		AsiaDirParser.print("writeShipsWithNumPersons: END");
	}
	
	/**
	 * 
	 * @param baseFolder
	 * @param places
	 * @param title
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void createWebsiteFromData(File baseFolder, List<Place> places, String title) throws FileNotFoundException, IOException {
		AsiaDirParser.print("createWebsiteFromData: START (BASE "+baseFolder.getAbsolutePath()+")");
		if(!baseFolder.exists()) {
			if(!baseFolder.mkdirs()) {
				AsiaDirParser.printError("--! createWebsiteFromData: ERROR could not create base directory");
				return;
			}
		}
		
		// FIRST FILE: IDX FILE
		File indexFile = new File(baseFolder.getAbsolutePath()+"/index.html");
		File placeFile;
		AsiaDirParser.print("createWebsiteFromData: CREATE "+indexFile.getAbsolutePath());
		String indexString = new String();
		String placeFileString;
		indexString  = getHTMLStart(title);
		indexString += " <h1>"+title+"</h1>"+NL;
		indexString += "  <table style=\"border: 1px solid;\">"+NL;
		for(Place p : places) {
			// ADD TO INDEX TABLE
			if(p.getPersons().size()>0) {
				indexString += "   <tr>"+NL+
							   "     <td>"+p.getPreferredLabel()+"</td>" + NL +
							   "     <td><a href=\""+p.getFilenameLabel()+".html\">"+p.getPersons().size()+" persons.</a></td>" + NL +
							   "   </tr>"+NL;
			
				// CREATE PLACE FILE
				placeFile = new File(baseFolder.getAbsolutePath()+"/"+p.getFilenameLabel()+".html");
				AsiaDirParser.print("createWebsiteFromData: CREATE "+placeFile.getAbsolutePath());
				String placetitle = "Persons in "+p.getPreferredLabel();
				placeFileString  = getHTMLStart(placetitle);
				placeFileString += "  <a href=\"index.html\">Back to place list</a>"+NL;
				placeFileString += "  <h1>"+placetitle+"</h1>"+NL;
				placeFileString += "  <table>"+NL;
				for(Person pe : p.getPersons()) {
					placeFileString += "    <tr>"+NL;
					placeFileString += "      <td>"+pe.getForename()+"</td>"+NL;
					placeFileString += "      <td>"+pe.getName()+"</td>"+NL;
					placeFileString += "      <td><img src=\""+pe.getImagePath()+"\" /></td>"+NL;
					placeFileString += "    </tr>"+NL;
				}
				placeFileString += "  </table>"+NL;
				placeFileString += getHTMLEnd();
				
				try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(placeFile), StandardCharsets.UTF_8)) {
					 writer.write(placeFileString);
					 writer.close();
				}
			}
			else {
				// Skipped place because it has no persons
			}
		}
		indexString += "  </table>"+NL;		
		indexString += getHTMLEnd();
		
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(indexFile), StandardCharsets.UTF_8)) {
			 writer.write(indexString);
			 writer.close();
		}
		
		AsiaDirParser.print("createWebsiteFromData: END");
	}

	/**
	 * 
	 * @param personInputFile
	 * @param places
	 * @return
	 * @throws IOException
	 */
	public static List<String> getUnrecognizedPlaces(File personInputFile, List<Place> places) throws IOException {
		AsiaDirParser.print("getUnrecognizedPlaces: START");
		String row;
		BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(personInputFile), StandardCharsets.UTF_8));
		
		List<String> unrecognizedPlaces = new ArrayList<>();
		
		// SEARCH THROUGH PERSONS AND ADD THEM TO PLACES
		while ((row = csvReader.readLine()) != null) {    
	    	String[] data = row.split("\t");
	    	// If at least a preferred label is set:
	    	// we compute the entry
	    	if(data.length>=4) {
	    		String placename = data[idxPlace].trim();
	    		Place place = PlaceCreator.searchForPlace(places,placename);
	    		if(place==null) {
	    			if(!unrecognizedPlaces.contains(placename)) {
	    				unrecognizedPlaces.add(placename);
	    			}
	    		}
	    	}
	    	else {
	    		AsiaDirParser.printWarning("--> getUnrecognizedPlaces: IGNORED [person] b/c input line was malformed: '"+row+"'");
	    	}
		}
		csvReader.close();
		AsiaDirParser.print("--! getUnrecognizedPlaces: FOUND [unidentified places] "+unrecognizedPlaces.size());
		AsiaDirParser.print("getUnrecognizedPlaces: END");
		
		return unrecognizedPlaces;
	}
}
