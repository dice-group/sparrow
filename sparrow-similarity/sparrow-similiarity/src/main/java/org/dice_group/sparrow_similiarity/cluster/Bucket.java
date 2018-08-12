package org.dice_group.sparrow_similiarity.cluster;


import java.util.HashMap;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;

/**
 * Bucket which will contain the queries with the same concept. 
 * 
 * @author felix conrads
 *
 */
public class Bucket extends HashMap<Integer, OWLClassExpression> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5298832410243976528L;

	private OWLClass concept;

	/**
	 * @return the concept
	 */
	public OWLClass getConcept() {
		return concept;
	}

	/**
	 * @param concept the concept to set
	 */
	public void setConcept(OWLClass concept) {
		this.concept = concept;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Bucket)
			return this.concept.equals(((Bucket) obj).getConcept());
		return false;
	}


	
	@Override
	public int hashCode() {
		return this.concept.hashCode();
	}
}
