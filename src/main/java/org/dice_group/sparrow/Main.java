package org.dice_group.sparrow;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.dice_group.sparrow.exceptions.RootNodeNotVarException;
import org.dice_group.sparrow.exceptions.RuleHasNotNObjectsException;
import org.dice_group.sparrow.exceptions.RuleNotAvailableException;
import org.dice_group.sparrow.sparql.Sparql2Owl;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException, RootNodeNotVarException, RuleHasNotNObjectsException, RuleNotAvailableException {
		if(args.length<3) {
			System.out.println("USAGE: sparrow [-nD] ruleFile queryInputFile queryOutputFile");
			System.out.println("\t -nD := Dismiss URI Quotes in OWL. <http:test.com> -> http://test.com");
			return;
		}
		String ruleFile = args[args.length-3];
		String inputFile = args[args.length-2];
		String outputFile = args[args.length-1];
		boolean dismissURIQuotes=true;
		List<String> options = Arrays.asList(ArrayUtils.subarray(args, 0, args.length-3));
		if(options.contains("-nD")) {
			dismissURIQuotes=false;
		}
		try(BufferedReader reader = new BufferedReader(new FileReader(inputFile));
				PrintWriter pw = new PrintWriter(outputFile)){
			String query;
			Sparql2Owl bridge = new Sparql2Owl(ruleFile, dismissURIQuotes);
			while((query=reader.readLine())!=null) {
				pw.println(bridge.convertSparqlQuery(query));
			}
		}
	}
	
}
