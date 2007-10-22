/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Hardware Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Hardware
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.LoggingTool;

import javax.vecmath.Point3d;
import java.io.IOException;

/**
 *  Inductive atomic hardness of an atom in a polyatomic system can be defined
 *  as the "resistance" to a change of the atomic charge. Only works with 3D coordinates, which must be calculated beforehand. <p>
 *
 *  This descriptor uses these parameters:
 *  <tableborder="1">
 *
 *    <tr>
 *
 *      <td>
 *        Name
 *      </td>
 *
 *      <td>
 *        Default
 *      </td>
 *
 *      <td>
 *        Description
 *      </td>
 *
 *    </tr>
 *
 *    <tr>
 *
 *      <td>
 *        
 *      </td>
 *
 *      <td>
 *        
 *      </td>
 *
 *      <td>
 *        no parameters
 *      </td>
 *
 *    </tr>
 *
 *  </table>
 *
 *
 *@author         mfe4
 *@cdk.created    2004-11-03
 *@cdk.module     qsar
 * @cdk.svnrev  $Revision$
 *@cdk.set        qsar-descriptors
 * @cdk.dictref   qsar-descriptors:atomicHardness
 */
public class InductiveAtomicHardnessDescriptor implements IAtomicDescriptor {

	private LoggingTool logger;
	private AtomTypeFactory factory = null;


	/**
	 *  Constructor for the InductiveAtomicHardnessDescriptor object
	 *
	 *@exception  IOException             Description of the Exception
	 *@exception  ClassNotFoundException  Description of the Exception
	 */
	public InductiveAtomicHardnessDescriptor() throws IOException, ClassNotFoundException {
		logger = new LoggingTool(this);
	}


	/**
	 *  Gets the specification attribute of the InductiveAtomicHardnessDescriptor
	 *  object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#atomicHardness",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
	}


	/**
     * This descriptor does have any parameter.
     */
    public void setParameters(Object[] params) throws CDKException {
    }


	/**
	 *  Gets the parameters attribute of the InductiveAtomicHardnessDescriptor
	 *  object
	 *  
	 * @return    The parameters value
     * @see #setParameters
     */
    public Object[] getParameters() {
        return null;
    }


	/**
	 *  It is needed to call the addExplicitHydrogensToSatisfyValency method from
	 *  the class tools.HydrogenAdder, and 3D coordinates.
	 *
	 *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  ac                AtomContainer
	 *@return                   a double with polarizability of the heavy atom
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorValue calculate(IAtom atom, IAtomContainer ac) throws CDKException {
		if (factory == null)
            try {
                factory = AtomTypeFactory.getInstance(
                    "org/openscience/cdk/config/data/jmol_atomtypes.txt", 
                    ac.getBuilder()
                );
            } catch (Exception exception) {
                throw new CDKException("Could not instantiate AtomTypeFactory!", exception);
            }

        double atomicHardness;

		double radiusTarget;
		
		java.util.Iterator allAtoms = ac.atoms();
		atomicHardness = 0;
		double partial;
		double radius;
		String symbol;
		IAtomType type;
		try {
			symbol = atom.getSymbol();
			type = factory.getAtomType(symbol);
			radiusTarget = type.getCovalentRadius();
		} catch (Exception ex1) {
			logger.debug(ex1);
			throw new CDKException("Problems with AtomTypeFactory due to " + ex1.toString(), ex1);
		}

		while (allAtoms.hasNext()) {
			IAtom curAtom = (IAtom)allAtoms.next();
			if (atom.getPoint3d() == null || curAtom.getPoint3d() == null) {
				throw new CDKException("The target atom or current atom had no 3D coordinates. These are required");
			}


			if (!atom.equals(curAtom)) {
				partial = 0;
				symbol = curAtom.getSymbol();

				try {
					type = factory.getAtomType(symbol);
				} catch (Exception ex1) {
					logger.debug(ex1);
					throw new CDKException("Problems with AtomTypeFactory due to " + ex1.toString(), ex1);
				}
				radius = type.getCovalentRadius();
				partial += radius * radius;
				partial += (radiusTarget * radiusTarget);
				partial = partial / (calculateSquareDistanceBetweenTwoAtoms(atom, curAtom));
				atomicHardness += partial;
			}
		}

		atomicHardness = 2 * atomicHardness;
		atomicHardness = atomicHardness * 0.172;
		atomicHardness = 1 / atomicHardness;
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(atomicHardness));
	}

	private double calculateSquareDistanceBetweenTwoAtoms(org.openscience.cdk.interfaces.IAtom atom1, org.openscience.cdk.interfaces.IAtom atom2) {
		double distance;
		double tmp;
		Point3d firstPoint = atom1.getPoint3d();
		Point3d secondPoint = atom2.getPoint3d();
		tmp = firstPoint.distance(secondPoint);
		distance = tmp * tmp;
		return distance;
	}

	/**
	 *  Gets the parameterNames attribute of the InductiveAtomicHardnessDescriptor
	 *  object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
        return new String[0];
    }


	/**
	 *  Gets the parameterType attribute of the InductiveAtomicHardnessDescriptor
	 *  object
	 *
	 * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    public Object getParameterType(String name) {
        return null;
    }
}

