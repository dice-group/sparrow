package org.dice_group.sparrow_similiarity.print;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.query.Query;
import org.dice_group.sparrow_similiarity.graph.SimilarityGraph;
import org.dice_group.sparrow_similiarity.graph.SimilarityPair;
import org.dice_group.sparrow_similiarity.io.SparrowQueryTableReader;

public class PrintTable {

	
	private Double[][] arr;
	private SparrowQueryTableReader reader;
	
	public PrintTable(SparrowQueryTableReader reader) {
		this.reader= reader;
	}
	
	public void create(SimilarityGraph graph) {
		arr = new Double[reader.getSparql().size()][reader.getSparql().size()]; 
		for(SimilarityPair pair : graph.getPairs()){
			arr[pair.getId1()][pair.getId2()]=pair.getValue();
			arr[pair.getId2()][pair.getId1()]=pair.getValue();
		}
		for(int i=0;i<arr.length;i++) {
			arr[i][i]=1.0;
		}
	}

	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(Query sparql: reader.getSparql()) {
			builder.append("\t").append(sparql.toString().replace("\n", " "));
		}
		builder.append("\n");
		for(int i=0;i<arr.length;i++) {
			Query sparql = reader.getSparql().get(i);
			builder.append(sparql.toString().replace("\n", " "));
			for(int k=0;k<arr[i].length;k++) {
				if(arr[i][k]!=null) {
					builder.append("\t").append(arr[i][k]);
				}
				else {
					builder.append("\t");
				}
			}
			builder.append("\n");
		}
		return builder.toString();
	}
	
}
