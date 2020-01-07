package org.unibas.asiadir.persons;

import java.util.ArrayList;
import java.util.List;

public class Group {

	String name;
	String label;
	String longitude;
	String latitude;
	List<Place> places;
	List<Person> persons;
	String placeCategory;
	
	public Group(String name) {
		this.name = name;
		this.label = name;
		longitude = "";
		latitude = "";
		places = new ArrayList<Place>();
		persons = new ArrayList<Person>();
		placeCategory = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}
	
	public void addPerson(Person person) {
		this.persons.add(person);
	}
	
	public List<Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}
	
	public void addPlace(Place place) {
		this.places.add(place);
	}

	public String getPlaceCategory() {
		return placeCategory;
	}

	public void setPlaceCategory(String placeCategory) {
		this.placeCategory = placeCategory;
	}
}
