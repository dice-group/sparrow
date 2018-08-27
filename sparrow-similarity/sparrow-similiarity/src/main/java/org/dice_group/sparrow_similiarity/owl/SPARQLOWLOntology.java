package org.dice_group.sparrow_similiarity.owl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLMutableOntology;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

public class SPARQLOWLOntology extends OWLObjectImpl implements OWLMutableOntology {

	private OWLOntologyID ontologyID;
	private OWLOntologyManager manager;
	private String service;

	@Override
	public void accept(OWLObjectVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <O> O accept(OWLObjectVisitorEx<O> visitor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OWLOntologyManager getOWLOntologyManager() {
		return manager;
	}

	@Override
	public OWLOntologyID getOntologyID() {
		return ontologyID;
	}

	@Override
	public boolean isAnonymous() {
		return ontologyID.isAnonymous();
	}

	@Override
	public Set<OWLAnnotation> getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRI> getDirectImportsDocuments() throws UnknownOWLOntologyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLOntology> getDirectImports() throws UnknownOWLOntologyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLOntology> getImports() throws UnknownOWLOntologyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLOntology> getImportsClosure() throws UnknownOWLOntologyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLImportsDeclaration> getImportsDeclarations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		String query  = "ASK {?s ?p ?o}";
		QueryExecution exec = QueryExecutionFactory.createServiceRequest(service, QueryFactory.create(query));
		return !exec.execAsk();
	}

	@Override
	public Set<OWLAxiom> getAxioms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getAxiomCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<OWLLogicalAxiom> getLogicalAxioms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLogicalAxiomCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> axiomType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends OWLAxiom> Set<T> getAxioms(AxiomType<T> axiomType, boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAxiom> getTBoxAxioms(boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAxiom> getABoxAxioms(boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAxiom> getRBoxAxioms(boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends OWLAxiom> int getAxiomCount(AxiomType<T> axiomType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T extends OWLAxiom> int getAxiomCount(AxiomType<T> axiomType, boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean containsAxiom(OWLAxiom axiom) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAxiom(OWLAxiom axiom, boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAxiomIgnoreAnnotations(OWLAxiom axiom) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<OWLAxiom> getAxiomsIgnoreAnnotations(OWLAxiom axiom) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAxiom> getAxiomsIgnoreAnnotations(OWLAxiom axiom, boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsAxiomIgnoreAnnotations(OWLAxiom axiom, boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<OWLClassAxiom> getGeneralClassAxioms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLEntity> getSignature(boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Set<OWLClass> getClassesInSignature(boolean includeImportsClosure) {
		Set<OWLClass> classes = new HashSet<OWLClass>();
		String query  = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?class WHERE {?class rdf:type owl:Class}";
		QueryExecution exec = QueryExecutionFactory.createServiceRequest(service, QueryFactory.create(query));
		ResultSet res = exec.execSelect();
		while(res.hasNext()){
			QuerySolution solution = res.next();
			String className = solution.get("class").toString();
			OWLClass clazz = new OWLClassImpl(IRI.create(className));
			classes.add(clazz);
		}
		return classes;
	}

	@Override
	public Set<OWLObjectProperty> getObjectPropertiesInSignature(boolean includeImportsClosure) {
		Set<OWLObjectProperty> objProps = new HashSet<OWLObjectProperty>();
		String query  = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?class WHERE {?class rdf:type owl:ObjectProperty}";
		QueryExecution exec = QueryExecutionFactory.createServiceRequest(service, QueryFactory.create(query));
		ResultSet res = exec.execSelect();
		while(res.hasNext()){
			QuerySolution solution = res.next();
			String className = solution.get("class").toString();
			OWLObjectProperty clazz = new OWLObjectPropertyImpl(IRI.create(className));
			objProps.add(clazz);
		}
		return objProps;
	}

	@Override
	public Set<OWLDataProperty> getDataPropertiesInSignature(boolean includeImportsClosure) {
		Set<OWLDataProperty> objProps = new HashSet<OWLDataProperty>();
		String query  = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?class WHERE {?class rdf:type rdf:Property. MINUS {?class rdf:type owl:ObjectProperty}}";
		QueryExecution exec = QueryExecutionFactory.createServiceRequest(service, QueryFactory.create(query));
		ResultSet res = exec.execSelect();
		while(res.hasNext()){
			QuerySolution solution = res.next();
			String className = solution.get("class").toString();
			OWLDataProperty clazz = new OWLDataPropertyImpl(IRI.create(className));
			objProps.add(clazz);
		}
		return objProps;
	}

	@Override
	public Set<OWLNamedIndividual> getIndividualsInSignature(boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAnonymousIndividual> getReferencedAnonymousIndividuals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLDatatype> getDatatypesInSignature(boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAxiom> getReferencingAxioms(OWLEntity owlEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAxiom> getReferencingAxioms(OWLEntity owlEntity, boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAxiom> getReferencingAxioms(OWLAnonymousIndividual individual) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsEntityInSignature(OWLEntity owlEntity, boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsEntityInSignature(IRI entityIRI) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsEntityInSignature(IRI entityIRI, boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDeclared(OWLEntity owlEntity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDeclared(OWLEntity owlEntity, boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsClassInSignature(IRI owlClassIRI) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsClassInSignature(IRI owlClassIRI, boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsObjectPropertyInSignature(IRI owlObjectPropertyIRI) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsObjectPropertyInSignature(IRI owlObjectPropertyIRI, boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsDataPropertyInSignature(IRI owlDataPropertyIRI) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsDataPropertyInSignature(IRI owlDataPropertyIRI, boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAnnotationPropertyInSignature(IRI owlAnnotationPropertyIRI) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAnnotationPropertyInSignature(IRI owlAnnotationPropertyIRI, boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsIndividualInSignature(IRI owlIndividualIRI) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsIndividualInSignature(IRI owlIndividualIRI, boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsDatatypeInSignature(IRI owlDatatypeIRI) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsDatatypeInSignature(IRI owlDatatypeIRI, boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<OWLEntity> getEntitiesInSignature(IRI iri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLEntity> getEntitiesInSignature(IRI iri, boolean includeImportsClosure) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLClassAxiom> getAxioms(OWLClass cls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLObjectPropertyAxiom> getAxioms(OWLObjectPropertyExpression prop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLDataPropertyAxiom> getAxioms(OWLDataProperty prop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLIndividualAxiom> getAxioms(OWLIndividual individual) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAnnotationAxiom> getAxioms(OWLAnnotationProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLDatatypeDefinitionAxiom> getAxioms(OWLDatatype datatype) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLSubAnnotationPropertyOfAxiom> getSubAnnotationPropertyOfAxioms(OWLAnnotationProperty subProperty) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAnnotationPropertyDomainAxiom> getAnnotationPropertyDomainAxioms(OWLAnnotationProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAnnotationPropertyRangeAxiom> getAnnotationPropertyRangeAxioms(OWLAnnotationProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLDeclarationAxiom> getDeclarationAxioms(OWLEntity subject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(OWLAnnotationSubject entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLSubClassOfAxiom> getSubClassAxiomsForSubClass(OWLClass cls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLSubClassOfAxiom> getSubClassAxiomsForSuperClass(OWLClass cls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLEquivalentClassesAxiom> getEquivalentClassesAxioms(OWLClass cls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLDisjointClassesAxiom> getDisjointClassesAxioms(OWLClass cls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLDisjointUnionAxiom> getDisjointUnionAxioms(OWLClass owlClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLHasKeyAxiom> getHasKeyAxioms(OWLClass cls) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSubProperty(
			OWLObjectPropertyExpression subProperty) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSuperProperty(
			OWLObjectPropertyExpression superProperty) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLObjectPropertyDomainAxiom> getObjectPropertyDomainAxioms(OWLObjectPropertyExpression property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLObjectPropertyRangeAxiom> getObjectPropertyRangeAxioms(OWLObjectPropertyExpression property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLInverseObjectPropertiesAxiom> getInverseObjectPropertyAxioms(OWLObjectPropertyExpression property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLEquivalentObjectPropertiesAxiom> getEquivalentObjectPropertiesAxioms(
			OWLObjectPropertyExpression property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLDisjointObjectPropertiesAxiom> getDisjointObjectPropertiesAxioms(
			OWLObjectPropertyExpression property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLFunctionalObjectPropertyAxiom> getFunctionalObjectPropertyAxioms(
			OWLObjectPropertyExpression property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLInverseFunctionalObjectPropertyAxiom> getInverseFunctionalObjectPropertyAxioms(
			OWLObjectPropertyExpression property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLSymmetricObjectPropertyAxiom> getSymmetricObjectPropertyAxioms(OWLObjectPropertyExpression property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLAsymmetricObjectPropertyAxiom> getAsymmetricObjectPropertyAxioms(
			OWLObjectPropertyExpression property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLReflexiveObjectPropertyAxiom> getReflexiveObjectPropertyAxioms(OWLObjectPropertyExpression property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLIrreflexiveObjectPropertyAxiom> getIrreflexiveObjectPropertyAxioms(
			OWLObjectPropertyExpression property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLTransitiveObjectPropertyAxiom> getTransitiveObjectPropertyAxioms(
			OWLObjectPropertyExpression property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSubProperty(OWLDataProperty subProperty) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSuperProperty(
			OWLDataPropertyExpression superProperty) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLDataPropertyDomainAxiom> getDataPropertyDomainAxioms(OWLDataProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLDataPropertyRangeAxiom> getDataPropertyRangeAxioms(OWLDataProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLEquivalentDataPropertiesAxiom> getEquivalentDataPropertiesAxioms(OWLDataProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLDisjointDataPropertiesAxiom> getDisjointDataPropertiesAxioms(OWLDataProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLFunctionalDataPropertyAxiom> getFunctionalDataPropertyAxioms(OWLDataPropertyExpression property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(OWLIndividual individual) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLClassAssertionAxiom> getClassAssertionAxioms(OWLClassExpression ce) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLDataPropertyAssertionAxiom> getDataPropertyAssertionAxioms(OWLIndividual individual) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertionAxioms(OWLIndividual individual) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLNegativeObjectPropertyAssertionAxiom> getNegativeObjectPropertyAssertionAxioms(
			OWLIndividual individual) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLNegativeDataPropertyAssertionAxiom> getNegativeDataPropertyAssertionAxioms(OWLIndividual individual) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLSameIndividualAxiom> getSameIndividualAxioms(OWLIndividual individual) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLDifferentIndividualsAxiom> getDifferentIndividualAxioms(OWLIndividual individual) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<OWLDatatypeDefinitionAxiom> getDatatypeDefinitions(OWLDatatype datatype) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int compareObjectOfSameType(OWLObject object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<OWLOntologyChange> applyChange(OWLOntologyChange change) throws OWLOntologyChangeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OWLOntologyChange> applyChanges(List<OWLOntologyChange> changes) throws OWLOntologyChangeException {
		// TODO Auto-generated method stub
		return null;
	}


}
