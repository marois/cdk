/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.silent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.interfaces.AbstractRingTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRing;

/**
 * Checks the functionality of the {@link Ring}.
 *
 */
class RingTest extends AbstractRingTest {

    @BeforeAll
    static void setUp() {
        setTestObjectBuilder(Ring::new);
    }

    @Test
    void testRing_int_String() {
        IRing r = new Ring(5, "C");
        Assertions.assertEquals(5, r.getAtomCount());
        Assertions.assertEquals(5, r.getBondCount());
    }

    @Test
    void testRing_int() {
        IRing r = new Ring(5); // This does not create a ring!
        Assertions.assertEquals(0, r.getAtomCount());
        Assertions.assertEquals(0, r.getBondCount());
    }

    @Test
    void testRing() {
        IRing ring = new Ring();
        Assertions.assertNotNull(ring);
        Assertions.assertEquals(0, ring.getAtomCount());
        Assertions.assertEquals(0, ring.getBondCount());
    }

    @Test
    void testRing_IAtomContainer() {
        IAtomContainer container = newChemObject().getBuilder().newInstance(IAtomContainer.class);
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));

        IRing ring = new Ring(container);
        Assertions.assertNotNull(ring);
        Assertions.assertEquals(2, ring.getAtomCount());
        Assertions.assertEquals(0, ring.getBondCount());
    }

    // Overwrite default methods: no notifications are expected!

    @Test
    @Override
    public void testNotifyChanged() {
        ChemObjectTestHelper.testNotifyChanged(newChemObject());
    }

    @Test
    @Override
    public void testNotifyChanged_SetFlag() {
        ChemObjectTestHelper.testNotifyChanged_SetFlag(newChemObject());
    }

    @Test
    @Override
    public void testNotifyChanged_SetFlags() {
        ChemObjectTestHelper.testNotifyChanged_SetFlags(newChemObject());
    }

    @Test
    @Override
    public void testNotifyChanged_IChemObjectChangeEvent() {
        ChemObjectTestHelper.testNotifyChanged_IChemObjectChangeEvent(newChemObject());
    }

    @Test
    @Override
    public void testStateChanged_IChemObjectChangeEvent() {
        ChemObjectTestHelper.testStateChanged_IChemObjectChangeEvent(newChemObject());
    }

    @Test
    @Override
    public void testClone_ChemObjectListeners() throws Exception {
        ChemObjectTestHelper.testClone_ChemObjectListeners(newChemObject());
    }

    @Test
    @Override
    public void testAddListener_IChemObjectListener() {
        ChemObjectTestHelper.testAddListener_IChemObjectListener(newChemObject());
    }

    @Test
    @Override
    public void testGetListenerCount() {
        ChemObjectTestHelper.testGetListenerCount(newChemObject());
    }

    @Test
    @Override
    public void testRemoveListener_IChemObjectListener() {
        ChemObjectTestHelper.testRemoveListener_IChemObjectListener(newChemObject());
    }

    @Test
    @Override
    public void testSetNotification_true() {
        ChemObjectTestHelper.testSetNotification_true(newChemObject());
    }

    @Test
    @Override
    public void testNotifyChanged_SetProperty() {
        ChemObjectTestHelper.testNotifyChanged_SetProperty(newChemObject());
    }

    @Test
    @Override
    public void testNotifyChanged_RemoveProperty() {
        ChemObjectTestHelper.testNotifyChanged_RemoveProperty(newChemObject());
    }

    @Test
    @Override
    public void testSetAtoms_removeListener() {
        ChemObjectTestHelper.testSetAtoms_removeListener(newChemObject());
    }
}
