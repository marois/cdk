/**
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman {asad@ebi.ac.uk}
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
 * You should have received atom copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * Class that handles some customised features for SMSD atom containers.
 * <p>This is an extension of CDK AtomContainer.
 * Some part of this code was taken from CDK source code and modified.</p>
 *
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class ExtAtomContainerManipulator extends AtomContainerManipulator {

    private static void printAtoms(IAtomContainer mol) {
        System.out.print("Atom: ");
        for (IAtom a : mol.atoms()) {

            System.out.print(a.getSymbol());
            System.out.print("[" + a.getFormalCharge() + "]");
            if (a.getID() != null) {
                System.out.print("[" + a.getID() + "]");
            }

        }
        System.out.println();
        System.out.println();
    }

    /**
     * Retrurns deep copy of the molecule
     * @param container
     * @return deep copy of the mol
     */
    public static IAtomContainer makeDeepCopy(IAtomContainer container) {

        IAtomContainer newAtomContainer = container.getBuilder().newInstance(IAtomContainer.class);
        //      Deep copy of the Atoms
        IAtom[] atoms = copyAtoms(container, newAtomContainer);

        //      Deep copy of the bonds
        copyBonds(atoms, container, newAtomContainer);

        //      Deep copy of the LonePairs
        for (int index = 0; index < container.getLonePairCount(); index++) {
            if (container.getAtom(index).getSymbol().equalsIgnoreCase("R")
                    || container.getAtom(index).getSymbol().equalsIgnoreCase("A")) {
                newAtomContainer.addLonePair(container.getBuilder().newInstance(ILonePair.class,
                        container.getAtom(index)));
            } else {
                newAtomContainer.addLonePair(index);
            }
        }

        for (int index = 0; index < container.getSingleElectronCount(); index++) {
            newAtomContainer.addSingleElectron(index);
        }
        newAtomContainer.addProperties(container.getProperties());
        newAtomContainer.setFlags(container.getFlags());
        newAtomContainer.setID(container.getID());
        newAtomContainer.notifyChanged();

        return newAtomContainer;
    }

    /**
     * This function finds rings and uses aromaticity detection code to
     * aromatize the molecule.
     * @param mol input molecule
     */
    public static void aromatizeMolecule(IAtomContainer mol) {

        // need to find rings and aromaticity again since added H's

        IRingSet ringSet = null;
        try {
            AllRingsFinder arf = new AllRingsFinder();
            ringSet = arf.findAllRings(mol);

            // SSSRFinder s = new SSSRFinder(atomContainer);
            // srs = s.findEssentialRings();

        } catch (Exception e) {
            LoggingToolFactory.createLoggingTool(ExtAtomContainerManipulator.class)
                              .warn("Unexpected Error:", e);
        }

        try {
            // figure out which atoms are in aromatic rings:
            //            printAtoms(atomContainer);
            ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
            //            printAtoms(atomContainer);
            Aromaticity.cdkLegacy().apply(mol);
            //            printAtoms(atomContainer);
            // figure out which rings are aromatic:
            RingSetManipulator.markAromaticRings(ringSet);
            //            printAtoms(atomContainer);
            // figure out which simple (non cycles) rings are aromatic:
            // HueckelAromaticityDetector.detectAromaticity(atomContainer, srs);
        } catch (Exception e) {
            LoggingToolFactory.createLoggingTool(ExtAtomContainerManipulator.class)
                              .warn("Unexpected Error:", e);
        }

        // only atoms in 6 membered rings are aromatic
        // determine largest ring that each atom is atom part of

        for (int i = 0; i <= mol.getAtomCount() - 1; i++) {

            mol.getAtom(i).setFlag(IChemObject.AROMATIC, false);

            jloop: for (int j = 0; j <= ringSet.getAtomContainerCount() - 1; j++) {
                //logger.debug(i+"\t"+j);
                IRing ring = (IRing) ringSet.getAtomContainer(j);
                if (!ring.getFlag(IChemObject.AROMATIC)) {
                    continue jloop;
                }

                boolean haveatom = ring.contains(mol.getAtom(i));

                //logger.debug("haveatom="+haveatom);

                if (haveatom && ring.getAtomCount() == 6) {
                    mol.getAtom(i).setFlag(IChemObject.AROMATIC, true);
                }
            }
        }
    }

    /**
     * Returns The number of explicit hydrogens for a given IAtom.
     * @param atomContainer
     * @param atom
     * @return The number of explicit hydrogens on the given IAtom.
     */
    public static int getExplicitHydrogenCount(IAtomContainer atomContainer, IAtom atom) {
        int hCount = 0;
        for (IAtom iAtom : atomContainer.getConnectedAtomsList(atom)) {
            IAtom connectedAtom = iAtom;
            if (connectedAtom.getAtomicNumber() == IElement.H) {
                hCount++;
            }
        }
        return hCount;
    }

    /**
     * Returns The number of Implicit Hydrogen Count for a given IAtom.
     * @param atom
     * @return Implicit Hydrogen Count
     */
    public static int getImplicitHydrogenCount(IAtom atom) {
        return atom.getImplicitHydrogenCount() == CDKConstants.UNSET ? 0 : atom.getImplicitHydrogenCount();
    }

    /**
     * The summed implicit + explicit hydrogens of the given IAtom.
     * @param atomContainer
     * @param atom
     * @return The summed implicit + explicit hydrogens of the given IAtom.
     */
    public static int getHydrogenCount(IAtomContainer atomContainer, IAtom atom) {
        return getExplicitHydrogenCount(atomContainer, atom) + getImplicitHydrogenCount(atom);
    }

    /**
     * Returns IAtomContainer without Hydrogen. If an AtomContainer has atom single atom which
     * is atom Hydrogen then its not removed.
     * @param atomContainer
     * @return IAtomContainer without Hydrogen. If an AtomContainer has atom single atom which
     * is atom Hydrogen then its not removed.
     */
    public static IAtomContainer removeHydrogensExceptSingleAndPreserveAtomID(IAtomContainer atomContainer) {
        Map<IAtom, IAtom> map = new HashMap<>(); // maps original atoms to clones.
        List<IAtom> remove = new ArrayList<>(); // lists removed Hs.
        IAtomContainer mol;
        if (atomContainer.getBondCount() > 0) {
            // Clone atoms except those to be removed.
            mol = atomContainer.getBuilder().newInstance(IAtomContainer.class);
            int count = atomContainer.getAtomCount();
            for (int i = 0; i < count; i++) {
                // Clone/remove this atom?
                IAtom atom = atomContainer.getAtom(i);
                if (atom.getAtomicNumber() != IElement.H) {
                    IAtom clonedAtom = null;
                    try {
                        clonedAtom = atom.clone();
                        //added by Asad to preserve the Atom ID for atom mapping without Hydrogen
                        clonedAtom.setID(atom.getID());
                        clonedAtom.setFlags(atom.getFlags());
                        int countH = 0;
                        if (atom.getImplicitHydrogenCount() != null) {
                            countH = atom.getImplicitHydrogenCount();
                        }
                        clonedAtom.setImplicitHydrogenCount(countH);
                        mol.addAtom(clonedAtom);
                        map.put(atom, clonedAtom);
                    } catch (CloneNotSupportedException e) {
                        throw new IllegalStateException("Atom could not be cloned", e);
                    }
                } else {
                    remove.add(atom); // maintain list of removed H.
                }
            }
            //            Clone bonds except those involving removed atoms.
            mol = cloneAndMarkNonHBonds(mol, atomContainer, remove, map);
            //            Recompute hydrogen counts of neighbours of removed Hydrogens.
            mol = reComputeHydrogens(mol, atomContainer, remove, map);

        } else {
            mol = atomContainer.getBuilder().newInstance(IAtomContainer.class, atomContainer);
            if (atomContainer.getAtom(0).getSymbol().equalsIgnoreCase("H")) {
                System.err.println("WARNING: single hydrogen atom removal not supported!");
            }
            mol.setFlags(atomContainer.getFlags());
        }
        mol.addProperties(atomContainer.getProperties());
        if (atomContainer.getID() != null) {
            mol.setID(atomContainer.getID());
        }

        return mol;
    }

    /**
     * Returns IAtomContainer without Hydrogen. If an AtomContainer has atom single atom which
     * is atom Hydrogen then its not removed.
     * @param atomContainer
     * @return IAtomContainer without Hydrogen. If an AtomContainer has atom single atom which
     * is atom Hydrogen then its not removed.
     */
    public static IAtomContainer convertExplicitToImplicitHydrogens(IAtomContainer atomContainer) {
        IAtomContainer mol = atomContainer.getBuilder().newInstance(IAtomContainer.class, atomContainer);
        convertImplicitToExplicitHydrogens(mol);
        if (mol.getAtomCount() > 1) {
            mol = removeHydrogens(mol);
        } else if (atomContainer.atoms().iterator().next().getSymbol().equalsIgnoreCase("H")) {
            System.err.println("WARNING: single hydrogen atom removal not supported!");
        }
        mol.addProperties(atomContainer.getProperties());
        mol.setFlags(atomContainer.getFlags());
        if (atomContainer.getID() != null) {
            mol.setID(atomContainer.getID());
        }
        return mol;
    }

    /**
     * Convenience method to perceive atom types for all <code>IAtom</code>s in the
     * <code>IAtomContainer</code>, using the <code>CDKAtomTypeMatcher</code>. If the
     * matcher finds atom matching atom type, the <code>IAtom</code> will be configured
     * to have the same properties as the <code>IAtomType</code>. If no matching atom
     * type is found, no configuration is performed.
     * @param container
     * @throws CDKException
     */
    public static void percieveAtomTypesAndConfigureAtoms(IAtomContainer container) throws CDKException {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(container.getBuilder());
        for (IAtom atom : container.atoms()) {
            if (!(atom instanceof IPseudoAtom)) {

                IAtomType matched = matcher.findMatchingAtomType(container, atom);
                if (matched != null) {
                    AtomTypeManipulator.configure(atom, matched);
                }

            }
        }
    }

    private static IAtom[] copyAtoms(IAtomContainer container, IAtomContainer newAtomContainer) {
        int atomCount = container.getAtomCount();
        IAtom[] atoms = new IAtom[atomCount];
        for (int index = 0; index < container.getAtomCount(); index++) {

            if (container.getAtom(index) instanceof IPseudoAtom) {
                atoms[index] = container.getBuilder().newInstance(IPseudoAtom.class, container.getAtom(index));
            } else {
                atoms[index] = container.getBuilder().newInstance(IAtom.class, container.getAtom(index));
            }

            set2D(container, index, atoms);
            set3D(container, index, atoms);
            setFractionalPoint3d(container, index, atoms);
            setID(container, index, atoms);
            setHydrogenCount(container, index, atoms);
            setCharge(container, index, atoms);
            newAtomContainer.addAtom(atoms[index]);
        }

        for (IStereoElement stereoElement : container.stereoElements()) {
            newAtomContainer.addStereoElement(stereoElement);
        }

        return atoms;
    }

    private static void copyBonds(IAtom[] atoms, IAtomContainer container, IAtomContainer newAtomContainer) {
        int bondCount = container.getBondCount();
        IBond[] bonds = new IBond[bondCount];
        for (int index = 0; index < container.getBondCount(); index++) {
            bonds[index] = new Bond();
            int indexI = 999;
            for (int i = 0; i < container.getAtomCount(); i++) {
                if (container.getBond(index).getBegin().equals(container.getAtom(i))) {
                    indexI = i;
                    break;
                }
            }
            int indexJ = 999;
            for (int j = 0; j < container.getAtomCount(); j++) {
                if (container.getBond(index).getEnd().equals(container.getAtom(j))) {
                    indexJ = j;
                    break;
                }
            }

            IAtom atom1 = atoms[indexI];
            IAtom atom2 = atoms[indexJ];

            Order order = container.getBond(index).getOrder();
            IBond.Stereo stereo = container.getBond(index).getStereo();
            bonds[index] = new Bond(atom1, atom2, order, stereo);
            if (container.getBond(index).getID() != null) {
                bonds[index].setID(container.getBond(index).getID());
            }
            newAtomContainer.addBond(bonds[index]);

        }
    }

    private static IAtomContainer reComputeHydrogens(IAtomContainer mol, IAtomContainer atomContainer,
            List<IAtom> remove, Map<IAtom, IAtom> map) {

        // Recompute hydrogen counts of neighbours of removed Hydrogens.
        for (IAtom aRemove : remove) {
            // Process neighbours.
            for (IAtom iAtom : atomContainer.getConnectedAtomsList(aRemove)) {
                final IAtom neighb = map.get(iAtom);
                if (neighb == null) {
                    continue; // since for the case of H2, neight H has atom heavy atom neighbor
                }
                //Added by Asad
                if (!(neighb instanceof IPseudoAtom)) {
                    neighb.setImplicitHydrogenCount((neighb.getImplicitHydrogenCount() == null ? 0 : neighb
                            .getImplicitHydrogenCount()) + 1);
                } else {
                    neighb.setImplicitHydrogenCount(0);
                }
            }
        }
        mol.addProperties(atomContainer.getProperties());
        mol.setFlags(atomContainer.getFlags());
        if (atomContainer.getID() != null) {
            mol.setID(atomContainer.getID());
        }
        return mol;
    }

    private static IAtomContainer cloneAndMarkNonHBonds(IAtomContainer mol, IAtomContainer atomContainer,
            List<IAtom> remove, Map<IAtom, IAtom> map) {
        // Clone bonds except those involving removed atoms.
        int count = atomContainer.getBondCount();
        for (int i = 0; i < count; i++) {
            // Check bond.
            final IBond bond = atomContainer.getBond(i);
            boolean removedBond = false;
            final int length = bond.getAtomCount();
            for (int k = 0; k < length; k++) {
                if (remove.contains(bond.getAtom(k))) {
                    removedBond = true;
                    break;
                }
            }

            // Clone/remove this bond?
            if (!removedBond) // if (!remove.contains(atoms[0]) && !remove.contains(atoms[1]))
            {
                IBond clone = null;
                try {
                    clone = atomContainer.getBond(i).clone();
                } catch (CloneNotSupportedException e) {
                    LoggingToolFactory.createLoggingTool(ExtAtomContainerManipulator.class)
                                      .warn("Unexpected Error:", e);
                }
                assert clone != null;
                clone.setAtoms(new IAtom[]{map.get(bond.getBegin()), map.get(bond.getEnd())});
                clone.setOrder(atomContainer.getBond(i).getOrder());
                clone.setStereo(atomContainer.getBond(i).getStereo());
                mol.addBond(clone);
            }
        }

        return mol;
    }

    private static void set2D(IAtomContainer container, int index, IAtom[] atoms) {
        if ((container.getAtom(index)).getPoint2d() != null) {
            atoms[index].setPoint2d(new Point2d(container.getAtom(index).getPoint2d()));
        }
    }

    private static void set3D(IAtomContainer container, int index, IAtom[] atoms) {
        if ((container.getAtom(index)).getPoint3d() != null) {
            atoms[index].setPoint3d(new Point3d(container.getAtom(index).getPoint3d()));
        }
    }

    private static void setFractionalPoint3d(IAtomContainer container, int index, IAtom[] atoms) {
        if ((container.getAtom(index)).getFractionalPoint3d() != null) {
            atoms[index].setFractionalPoint3d(new Point3d(container.getAtom(index).getFractionalPoint3d()));
        }
    }

    private static void setID(IAtomContainer container, int index, IAtom[] atoms) {

        if (container.getAtom(index).getID() != null) {
            atoms[index].setID(container.getAtom(index).getID());
        }
    }

    private static void setHydrogenCount(IAtomContainer container, int index, IAtom[] atoms) {
        if (container.getAtom(index).getImplicitHydrogenCount() != null) {
            atoms[index].setImplicitHydrogenCount(container.getAtom(index).getImplicitHydrogenCount());
        }
    }

    private static void setCharge(IAtomContainer container, int index, IAtom[] atoms) {
        if (container.getAtom(index).getCharge() != null) {
            atoms[index].setCharge(container.getAtom(index).getCharge());
        }
    }
}
