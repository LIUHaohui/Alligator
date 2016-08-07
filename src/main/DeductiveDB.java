package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import org.jpl7.Query;
import org.jpl7.Term;

import util.ConfigManager;

/**
 * 
 * @author Irlan
 *
 */
public class DeductiveDB {

	private String extractedAttr;
	private String orignalText;
	private ArrayList<String> baseClass;
	private ArrayList<String> attrName;

	/**
	 * Querying the knowledge base.
	 * 
	 * @throws Throwable
	 */
	public void consultKB() throws Throwable {

		// Queries prolog

		// setting working directory
		String path = System.getProperty("user.dir");
		File myUri = new File(path);
		path = myUri.toURI().toString().replace("file:/", "");
		Query.hasSolution("working_directory(_," + "'" + path + "')");

		// Queries evalAMl.pl
		String evalAML = "consult('resources/files/evalAML.pl')";
		System.out.println(evalAML + " " + (Query.hasSolution(evalAML) ? "succeeded" : "failed"));

		// Queries eval.
		String eval = "eval";
		System.out.println(eval + " " + (Query.hasSolution(eval) ? "succeeded" : "failed"));

		// Queries writePredicates.
		String writeFiles = "writePredicates";
		System.out.println(writeFiles + " " + (Query.hasSolution(writeFiles) ? "succeeded" : "failed"));

		// formats the output.txt in java objects
		readOutput();

		String attributes[] = extractedAttr.split(",");

		attrName = new ArrayList<String>();

		// loops through all atributes
		int j = 0;

		while (j < attributes.length) {

			// performs query to get the attribute name
			Map<String, Term>[] results = Query.allSolutions("hasAttributeName(" + attributes[j] + ",Y)");
			for (int i = 0; i < results.length; i++) {
				// stores in array
				attrName.add(results[i].get("Y").toString());

				// updates output.txt
				orignalText = orignalText.replaceAll(attributes[j], results[i].get("Y").toString());

			}
			j++;
		}

		// writes the attributes names in the output.txt
		PrintWriter prologWriter = new PrintWriter(new File(ConfigManager.getFilePath() + "/output.txt"));
		prologWriter.println(orignalText);
		prologWriter.close();

	}

	/**
	 * Reads the output.txt for mapping the attributes to names or values so
	 * that integration can be performed. Mapping is important to identify the
	 * attributes in AML files. This extracts the attributes from datalog format
	 * to java string objects so that query can be made on them.
	 * 
	 * @param extractedAttr
	 * @param orignalText
	 * @throws Exception
	 */
	void readOutput() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(ConfigManager.getFilePath() + "/output.txt"));
		try {
			StringBuilder sb = new StringBuilder();
			StringBuilder orignal = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				orignal.append(line);
				orignal.append(System.lineSeparator());

				int a = line.indexOf('(');
				int b = line.indexOf(')');
				line = line.substring(a + 1, b);
				sb.append(line + ",");
				line = br.readLine();
			}
			extractedAttr = sb.toString();
			orignalText = orignal.toString();
		} finally {
			br.close();
		}

	}

	/**
	 * (Work in progress) This function adds a new output.txt for integration
	 * which mentions the attribute names and the classes it belongs to. This is
	 * required for identification of attributes if there are multiple
	 * attributes with same name. This helps in integration process.
	 * 
	 * @param attributes
	 * @throws FileNotFoundException
	 */

	void addBaseClass(String attributes[]) throws FileNotFoundException {
		int j = 0;
		baseClass = new ArrayList<String>();
		while (j < attributes.length) {

			if (Query.hasSolution("hasAttribute(Y," + attributes[j] + ")")) {
				Map<String, Term>[] results2 = Query.allSolutions("hasAttribute(Y," + attributes[j] + ")");

				for (int i = 0; i < results2.length; i++) {
					Map<String, Term>[] results3 = Query
							.allSolutions("hasAttributeName(" + results2[i].get("Y").toString() + ",Y)");
					for (int k = 0; k < results3.length; k++) {
						baseClass.add(results3[k].get("Y").toString());
					}
				}
			} else {
				baseClass.add(attributes[j]);
			}

			j++;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < baseClass.size(); i++) {

			if (!sb.toString().contains(baseClass.get(i) + "," + attrName.get(i))) {
				sb.append(baseClass.get(i) + "," + attrName.get(i));
				sb.append(System.lineSeparator());
			}
		}
		PrintWriter prologWriter = new PrintWriter(new File(ConfigManager.getFilePath() + "/output2.txt"));
		prologWriter.println(sb.toString());
		prologWriter.close();

	}

}