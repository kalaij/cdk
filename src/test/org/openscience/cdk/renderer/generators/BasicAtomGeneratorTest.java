/* Copyright (C) 2010  Gilleain Torrance <gilleain.torrance@gmail.com>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer.generators;

import java.awt.Rectangle;
import java.util.List;

import javax.vecmath.Point2d;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.Shape;

public class BasicAtomGeneratorTest extends AbstractGeneratorTest {
	
	private BasicAtomGenerator generator;
	
	@Override
	public Rectangle getCustomCanvas() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		super.setup();
		this.generator = new BasicAtomGenerator();
		((IGeneratorParameter<Shape>)generator.getParameters().get(4)).setValue(Shape.OVAL);
		((IGeneratorParameter<Boolean>)generator.getParameters().get(5)).setValue(true);
		((IGeneratorParameter<Boolean>)generator.getParameters().get(6)).setValue(true);
		((IGeneratorParameter<Boolean>)generator.getParameters().get(7)).setValue(true);
	}
	
	@Test
	public void testSingleAtom() {
		IAtomContainer singleAtom = makeSingleAtom();
		
		// nothing should be made
		IRenderingElement root = generator.generate(singleAtom, model);
		List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
		Assert.assertEquals(1, elements.size());
	}
	
	@Test
	public void testSingleBond() {
		IAtomContainer container = makeSingleBond();
		
		// generate the single line element
		IRenderingElement root = generator.generate(container, model);
		List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
		Assert.assertEquals(2, elements.size());
		
		// test that the endpoints are distinct
		OvalElement ovalA = (OvalElement) elements.get(0);
		OvalElement ovalB = (OvalElement) elements.get(1);
		Assert.assertNotSame(0, distance(ovalA.x, ovalA.y, ovalB.x, ovalB.y));
	}
	
	@Test
	public void testSquare() {
		IAtomContainer square = makeSquare();
		
		// generate all four atoms
		IRenderingElement root = generator.generate(square, model);
		List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
		Assert.assertEquals(4, elements.size());
		
		// test that the center is at the origin
		Assert.assertEquals(new Point2d(0,0), center(elements));
	}
}
