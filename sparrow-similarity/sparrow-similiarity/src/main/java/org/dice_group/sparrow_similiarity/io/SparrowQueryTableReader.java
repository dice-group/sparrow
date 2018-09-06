package org.dice_group.sparrow_similiarity.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.normalform.NegationalNormalFormConverter;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.tu_dresden.elastiq.owl.io.SimpleOWLEntityChecker;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Reads a sparrow generated csv file as input and creates a List of OWL Class
 * expression as well as sparql queries and their mappings to their ids.
 * 
 * @author felix conrads
 *
 */
public class SparrowQueryTableReader {

	private Map<Integer, Integer> listIndex2ID = new HashMap<Integer, Integer>();
	private List<Query> sparql = new LinkedList<Query>();
	private List<OWLClassExpression> owl = new LinkedList<OWLClassExpression>();
	private OWLOntologyManager manager;
	private OWLOntology ontology;
	private String service = "http://dbpedia.org/sparql";
	private NegationalNormalFormConverter normalFormConverter;
	
	/**
	 * @return the listIndex2ID
	 */
	public Map<Integer, Integer> getListIndex2ID() {
		return listIndex2ID;
	}

	/**
	 * @param listIndex2ID
	 *            the listIndex2ID to set
	 */
	public void setListIndex2ID(Map<Integer, Integer> listIndex2ID) {
		this.listIndex2ID = listIndex2ID;
	}

	/**
	 * @return the sparql
	 */
	public List<Query> getSparql() {
		return sparql;
	}

	/**
	 * @param sparql
	 *            the sparql to set
	 */
	public void setSparql(List<Query> sparql) {
		this.sparql = sparql;
	}

	/**
	 * @return the owl
	 */
	public List<OWLClassExpression> getOwl() {
		return owl;
	}

	/**
	 * @param owl
	 *            the owl to set
	 */
	public void setOwl(List<OWLClassExpression> owl) {
		this.owl = owl;
	}

