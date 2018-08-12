package org.dice_group.sparrow_similiarity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.jena.query.Query;
import org.dice_group.sparrow_similiarity.cluster.Bucket;
import org.dice_group.sparrow_similiarity.cluster.BucketsGenerator;
import org.dice_group.sparrow_similiarity.graph.QueryProber;
import org.dice_group.sparrow_similiarity.graph.SimilarityGraph;
import org.dice_group.sparrow_similiarity.io.SparrowQueryTableReader;
import org.dice_group.sparrow_similiarity.print.SimilarityPrinter;
import org.dice_group.sparrow_similiarity.print.StatisticsPrinter;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.tu_dresden.elastiq.similarity.algorithms.specifications.parser.WeightedInputSpecificationParser;

/**
 * Hello world!
 *
 */
public class Main 
{
    public static void main( String[] args ) throws OWLOntologyCreationException, IOException
    {
       workflow(Arrays.copyOfRange(args, 2, args.length), args[0], args[1]); 
    }
    
    
    public static void workflow(String[] ontologieFileNames, String sparrowFile, String spec) throws OWLOntologyCreationException, IOException {
    	SparrowQueryTableReader reader = new SparrowQueryTableReader(ontologieFileNames);
    	reader.parse(sparrowFile);
    	List<Bucket> buckets = BucketsGenerator.create(reader.getOwl());
    	WeightedInputSpecificationParser parser = new WeightedInputSpecificationParser(new File(spec));
    	org.tu_dresden.elastiq.main.Main.setInput(parser.parse());

    	SimilarityGraph graph = new SimilarityGraph(reader.getOWLOntology(), parser.parse());
    	graph.executeSimilarity(buckets);
//    	SimilarityPrinter.print(graph);
    	//StatisticsPrinter.print(graph, buckets);
    	
    	QueryProber prober = new QueryProber(reader);
    	int k = buckets.size();

    	System.out.println("Clustering with k="+k);
    	List<Query> queries = prober.getProbeQueries(graph, k);
    	try(PrintWriter pw = new PrintWriter("probeQueries.txt")){
    		for(Query q : queries) {
    			pw.println(q.serialize().replace("\n", " "));
    		}
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
}
