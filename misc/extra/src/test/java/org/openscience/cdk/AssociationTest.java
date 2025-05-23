/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the functionality of the Association class.
 *
 *
 * @see org.openscience.cdk.Association
 */
class AssociationTest extends CDKTestCase {

    @Test
    void testAssociation() {
        Association association = new Association();
        Assertions.assertEquals(0, association.getElectronCount().intValue());
        Assertions.assertEquals(0, association.getAtomCount());
    }

    @Test
    void testAssociation_IAtom_IAtom() {
        Association association = new Association(new Atom("C"), new Atom("C"));
        Assertions.assertEquals(0, association.getElectronCount().intValue());
        Assertions.assertEquals(2, association.getAtomCount());
    }

    /** Test for RFC #9 */
    @Test
    void testToString() {
        Association association = new Association();
        String description = association.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test
    void testToStringWithAtoms() {
        Association association = new Association(new Atom("C"), new Atom("C"));
        String description = association.toString();
        Assertions.assertTrue(description.contains(","));
    }

    @Test
    void testContains() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");

        Association association = new Association(c, o);

        Assertions.assertTrue(association.contains(c));
        Assertions.assertTrue(association.contains(o));
    }

    @Test
    void testGetAtomCount() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");

        Association association = new Association(c, o);

        Assertions.assertEquals(2, association.getAtomCount());
    }

    @Test
    void testGetAtoms() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");

        Association association = new Association(c, o);

        IAtom[] atoms = association.getAtoms();
        Assertions.assertEquals(2, atoms.length);
        Assertions.assertNotNull(atoms[0]);
        Assertions.assertNotNull(atoms[1]);
    }

    @Test
    void testSetAtoms() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        Association association = new Association();
        association.setAtoms(new IAtom[]{c, o});

        Assertions.assertTrue(association.contains(c));
        Assertions.assertTrue(association.contains(o));
    }

    @Test
    void testSetAtomAt() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        Atom n = new Atom("N");
        Association association = new Association(c, o);
        association.setAtomAt(n, 1);

        Assertions.assertTrue(association.contains(c));
        Assertions.assertTrue(association.contains(n));
        Assertions.assertFalse(association.contains(o));
    }

    @Test
    void testGetAtomAt() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        Atom n = new Atom("N");
        Association association = new Association(c, o);

        Assertions.assertEquals(c, association.getAtomAt(0));
        Assertions.assertEquals(o, association.getAtomAt(1));

        association.setAtomAt(n, 0);
        Assertions.assertEquals(n, association.getAtomAt(0));
    }

    @Test
    void testGetElectronCount() {
        Association association = new Association();
        Assertions.assertEquals(0, association.getElectronCount(), 0.00001);
    }
}
