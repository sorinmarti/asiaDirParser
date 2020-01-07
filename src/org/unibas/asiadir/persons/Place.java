package org.unibas.asiadir.persons;

import java.util.ArrayList;
import java.util.List;

public class Place implements Comparable<Place> {

	private final String preferredLabel;
	
	private String tgnNumber;
	private String longitude;
	private String latitude;
	
	private List<String> nameVariations;
	private List<String> groupMemberships; 
	private List<Person> persons;
	
	private String reviewComment;
	private boolean uncertain;
	private String placeType;
	private String placeCategory;
	
	protected Place(String preferred) {
		preferredLabel = preferred;
		nameVariations = new ArrayList<String>();
		groupMemberships = new ArrayList<String>();
		persons = new ArrayList<Person>();
		reviewComment = "";
		uncertain = false;
		placeType = "";
		placeCategory = "";
	}

	protected String getCSVLine() {
		String separator = "\t";
		String retString = "";
		
		retString += preferredLabel + separator +
					 tgnNumber + separator +
					 latitude + separator +
					 longitude + separator +
					 placeType + separator +
					 placeCategory + separator;
		if(groupMemberships.size()>0) {
			for(String group : groupMemberships) {
				retString += group + ", "; 
			}
			retString = retString.substring(0, retString.length()-2);
			retString += separator;
		}
		else {
			retString += separator;
		}
		retString += "https://www.google.com/maps/place/"+latitude+"+"+longitude + separator +
					 reviewComment + separator +
					 uncertain + separator;
		for(String var : nameVariations) {
			retString += var + separator;
		}
		retString = retString.substring(0, retString.length()-separator.length());
		return retString;
	}
	
	protected String getJSONLine() {
		String NL = "\n";
		String retString = "  {" + NL + 
						   "    \"type\": \"Feature\"," + NL +
						   "    \"properties\": {" + NL + 
						   "       \"name\": \""+getPreferredLabel()+"\", " + NL;
		if(!tgnNumber.isEmpty()) {
			retString += "       \"tgn\": "+getTgnNumber()+", " + NL;
			retString += "       \"placetype\": \""+getPlaceType()+"\", " + NL;
			retString += "       \"placecategory\": \""+getPlaceCategory()+"\", " + NL;
		}
		if(hasValidCoordinates()) {
			retString += "       \"googlemaps\": \"https://www.google.com/maps/place/"+latitude+"+"+longitude+"\", " + NL;
		}
		retString += "       \"comment\": \""+getReviewComment().replaceAll("\"", "\\\\\"") +"\", " + NL +
					 "       \"uncertain\": \""+isUncertain()+"\", " + NL +
					 "       \"variations\": [ " + NL;
		int i = 0;
		retString +=  "         ";
		for(String var : nameVariations) {
			retString += "\"" + var +"\", ";
			i++;
		}
		if(i>0) {
			retString = retString.substring(0, retString.length()-2);
		}
		retString += NL + "       ]" + NL;
		
		if(groupMemberships.size()>0) {
			retString += "       ,"+NL;
			retString += "       \"groups\": [ " + NL;
			i = 0;
			for(String group : groupMemberships) {
				retString += "\"" + group +"\", ";
				i++;
			}
			if(i>0) {
				retString = retString.substring(0, retString.length()-2);
			}
			retString += NL + "       ]" + NL;
		}
		
		retString += "    }";
		
		if(hasValidCoordinates()) {
			retString += ", " + NL +
						 "    \"geometry\": {" + NL +
						 "      \"type\": \"Point\"," + NL +
						 "      \"coordinates\": [" + NL +
						 "          "+getLongitude() + ", " + NL +
						 "          "+getLatitude()+ NL +
						 "       ]"+ NL +
						 "    }" + NL;
		}
		else {
			retString += NL;
		}
		
		retString += "  }";
		
		return retString;
	}
	
	public boolean hasValidCoordinates() {
		if(getLatitude()!=null && !getLatitude().equals("null") && !getLatitude().isEmpty() && 
		   getLongitude()!=null && !getLongitude().equals("null") && !getLongitude().isEmpty()) {
			return true;
		}
		return false;
	}
	

	public String getTgnNumber() {
		return tgnNumber;
	}

	public void setTgnNumber(String tgnNumber) {
		this.tgnNumber = tgnNumber;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public List<String> getGroupMemberships() {
		return groupMemberships;
	}

	public void addGroupMemberships(String groupMembership) {
		this.groupMemberships.add(groupMembership);
	}
	
	public void setGroupMemberships(List<String> groupMemberships) {
		this.groupMemberships = groupMemberships;
	}
	
	public List<String> getNameVariations() {
		return nameVariations;
	}

	public void addNameVariation(String nameVariation) {
		this.nameVariations.add(nameVariation);
	}
	
	public void setNameVariations(List<String> nameVariations) {
		this.nameVariations = nameVariations;
	}
	
	public List<Person> getPersons() {
		return persons;
	}

	public void addPerson(Person person) {
		this.persons.add(person);
	}

	public String getPreferredLabel() {
		return preferredLabel;
	}

	public String getReviewComment() {
		return reviewComment;
	}

	public void setReviewComment(String reviewComment) {
		this.reviewComment = reviewComment;
	}

	public boolean isUncertain() {
		return uncertain;
	}

	public void setUncertain(boolean uncertain) {
		this.uncertain = uncertain;
	}
	
	public String getPlaceType() {
		return placeType;
	}

	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}

	public String getPlaceCategory() {
		return placeCategory;
	}

	public void setPlaceCategory(String placeCategory) {
		this.placeCategory = placeCategory;
	}
	
	@Override
	public int compareTo(Place o) {
		if(o!=null) {
			return preferredLabel.compareTo(o.getPreferredLabel());
		}
		return -1;
	}

	public String getFilenameLabel() {
		//return longitude+"-"+latitude;
		return preferredLabel;
	}
}
