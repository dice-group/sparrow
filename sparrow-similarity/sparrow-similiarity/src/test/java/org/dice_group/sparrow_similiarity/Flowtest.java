package org.dice_group.sparrow_similiarity;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class Flowtest {

	
	@Test
	public void fullFlow() throws OWLOntologyCreationException, IOException {
		Main.workflow(new String[] {"src/test/resources/ontology.owl"}, "src/test/resources/sparrow.tsv", "src/test/resources/spec");
	}
}
