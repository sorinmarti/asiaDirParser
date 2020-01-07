package org.unibas.asiadir.persons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;

public class TGNParser {

	public static String[] getCoordinatesFromTGN(String tgn) throws URISyntaxException, IOException {
		final String fileUrl = "http://vocab.getty.edu/tgn/"+tgn+".ttl";
		final String fileName = tgn+".ttl";
		String dir = PlaceCreator.class.getResource("/").getFile();
		File tgnFile = new File(dir + "/" +fileName);
				
		if(!tgnFile.exists()) {
			URL website = new URL(fileUrl);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(dir + "/" + fileName);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		}
		else {
			AsiaDirParser.printInfo("--> getCoordinatesFromTGN: File exists locally. Skip download");
		}

        // Create a PipedRDFStream to accept input and a PipedRDFIterator to
        // consume it
        // You can optionally supply a buffer size here for the
        // PipedRDFIterator, see the documentation for details about recommended
        // buffer sizes
        PipedRDFIterator<Triple> iter = new PipedRDFIterator<>();
        final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);

        // PipedRDFStream and PipedRDFIterator need to be on different threads
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Create a runnable for our parser thread
        Runnable parser = new Runnable() {

            @Override
            public void run() {
                // Call the parsing process.
                RDFDataMgr.parse(inputStream, fileName);
            }
        };

        // Start the parser on another thread
        executor.submit(parser);

        // We will consume the input on the main thread here

        String longitude = null;
		String latitude = null;
		String placeType = null;
		
        // We can now iterate over data as it is parsed, parsing only runs as
        // far ahead of our consumption as the buffer size allows
        while (iter.hasNext()) {
        	Triple next = iter.next();
			if(next.getPredicate().toString().endsWith("longitude")) {
				longitude = next.getObject().getIndexingValue().toString();
			}
			if(next.getPredicate().toString().endsWith("latitude")) {
				latitude = next.getObject().getIndexingValue().toString();
			}
			if(next.getPredicate().toString().contains("placeTypePreferred")) {
				placeType = next.getObject().toString();
			}
			else {
				if(placeType==null) {
					placeType = "No preferred PlaceType";
				}
			}
        }
		
        executor.shutdown();
        String category = categoryOfPlaceType(placeType);
        placeType = translatePlaceType(placeType);
        
        
        return new String[]{latitude, longitude, placeType, category};
	}
	
	private static String categoryOfPlaceType(String placeType) {
		if(placeType==null) {
			return "no type specified";
		}
		String[] typeSplit = placeType.split("/");
		String lastPart = typeSplit[typeSplit.length-1]; 
		String placeTypeName = "Undefined";
		switch(lastPart) {
		case "300008347":	// Inhabited place
		case "300000745":	// Neighborhoos
		case "300387067":	// Special City
			placeTypeName = "Point";	
			break;
		default:
			placeTypeName = "Polygon";
			
		}
		return placeTypeName; 
	}
	
	protected static int translateBackCategory(String placeCategory) {
		if(placeCategory.equals("Point")) {
			return 1;
		}
		if(placeCategory.equals("Polygon")) {
			return 2;
		}
		return 0;
	}
	
	private static String translatePlaceType(String placeType) {
		if(placeType==null) {
			return "no type specified";
		}
		String[] typeSplit = placeType.split("/");
		String lastPart = typeSplit[typeSplit.length-1]; 
		String placeTypeName = "undefined ("+lastPart+")";
		switch(lastPart) {
		case "300008347":
			placeTypeName = "Inhabited place";						// 1
			break;
		case "300387178":
			placeTypeName = "Historical region";					// 2
			break;
		case "300000776":
			placeTypeName = "State (political division)";			// 3
			break;
		case "300128207":
			placeTypeName = "Nation";								// 4
			break;
		case "300000774":
			placeTypeName = "Province";								// 5
			break;
		case "300412029":
			placeTypeName = "Former territory/Colony/Dependent state";	// 6
			break;
		case "300000745":
			placeTypeName = "Neighborhood";							// 7
			break;
		case "300008804":
			placeTypeName = "Peninsula";							// 8
			break;
		case "300182722":
			placeTypeName = "Region (geographic)";					// 9
			break;
		case "300387123":
			placeTypeName = "Federal territory";					// 10
			break;
		case "300387080":
			placeTypeName = "Autonomous district";					// 11
			break;
		case "300387052":
			placeTypeName = "Semi-independent political entity";	// 12
			break;
		case "300235099":
			placeTypeName = "Prefecture";							// 13
			break;
		case "300008791":
			placeTypeName = "Island (landform)";					// 14
			break;
		case "300387346":
			placeTypeName = "General region";						// 15
			break;
		case "300387064":
			placeTypeName = "First level subdivision";
			break;
		case "300387199":
			placeTypeName = "Fourth level subdivision";
			break;
		case "300387331":
			placeTypeName = "Part of inhabited place";
			break;
		case "300000771":
			placeTypeName = "County";
			break;
		case "300387067":
			placeTypeName = "Special city";
			break;	
		case "300008707":
			placeTypeName = "River";
			break;	
		case "300387145":
			placeTypeName = "Second level subdivision";
			break;	
		case "300387213":
			placeTypeName = "Special municipality";
			break;	
		case "300387107":
			placeTypeName = "Autonomous region";
			break;	
		case "300387179":
			placeTypeName = "Former administrative division";
			break;	
		case "300387198":
			placeTypeName = "Third level subdivision";
			break;
			
		}
		
		return placeTypeName; 
	}
	
	protected static int translateBackPlaceType(String placeTypeName) {
		switch(placeTypeName) {
		case "Inhabited place":
			return 300008347;
		case "Historical region":
			return 300387178;
		case "State (political division)":
			return 300000776;
		case "Nation":
			return 300128207;
		case "Province":
			return 300000774;
		case "Former territory/Colony/Dependent state":
			return 300412029;
		case "Neighborhood":
			return 300000745;
		case "Peninsula":
			return 300008804;
		case "Region (geographic)":
			return 300182722;
		case "Federal territory":
			return 300387123;
		case "Autonomous district":
			return 300387080;
		case "Semi-independent political entity":
			return 300387052;
		case "Prefecture":
			return 300235099;
		case "Island (landform)":
			return 300008791;
		case "General region":
			return 300387346;
		case "First level subdivision":
			return 300387064;
		case "Fourth level subdivision":
			return 300387199;
		case "Part of inhabited place":
			return 300387331;
		case "County":
			return 300000771;
		case "Special city":
			return 300387067;
		case "River":
			return 300008707;
		case "Second level subdivision":
			return 300387145;
		case "Special municipality":
			return 300387213;
		case "Autonomous region":
			return 300387107;
		case "Former administrative division":
			return 300387179;
		case "Third level subdivision":
			return 300387198;
		}
		return 0;
	}

	
}
