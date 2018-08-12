package org.dice_group.sparrow_similiarity.print;

import java.util.LinkedList;
import java.util.List;
import org.dice_group.sparrow_similiarity.graph.SimilarityGraph;
import org.dice_group.sparrow_similiarity.graph.SimilarityPair;

public class PrintTable {

	private List<String> column  = new LinkedList<String>();
	private List<String>  row = new LinkedList<String>();

	private List<List<Double>> rows = new LinkedList<List<Double>>();
	
	public void create(SimilarityGraph graph) {
		for(SimilarityPair pair : graph.getPairs()){
			if(!column.contains(pair.getId1())) {
				column.add(pair.getId1().toString());
			}
			int colI = column.indexOf(pair.getId1().toString());
			if(!row.contains(pair.getId2())) {
				row.add(pair.getId2().toString());
			}
			int rowI = row.indexOf(pair.getId2().toString());
			
			addValue(colI, rowI, pair.getValue());
		}
	}

	private void addValue(int colI, int rowI, double value) {
		if(rows.size()<=rowI) {
			for(int i=rows.size();i<=rowI;i++) {
				rows.add(new LinkedList<Double>());
			}
		}
		List<Double> row = rows.get(rowI);
		if(row.size()<=colI) {
			for(int i=row.size();i<=colI;i++) {
				row.add(Double.NaN);
			}
		}
		row.set(colI, value);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(String col : column) {
			builder.append("\t").append(col);
		}
		for(List<Double> row : rows) {
			builder.append(this.row.get(rows.indexOf(row)));
			for(Double d : row) {
				builder.append("\t").append(d);
			}
			builder.append("\n");
		}
		return builder.toString();
	}
	
}
