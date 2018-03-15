package org.dice_group.sparrow.sparql;

import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementVisitor;
import org.apache.jena.sparql.syntax.RecursiveElementVisitor;

public class SparqlElementVisitor extends RecursiveElementVisitor {

	public SparqlElementVisitor(ElementVisitor visitor) {
		super(visitor);
	}

	private boolean started = false;
	private Element where;


	/**
	 * Sets the complete where clause element
	 * 
	 * @param el
	 */
	public void setElementWhere(Element el) {
		this.where = el;
	}

	public void endElement(ElementFilter el) {
		// TODO create filter step

		// TODO add filter merger
	}



	public void startElement(ElementGroup el) {
		if (!started && el.equals(where)) {
			// root element found
			started = true;
			// set initial merger
			rootStep = new GroupStep();
			lastStep = rootStep;

		} else if (started) {
			GroupStep group = new GroupStep();
			group.setParent(lastStep);
			lastStep.getChildSteps().add(group);
			lastStep = group;

		}
	}

	public void endElement(ElementGroup el) {
		// set back to parent
		if (started)
			this.lastStep = lastStep.getParent();
	}



	public void endElement(ElementPathBlock el) {
		if (started) {
			for (TriplePath path : el.getPattern().getList()) {
				if (path.getPredicate() != null) {
					// plain predicate
					PatternStep step = new PatternStep();
					step.setPattern(path);
					step.setParent(lastStep);
					lastStep.getChildSteps().add(step);
				}
			}

		}
	}



}
