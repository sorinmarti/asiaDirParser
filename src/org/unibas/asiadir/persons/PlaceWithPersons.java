package org.unibas.asiadir.persons;

import java.util.ArrayList;
import java.util.List;

public class PlaceWithPersons {

	private List<Person> persons;
	
	public PlaceWithPersons() {
		persons = new ArrayList<>();
	}
	
	public void addPerson(Person p) {
		persons.add(p);
	}
}
