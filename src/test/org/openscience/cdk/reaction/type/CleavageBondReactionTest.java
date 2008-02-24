/* $RCSfile$
 * $Author: miguelrojasch $
 * $Date: 2006-05-11 14:25:07 +0200 (Do, 11 Mai 2006) $
 * $Revision: 6221 $
 *
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.test.reaction.type;


import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.CleavageBondReaction;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LonePairElectronChecker;

/**
 * TestSuite that runs a test for the CleavageBondReactionTest.
 * Generalized Reaction: A=B => [A*]-[B*]
 *
 * @cdk.module test-reaction
 */
public class CleavageBondReactionTest extends CDKTestCase {
	
	private IReactionProcess type;

	/**
	 * Constructror of the CleavageBondReactionTest object
	 *
	 */
	public  CleavageBondReactionTest() {
		type  = new CleavageBondReaction();
	}
    
	public static Test suite() {
		return new TestSuite(CleavageBondReactionTest.class);
	}

	/**
	 * A unit test suite for JUnit. Reaction: 
	 * CCc1ccc(C=O)cc1  =>  C+ Cc1ccc(C=O)cc1
	 * CCc1ccc(C=O)cc1  =>  CC + c1ccc(C=O)cc1 
	 * Automatic sarch of the centre active.
	 *
	 * @return    The test suite
	 */
	public void test1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		
		/*C=O*/
		IMolecule molecule = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("CCc1ccc(C=O)cc1");
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(molecule);
		
		molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
		setOfReactants.addMolecule(molecule);
		
		/*has active center*/
        Object[] params = {Boolean.TRUE};
        type.setParameters(params);
        
        /* iniciate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(2, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());
        Assert.assertEquals(2, setOfReactions.getReaction(1).getProductCount());

        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        /*C*/
		IMolecule molecule2 = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("C");
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
		
		product = setOfReactions.getReaction(0).getProducts().getMolecule(1);
		/*Cc1ccc(C=O)cc1*/
		IMolecule molecule3 = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("Cc1ccc(C=O)cc1");
		
        qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule3,qAC));
		
		product = setOfReactions.getReaction(1).getProducts().getMolecule(0);
        /*CC*/
		molecule2 = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("CC");
        qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));

		product = setOfReactions.getReaction(1).getProducts().getMolecule(1);
		/*c1ccc(C=O)cc1*/
		molecule3 = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("c1ccc(C=O)cc1");
		molecule2.addSingleElectron(new SingleElectron(molecule3.getAtom(0)));
		
        qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule3,qAC));
		
		
	}
}