package org.dice_group.sparrow.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.dice_group.sparrow.annotation.impl.GroupAnnotation;
import org.junit.Test;

public class AnnotationsTest {

	@Test
	public void subTest() {
		GroupAnnotation anno = new GroupAnnotation();
		anno.setAnnotations(Sets.newHashSet(new GroupAnnotation(2), new GroupAnnotation(3), new GroupAnnotation(2)));
		assertEquals(2, anno.getAnnotations().size());
		assertTrue(anno.getAnnotations().contains(new GroupAnnotation(3)));
		assertTrue(anno.getAnnotations().contains(new GroupAnnotation(2)));
		assertNotEquals(anno, 0);
		
		assertEquals(0, anno.getID());
		anno.setID(1);
		assertEquals(1, anno.getID());
		
		anno.setParent(new GroupAnnotation());
		assertEquals(0, anno.getParent().getID());
	}
	
}