	public SparrowQueryTableReader(String[] ontologyFiles) throws OWLOntologyCreationException {
		manager = OWLManager.createOWLOntologyManager();
		Set<OWLOntology> remove = new HashSet<OWLOntology>();
		for(String file : ontologyFiles) {
//			manager.loadOntologyFromOntologyDocument(new File(file));
			manager.loadOntology(IRI.create(new File(file).toURI()));
			try(BufferedReader br = new BufferedReader(new FileReader(new File(file)))){
				String[] prefixes = br.readLine().split(" ");
				for(String ont : prefixes) {
					if(ont.contains("base=") || ont.contains("xmlns=")) {
						continue;
					}
					if(ont.contains("=")) {
						String onto = ont.substring(ont.indexOf("=")+2).replace("\"", "");
						try {
						manager.loadOntology(IRI.create(onto));
						}catch(Exception e){}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for(OWLOntology ont : manager.getOntologies()) {
			remove.add(ont);
		}
//		ontology=manager.getOntologies().iterator().next();
		OWLOntologyManager tmp = OWLManager.createOWLOntologyManager();
		ontology = new OWLOntologyMerger(manager).createMergedOntology(tmp, IRI.create("http://sparrow.dice.owl"));
		manager = tmp;
		normalFormConverter = new NegationalNormalFormConverter(manager.getOWLDataFactory());
		
//		OWLOntologyManager tmp2 = OWLManager.createOWLOntologyManager();
//		tmp2.getOntologies().add(ontology);
//		System.out.println(tmp.getOWLDataFactory().getOWLClass(IRI.create("<http://dbpedia.org/ontology/Film>")));
//		System.out.println(tmp2.getOWLDataFactory().getOWLClass(IRI.create("<http://dbpedia.org/ontology/Film>")));
		try {
			manager.saveOntology(ontology, IRI.create(new File("onto.owl")));
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(manager.getOntologies().size());
	}

	public void parse(String fileName) {
		parse(new File(fileName));
	}

	public void parse(File file) {
		int i = 0;
		try (CSVReader reader = new CSVReader(new FileReader(file))) {

			String[] tokens;
			reader.readNext();
			while ((tokens = reader.readNext()) != null) {
				if (tokens.length == 0 || tokens[0].isEmpty()) {
					continue;
				}
				int start = 0;
				if (tokens.length > 2) {
					start++;
//					i = Integer.parseInt(tokens[0]);
				}
				// remove
				if (tokens[start].startsWith("\"") && tokens[start].endsWith("\"")) {
					tokens[start] = tokens[start].substring(1, tokens[start].length() - 1);
				}
				try {
					Query q = QueryFactory.create(tokens[start]);

					start++;
					if (tokens[start].startsWith("\"") && tokens[start].endsWith("\"")) {
						tokens[start] = tokens[start].substring(1, tokens[start].length() - 1);
					}
				
					OWLClassExpression cl = parseSingle(tokens[start]);
					owl.add(normalFormConverter.convertToNormalForm(cl));
					sparql.add(q);
					listIndex2ID.put(sparql.size() - 1, i++);
				} catch (Exception e) {
					System.out.println("Could not make query " + i + " " + e);
					
				}
			}
		} catch (IOException | QueryException e) {
			e.printStackTrace();
		}
		try(PrintWriter cleanpw = new PrintWriter(file.getName()+"_clean.csv")){
			for(int j=0;j<sparql.size();j++) {
				cleanpw.println(j+",\""+sparql.get(j).toString().replace("\n"," ")+"\",\""+owl.get(j).toString().replace("\n"," ")+"\"");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(owl.size());
	}

	public OWLClassExpression parseSingle(String expression) {
		ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(manager.getOWLDataFactory(),
				expression);
		SimpleOWLEntityChecker entityChecker = new SimpleOWLEntityChecker(manager.getOntologies(), manager.getOWLDataFactory());

		parser.setOWLEntityChecker(entityChecker);
		try {
			OWLClassExpression expr = parser.parseClassExpression();
			return expr;
		}catch(ParserException e) {
//		}catch(NullPointerException e) {
			//TODO try to convert query to ontology
			String newExpression = expression;
			Pattern p = Pattern.compile("<[^>]+>");
			Matcher m = p.matcher(expression);
			int i=0;
			while(m.find()) {
				String uri = m.group();
				//check if uri is ontology
				if(!entityChecker.checkContains(uri)) {
					//not ontology resource 
					//try to get most restrictive rdf:type
					String newUri = getMostRestrictiveClass(uri, entityChecker);
					if(newUri==null) {
						System.out.println("Could not convert "+uri);
						throw e;
						}
					newExpression = newExpression.replace(uri, newUri);
				}
			}
			parser = new ManchesterOWLSyntaxEditorParser(manager.getOWLDataFactory(),
					newExpression);

			parser.setOWLEntityChecker(entityChecker);
			return parser.parseClassExpression();
		}
		
	}

	private String getMostRestrictiveClass(String uri, SimpleOWLEntityChecker entityChecker) {
		String getClass = "prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> select distinct ?class where {"+uri+" rdf:type ?class} ";
		QueryExecution exec = QueryExecutionFactory.sparqlService(service, getClass);
		ResultSet res = exec.execSelect();
		Set<String> classes  =new HashSet<String>();
		Set<String> subClasses  =new HashSet<String>();
		Set<String> superClasses  =new HashSet<String>();
		Set<String> singleClasses = new HashSet<String>();
		while(res.hasNext()) {
			String clazz = "<"+res.next().get("class").toString()+">";
			if(entityChecker.checkContains(clazz)) {
				classes.add(clazz);
			}
		}
		if(classes.size()>1) {
			for(String clazz : classes) {
				if(superClasses.contains(clazz)) {
					continue;
				}
				String sel = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?sCl {"+clazz+" rdfs:subClassOf* ?sCl}";
				exec = QueryExecutionFactory.sparqlService(service, sel);
				ResultSet res2 = exec.execSelect();
				boolean isSub=false;
				while(res2.hasNext()) {
					String sCl = "<"+res2.next().get("sCl").toString()+">";
					if(classes.contains(sCl) && !sCl.equals(clazz)) {
						superClasses.add(sCl);
						subClasses.remove(sCl);
						singleClasses.remove(sCl);
						isSub=true;
					}
				}
				if(isSub) {
					subClasses.add(clazz);
				}
				else {
					//not in super Classes & not sub
					singleClasses.add(clazz);
				}
			}
			
			
//			for(String ont1 :classes) {	
//				if(superClasses.contains(ont1)) {
//					continue;
//				}
//				for(String ont2 : classes) {
//					if(!ont1.equals(ont2)) {
//						String ask = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> ASK {"+ont1+" rdfs:subClassOf* "+ont2+"}";
//						exec = QueryExecutionFactory.sparqlService(service, ask);
//						if(exec.execAsk()) {
//							if(!superClasses.contains(ont1)) {
//								subClasses.add(ont1);
//							}
//							subClasses.remove(ont2);
//							superClasses.add(ont2);
//						}
//						else {
//							if(!superClasses.contains(ont1))
//								singleClasses.add(ont1);
//							if(!superClasses.contains(ont2))
//								singleClasses.add(ont2);
//						}
//					}
//				}
//			}
			if(subClasses.size()>0) {
				Random rand = new Random();
				int i =rand.nextInt(subClasses.size());
				int j=0;
				for(String sub : subClasses ) {
					if(j==i) {
						return sub;}
					j++;
				}
			}
			else if(singleClasses.size()>0) {
				Random rand = new Random();
				int i =rand.nextInt(singleClasses.size());
				int j=0;
				for(String sub : singleClasses ) {
					if(j==i) {
						return sub;}
					j++;
				}
			}
		}
		else if(classes.size()==1) {
			return classes.iterator().next();
		}
		if(classes.size()>0) {
			System.out.println(uri);
		}
		return null;
	}
	
	public OWLOntology getOWLOntology() {
		// TODO Auto-generated method stub
		return this.ontology;
	}
}
