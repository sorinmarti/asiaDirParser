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

public class GroupCreator extends AbstractCreator {

	private static final int idxName      = 0;
	private static final int idxLabel     = 1;
	private static final int idxLatitude  = 2;
	private static final int idxLongitude = 3;
	private static final int idxPlaceCategory = 4;
	
	protected static List<Group> getGroupsFromFile(File groupFile, boolean hasHeader) throws IOException {
		AsiaDirParser.print("addPersonsToGroups: START (FILE "+groupFile.getAbsolutePath()+")");
		String row;
		BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(groupFile), StandardCharsets.UTF_8));
		
		List<Group> groups = new ArrayList<>();
		
		// SEARCH THROUGH PERSONS AND ADD THEM TO PLACES
		int line = 0;
		while ((row = csvReader.readLine()) != null) {    
			if((hasHeader && line>0) || (!hasHeader)) {
				String[] data = row.split("\t");
				if(data.length>=3) {
		    		String groupName = data[idxName];
					if(!groupExistsInList(groups, groupName)) {
						Group group = new Group(groupName);
						group.setLabel(data[idxLabel]);
						group.setLatitude(data[idxLatitude]);
						group.setLongitude(data[idxLongitude]);
						group.setPlaceCategory(data[idxPlaceCategory]);
						groups.add(group);
						AsiaDirParser.printInfo("--> addGroups: ADDED [group] '"+groupName+"'");
					}
					else {
						// group exists
						
					}
		    	}
		    	else {
		    		AsiaDirParser.printWarning("--> addGroups: IGNORED [group] b/c input line was malformed: '"+row+"'");
		    	}
			}
			line++;
		}
		csvReader.close();
		AsiaDirParser.print("--! addPersonsToGroups: PARSED");
		AsiaDirParser.print("addPersonsToGroups: END");
		
		return groups;
	}

	private static boolean groupExistsInList(List<Group> groups, String groupName) {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected static void createGroupCSVStringFromData(File file, List<Group> groups) throws FileNotFoundException, IOException {
		AsiaDirParser.print("writeGroupsWithNumPersons: START (FILE "+file.getAbsolutePath()+")");
		//String output = "id;name;lat;long;value";
		String output = "";
		int id = 1;
		for(Group g : groups) {
			//TODO xxx
		}
		
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			 writer.write(output);
			 writer.close();
		}
		AsiaDirParser.print("--! writeGroupsWithNumPersons: WROTE [groups] (Groups: "+id+")");
		AsiaDirParser.print("writePlaceWithNumPersons: END");
	}

	public static void addPlacesToGroups(List<Group> groups, List<Place> places) {
		for(Place p : places) {
			for(String g : p.getGroupMemberships()) {
				Group grp = searchForGroup(groups, g);
				if(grp!=null) {
					grp.addPlace( p );
				}
				else {
					System.err.println("Did not find group "+g);
				}
			}
		}
	}

	public static Group searchForGroup(List<Group> groups, String groupName) {
		for(Group p : groups) {
			if(p.getName().equals(groupName)) {
				return p;
			}
		}
		return null;
	}

	public static void createMergedGroupAndPlacesFile(File file, List<Group> groups, List<Place> places) throws IOException {
		AsiaDirParser.print("writePlacesAndGroupsWithNumPersons: START (FILE "+file.getAbsolutePath()+")");
		//String output = "id;name;category;lat;long;value";
		String output = "id;placename;groupmode;y_lat;x_long;personcount;place_category"+NL;
		
		int numPlaces = 0;
		int numGroups = 0;
		int id = 1;
		
		for(Place p : places) {
			// Only print them if they are not in a group
			if(p.getGroupMemberships().size()==0) {
				if(p.getLatitude()!=null && p.getLongitude()!=null && !p.getLatitude().isEmpty() && !p.getLongitude().isEmpty() && p.getPersons().size()>0 ) {
					//output += id + ";" + p.getPreferredLabel() + ";P;" + p.getLatitude() + ";" + p.getLongitude() + ";" + p.getPersons().size() + "\n";
					output += id + ";" + p.getPreferredLabel() + ";P;" + p.getLatitude() + ";" + p.getLongitude() + ";" + p.getPersons().size() + ";" + p.getPlaceCategory()+ NL;
					id++;
				}
			}
		}
		for(Group g : groups) {
			int persons = 0;
			for(Place p : g.getPlaces()) {
				persons += p.getPersons().size();
			}
			if(persons>0) {
				//output += id + ";" + g.getName() + ";G;" + g.getLatitude() + ";" + g.getLongitude() + ";" + persons + "\n";
				output += id + ";" + g.getLabel() + ";G;" + g.getLatitude() + ";" + g.getLongitude() + ";" + persons + ";" + g.getPlaceCategory() + NL;
				id++;
			}
		}
		
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			 writer.write(output);
			 writer.close();
		}
		
		AsiaDirParser.print("--! writePlacesAndGroupsWithNumPersons: WROTE [lines] (Places: "+numPlaces+", Groups: "+numGroups+")");
		AsiaDirParser.print("writePlacesAndGroupsWithNumPersons: END");
		
	}
	
}
