package org.dice_group.sparrow_similiarity.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dice_group.sparrow_similiarity.cluster.Bucket;
import org.semanticweb.elk.owlapi.ElkReasonerHirarchyExpansion;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.tu_dresden.elastiq.interpretation.ds.CanonicalInterpretation;
import org.tu_dresden.elastiq.interpretation.ds.PointedInterpretation;
import org.tu_dresden.elastiq.interpretation.generator.IterativeQTBoxModelGenerator;
import org.tu_dresden.elastiq.similarity.algorithms.generalEL.GeneralELRelaxedInstancesAlgorithm;
import org.tu_dresden.elastiq.similarity.algorithms.specifications.BasicInputSpecification;
import org.tu_dresden.elastiq.similarity.measures.PointedISM;

import functions.connector.AverageFunction;
import functions.conorm.ProbabilisticSum;
import similarity.EntityWeightingFunction;
import similarity.measures.concepts.AtomicCSM;
import similarity.measures.concepts.DirectedCSM;
import similarity.measures.concepts.SymmetricCSM;
import similarity.measures.entities.PrimitiveEntitySimilarityMeasure;
import similarity.measures.entities.RandomPrimitiveEntitySimilarityMeasure;
import transform.normalize.ELHConceptNormalizer;

/**
 * 
 * In tribute to all the climbers of Yosemite Valley. ____________ | \_/ \_ | \
 * \ | \ \ | \ \ | \ \_ / \ \_ / | \ | | \ | | \ | | \ | | \ / | | / | | / | |
 * 
 * @author felix conrads
 *
 */
public class SimilarityGraph {

	private Set<SimilarityPair> pairs = new HashSet<SimilarityPair>();
	private OWLOntology ontology;
	private BasicInputSpecification inputSpec;
	private OWLReasoner reasoner;
	private SymmetricCSM simi;
	private ELHConceptNormalizer normalizer;

	public SimilarityGraph (Set<SimilarityPair> pairs) {
		this.pairs=pairs;
	}
	
	public SimilarityGraph(OWLOntology ontology, BasicInputSpecification inputSpec) {
		this.ontology = ontology;
		this.inputSpec = inputSpec;
		reasoner = new ElkReasonerHirarchyExpansion(ontology, false);
		PrimitiveEntitySimilarityMeasure pm = new ClassHierarchyPrimitiveEntitySimilarityMeasure(this.ontology);
		pm.deployRBox(ontology, reasoner);
		pm.printNonZeroSimilarities();

		AtomicCSM atomic = new AtomicCSM(pm, new DirectedCSM(ontology), 0.8);
		DirectedCSM simiD = new DirectedCSM(atomic, new ProbabilisticSum(), new EntityWeightingFunction(), ontology);

		simi = new SymmetricCSM(simiD, new AverageFunction());
		normalizer = new ELHConceptNormalizer(reasoner);
		simiD.registerNormalizer(normalizer);
	}

	public void executeSimilarity(List<Bucket> buckets) {
		int count = 0;
		for (Bucket bucket : buckets) {
			count++;
			int bCount = 0;
			System.out.println("Calculating Similarity values for Bucket [" + bucket.getConcept().toString() + "]");
			for (Integer id1 : bucket.keySet()) {
				for (Integer id2 : bucket.keySet()) {
					if (!id1.equals(id2) && !isCalculated(id1, id2)) {
						double simValue = 0, simValueI = 0;
//						try {
						OWLClassExpression q1 = bucket.get(id1);
						OWLClassExpression q2 = bucket.get(id2);

						q1 = normalizer.normalize(q1);
						q2 = normalizer.normalize(q2);
						simValue = simi.similarity(q1, q2);
						System.out.println(bucket.getConcept() + "[" + count + "/" + buckets.size() + ":" + (bCount)
								+ "/" + bucket.size() + "]: DirectedCSM Comparing " + id1 + " with " + id2
								+ " with sim value: " + simValue);

//						System.out.println(bucket.getConcept()+"["+count+"/"+buckets.size()+":"+bucket.size()+"]: Elastiq Comparing "+id1+" with "+id2+" with sim value: "+simValue);
//						}catch(Exception e) {e.printStackTrace();}
						SimilarityPair pair = new SimilarityPair(id1, id2, simValue);
						SimilarityPair pairI = new SimilarityPair(id2, id1, simValue);
						pairs.add(pair);
						pairs.add(pairI);
					}
				}
				bCount++;
			}

		}
	}

	private boolean isCalculated(Integer id1, Integer id2) {
		for (SimilarityPair pair : pairs) {
			if ((pair.getId1() == id1 && pair.getId2() == id2) || (pair.getId1() == id2 && pair.getId2() == id1)) {
				return true;
			}
		}
		return false;
	}

	public SimilarityPair getPair(Integer id1, Integer id2) {
		for (SimilarityPair pair : pairs) {
			if ((pair.getId1() == id1 && pair.getId2() == id2) || (pair.getId1() == id2 && pair.getId2() == id1)) {
				return pair;
			}
		}
		return null;

	}

	public Set<SimilarityPair> getPairs() {
		return this.pairs;
	}

}
