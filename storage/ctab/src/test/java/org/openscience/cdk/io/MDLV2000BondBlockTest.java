/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author John May
 */
class MDLV2000BondBlockTest {

    private final MDLV2000Reader     reader  = new MDLV2000Reader();
    private final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    private final IAtom[]            atoms   = new IAtom[]{Mockito.mock(IAtom.class), Mockito.mock(IAtom.class),
            Mockito.mock(IAtom.class), Mockito.mock(IAtom.class), Mockito.mock(IAtom.class)};

    @Test
    void atomNumbers() throws Exception {
        String input = "  1  3  1  0  0  0  0";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getBegin(), is(atoms[0]));
        assertThat(bond.getEnd(), is(atoms[2]));
    }

    @Test
    void singleBond() throws Exception {
        String input = "  1  3  1  0  0  0  0";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getOrder(), is(IBond.Order.SINGLE));
        assertThat(bond.getStereo(), is(IBond.Stereo.NONE));
        Assertions.assertFalse(bond.getFlag(IChemObject.AROMATIC));
        Assertions.assertFalse(bond.getFlag(IChemObject.SINGLE_OR_DOUBLE));
    }

    @Test
    void doubleBond() throws Exception {
        String input = "  1  3  2  0  0  0  0";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getOrder(), is(IBond.Order.DOUBLE));
        assertThat(bond.getStereo(), is(IBond.Stereo.E_Z_BY_COORDINATES));
        Assertions.assertFalse(bond.getFlag(IChemObject.AROMATIC));
        Assertions.assertFalse(bond.getFlag(IChemObject.SINGLE_OR_DOUBLE));
    }

    @Test
    void tripleBond() throws Exception {
        String input = "  1  3  3  0  0  0  0";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getOrder(), is(IBond.Order.TRIPLE));
        assertThat(bond.getStereo(), is(IBond.Stereo.NONE));
        Assertions.assertFalse(bond.getFlag(IChemObject.AROMATIC));
        Assertions.assertFalse(bond.getFlag(IChemObject.SINGLE_OR_DOUBLE));
    }

    @Test
    void aromaticBond() throws Exception {
        String input = "  1  3  4  0  0  0  0";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getStereo(), is(IBond.Stereo.NONE));
        Assertions.assertTrue(bond.getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(bond.getFlag(IChemObject.SINGLE_OR_DOUBLE));
    }

    @Test
    void singleOrDoubleBond() throws Exception {
        String input = "  1  3  5  0  0  0  0";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getStereo(), is(IBond.Stereo.NONE));
        Assertions.assertFalse(bond.getFlag(IChemObject.AROMATIC));
        Assertions.assertFalse(bond.getFlag(IChemObject.SINGLE_OR_DOUBLE));
        assertThat(bond, is(instanceOf(QueryBond.class)));
        assertThat(((QueryBond) bond).getExpression().type(), is(Expr.Type.SINGLE_OR_DOUBLE));
    }

    @Test
    void singleOrAromaticBond() throws Exception {
        String input = "  1  3  6  0  0  0  0";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getStereo(), is(IBond.Stereo.NONE));
        Assertions.assertFalse(bond.getFlag(IChemObject.AROMATIC));
        Assertions.assertFalse(bond.getFlag(IChemObject.SINGLE_OR_DOUBLE));
        assertThat(bond, is(instanceOf(QueryBond.class)));
        assertThat(((QueryBond) bond).getExpression().type(), is(Expr.Type.SINGLE_OR_AROMATIC));
    }

    @Test
    void doubleOrAromaticBond() throws Exception {
        String input = "  1  3  7  0  0  0  0";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getStereo(), is(IBond.Stereo.NONE));
        Assertions.assertFalse(bond.getFlag(IChemObject.AROMATIC));
        Assertions.assertFalse(bond.getFlag(IChemObject.SINGLE_OR_DOUBLE));
        assertThat(bond, is(instanceOf(QueryBond.class)));
        assertThat(((QueryBond) bond).getExpression().type(), is(Expr.Type.DOUBLE_OR_AROMATIC));
    }

    @Test
    void anyBond() throws Exception {
        String input = "  1  3  8  0  0  0  0";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getStereo(), is(IBond.Stereo.NONE));
        Assertions.assertFalse(bond.getFlag(IChemObject.AROMATIC));
        Assertions.assertFalse(bond.getFlag(IChemObject.SINGLE_OR_DOUBLE));
        assertThat(bond, is(instanceOf(QueryBond.class)));
        assertThat(((QueryBond) bond).getExpression().type(), is(Expr.Type.TRUE));
    }

    @Test
    void upBond() throws Exception {
        String input = "  1  3  1  1  0  0  0";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getOrder(), is(IBond.Order.SINGLE));
        assertThat(bond.getStereo(), is(IBond.Stereo.UP));
    }

    @Test
    void downBond() throws Exception {
        String input = "  1  3  1  6  0  0  0";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getOrder(), is(IBond.Order.SINGLE));
        assertThat(bond.getStereo(), is(IBond.Stereo.DOWN));
    }

    @Test
    void upOrDownBond() throws Exception {
        String input = "  1  3  1  4  0  0  0";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getOrder(), is(IBond.Order.SINGLE));
        assertThat(bond.getStereo(), is(IBond.Stereo.UP_OR_DOWN));
    }

    @Test
    void cisOrTrans() throws Exception {
        String input = "  1  3  2  3  0  0  0";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getOrder(), is(IBond.Order.DOUBLE));
        assertThat(bond.getStereo(), is(IBond.Stereo.E_OR_Z));
    }

    @Test
    void cisOrTransByCoordinates() throws Exception {
        String input = "  1  3  2  0  0  0  0";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getOrder(), is(IBond.Order.DOUBLE));
        assertThat(bond.getStereo(), is(IBond.Stereo.E_Z_BY_COORDINATES));
    }

    @Test
    void upDoubleBond() throws Exception {
        String input = "  1  3  2  1  0  0  0";
        reader.setReaderMode(IChemObjectReader.Mode.STRICT);
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
                                });
    }

    @Test
    void downDoubleBond() throws Exception {
        String input = "  1  3  2  1  0  0  0";
        reader.setReaderMode(IChemObjectReader.Mode.STRICT);
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
                                });
    }

    @Test
    void upOrDownDoubleBond() throws Exception {
        String input = "  1  3  2  4  0  0  0";
        reader.setReaderMode(IChemObjectReader.Mode.STRICT);
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
                                });
    }

    @Test
    void cisOrTransSingleBond() throws Exception {
        String input = "  1  3  1  3  0  0  0";
        reader.setReaderMode(IChemObjectReader.Mode.STRICT);
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
                                });
    }

    @Test
    void longLine() throws Exception {
        String input = "  1  3  1  0  0  0  0  0  0";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getBegin(), is(atoms[0]));
        assertThat(bond.getEnd(), is(atoms[2]));
        assertThat(bond.getOrder(), is(IBond.Order.SINGLE));
        assertThat(bond.getStereo(), is(IBond.Stereo.NONE));
        Assertions.assertFalse(bond.getFlag(IChemObject.AROMATIC));
        Assertions.assertFalse(bond.getFlag(IChemObject.SINGLE_OR_DOUBLE));
    }

    @Test
    void longLineWithPadding() throws Exception {
        String input = "  1  3  1  0  0  0  0    ";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getBegin(), is(atoms[0]));
        assertThat(bond.getEnd(), is(atoms[2]));
        assertThat(bond.getOrder(), is(IBond.Order.SINGLE));
        assertThat(bond.getStereo(), is(IBond.Stereo.NONE));
        Assertions.assertFalse(bond.getFlag(IChemObject.AROMATIC));
        Assertions.assertFalse(bond.getFlag(IChemObject.SINGLE_OR_DOUBLE));
    }

    @Test
    void shortLine() throws Exception {
        String input = "  1  3  1  0";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getBegin(), is(atoms[0]));
        assertThat(bond.getEnd(), is(atoms[2]));
        assertThat(bond.getOrder(), is(IBond.Order.SINGLE));
        assertThat(bond.getStereo(), is(IBond.Stereo.NONE));
        Assertions.assertFalse(bond.getFlag(IChemObject.AROMATIC));
        Assertions.assertFalse(bond.getFlag(IChemObject.SINGLE_OR_DOUBLE));
    }

    @Test
    void shortLineWithPadding() throws Exception {
        String input = "  1  3  1  0       ";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getBegin(), is(atoms[0]));
        assertThat(bond.getEnd(), is(atoms[2]));
        assertThat(bond.getOrder(), is(IBond.Order.SINGLE));
        assertThat(bond.getStereo(), is(IBond.Stereo.NONE));
        Assertions.assertFalse(bond.getFlag(IChemObject.AROMATIC));
        Assertions.assertFalse(bond.getFlag(IChemObject.SINGLE_OR_DOUBLE));
    }

    @Test
    void shortLineNoStereo() throws Exception {
        String input = "  1  3  1";
        IBond bond = reader.readBondFast(input, builder, atoms, new int[atoms.length], 1);
        assertThat(bond.getBegin(), is(atoms[0]));
        assertThat(bond.getEnd(), is(atoms[2]));
        assertThat(bond.getOrder(), is(IBond.Order.SINGLE));
        assertThat(bond.getStereo(), is(IBond.Stereo.NONE));
        Assertions.assertFalse(bond.getFlag(IChemObject.AROMATIC));
        Assertions.assertFalse(bond.getFlag(IChemObject.SINGLE_OR_DOUBLE));
    }
}
