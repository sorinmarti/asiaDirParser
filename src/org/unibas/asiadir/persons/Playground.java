package org.unibas.asiadir.persons;

public class Playground {
	
	
	
	/* READS TGNS FROM SINGLE ROW
	public static void main(String[] args) throws IOException, URISyntaxException {
		BufferedReader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("res/tgnrows2.tsv")), StandardCharsets.UTF_8));
		
		String output = "";
		String row;
		while ((row = csvReader.readLine()) != null) {
			if(!row.isEmpty()) {
				String[] result = TGNParser.getCoordinatesFromTGN(row);
				output += row + "\t" + result[0] + "\t" + result[1] + "\n";
			}
			else {
				output += row + "\n";
			}
		}
		
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("res/tgnrows_out2.tsv"), StandardCharsets.UTF_8)) {
			writer.write(output);
			writer.close();
		}
	}
	*/
}
