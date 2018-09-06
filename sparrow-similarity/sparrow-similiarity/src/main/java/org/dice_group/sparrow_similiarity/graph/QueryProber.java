package org.dice_group.sparrow_similiarity.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.query.Query;
import org.dice_group.sparrow_similiarity.cluster.SimpleClusterAlgorithm;
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

	public List<Query> getProbeQueries(SimilarityGraph graph, int k) throws IOException {
		// create tmp file for bf algo
		String tmpFile = "tmpbf1.csv";
		try (PrintWriter pw = new PrintWriter(tmpFile)) {
			for (SimilarityPair pair : graph.getPairs()) {
				if(pair.getValue()!=0) {
					pw.println(pair.toString().replace("\t", ","));
				}
			}
		}
		BorderFlow flow = new BorderFlow(tmpFile, ",",new HardenSuperset());
//		String clusters = flow.knnToString(0.8, 250);
//		flow.
		String clusters  = flow.cluster(80.0, false, true, true);
//		String clusters = flow.knnToString(1, k);
//		SimpleClusterAlgorithm algo = new SimpleClusterAlgorithm();
//		List<Set<Integer>> clusters = algo.cluster(graph);
		
		System.out.println(clusters);
//		return cluster2Probes(clusters, tmpFile, graph);
		return cluster2Probes(clusters, tmpFile, graph);
	}

	private List<Query> cluster2Probes(String clusters, String tmpFile, SimilarityGraph graph) throws IOException {
		String[] clusterLines = clusters.split("\n");
		List<Query> probes = new LinkedList<Query>();
		File f = new File("cluster.out");
		f.createNewFile();
		PrintWriter pw2 = new PrintWriter("cluster.out");
		for (int i = 1; i < clusterLines.length; i++) {
			String cl = clusterLines[i].split("\t")[1];
			cl = cl.replace("[", "").replace("]", "");
			String[] ids;
			if (cl.contains(",")) {
				ids = cl.split(",");
			} else {
				ids = new String[] { cl.trim() };
			}
			try {
				Integer probeID = getMedianQueryID(ids, graph);
				probes.add(reader.getSparql().get(probeID));

				pw2.println("#### Cluster " + i + " ####");
				for (String idStr : ids) {
					int idInt = Integer.parseInt(idStr.trim());
					pw2.println(idStr + "\t" + reader.getSparql().get(idInt));
				}
				pw2.println();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		pw2.close();
//		new File(tmpFile).delete();

		return probes;
	}

	private List<Query> cluster2Probes(List<Set<Integer>> clusters, String tmpFile, SimilarityGraph graph) throws IOException {
		List<Query> probes = new LinkedList<Query>();
		File f = new File("cluster.out");
		f.createNewFile();
		int i=0;
		PrintWriter pw2 = new PrintWriter("cluster.out");
		for (Set<Integer> cluster : clusters) {

			try {
				Integer probeID = getMedianQueryID(cluster, graph);
				probes.add(reader.getSparql().get(probeID));
				i++;
				pw2.println("#### Cluster " + i + " ####");
				for (Integer idInt : cluster) {
					pw2.println(idInt + "\t" + reader.getSparql().get(idInt));
				}
				pw2.println();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		pw2.close();
//		new File(tmpFile).delete();

		return probes;
	}

	
	private Integer getMedianQueryID(Set<Integer> cluster, SimilarityGraph g) {
		Map<Integer, Double> sortedMap = new HashMap<Integer, Double>();
		List<Integer> idsInCluster = Lists.newArrayList(cluster);
		for (int i = 0; i < idsInCluster.size(); i++) {
			for (int j = i + 1; j < idsInCluster.size(); j++) {
				Integer id1 = idsInCluster.get(i);
				Integer id2 = idsInCluster.get(j);
				SimilarityPair pair = g.getPair(id1, id2);
				double value = 0.0;
				if (pair != null) {
					value = pair.getValue();
				}
				double old = 0.0;
				if (sortedMap.containsKey(id1)) {
					old = sortedMap.get(id1);
				}
				sortedMap.put(id1, old + value);
				old = 0.0;
				if (sortedMap.containsKey(id2)) {
					old = sortedMap.get(id2);
				}
				sortedMap.put(id2, old + value);
			}
		}
		Integer queryId = null;
		double highPoint = -1.0;
		double avg=0.0;
		for (Integer key : sortedMap.keySet()) {
			if (sortedMap.get(key) > highPoint) {
				highPoint = sortedMap.get(key);
				queryId = key;
			}
			avg+=sortedMap.get(key);
		}
		if (sortedMap.isEmpty()) {
			System.out.println("Cluster found : "+idsInCluster.get(0)+" with single Entity");
			return idsInCluster.get(0);
		}
		System.out.println("Cluster found : "+queryId+" (avg HP: "+(avg/sortedMap.size())+")with highpoint of "+highPoint);
		return queryId;
	}
	
	private Integer getMedianQueryID(String[] idsInCluster, SimilarityGraph g) {
		Map<Integer, Double> sortedMap = new HashMap<Integer, Double>();
		for (int i = 0; i < idsInCluster.length; i++) {
			for (int j = i + 1; j < idsInCluster.length; j++) {
				Integer id1 = Integer.parseInt(idsInCluster[i].trim());
				Integer id2 = Integer.parseInt(idsInCluster[j].trim());
				SimilarityPair pair = g.getPair(id1, id2);
				double value = 0.0;
				if (pair != null) {
					value = pair.getValue();
				}
				double old = 0.0;
				if (sortedMap.containsKey(id1)) {
					old = sortedMap.get(id1);
				}
				sortedMap.put(id1, old + value);
				old = 0.0;
				if (sortedMap.containsKey(id2)) {
					old = sortedMap.get(id2);
				}
				sortedMap.put(id2, old + value);
			}
		}
		Integer queryId = null;
		double highPoint = -1.0;
		for (Integer key : sortedMap.keySet()) {
			if (sortedMap.get(key) > highPoint) {
				highPoint = sortedMap.get(key);
				queryId = key;
			}
		}
		if (sortedMap.isEmpty()) {
			return Integer.parseInt(idsInCluster[0]);
		}
		System.out.println("Cluster found : "+queryId+" with highpoint of "+highPoint);
		return queryId;
	}

}
