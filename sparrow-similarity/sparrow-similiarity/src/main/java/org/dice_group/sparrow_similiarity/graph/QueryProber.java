package org.dice_group.sparrow_similiarity.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.jena.query.Query;
import org.dice_group.sparrow_similiarity.io.SparrowQueryTableReader;

import de.uni_leipzig.bf.cluster.BorderFlow;
import de.uni_leipzig.bf.cluster.Main.HardenStrategy;
import de.uni_leipzig.bf.cluster.harden.Harden;
import de.uni_leipzig.bf.cluster.harden.HardenMaxQuality;
import de.uni_leipzig.bf.cluster.harden.HardenSuperset;

public class QueryProber {

	private SparrowQueryTableReader reader;

	public QueryProber(SparrowQueryTableReader reader) {
		this.reader = reader;
	}

	public List<Query> getProbeQueries(SimilarityGraph graph, int k) throws FileNotFoundException {
		//create tmp file for bf algo
		String tmpFile = "tmpbf.tsv";
		try(PrintWriter pw = new PrintWriter(tmpFile)){
			for(SimilarityPair pair : graph.getPairs()) {
				pw.println(pair.toString());
			}			
		}
		BorderFlow flow = new BorderFlow(tmpFile, new HardenSuperset());
		String clusters = flow.cluster(k, true, true, false);
//		String clusters = flow.knnToString(1, k);
		System.out.println(clusters);
		String[] clusterLines = clusters.split("\n");
		List<Query> probes = new LinkedList<Query>();

		for(int i=1;i<clusterLines.length;i++) {
			String cl = clusterLines[i].split("\t")[1];
			cl = cl.replace("[", "").replace("]","");
			int probeID = Integer.parseInt(cl.split(",")[0]);
			probes.add(reader.getSparql().get(probeID));
		}
		new File(tmpFile).delete();
		return probes;
	}

}
