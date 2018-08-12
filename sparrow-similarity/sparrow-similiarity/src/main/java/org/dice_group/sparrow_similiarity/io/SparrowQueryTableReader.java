package org.dice_group.sparrow_similiarity.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryFactory;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.OWLOntologyChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.UnloadableImportException;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.tu_dresden.elastiq.main.Main;
import org.tu_dresden.elastiq.owl.io.SimpleIntegerAcceptingShortFormProvider;
import org.tu_dresden.elastiq.owl.io.SimpleOWLEntityChecker;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Reads a sparrow generated csv file as input and creates a List of OWL Class expression as well as sparql queries and their
 * mappings to their ids.
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

	/**
	 * @return the listIndex2ID
	 */
	public Map<Integer, Integer> getListIndex2ID() {
		return listIndex2ID;
	}

	/**
	 * @param listIndex2ID the listIndex2ID to set
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
	 * @param sparql the sparql to set
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
	 * @param owl the owl to set
	 */
	public void setOwl(List<OWLClassExpression> owl) {
		this.owl = owl;
	}

	
	
	public SparrowQueryTableReader(String[] ontologyFiles) throws OWLOntologyCreationException {
		manager = OWLManager.createOWLOntologyManager();
		for(String file : ontologyFiles) {
			manager.loadOntology(IRI.create(new File(file).toURI()));
		}
		ontology=manager.getOntologies().iterator().next();
	}
	
	public void parse(String fileName) {
		parse(new File(fileName));
	}
	
	public void parse(File file) {
		int i=0;
		try(CSVReader reader = new CSVReader(new FileReader(file))){
			
			String [] tokens;
	        while ((tokens = reader.readNext()) != null ) {
	        	if(tokens.length==0 || tokens[0].isEmpty()) {
	        		continue;
	        	}
	        	int start=0;
	            if(tokens.length>2) {
	            	start++;
	            	i=Integer.parseInt(tokens[0]);
	            }
	            //remove 
	            if(tokens[start].startsWith("\"") && tokens[start].endsWith("\"")) {
	            	tokens[start] = tokens[start].substring(1, tokens[start].length()-1);
	            }
	            sparql.add(QueryFactory.create(tokens[start]));
	            start++;
	            if(tokens[start].startsWith("\"") && tokens[start].endsWith("\"")) {
	            	tokens[start] = tokens[start].substring(1, tokens[start].length()-1);
	            }
	            owl.add(parseSingle(tokens[start]).getNNF());
	            listIndex2ID.put(sparql.size()-1, i++);
	        }
		} catch (IOException | QueryException e) {
			e.printStackTrace();
		} 
		
		
	}
	
	public OWLClassExpression parseSingle(String expression) {
		ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(manager.getOWLDataFactory(), expression);
		OWLEntityChecker entityChecker = new SimpleOWLEntityChecker(ontology, manager.getOWLDataFactory());
		
		parser.setOWLEntityChecker(entityChecker);
		return parser.parseClassExpression();
	}

	public OWLOntology getOWLOntology() {
		// TODO Auto-generated method stub
		return this.ontology;
	}
}
