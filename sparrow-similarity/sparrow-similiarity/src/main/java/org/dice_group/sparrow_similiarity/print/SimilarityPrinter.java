package org.dice_group.sparrow_similiarity.print;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import org.dice_group.sparrow_similiarity.graph.SimilarityGraph;
import org.dice_group.sparrow_similiarity.io.SparrowQueryTableReader;

public class SimilarityPrinter {

	public static final String FILENAME="sim"+Calendar.getInstance().getTimeInMillis()+".tsv";
	
	public static void print(SimilarityGraph graph, SparrowQueryTableReader reader) throws IOException {
		PrintTable table = new PrintTable(reader);
		table.create(graph);
		File f = new File(FILENAME);
		f.createNewFile();
		try(PrintWriter pw = new PrintWriter(FILENAME)){
			pw.print(table.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
