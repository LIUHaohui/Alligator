package main;

import util.ConfigManager;

public class Rules4AMLIntegration {

	public static void main(String[] args) throws Throwable {

		// Owl2PrologFactGenerator test = new Owl2PrologFactGenerator();
		// Generating Intentional DB from the Ontology
		// test.readOntology(localIRI);
		// test.generateIntentionalDB(conf.getFilePath() + "IntentionalDB.pl");

		// Generating Extentional DB from the Ontology, if the
		// test.generateABoxFacts(conf.getFilePath() + "ExtensionalDB.pl");

		// Generating facts from the AML files, they are converted into RDF
		Files2Facts filesAMLInRDF = new Files2Facts();
		try {

			filesAMLInRDF.prologFilePath();
			filesAMLInRDF.readFiles(ConfigManager.getFilePath(), ".aml");
			filesAMLInRDF.convertRdf();
			filesAMLInRDF.readFiles(ConfigManager.getFilePath(), ".ttl");
			filesAMLInRDF.generateExtensionalDB(ConfigManager.getFilePath());
			DeductiveDB deductiveDB = new DeductiveDB();
			deductiveDB.consultKB();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}