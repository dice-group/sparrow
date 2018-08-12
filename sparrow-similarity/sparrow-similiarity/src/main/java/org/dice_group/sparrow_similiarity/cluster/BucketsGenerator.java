package org.dice_group.sparrow_similiarity.cluster;

import java.util.LinkedList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;

/**
 * Creates Buckets from OWLClassExpressions
 * @author minimal
 *
 */
public class BucketsGenerator {

	public static List<Bucket> create(List<OWLClassExpression> expressions){
		List<Bucket> buckets = new LinkedList<Bucket>();
		for(int i=0;i<expressions.size();i++) {
			OWLClassExpression expression = expressions.get(i);
			for(OWLClass concept : expression.getClassesInSignature()) {
				Bucket bucket = getCorrectBucket(buckets, concept);
				bucket.put(i, expression);
			}
		}
		
		return buckets;
	}
	
	private static Bucket getCorrectBucket(List<Bucket> buckets, OWLClass concept) {
		
		for(Bucket b : buckets) {
			if(concept == null && b.getConcept()==null) {
				return b;
			}
			else if(concept.equals(b.getConcept())) {
				return b;
			}
		}
		Bucket b  = new Bucket();
		b.setConcept(concept);
		buckets.add(b);
		return b;
	}
	
}
