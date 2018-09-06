package org.dice_group.sparrow_similiarity.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import similarity.measures.entities.PrimitiveEntitySimilarityMeasure;

public class ClassHierarchyPrimitiveEntitySimilarityMeasure extends PrimitiveEntitySimilarityMeasure {

	private static double DEFAULT_LEAST_ROLE_SIMILARITY = 0.01;

	private double discount;
	private OWLOntology o;

	public ClassHierarchyPrimitiveEntitySimilarityMeasure(OWLOntology o) {
		this(0.8, o);
	}

	public ClassHierarchyPrimitiveEntitySimilarityMeasure(double discount, OWLOntology o) {
		this.discount = discount;
		this.o = o;
	}

	@Override
	public double similarity(OWLEntity obj1, OWLEntity obj2) {
		if (canHaveValue(obj1, obj2)) {
			double simi = getClassHierarchySimi(obj1, obj2);
			if (obj1 instanceof OWLObjectProperty && obj2 instanceof OWLObjectProperty) {
				registerSimilarity((OWLObjectProperty) obj1, (OWLObjectProperty) obj2, simi);
			} else if (obj1 instanceof OWLClass && obj2 instanceof OWLClass) {
				registerSimilarity((OWLClass) obj1, (OWLClass) obj2, simi);
			}
		}
		return super.similarity(obj1, obj2);
	}

	private double getClassHierarchySimi(OWLEntity obj1, OWLEntity obj2) {
		if (obj1 instanceof OWLClass && obj2 instanceof OWLClass) {
			List<Set<? extends OWLObject>> obj1SuperClasses = buildSubClassOfTree((OWLClass) obj1);
			List<Set<? extends OWLObject>> obj2SuperClasses = buildSubClassOfTree((OWLClass) obj2);
			// iterate through
			int depthO1 = getDepth(obj1SuperClasses, obj2SuperClasses);
			int depthO2 = getDepth(obj2SuperClasses, obj1SuperClasses);
			if (depthO1 == -1 || depthO2 == -1) {
				return 0;
			}
			double discount1 = Math.pow(discount, depthO1);
			double discount2 = Math.pow(discount, depthO2);

			return (discount1 + discount2) / 2;
		}
		if (obj1 instanceof OWLObjectProperty && obj2 instanceof OWLObjectProperty) {
			List<Set<? extends OWLObject>> obj1SuperClasses = buildSubProperyOfTree((OWLObjectProperty) obj1);
			List<Set<? extends OWLObject>> obj2SuperClasses = buildSubProperyOfTree((OWLObjectProperty) obj2);
			// iterate through
			int depthO1 = getDepth(obj1SuperClasses, obj2SuperClasses);
			int depthO2 = getDepth(obj2SuperClasses, obj1SuperClasses);
			if (depthO1 == -1 || depthO2 == -1) {
				return 0;
			}
			double discount1 = Math.pow(discount, depthO1);
			double discount2 = Math.pow(discount, depthO2);

			return (discount1 + discount2) / 2;
		}
		return 0;
	}

	private List<Set<? extends OWLObject>> buildSubProperyOfTree(OWLObjectProperty obj1) {
		List<Set<? extends OWLObject>> ret = new ArrayList<Set<? extends OWLObject>>();
		ret.add(Sets.newHashSet(obj1));
		Set<OWLObjectPropertyExpression> superCl = obj1.getSuperProperties(o);
		if (superCl.isEmpty()) {
//			OWLObjectPropertyExpression inverse = 
			superCl.addAll(obj1.getInverses(o));
		}
		if (superCl.isEmpty()) {
			return ret;
		}
		ret.add(superCl);
		boolean hasTop = false;
		OWLObject top = null;
		do {
			Set<OWLObjectPropertyExpression> tmp = new HashSet<OWLObjectPropertyExpression>();
			for (OWLObject superClass : superCl) {
				if (superClass.isTopEntity()) {
					hasTop = true;
					top = superClass;
					continue;

				}
				
				Set<OWLObjectPropertyExpression> tmpDepth = ((OWLObjectProperty) superClass).getSuperProperties(o);
				if (!tmpDepth.isEmpty()) {
					boolean contains = false;
					for(OWLObject tmpO : tmpDepth) {
						for (Set<? extends OWLObject> prop : ret) {
							for(OWLObject propO : prop) {
								if(prop.contains(tmpO) || propO.toString().equals(tmpO.toString())){
									contains=true;
									break;
								}
							}
							if(contains) {break;}
						}
						if(contains) {break;}
					}
					if(!contains) {
						tmp.addAll(tmpDepth);
					}
				}
			}
			if (top != null) {
				superCl.remove(top);
				top = null;
			}
			superCl = tmp;
			if (!superCl.isEmpty()) {
				ret.add(superCl);
			}

		} while (!hasTop && !superCl.isEmpty());
		return ret;
	}

