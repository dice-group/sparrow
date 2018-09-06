package org.dice_group.sparrow_similiarity.cluster;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.dice_group.sparrow_similiarity.graph.QueryProber;
import org.dice_group.sparrow_similiarity.graph.SimilarityGraph;
import org.dice_group.sparrow_similiarity.graph.SimilarityPair;

public class SimpleClusterAlgorithm {

	private List<Set<Integer>> clusters = new LinkedList<Set<Integer>>();
	private double threshold = 0.5;
	private int minChanges = 1;
	private int changes = 0;
	private int maxIteration = 10;

	public static void main(String[] args) {
		try (BufferedReader reader = new BufferedReader(new FileReader(args[0]));
				PrintWriter pw = new PrintWriter("simpleCluster.txt");) {
			String line;
			Set<SimilarityPair> pairs = new HashSet<SimilarityPair>();
			while ((line = reader.readLine()) != null) {
				if (!line.isEmpty()) {
					String[] pairStr = line.split("\t");
					SimilarityPair pair = new SimilarityPair(Integer.parseInt(pairStr[0]), Integer.parseInt(pairStr[1]),
							Double.parseDouble(pairStr[2]));
					pairs.add(pair);
				}
			}
			SimilarityGraph g = new SimilarityGraph(pairs);
			SimpleClusterAlgorithm algo = new SimpleClusterAlgorithm();
			List<Set<Integer>> cl = algo.cluster(g);
			int clusterID = 0;
			for (Set<Integer> c : cl) {
				pw.println("####### Cluster " + (clusterID++) + "  #######");
				for (Integer i : c) {
					pw.print(i + ", ");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Set<Integer>> cluster(SimilarityGraph graph) {
		init(graph);
		int iteration = 0;
		do {
			changes = 0;
//			clusterFock(graph);
			iteration++;
		} while (minChanges <= changes && maxIteration > iteration);
		return clusters;

	}

	private void init(SimilarityGraph graph) {
		int i = 1;
		for (SimilarityPair pair : graph.getPairs()) {
			System.out.println("At : " + (i++) + "/" + graph.getPairs().size());
			Set<Integer> id1Cl = contains(pair.getId1());
			Set<Integer> id2Cl = contains(pair.getId2());

			if (pair.getValue() == 1) {
				// both the same -> put in same cluster
				// check if id is already in cluster
				if (id1Cl == null && id2Cl == null) {
					Set<Integer> bestCluster = findBestCluster(pair.getId1(), graph);
					bestCluster.add(pair.getId1());
					bestCluster.add(pair.getId2());
				}
				else if(id1Cl==null && id2Cl!=null) {
					id2Cl.add(pair.getId1());
				}
				else if(id1Cl!=null && id2Cl==null) {
					id1Cl.add(pair.getId2());
				}
			} else {
				// TODO check if id is already in cluster
				if (id1Cl == null) {
					Set<Integer> bestCluster = findBestCluster(pair.getId1(), graph);
					bestCluster.add(pair.getId1());
				}
				if (id2Cl == null) {
					Set<Integer> bestCluster = findBestCluster(pair.getId2(), graph);
					bestCluster.add(pair.getId2());
				}
			}
		}
	}

	private Set<Integer> contains(Integer id1) {
		for (Set<Integer> cl : clusters) {
			if (cl.contains(id1)) {
				return cl;
			}
		}
		return null;
	}

	private void clusterFock(SimilarityGraph graph) {
		for (SimilarityPair pair : graph.getPairs()) {
			double simi1H = -1.0;
			double simi2H = -1.0;
			Set<Integer> cluster1=null, cluster2 = null;
			for (Set<Integer> cluster : clusters) {
				double simi1 = getSimi(pair.getId1(), cluster, graph);
				double simi2 = getSimi(pair.getId2(), cluster, graph);
				if (simi1 > simi1H) {
					// add to cluster
					if (!cluster.contains(pair.getId1())) {
						// add to cluster and remove from old
						cluster1 = cluster;
					}
				}
				if (simi2 > simi2H) {
					if (!cluster.contains(pair.getId2())) {
						// add to cluster and remove from old
						cluster2 = cluster;
					}
				}
				
			}
			if(cluster1!=null) {
				removeFromOldCluster(pair.getId1());
				cluster1.add(pair.getId1());
				changes++;
			}
			if(cluster2!=null) {
				removeFromOldCluster(pair.getId2());
				cluster2.add(pair.getId2());
				changes++;
			}
		}
	}

	private void removeFromOldCluster(Integer id1) {
		for (Set<Integer> cluster : clusters) {
			if (cluster.contains(id1)) {
				cluster.remove(id1);
				return;
			}
		}
	}

	private Set<Integer> findBestCluster(Integer id1, SimilarityGraph graph) {
		Set<Integer> cluster = Sets.newHashSet();
		boolean foundSome = false;
		double old = -1.0;
		for (Set<Integer> cluster1 : clusters) {
			double newVal = getSimi(id1, cluster1, graph);
			if (newVal >= threshold && newVal > old) {
				// better cluster found
				cluster = cluster1;
				foundSome = true;
			}
		}
		if (!foundSome) {
			clusters.add(cluster);
		}
		cluster.add(id1);
		return cluster;
	}

	private double getSimi(Integer id1, Set<Integer> cluster1, SimilarityGraph graph) {
		Double simi = 0.0;
		for (Integer id2 : cluster1) {
			SimilarityPair curr;
			if ((curr = graph.getPair(id1, id2)) != null) {
				simi += curr.getValue();
			}
		}
		return simi / getDiscount(cluster1.size());
	}

	private double getDiscount(int size) {
		double avg = 0.0;
		if (clusters.size() == 0) {
			return 1;
		}
		for (Set<Integer> cl : clusters) {
			avg += cl.size();
		}
		avg = avg / clusters.size();
		return size / avg;
	}

}
