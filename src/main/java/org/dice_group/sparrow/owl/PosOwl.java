package org.dice_group.sparrow.owl;

public class PosOwl {

	private OWLNode node;
	private int pos;
	private boolean isFinished=false;
	/**
	 * @param isFinished the isFinished to set
	 */
	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	/**
	 * @return the node
	 */
	public OWLNode getNode() {
		return node;
	}
	/**
	 * @param node the node to set
	 */
	public void setNode(OWLNode node) {
		this.node = node;
	}
	/**
	 * @return the pos
	 */
	public int getPos() {
		return pos;
	}
	/**
	 * @param pos the pos to set
	 */
	public void setPos(int pos) {
		this.pos = pos;
	}
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return isFinished;
	}
}
