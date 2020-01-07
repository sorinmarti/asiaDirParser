package org.unibas.asiadir.persons;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractCreator {
	
	protected static final String NL = "\n";
	
	protected static List<Place> getPlacesCopy(List<Place> places) {
		List<Place> placeCopy = new ArrayList<Place>();
		for(Place p : places) {
			Place np = new Place(p.getPreferredLabel());
			np.setLatitude( p.getLatitude() );
			np.setLongitude(p.getLongitude());
			np.setReviewComment( p.getReviewComment() );
			np.setUncertain( p.isUncertain() );
			np.setTgnNumber( p.getTgnNumber() );
			np.setNameVariations(p.getNameVariations());
			np.setGroupMemberships( p.getGroupMemberships() );
			np.setPlaceCategory( p.getPlaceCategory() );
			np.setPlaceType( p.getPlaceType() );
			placeCopy.add( np );
		}
		return placeCopy;
	}
	
	protected static List<Group> getGroupsCopy(List<Group> groups) {
		List<Group> groupCopy = new ArrayList<Group>();
		for(Group g : groups) {
			Group ng = new Group(g.getName());
			ng.setLatitude( g.getLatitude() );
			ng.setLongitude(g.getLongitude());
			groupCopy.add( ng );
		}
		return groupCopy;
	}
	
	protected static String getHTMLStart(String title) {
		String str  = "<html>"+NL;
		str += " <head>" + NL;
		str += "  <title>"+title+"</title>" + NL;
		str += "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>" + NL;
		str += " </head>" + NL;
		str += " <body>" + NL;
		return str;
	}
	
	protected static String getHTMLEnd() {
		String str  = " </body>"+NL;
		str += "</html>" + NL;
		str += "<!-- This page has been automatically generated on " + new Date() + "-->";
		return str;
	}
}
