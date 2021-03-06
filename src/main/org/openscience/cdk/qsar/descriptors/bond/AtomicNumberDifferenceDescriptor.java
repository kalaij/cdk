/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2007-2008  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.bond;

import java.io.IOException;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IBondDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * Describes the imbalance in atomic number of the IBond.
 *
 * @author      Egon Willighagen
 * @cdk.created 2007-12-29
 * @cdk.module  qsarbond
 * @cdk.githash
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:bondAtomicNumberImbalance
 */
@TestClass("org.openscience.cdk.qsar.descriptors.bond.AtomicNumberDifferenceDescriptorTest")
public class AtomicNumberDifferenceDescriptor implements IBondDescriptor {

	private static IsotopeFactory factory = null;
	
	private final static String[] descriptorName = {"MNDiff"};
	
    public AtomicNumberDifferenceDescriptor() {
    }	
    
    private void ensureIsotopeFactory(IChemObjectBuilder builder) {
    	if (factory == null) {
    		try {
	            factory = IsotopeFactory.getInstance(builder);
            } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
    	}
    }

	@TestMethod("testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#bondAtomicNumberImbalance",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit");
    }

	@TestMethod("testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
    }

	@TestMethod("testGetParameters")
    public Object[] getParameters() {
        return null;
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return descriptorName;
    }

    @TestMethod(value="testCalculate_IBond_IAtomContainer,testDescriptor1," +
        "testDescriptor2")
    public DescriptorValue calculate(IBond bond, IAtomContainer ac) {
    	ensureIsotopeFactory(ac.getBuilder());
    	if (bond.getAtomCount() != 2) {
    		return new DescriptorValue(
    			getSpecification(), getParameterNames(),
    			getParameters(),
    			new DoubleResult(Double.NaN),
    			descriptorName, new CDKException("Only 2-center bonds are considered")
    		);
    	}
    	
    	IAtom[] atoms = BondManipulator.getAtomArray(bond);

    	return new DescriptorValue(
    		getSpecification(), getParameterNames(),
    		getParameters(),
    		new DoubleResult(
    			Math.abs(factory.getElement(atoms[0].getSymbol()).getAtomicNumber() - 
    					 factory.getElement(atoms[1].getSymbol()).getAtomicNumber())
    		),
    		descriptorName);
    }

    @TestMethod(value="testGetParameterNames")
    public String[] getParameterNames() {
        return new String[0];
    }

    @TestMethod(value="testGetParameterType_String")
    public Object getParameterType(String name) {
        return null;
    }
}

