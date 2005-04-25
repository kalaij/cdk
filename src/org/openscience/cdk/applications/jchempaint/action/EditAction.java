/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2005  The JChemPaint project
 *
 * Contact: jchempaint-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.event.ActionEvent;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;
import org.openscience.cdk.tools.manipulator.SetOfMoleculesManipulator;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;


/**
 * This class implements editing options from the 'Edit' menu.
 * These actions are implemented:
 * <ul>
 *   <li>cut, deletes all atoms and connected electron containers
 *   <li>cutSelected, deletes all selected atoms and electron containers
 *   <li>selectAll, selects all atoms and electron containers
 *   <li>selectFromChemObject,selects all atoms and electron containers in
 *       the ChemObject set in the event source
 * </ul>
 * @cdk.module jchempaint
 */
public class EditAction extends JCPAction {

    public void actionPerformed(ActionEvent event) {
        // learn some stuff about event
        logger.debug("Event source: ", event.getSource().getClass().getName());
        logger.debug("  ChemObject: ", getSource(event));
        
        JChemPaintModel jcpModel = jcpPanel.getJChemPaintModel();
        Renderer2DModel renderModel = jcpModel.getRendererModel();
        ChemModel chemModel = jcpModel.getChemModel();
        if (type.equals("cut")) {
            Atom atomInRange = null;
            ChemObject object = getSource(event);
            logger.debug("Source of call: ", object);
            if (object instanceof Atom) {
                atomInRange = (Atom)object;
            } else {
                atomInRange = renderModel.getHighlightedAtom();
            }
            if (atomInRange != null) {
                ChemModelManipulator.removeAtomAndConnectedElectronContainers(chemModel, atomInRange);
            } else {
                Bond bond = renderModel.getHighlightedBond();
                if (bond != null) {
                    ChemModelManipulator.removeElectronContainer(chemModel, bond);
                }
            }
            jcpModel.fireChange();
        } else if (type.equals("cutSelected")) {
            logger.debug("Deleting all selected atoms...");
            Atom[] selected = renderModel.getSelectedPart().getAtoms();
            logger.debug("Found # atoms to delete: ", selected.length);
            for (int i=0; i < selected.length; i++) {
                ChemModelManipulator.removeAtomAndConnectedElectronContainers(chemModel, selected[i]);
            }
            jcpModel.fireChange();
        } else if (type.equals("selectAll")) {
            renderModel.setSelectedPart(ChemModelManipulator.getAllInOneContainer(jcpModel.getChemModel()));
            jcpModel.fireChange();
        } else if (type.equals("selectFromChemObject")) {
            // FIXME: implement for others than Reaction, Atom, Bond
            ChemObject object = getSource(event);
            if (object instanceof Atom) {
                AtomContainer container = new AtomContainer();
                container.addAtom((Atom)object);
                renderModel.setSelectedPart(container);
                jcpModel.fireChange();
            } else if (object instanceof Bond) {
                AtomContainer container = new AtomContainer();
                container.addBond((Bond)object);
                renderModel.setSelectedPart(container);
                jcpModel.fireChange();
            } else if (object instanceof Reaction) {
                renderModel.setSelectedPart(ReactionManipulator.getAllInOneContainer((Reaction)object));
                jcpModel.fireChange();
            } else {
                logger.warn("Cannot select everything in : ", object);
            }
        } else if (type.equals("selectReactionReactants")) {
            ChemObject object = getSource(event);
            if (object instanceof Reaction) {
                Reaction reaction = (Reaction)object;
                renderModel.setSelectedPart(SetOfMoleculesManipulator.getAllInOneContainer(reaction.getReactants()));
                jcpModel.fireChange();
            } else {
                logger.warn("Cannot select reactants from : ", object);
            }
        } else if (type.equals("selectReactionProducts")) {
            ChemObject object = getSource(event);
            if (object instanceof Reaction) {
                Reaction reaction = (Reaction)object;
                renderModel.setSelectedPart(SetOfMoleculesManipulator.getAllInOneContainer(reaction.getProducts()));
                jcpModel.fireChange();
            } else {
                logger.warn("Cannot select reactants from : ", object);
            }
        } else {
            logger.warn("Unsupported EditAction: " + type);
        }
    }
    
}
