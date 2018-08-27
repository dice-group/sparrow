package org.dice_group.sparrow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.PrefixMapping;
import org.dice_group.sparrow.exceptions.GraphContainsCycleException;
import org.dice_group.sparrow.exceptions.RootNodeNotVarException;
import org.dice_group.sparrow.exceptions.RuleHasNotNObjectsException;
import org.dice_group.sparrow.exceptions.RuleNotAvailableException;
import org.dice_group.sparrow.exceptions.TooManyProjectVarsException;
import org.dice_group.sparrow.sparql.Sparql2Owl;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException, RootNodeNotVarException,
			RuleHasNotNObjectsException, RuleNotAvailableException, GraphContainsCycleException, TooManyProjectVarsException {
		if (args.length < 3) {
			System.out.println("USAGE: sparrow [-nD] ruleFile queryInputFile queryOutputFile");
			System.out.println("\t -nD := Dismiss URI Quotes in OWL. <http:test.com> -> http://test.com");
			return;
		}
		String ruleFile = args[args.length - 3];
		String inputFile = args[args.length - 2];
		if(inputFile.endsWith(".log")) {
			inputFile = cleanLogQueries(inputFile);
		}
		String outputFile = args[args.length - 1];
		int succeded=0, failed=0;
		boolean dismissURIQuotes = true;
		List<String> options = Arrays.asList(ArrayUtils.subarray(args, 0, args.length - 3));
		if (options.contains("-nD")) {
			dismissURIQuotes = false;
		}
		int id=0;
		try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
				PrintWriter pw = new PrintWriter(outputFile); PrintWriter owlOut = new PrintWriter(outputFile.replace(".", "")+"_rules.owl")) {
			String query;
			Sparql2Owl bridge = new Sparql2Owl(ruleFile, dismissURIQuotes);
			pw.println("id,sparql,owl");
			Set<String> rules = new HashSet<String>();
			while ((query = reader.readLine()) != null) {
				Query q = QueryFactory.create(query);
				String owl = bridge.convert(q, q.getResultVars().get(0));
				if (!owl.isEmpty()) {
					String[] owls = owl.split("\n");
					for(int j=0;j<owls.length-1;j++) {
						rules.add(owls[j]);
					}
					print(id++, query, owls[owls.length-1], pw);
					succeded++;
				}
				else {
					id++;
					failed++;
				}
			}
			StringBuilder owlStr = new StringBuilder();
			owlStr.append("@prefix dc: <http://purl.org/dc/elements/1.1/> .\n" + 
					"@prefix grddl: <http://www.w3.org/2003/g/data-view#> .\n" + 
					"@prefix owl: <http://www.w3.org/2002/07/owl#> .\n" + 
					"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" + 
					"@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" + 
					"@prefix xml: <http://www.w3.org/XML/1998/namespace> .\n" + 
					"@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" + 
					"\n" + 
					"<http://sparrow.dice/rules/inverse> a owl:Ontology ;\n" + 
					"     dc:title \"Inverse Rules created by Sparrow \" .\n\n");
			for(String rule : rules) {
				owlStr.append(rule).append("\n");
			}
			Model m = ModelFactory.createDefaultModel();
//			Model ont = ModelFactory.createOntologyModel();
			System.out.println(owlStr);
			m.read(new StringReader(owlStr.toString()), "http://sparrow.dice/rules/inverse", "TTL");
			m.write(owlOut, "RDF/XML");
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

	
	public static String cleanLogQueries(String queryLogFile) {
		File f = new File(queryLogFile);
		
		try(BufferedReader reader = new BufferedReader(new FileReader(f))){
			PrintWriter pw = new PrintWriter(queryLogFile.replace(".log", "clean.txt"));
			String line;
			Set<String> queries = new HashSet<String>();
			while((line=reader.readLine())!=null) {
				if(line.contains("Query String: ")) {
					int startIndex = line.indexOf("Query String: ")+14;
					line = line.substring(startIndex);
					if(line.indexOf(" ")>0) {
						int endIndex = line.indexOf(" ");
						line = line.substring(0, endIndex);
					}
					String old = line;
					try {
						if(line.indexOf("&")>0) {
							line = line.substring(0, line.indexOf("&"));
						}else if(line.indexOf("\"")>0) {
							line = line.substring(0, line.indexOf('\"'));

						}
						line = URLDecoder.decode(line, "UTF-8");
						
						if(line.toUpperCase().contains("SELECT")) {
						Query q = QueryFactory.create();
						q.setPrefixMapping(PrefixMapping.Extended);
						String prol = q.serialize();
						q = QueryFactory.create(prol+" "+line);
						if(line.contains("OPTIONAL") || line.contains("UNION") || line.contains("FILTER")){
							continue;
						}
						if(q.isSelectType()) {
							q.setLimit(Query.NOLIMIT);
							q.getPrefixMapping().clearNsPrefixMap();
							queries.add(q.toString().replace("\n", " "));
						}
						}
					}catch(Exception e) {
						System.out.println("NOT: "+line+" "+e);
					}
					
				}
			}
			for(String q : queries) {
				pw.println(q);						
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryLogFile.replace(".log", "clean.txt");
	}
}
