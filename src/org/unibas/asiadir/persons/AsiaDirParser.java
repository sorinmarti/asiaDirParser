package org.unibas.asiadir.persons;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class AsiaDirParser {

	protected static final int ERROR = 0;
	protected static final int WARN  = 1;
	protected static final int INFO  = 2;
	protected static final int DEBUG = 3;
	
	private static int logLevel = ERROR;
	private static String logOutput = "";	
	
	public static void main(String[] args) throws IOException, CloneNotSupportedException {
		File configurationFile = new File("asiaDirParser.conf");
		if(!configurationFile.exists()) {
			printError("No configurationFile present. Exiting...");
			System.exit(-1);
		}
		
		Configuration conf = ConfigurationReader.read(configurationFile);
		//conf.print();
		
		// Set global options
		logLevel = conf.getTranslatedValue("log.level");
		
		//////////////////////////////////////////////////////
		//////////////////////////////////////////////////////
		// (1) UPDATE A PLACES FILE
		if(conf.getBooleanValue("function.updatePlaces")) {
			AsiaDirParser.print("!!! (1) UPDATE PLACES !!!");
			File inputFile = new File(conf.getValue("updatePlaces.file.in"));
			List<Place> places = PlaceCreator.getPlacesWithVariationsFromTSV(inputFile, 
																			 conf.getBooleanValue("updatePlaces.hasHeaders"), 
																			 conf.getBooleanValue("updatePlaces.hasIdColumn"));
			if(conf.getBooleanValue("updatePlaces.updateGeoData")) {
				PlaceCreator.updateGeoData( places, conf.getBooleanValue("updatePlaces.forceReloadGeoData") );
			}
			if(conf.getBooleanValue("updatePlaces.sortByPlaceNames")) {
				Collections.sort(places);
			}
			String outputFileStart = conf.getValue("updatePlaces.file.out");
			int outputType = conf.getTranslatedValue("updatePlaces.output.type");
			switch(outputType) {
			case PlaceCreator.ALL:
				PlaceCreator.savePlaceList_All(new File(outputFileStart+".tsv"), places, PlaceCreator.TSV);
				PlaceCreator.savePlaceList_All(new File(outputFileStart+".json"), places, PlaceCreator.JSON);
				if(conf.getBooleanValue("updatePlaces.saveFilteredLists")) {
					PlaceCreator.savePlaceList_NoCoordinates( new File(outputFileStart+"_noCoords"+".tsv"),  places, PlaceCreator.TSV);
					PlaceCreator.savePlaceList_Uncertain(     new File(outputFileStart+"_uncertain"+".tsv"), places, PlaceCreator.TSV);
					PlaceCreator.savePlaceList_Complete(      new File(outputFileStart+"_complete"+".tsv"),  places, PlaceCreator.TSV);
					PlaceCreator.savePlaceList_NoCoordinates( new File(outputFileStart+"_noCoords"+".json"),  places, PlaceCreator.JSON);
					PlaceCreator.savePlaceList_Uncertain(     new File(outputFileStart+"_uncertain"+".json"), places, PlaceCreator.JSON);
					PlaceCreator.savePlaceList_Complete(      new File(outputFileStart+"_complete"+".json"),  places, PlaceCreator.JSON);
				}
				break;
			case PlaceCreator.TSV:
				PlaceCreator.savePlaceList_All(new File(outputFileStart+".tsv"), places, PlaceCreator.TSV);
				if(conf.getBooleanValue("updatePlaces.saveFilteredLists")) {
					PlaceCreator.savePlaceList_NoCoordinates( new File(outputFileStart+"_noCoords"+".tsv"),  places, PlaceCreator.TSV);
					PlaceCreator.savePlaceList_Uncertain(     new File(outputFileStart+"_uncertain"+".tsv"), places, PlaceCreator.TSV);
					PlaceCreator.savePlaceList_Complete(      new File(outputFileStart+"_complete"+".tsv"),  places, PlaceCreator.TSV);
				}
				break;
			case PlaceCreator.JSON:
				PlaceCreator.savePlaceList_All(new File(outputFileStart+".json"), places, PlaceCreator.JSON);
				if(conf.getBooleanValue("updatePlaces.saveFilteredLists")) {
					PlaceCreator.savePlaceList_NoCoordinates( new File(outputFileStart+"_noCoords"+".json"),  places, PlaceCreator.JSON);
					PlaceCreator.savePlaceList_Uncertain(     new File(outputFileStart+"_uncertain"+".json"), places, PlaceCreator.JSON);
					PlaceCreator.savePlaceList_Complete(      new File(outputFileStart+"_complete"+".json"),  places, PlaceCreator.JSON);
				}
				break;
			}
			
			
		}
		
		//////////////////////////////////////////////////////
		//////////////////////////////////////////////////////
		// (2) CHECK INTEGRITY OF PLACE FILE		
		if(conf.getBooleanValue("function.checkIntegrity")) {
			AsiaDirParser.print("!!! (2) CHECK INTEGRITY !!!");
			File inputFile = new File(conf.getValue("checkIntegrity.file.in"));
			List<Place> places = PlaceCreator.getPlacesWithVariationsFromTSV(inputFile, 
					 														 conf.getBooleanValue("checkIntegrity.hasHeaders"), 
					 														 conf.getBooleanValue("checkIntegrity.hasIdColumn"));
			AsiaDirParser.print("--> Duplicate PLACENAMES");
			PlaceCreator.printDuplicatePlacenames(places);
			AsiaDirParser.print("--> Duplicate placenames: Done");
			
			AsiaDirParser.print("--> Duplicate VARIATIONS");
			PlaceCreator.printDuplicateVariations(places);
			AsiaDirParser.print("--> Duplicate variations:Done");
		}
		
		//////////////////////////////////////////////////////
		//////////////////////////////////////////////////////
		// (3) ADD PERSONS TO PLACES		
		if(conf.getBooleanValue("function.processPersonsFile")) {
			AsiaDirParser.print("!!! (3) PROCESS PERSONS !!!");
			File inputFile = new File(conf.getValue("processPersonsFile.placesFile.in"));
			List<Place> places = PlaceCreator.getPlacesWithVariationsFromTSV(inputFile, 
					 														 conf.getBooleanValue("processPersonsFile.hasHeaders"), 
					 														 conf.getBooleanValue("processPersonsFile.hasIdColumn"));
			
			File personInputFile = new File(conf.getValue("processPersonsFile.personsFile.in"));
			List<Place> placesWP = PersonCreator.addPersonsToPlaceList(personInputFile, places);
			
			File personOutputFile = new File(conf.getValue("processPersonsFile.personsFile.out"));
			PersonCreator.createPlaceCSVStringFromData(personOutputFile, placesWP);
		}
		
		//////////////////////////////////////////////////////
		//////////////////////////////////////////////////////
		// (4) ADD PERSONS TO PLACES AND GROUPS		
		if(conf.getBooleanValue("function.processPersonsFileToGroup")) {
			AsiaDirParser.print("!!! (4) PROCESS PERSONS TO GROUP !!!");
			File inputFile = new File(conf.getValue("processPersonsFileToGroup.placesFile.in"));
			List<Place> places = PlaceCreator.getPlacesWithVariationsFromTSV(inputFile, 
												 conf.getBooleanValue("processPersonsFileToGroup.hasHeaders"), 
												 conf.getBooleanValue("processPersonsFileToGroup.hasIdColumn"));
			
			List<Group> groups = GroupCreator.getGroupsFromFile(new File(conf.getValue("processPersonsFileToGroup.groupsFile.in")), true);
			
			File personInputFile = new File(conf.getValue("processPersonsFileToGroup.personsFile.in"));
			List<Place> placesWP = PersonCreator.addPersonsToPlaceList(personInputFile, places);
			GroupCreator.addPlacesToGroups(groups, placesWP);
			GroupCreator.createMergedGroupAndPlacesFile(new File(conf.getValue("processPersonsFileToGroup.personsFile.out")), groups, placesWP);
		}
		
		//////////////////////////////////////////////////////
		//////////////////////////////////////////////////////
		// (5) ADD PERSONS TO PLACES AND GROUPS		
		if(conf.getBooleanValue("function.showUnreckognizedPlaces")) {
			AsiaDirParser.print("!!! (5) UNRECKOGNIZED PLACES !!!");
			File inputFile = new File(conf.getValue("showUnreckognizedPlaces.placesFile.in"));
			List<Place> places = PlaceCreator.getPlacesWithVariationsFromTSV(inputFile, 
												 conf.getBooleanValue("showUnreckognizedPlaces.hasHeaders"), 
												 conf.getBooleanValue("showUnreckognizedPlaces.hasIdColumn"));
			List<String> unrecognized = PersonCreator.getUnrecognizedPlaces(new File(conf.getValue("showUnreckognizedPlaces.personsFile.in")), places);
			AsiaDirParser.print("--> Unreckognized PLACENAMES");
			for(String s : unrecognized) {
				print(s);
			};
			
		}
		
		//////////////////////////////////////////////////////
		//////////////////////////////////////////////////////
		// (6) HTML RENDER
		if(conf.getBooleanValue("function.createPlacesHtmlRender")) {
			AsiaDirParser.print("!!! (6) CREATE HTML RENDER !!!");
			File inputFile = new File(conf.getValue("createPlacesHtmlRender.placesFile.in"));
			List<Place> places = PlaceCreator.getPlacesWithVariationsFromTSV(inputFile, 
					 														 conf.getBooleanValue("createPlacesHtmlRender.hasHeaders"), 
					 														 conf.getBooleanValue("createPlacesHtmlRender.hasIdColumn"));
			
			File personInputFile = new File(conf.getValue("createPlacesHtmlRender.personsFile.in"));
			List<Place> placesWP = PersonCreator.addPersonsToPlaceList(personInputFile, places);
			
			PersonCreator.createWebsiteFromData(new File(conf.getValue("createPlacesHtmlRender.outFolder")), placesWP, conf.getValue("createPlacesHtmlRender.html.title"));
		}
		
		//////////////////////////////////////////////////////
		//////////////////////////////////////////////////////
		// (7) ADD PERSONS TO SHIPS	
		if(conf.getBooleanValue("function.processPersonsForShipsFile")) {
			AsiaDirParser.print("!!! (7) PROCESS PERSONS ON SHIPS !!!");
			File inputFile = new File(conf.getValue("processPersonsForShipsFile.shipsFile.in"));
			List<Place> ships = PlaceCreator.getShipsWithVariationsFromTSV(inputFile, 
					 														 conf.getBooleanValue("processPersonsForShipsFile.hasHeaders"), 
					 														 conf.getBooleanValue("processPersonsForShipsFile.hasIdColumn"));
			
			File personInputFile = new File(conf.getValue("processPersonsForShipsFile.personsFile.in"));
			List<Place> shipsWP = PersonCreator.addPersonsToPlaceList(personInputFile, ships);
			
			File personOutputFile = new File(conf.getValue("processPersonsForShipsFile.personsFile.out"));
			PersonCreator.createShipCSVStringFromData(personOutputFile, shipsWP);
		}
		
		/// TODO Untreated functions...
		//PlaceCreator.savePlaceListSQL(new File("res/places_all.sql"), places);
		//print(PlaceCreator.getDataInformation(placesWP1896, placesWP1899, placesWP1934, placesWP1937));
	}
	
	protected static void printError(String string) {
		print(string);
	}
	
	protected static void printWarning(String string) {
		if(logLevel>=WARN) {
			print(string);
		}
	}
	
	protected static void printInfo(String string) {
		if(logLevel>=INFO) {
			print(string);
		}
	}
	
	protected static void printDebug(String string) {
		if(logLevel>=DEBUG) {
			print(string);
		}
	}
	
	protected static void print(String string) {
		System.out.println(string);
		logOutput += string + "\n";
	}
	
	protected static void print() {
		System.out.println();
		logOutput += "\n";
	}
}
