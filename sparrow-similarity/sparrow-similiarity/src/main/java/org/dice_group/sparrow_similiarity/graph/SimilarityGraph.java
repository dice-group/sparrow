package org.dice_group.sparrow_similiarity.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dice_group.sparrow_similiarity.cluster.Bucket;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.tu_dresden.elastiq.interpretation.ds.CanonicalInterpretation;
import org.tu_dresden.elastiq.interpretation.ds.PointedInterpretation;
import org.tu_dresden.elastiq.interpretation.generator.IterativeQTBoxModelGenerator;
import org.tu_dresden.elastiq.similarity.algorithms.generalEL.GeneralELRelaxedInstancesAlgorithm;
import org.tu_dresden.elastiq.similarity.algorithms.specifications.BasicInputSpecification;
import org.tu_dresden.elastiq.similarity.measures.PointedISM;

/**        
 * 
 * In tribute to all the climbers of Yosemite Valley. 
 *         ____________
 *        | \_/           \_
 *        |    \             \
 *        |     \              \
 *        |       \              \
 *        |         \              \_ 
 *       /            \               \_
 *      /             |              	 \
 *     |               |                   \
 *    |                 |                   \
 *    |                 |                    \ 
 *    |                 |                     \ 
 *   /                  |                      |
 *  /                   |                       |
 * /                    |                        | 
 * 
 * @author felix conrads
 *
 */
public class SimilarityGraph {

	private Set<SimilarityPair> pairs = new HashSet<SimilarityPair>();
	private OWLOntology ontology;
	private BasicInputSpecification inputSpec;
	private GeneralELRelaxedInstancesAlgorithm algo;

	public SimilarityGraph(OWLOntology ontology, BasicInputSpecification inputSpec) {
		this.ontology = ontology;
		this.inputSpec=inputSpec;
		algo = new GeneralELRelaxedInstancesAlgorithm();
	}
	
	public void executeSimilarity(List<Bucket> buckets) {
		int count=0;
		for(Bucket bucket : buckets) {
			count++;
			System.out.println("Calculating Similarity values for Bucket ["+bucket.getConcept().toString()+"]");
			for(Integer id1 : bucket.keySet()) {
				for(Integer id2 : bucket.keySet()) {
					if(!id1.equals(id2) && !isCalculated(id1, id2)) {
						double simValue=0;
//						try {
						OWLClassExpression q1 = bucket.get(id1);
						OWLClassExpression q2 = bucket.get(id2);
						
						IterativeQTBoxModelGenerator generator1 = new IterativeQTBoxModelGenerator(q1, true);
						CanonicalInterpretation tBox1 = generator1.generate(ontology);
						OWLClass cl1 = generator1.getClassRepresentation(q1);
						PointedInterpretation piQuery1 = new PointedInterpretation(tBox1, tBox1.getDomain().getDomainNode(cl1));

						IterativeQTBoxModelGenerator generator2 = new IterativeQTBoxModelGenerator(q2, true);
						CanonicalInterpretation tBox2 = generator2.generate(ontology);
						OWLClass cl2 = generator2.getClassRepresentation(q2);
						PointedInterpretation piQuery2 = new PointedInterpretation(tBox2, tBox2.getDomain().getDomainNode(cl2));
						
						PointedISM ism = new PointedISM(inputSpec, algo);
						simValue = ism.similarity(piQuery1, piQuery2);
						System.out.println(bucket.getConcept()+"["+count+"/"+buckets.size()+":"+bucket.size()+"]: Comparing "+id1+" with "+id2+" with sim value: "+simValue);
//						}catch(Exception e) {e.printStackTrace();}
						SimilarityPair pair = new SimilarityPair(id1,id2,simValue);
						pairs.add(pair);
					}
				}
			}
		}
	}
	
	private boolean isCalculated(Integer id1, Integer id2) {
		for(SimilarityPair pair : pairs) {
			if((pair.getId1()==id1 && pair.getId2()==id2) || (pair.getId1()==id2 && pair.getId2()==id1 )) {
				return true;
			}
		}
		return false;
	}

	public SimilarityPair getPair(Integer id1, Integer id2) {
		for(SimilarityPair pair : pairs) {
			if((pair.getId1()==id1 && pair.getId2()==id2) || (pair.getId1()==id2 && pair.getId2()==id1 )) {
				return pair;
			}
		}
		return null;
		
	}
	
	public Set<SimilarityPair> getPairs(){
		return this.pairs;
	}

	
}
