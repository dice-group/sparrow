package org.dice_group.sparrow.rule;

import org.apache.jena.graph.Node;
import org.dice_group.sparrow.graph.impl.TreeGraphPattern;
import org.dice_group.sparrow.owl.OWLClassExpression;
import org.dice_group.sparrow.owl.PrimitiveOWLParser;
import org.dice_group.sparrow.owl.impl.OWLPrimitiveExpression;
import org.dice_group.sparrow.owl.impl.OWLRuleExpression;

public class Rule {

	private String[] input;
	private String output;

	public Rule(String[] input, String output) {
		this.input = input;
		this.output = output;
	}

	public boolean fits(TreeGraphPattern tgp) {
		boolean fits = true;
		fits &= fitSingle(0, tgp.getSubject());
		fits &= fitSingle(1, tgp.getPredicate());
		fits &= fitSingle(2, tgp.getObject());
		return fits;

	}

	private boolean fitSingle(int i, Node n) {

		String str = n.toString(true);
		if (n.isURI()) {
			str = "<" + str + ">";
		}

		if (this.input[i].equals(str)) {
			return true;
		} else if (this.input[i].toUpperCase().startsWith("URI") && n.isURI()) {
			return true;
		} else if (this.input[i].toUpperCase().startsWith("VAR") && n.isVariable()) {
			return true;
		} else if (this.input[i].toUpperCase().startsWith("BNODE") && n.isBlank()) {
			return true;
		} else if (this.input[i].toUpperCase().startsWith("LITERAL") && n.isLiteral()) {
			return true;
		}
		return false;
	}

	public OWLClassExpression execute(TreeGraphPattern tgp, OWLClassExpression sub) {
		// if sub == null, convert tgp normally
		// if sub != null convert tgp using sub as exchange in sub
		OWLClassExpression child = sub;
		if (child == null && tgp.getObject().isVariable()) {
			child = OWLPrimitiveExpression.OWLThing;
		} else if (child == null) {
			String obj = "";
			if (tgp.getObject().isURI()) {
				obj = "<" + tgp.getObject().toString() + ">";
			} else {
				obj = tgp.getObject().toString(true);
			}
			child = new OWLPrimitiveExpression(obj);
		}
		// exchange subject, predicate, child
		OWLClassExpression clE = PrimitiveOWLParser.parse(this.output);
		if (clE instanceof OWLRuleExpression) {
			//test if predicate is variable
			String rule = "";
			if (tgp.getPredicate().isVariable()) {
				rule = "owl:topObjectProperty";
			} else {
				String obj = "";
				if (tgp.getPredicate().isURI()) {
					obj = "<" + tgp.getPredicate().toString() + ">";
				} else {
					obj = tgp.getPredicate().toString(true);
				}
				rule = obj;
			}
			((OWLRuleExpression) clE).setRule(new OWLPrimitiveExpression(rule));
			((OWLRuleExpression) clE).setRuledClass(child);
		} else if (clE instanceof OWLPrimitiveExpression) {
			((OWLPrimitiveExpression) clE).setPrimitive(child.toString());
		}
		return clE;
	}

}
