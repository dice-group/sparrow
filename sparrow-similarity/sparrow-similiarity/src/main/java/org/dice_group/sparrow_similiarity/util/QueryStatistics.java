package org.dice_group.sparrow_similiarity.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.sparql.syntax.ElementWalker;

public class QueryStatistics {

	private int aggr=0;
	private int oneBGP=0;
	private int twoBGP=0;
	private int threeBGP=0;
	private int moreBGP=0;
	private int filter=0;
	private int optional=0;
	private int union=0;
	private int having=0;
	private int groupBy=0;
	private int offset=0;
	private double size=0.0;
	
	
	
	public static void main(String[] args) {
		try(BufferedReader reader = new BufferedReader(new FileReader(args[0]));
				PrintWriter pw = new PrintWriter(args[1])
						){
			String line;
			QueryStatistics qs = new QueryStatistics();
			int i=0;
			while((line=reader.readLine())!=null) {
				if(!line.isEmpty()) {
					Query q = QueryFactory.create(line);
					qs.getStatistics(q);
//					if(q.isSelectType() && (q.getLimit()>20000 || q.getLimit()<=0)) {
//						try {
//						QueryExecution exec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql",q);
//						ResultSet res = exec.execSelect();
//						int size = ResultSetFormatter.consume(res);
//						if(size>=10000)
//							q.setLimit(20000);
//						}catch(Exception e) {e.printStackTrace();}
//					}
//					System.out.println("did "+(i++));
//					pw.println(q.toString().replace("\n", " "));
				}
			}
			pw.println("1 BGP,"+qs.oneBGP/qs.size);
			pw.println("2 BGP,"+qs.twoBGP/qs.size);
			pw.println("3 BGP,"+qs.threeBGP/qs.size);
			pw.println(">3 BGP,"+qs.moreBGP/qs.size);
			pw.println("FILTER,"+qs.filter/qs.size);
			pw.println("OPTIONAL,"+qs.optional/qs.size);
			pw.println("UNION,"+qs.union/qs.size);
			pw.println("HAVING,"+qs.having/qs.size);
			pw.println("GROUP BY,"+qs.groupBy/qs.size);
			pw.println("AGGR,"+qs.aggr/qs.size);
			pw.println("OFFSET,"+qs.offset/qs.size);
			pw.println("SIZE,"+qs.size);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void getStatistics(Query q) {
		if(q.isSelectType()) {

			size++;
			offset+=q.hasOffset()?1:0;
			aggr+=q.hasAggregators()?1:0;
			groupBy+=q.hasGroupBy()?1:0;
			having+=q.hasHaving()?1:0;
			//TODO walk 
			StatisticsVisitor visitor = new StatisticsVisitor();
			visitor.setElementWhere(q.getQueryPattern());
			
			ElementWalker.walk(q.getQueryPattern(), visitor);
			union+=visitor.union?1:0;
			optional+=visitor.optional?1:0;
			filter+=visitor.filter?1:0;
			int bgps = visitor.bgps;
			if(bgps==1){oneBGP++;}
			if(bgps==2){twoBGP++;}
			if(bgps==3){threeBGP++;}
			if(bgps>3){moreBGP++;}
			
		}
	}
	
}
