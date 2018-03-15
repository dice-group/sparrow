package org.dice_group.sparrow.sparql;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.dice_group.sparrow.graph.GraphNode;

public class Sparql2Graph {
	
	public static GraphNode convertSparqlQuery(String query, String varName) {
		return convertSparqlQuery(QueryFactory.create(query), varName);
	}

	public static GraphNode convertSparqlQuery(Query create, String varName) {
		// TODO Auto-generated method stub
		return null;
	}

}
