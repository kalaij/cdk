/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.ringsearch;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Ring;
import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNChemFile;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.NewCDKTestCase;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.InputStream;
import java.util.Iterator;

/**
 * @cdk.module test-standard
 */
public class AllRingsFinderTest extends NewCDKTestCase
{
	boolean standAlone = false;
	
	public AllRingsFinderTest()
	{
		super();
	}	

	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}

    @Test public void testAllRingsFinder()
	{
		AllRingsFinder arf = new AllRingsFinder();
		Assert.assertNotNull(arf);
	}

	@Test public void testFindAllRings_IAtomContainer() throws Exception {
		IRingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		if (standAlone) arf.debug = true;
		Molecule molecule = MoleculeFactory.makeEthylPropylPhenantren();
		//display(molecule);

		ringSet = arf.findAllRings(molecule);

		Assert.assertEquals(6, ringSet.getAtomContainerCount());
	}
	
	/**
	 * @cdk.bug 746067
	 */
	@Test public void testBondsWithinRing() throws Exception {
		IRingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		if (standAlone) arf.debug = true;
		Molecule molecule = MoleculeFactory.makeEthylPropylPhenantren();
		//display(molecule);

		ringSet = arf.findAllRings(molecule);
		for (int i = 0; i < ringSet.getAtomContainerCount(); i++) 
		{
			Ring ring = (Ring)ringSet.getAtomContainer(i);
			for (int j = 0; j < ring.getBondCount(); j++) 
			{
				IBond ec = ring.getBond(j);

				IAtom atom1 = ec.getAtom(0);
				IAtom atom2 = ec.getAtom(1);
				Assert.assertTrue(ring.contains(atom1));
				Assert.assertTrue(ring.contains(atom2));
			}
		}
	}
	
	@Test public void testFindAllRings_IAtomContainer_boolean() throws CDKException {
		AllRingsFinder arf = new AllRingsFinder();
		Molecule molecule = MoleculeFactory.makeEthylPropylPhenantren();
		arf.findAllRings(molecule);
	}
	
	@Test(expected = CDKException.class) public void testSetTimeout_long() throws CDKException {
		AllRingsFinder arf = new AllRingsFinder();
		arf.setTimeout(1);
		Molecule molecule = MoleculeFactory.makeEthylPropylPhenantren();
        arf.findAllRings(molecule);
    }
	
	@Test public void testCheckTimeout() throws Exception {
		AllRingsFinder arf = new AllRingsFinder();
		arf.setTimeout(3);
		arf.checkTimeout();
	}
	
	@Test public void testGetTimeout()
	{
		AllRingsFinder arf = new AllRingsFinder();
		arf.setTimeout(3);
		Assert.assertEquals(3, arf.getTimeout(), 0.01);
	}
	
	@Test public void testPorphyrine() throws Exception {
		IRingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		if (standAlone) arf.debug = true;

		String filename = "data/mdl/porphyrin.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
		IChemSequence seq = chemFile.getChemSequence(0);
		IChemModel model = seq.getChemModel(0);
		IMolecule molecule = model.getMoleculeSet().getMolecule(0);

		ringSet = arf.findAllRings(molecule);
		Assert.assertEquals(20, ringSet.getAtomContainerCount());
	}
	
	@Test public void testBigRingSystem() throws Exception {
		if (!runSlowTests()) Assert.fail("Not running slow tests: this should find 1976 rings");
		
		IRingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		arf.setTimeout(120000); // 2 min should do, took 37 secs on my system

		String filename = "data/mdl/ring_03419.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		IChemFile chemFile = (IChemFile) reader.read(new NNChemFile());
		IChemSequence seq = chemFile.getChemSequence(0);
		IChemModel model = seq.getChemModel(0);
		IMolecule molecule = model.getMoleculeSet().getMolecule(0);

		ringSet = arf.findAllRings(molecule);
		// the 1976 value was empirically derived, and might not be accurate
		Assert.assertEquals(1976, ringSet.getAtomContainerCount());
	}
	
	@Test public void testCholoylCoA() throws Exception {
		IRingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		if (standAlone) arf.debug = true;

		String filename = "data/mdl/choloylcoa.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
		IChemSequence seq = chemFile.getChemSequence(0);
		IChemModel model = seq.getChemModel(0);
		IMolecule molecule = model.getMoleculeSet().getMolecule(0);

		ringSet = arf.findAllRings(molecule);
		Assert.assertEquals(14, ringSet.getAtomContainerCount());
	}
	
	public void showAzulene() throws Exception {
		MoleculeListViewer listview = new MoleculeListViewer();
		IRingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		if (standAlone) arf.debug = true;
		
		String filename = "data/mdl/azulene.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
		IChemSequence seq = chemFile.getChemSequence(0);
		IChemModel model = seq.getChemModel(0);
		IMolecule molecule = model.getMoleculeSet().getMolecule(0);
		listview.addStructure(molecule, "Azulene", false, false);

		ringSet = arf.findAllRings(molecule);
		for (int i = 0; i < ringSet.getAtomContainerCount(); i++) 
		{
			IAtomContainer ac = ringSet.getAtomContainer(i);
			Molecule newMol = new Molecule(ac);
			listview.addStructure(newMol, "ring no. " + (i+1), false, false);
		}
	}
	
	public void showPorphyrin() throws Exception
	{
		MoleculeListViewer listview = new MoleculeListViewer();
		IRingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		arf.setTimeout(10000);
		if (standAlone) arf.debug = true;
		
		String filename = "data/mdl/porphyrin.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
		IChemSequence seq = chemFile.getChemSequence(0);
		IChemModel model = seq.getChemModel(0);
		IMolecule molecule = model.getMoleculeSet().getMolecule(0);
		listview.addStructure(molecule, "Porphyrin", false, false);

		ringSet = arf.findAllRings(molecule);
		for (int i = 0; i < ringSet.getAtomContainerCount(); i++) 
		{
			IAtomContainer ac = ringSet.getAtomContainer(i);
			Molecule newMol = new Molecule(ac);
			String title = "ring no. " + (i + 1);
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(newMol);
			if (CDKHueckelAromaticityDetector.detectAromaticity(newMol)) title += " is aromatic";
			listview.addStructure(newMol, title , false, false);
		}
	}
	
	@Test public void testAzulene() throws Exception {
		IRingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		
		String filename = "data/mdl/azulene.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
		IChemSequence seq = chemFile.getChemSequence(0);
		IChemModel model = seq.getChemModel(0);
		IMolecule molecule = model.getMoleculeSet().getMolecule(0);

		ringSet = arf.findAllRings(molecule);
		Assert.assertEquals(3, ringSet.getAtomContainerCount());
	}

	@Test public void testBigMoleculeWithIsolatedRings() throws Exception {
        IRingSet ringSet = null;
        AllRingsFinder arf = new AllRingsFinder();
		if (standAlone) arf.debug = true;
        
        String filename = "data/cml/isolated_ringsystems.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
        IChemSequence seq = chemFile.getChemSequence(0);
        IChemModel model = seq.getChemModel(0);
        IMolecule mol = model.getMoleculeSet().getMolecule(0);
        //logger.debug("Constructed Molecule");
        //logger.debug("Starting AllRingsFinder");
        ringSet = new AllRingsFinder().findAllRings(mol);
        //logger.debug("Finished AllRingsFinder");
        Assert.assertEquals(24, ringSet.getAtomContainerCount());
        //display(mol);
    }
    
	/**
	 * This test takes a very long time. It was to ensure that
	 * AllRingsFinder acually stops for the given examples.
	 * And it does, after a very long time.
	 * So, the test is commented our because of its long runtime.
	 * 
	 * @cdk.bug 777488
	 */
	@Test public void testBug777488() throws Exception {
		if (!runSlowTests()) Assert.fail("Not running this slow test");
		
		//String filename = "data/Bug646.cml";
		String filename = "data/cml/testBug777488-1-AllRingsFinder.cml";
		//String filename = "data/NCI_diversity_528.mol.cml";
		//String filename = "data/NCI_diversity_978.mol.cml";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		CMLReader reader = new CMLReader(ins);
		IChemFile chemFile = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
		IChemSequence seq = chemFile.getChemSequence(0);
		IChemModel model = seq.getChemModel(0);
		IMolecule mol = model.getMoleculeSet().getMolecule(0);
		if (standAlone) System.out.println("Constructed Molecule");
		if (standAlone) System.out.println("Starting AllRingsFinder");
		IRingSet ringSet = new AllRingsFinder().findAllRings(mol);
		if (standAlone) System.out.println("Finished AllRingsFinder");
		if (standAlone) System.out.println("Found " + ringSet.getAtomContainerCount() + " rings.");

		//display(mol);
	}

    @Test public void testRingFlags1() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule molecule = sp.parseSmiles("c1ccccc1");

        IRingSet ringSet = null;
        AllRingsFinder arf = new AllRingsFinder();
        ringSet = arf.findAllRings(molecule);


        int count = 0;
        Iterator atoms = molecule.atoms();
        while (atoms.hasNext()) {
            IAtom atom = (IAtom) atoms.next();
            if (atom.getFlag(CDKConstants.ISINRING)) count++;
        }
        Assert.assertEquals("All atoms in benzene were not marked as being in a ring", 6, count);
    }

    @Test public void testRingFlags2() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule molecule = sp.parseSmiles("c1cccc1CC");

        IRingSet ringSet = null;
        AllRingsFinder arf = new AllRingsFinder();
        ringSet = arf.findAllRings(molecule);


        int count = 0;
        Iterator atoms = molecule.atoms();
        while (atoms.hasNext()) {
            IAtom atom = (IAtom) atoms.next();
            if (atom.getFlag(CDKConstants.ISINRING)) count++;
        }
        Assert.assertEquals("All atoms in 1-ethyl-cyclopentane were not marked as being in a ring", 5, count);
    }
}
