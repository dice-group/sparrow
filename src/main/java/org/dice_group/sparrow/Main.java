package org.dice_group.sparrow;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.dice_group.sparrow.exceptions.GraphContainsCycleException;
import org.dice_group.sparrow.exceptions.RootNodeNotVarException;
import org.dice_group.sparrow.exceptions.RuleHasNotNObjectsException;
import org.dice_group.sparrow.exceptions.RuleNotAvailableException;
import org.dice_group.sparrow.sparql.Sparql2Owl;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException, RootNodeNotVarException,
			RuleHasNotNObjectsException, RuleNotAvailableException, GraphContainsCycleException {
		if (args.length < 3) {
			System.out.println("USAGE: sparrow [-nD] ruleFile queryInputFile queryOutputFile");
			System.out.println("\t -nD := Dismiss URI Quotes in OWL. <http:test.com> -> http://test.com");
			return;
		}
		String ruleFile = args[args.length - 3];
		String inputFile = args[args.length - 2];
		String outputFile = args[args.length - 1];
		int succeded=0, failed=0;
		boolean dismissURIQuotes = true;
		List<String> options = Arrays.asList(ArrayUtils.subarray(args, 0, args.length - 3));
		if (options.contains("-nD")) {
			dismissURIQuotes = false;
		}
		int id=0;
		try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
				PrintWriter pw = new PrintWriter(outputFile)) {
			String query;
			Sparql2Owl bridge = new Sparql2Owl(ruleFile, dismissURIQuotes);
			pw.println("id,sparql,owl");
			while ((query = reader.readLine()) != null) {
				String owl = bridge.convertSparqlQuery(query);
				if (!owl.isEmpty()) {
					print(id++, query, owl, pw);
					succeded++;
				}
				else {
					id++;
					failed++;
				}
			}
		}
		System.out.println();
		System.out.println("STATS: ");
		System.out.println((failed+succeded)+" Queries are in the input query set.");
		System.out.println(succeded+" Queries could be succesfully converted.");
		System.out.println(failed+" Queries could not be converted.");

	}
	
	public static void print(int id, String sparql, String owl, PrintWriter pw) {
		Query q = QueryFactory.create(sparql);
		q.getPrefixMapping().clearNsPrefixMap();
		pw.print(id+",\"" + q.serialize().replace("\n", " ").replaceAll("\\s+", " ") + "\",");
		pw.println("\"" + owl + "\"");
	}

}