	private List<Set<? extends OWLObject>> buildSubClassOfTree(OWLClass obj1) {
		List<Set<? extends OWLObject>> ret = new LinkedList<Set<? extends OWLObject>>();
		ret.add(Sets.newHashSet(obj1));
		Set<? extends OWLObject> superCl = obj1.getSuperClasses(o);
		ret.add(superCl);
		boolean hasTop = false;
		do {
			Set<OWLObject> tmp = new HashSet<OWLObject>();
			for (OWLObject superClass : superCl) {
				if (superClass.isTopEntity()) {
					hasTop = true;
					continue;
				}
				Set<? extends OWLObject> tmpDepth = ((OWLClass) superClass).getSuperClasses(o);
				if (!tmpDepth.isEmpty()) {
					tmp.addAll(tmpDepth);
				}
			}
			superCl = tmp;
			if (!superCl.isEmpty()) {
				ret.add(superCl);
			}

		} while (!hasTop && !superCl.isEmpty());
		return ret;
	}

	private int getDepth(List<Set<? extends OWLObject>> obj1SuperClasses,
			List<Set<? extends OWLObject>> obj2SuperClasses) {
		int depthValue = 0;
		for (Set<? extends OWLObject> depth1 : obj1SuperClasses) {
			for (Set<? extends OWLObject> depth2 : obj2SuperClasses) {
				for (OWLObject superClass : depth2) {
					if (depth1.contains(superClass)) {
						return depthValue;
					}
				}
			}
			depthValue++;
		}
		return -1;
	}

	private boolean canHaveValue(OWLEntity obj1, OWLEntity obj2) {
		return !isRegistered(obj1, obj2) && !obj1.equals(obj2);
	}
	
	@Override
	public void deployRBox(OWLOntology ontology, OWLReasoner reasoner){
		if(!reasoner.isPrecomputed(InferenceType.OBJECT_PROPERTY_HIERARCHY))
			reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_HIERARCHY);
		
		for(OWLObjectProperty r : ontology.getObjectPropertiesInSignature()){
			// for sub-roles
			for(Node<OWLObjectPropertyExpression> n : reasoner.getSubObjectProperties(r, false)){
				for(OWLObjectPropertyExpression s : n.getEntities()){
					if(s.isBottomEntity()) continue;
					// here s is subsumed by r
					
					// first, for role subsumptions set similarity to 1 in the opposite direction
					this.registerSimilarity(r, s.asOWLObjectProperty(), 1);
					
					// second, for role subsumptions set similarity to something greater than 0
					if(similarity(s.asOWLObjectProperty(), r) == 0){
						registerSimilarity(s.asOWLObjectProperty(), r, DEFAULT_LEAST_ROLE_SIMILARITY); 
					}
				}
			}
			// for equivalent roles
			for(OWLObjectPropertyExpression s : reasoner.getEquivalentObjectProperties(r)){
				registerSimilarity(r, s.asOWLObjectProperty(), 1);
				registerSimilarity(s.asOWLObjectProperty(), r, 1);
			}
		}

	}
}
