package org.dice_group.sparrow_similiarity.graph;

public class SimilarityPair {

	
	private Integer id1;
	private Integer id2;
	private double value;

	public SimilarityPair(Integer id1, Integer id2, double value) {
		this.setId1(id1);
		this.setId2(id2);
		this.setValue(value);
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * @return the id2
	 */
	public Integer getId2() {
		return id2;
	}

	/**
	 * @param id2 the id2 to set
	 */
	public void setId2(Integer id2) {
		this.id2 = id2;
	}

	/**
	 * @return the id1
	 */
	public Integer getId1() {
		return id1;
	}

	/**
	 * @param id1 the id1 to set
	 */
	public void setId1(Integer id1) {
		this.id1 = id1;
	}
	
	@Override
	public String toString() {
		return id1+"\t"+id2+"\t"+value;
	}
}
