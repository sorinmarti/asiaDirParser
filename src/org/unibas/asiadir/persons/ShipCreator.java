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
import java.util.Collections;
import java.util.List;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class ShipCreator extends AbstractCreator {
	
	private static String[][] keywords = {
			{"steamer", ""},
			{"despatch vessel", ""},
			{"P. & O. S. N. Co.'s", ""},
			{"M\\.( *)M\\.", ""},
			{"(H(\\.|,)( *)I\\.)( *)(G\\.|German)( *)(M(\\.|'s(\\.|)|’s(\\.|))(( |)S\\.|'s|)|)(( *)flag( |)shi(n|)p| gunboat|)", "German"},
			{"(H\\.( *)B\\.( *)M(\\.|)(( |)S\\.|))|(H\\.( *)M\\.( *)S\\.)", "GB"},
			{"(French )(cruiser|gunboat|[Ff]lagship)", "F"},
			{"(Russian( torpedo|) )(gun(( *)|-)vessel|cruiser|ironclad|gunboat|battleship|traming ship)", "Russia"},
			{"((U\\.(.*)S\\.(.*)(A\\.(.*)|))\\b(gunvessel|gunboat|flagship|cruiser))|(U\\.(.*)S\\.(.*)(S\\.|A\\.))", "USA"},
			{"H\\.I\\.M\\.S.", ""},
			{"H\\.l\\.G\\.M\\.S\\.", ""},
			{"H\\.I\\.M\\.G\\.S\\.", ""},
			{"H\\.B\\.\\.M\\.", ""},
			{"H\\.M\\.B\\.", ""},
			{"German str.", ""}
			
		};
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		createSimilarityTable(0.85f, "ttt.tsv");
	}
	
	private static void createSimilarityTable(float similarity, String outFile) throws IOException, URISyntaxException {
		BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("res/shiptest_1.tsv")), StandardCharsets.UTF_8));
		System.out.println("START");
		List<Place> ships = new ArrayList<>();
		
		String output = "";
		String row;
		int shipRows = 0;
		while ((row = csvReader.readLine()) != null) {
			String[] rowSplit = row.split("\t");
			if(rowSplit[1].equals("SHIP")) {
				shipRows++;
				
				System.out.println(getStub(rowSplit[0]));
				
				Place existing = findShip(ships, rowSplit[0], similarity);
				if(existing==null) {
					Place s = new Place(rowSplit[0]);
					s.addNameVariation(rowSplit[0]);
					ships.add(s);
				}
				else {
					existing.addNameVariation(rowSplit[0]);
					//System.out.println("Already exists: "+existing.getPreferredLabel()+", added variation: "+rowSplit[0]);
				}
			}
		}
		csvReader.close();
		
		Collections.sort(ships);
		for(Place ship : ships) {
			output += ship.getPreferredLabel() + "\t";
			output += getStub(ship.getPreferredLabel()) + "\t";
			for(String var : ship.getNameVariations()) {
				output += var+ "\t";
			}
			output = output.substring(0, output.length()-"\t".length());
			output += "\n";
		}
		
		System.out.println("Number of Names: "+shipRows+", number of lines "+ships.size());
		
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("res/"+outFile), StandardCharsets.UTF_8)) {
			writer.write(output);
			writer.close();
		}
	}

	
	private static Place findShip(List<Place> ships, String shipName, float similarity) {
		String shipnameStub = getStub(shipName);
		
		for(Place s : ships) {
			// If preferred label exists
			if(s.getPreferredLabel().equals(shipName)) {
				return s;
			}
			// Check in variations
			for(String var : s.getNameVariations()) {
				// 1. Check if exact name exists
				if(var.equals(shipName)) {
					return s;
				}
				// 2. Remove keywords from shipname
				String variationStub = getStub(var);
				Levenshtein mc = new Levenshtein();
				float sim = mc.getSimilarity(shipnameStub, variationStub);
				if(sim>similarity) {
					return s;
				}
			}
		}
		return null;
	}
	
	
	private static String getStub(String name) {
		String nameStub = name;
		for(String[] kw : keywords) {
			nameStub = nameStub.replaceAll(kw[0], "");
		}
		nameStub = nameStub.replaceAll("[Tt]orpedo", "");
		nameStub = nameStub.replaceAll("[Bb]oat", "");
		nameStub = nameStub.replaceAll("[Dd]estroyer", "");
		nameStub = nameStub.replaceAll("cruiser", "");
		nameStub = nameStub.replaceAll("gunboat", "");
		nameStub = nameStub.replaceAll("[Ff]lagship", "");
		nameStub = nameStub.replaceAll("gunvessel", "");
		nameStub = nameStub.replaceAll("gun-vessel", "");
		nameStub = nameStub.replaceAll("battleship", "");
		nameStub = nameStub.replaceAll("refrigerating ship", "");
		nameStub = nameStub.replaceAll("gun ", "");
		nameStub = nameStub.replaceAll("S\\. ", "");
		nameStub = nameStub.replaceAll("S ", "");
		nameStub = nameStub.replaceAll("’s", "");
		nameStub = nameStub.replaceAll("'s", "");
		
		nameStub = nameStub.replaceAll("\"", "");
		nameStub = nameStub.replaceAll("“", "");
		nameStub = nameStub.replaceAll("”", "");
		nameStub = nameStub.replaceAll(",", "");
		
		
		nameStub = nameStub.trim();
		
		//System.out.println(name+ " -> "+nameStub);
		//System.out.println(nameStub);
		return nameStub;
	}
}
