/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@slists.sourceforge.net
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TestCase for the reading MDL RXN files using one test file.
 * @author Egon Willighagen
 * @author Uli Fechner
 *
 * @see org.openscience.cdk.io.MDLRXNV2000Reader
 */
class MDLRXNV2000ReaderTest extends SimpleChemObjectReaderTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDLRXNV2000ReaderTest.class);

    @BeforeAll
    static void setup() throws Exception {
        setSimpleChemObjectReader(new MDLRXNV2000Reader(), "0024.stg02.rxn");
    }

    @Test
    void testAccepts() {
        MDLRXNV2000Reader reader = new MDLRXNV2000Reader();
        Assertions.assertTrue(reader.accepts(ChemFile.class));
        Assertions.assertTrue(reader.accepts(ChemModel.class));
        Assertions.assertTrue(reader.accepts(Reaction.class));
    }

    /**
     * @cdk.bug 1849923
     */
    @Test
    void testReadReactions1() throws Exception {
        String filename1 = "0024.stg02.rxn";
        logger.info("Testing: " + filename1);
        InputStream ins1 = this.getClass().getResourceAsStream(filename1);
        MDLRXNV2000Reader reader1 = new MDLRXNV2000Reader(ins1, Mode.STRICT);
        IReaction reaction1 = new Reaction();
        reaction1 = reader1.read(reaction1);
        reader1.close();

        Assertions.assertNotNull(reaction1);
        Assertions.assertEquals(1, reaction1.getReactantCount());
        Assertions.assertEquals(1, reaction1.getProductCount());
        IAtomContainer reactant = reaction1.getReactants().getAtomContainer(0);
        Assertions.assertNotNull(reactant);
        Assertions.assertEquals(46, reactant.getAtomCount());
        Assertions.assertEquals(44, reactant.getBondCount());
        IAtomContainer product = reaction1.getProducts().getAtomContainer(0);
        Assertions.assertNotNull(product);
        Assertions.assertEquals(46, product.getAtomCount());
        Assertions.assertEquals(43, product.getBondCount());

    }

    /**
     * @cdk.bug 1851202
     */
    @Test
    void testBug1851202() throws Exception {
        String filename1 = "0002.stg01.rxn";
        logger.info("Testing: " + filename1);
        InputStream ins1 = this.getClass().getResourceAsStream(filename1);
        MDLRXNV2000Reader reader1 = new MDLRXNV2000Reader(ins1, Mode.STRICT);
        IReaction reaction1 = new Reaction();
        reaction1 = reader1.read(reaction1);
        reader1.close();

        Assertions.assertNotNull(reaction1);
        Assertions.assertEquals(1, reaction1.getReactantCount());
        Assertions.assertEquals(1, reaction1.getProductCount());
        IAtomContainer reactant = reaction1.getReactants().getAtomContainer(0);
        Assertions.assertNotNull(reactant);
        Assertions.assertEquals(30, reactant.getAtomCount());
        Assertions.assertEquals(25, reactant.getBondCount());
        IAtomContainer product = reaction1.getProducts().getAtomContainer(0);
        Assertions.assertNotNull(product);
        Assertions.assertEquals(30, product.getAtomCount());
        Assertions.assertEquals(26, product.getBondCount());

    }

    @Test
    void testReadMapping() throws Exception {
        String filename2 = "mappingTest.rxn";
        logger.info("Testing: " + filename2);
        InputStream ins2 = this.getClass().getResourceAsStream(filename2);
        MDLRXNV2000Reader reader2 = new MDLRXNV2000Reader(ins2);
        IReaction reaction2 = new Reaction();
        reaction2 = reader2.read(reaction2);
        reader2.close();

        Assertions.assertNotNull(reaction2);
        Iterator<IMapping> maps = reaction2.mappings().iterator();
        maps.next();
        Assertions.assertTrue(maps.hasNext());
    }

    @Test
    void countsLineHasAgents_countsLineMatchesMolfiles_modeRelaxed_test() throws Exception {
        // default mode of MDLRXNV2000Reader is RELAXED
        try (MDLRXNV2000Reader reader = new MDLRXNV2000Reader(
                this.getClass().getResourceAsStream("ethylesterification_countsLineHasAgents_countsLineMatchesMolfiles.rxn"))) {
            IReaction reaction = reader.read(SilentChemObjectBuilder.getInstance().newReaction());
            assertThat(reaction.getReactants().getAtomContainerCount()).isEqualTo(2);
            assertThat(reaction.getProducts().getAtomContainerCount()).isEqualTo(2);
            assertThat(reaction.getAgents().getAtomContainerCount()).isEqualTo(1);
        }
    }

    @Test
    void countsLineHasAgents_countsLineMismatchMolfiles_modeRelaxed_test() throws Exception {
        // default mode of MDLRXNV2000Reader is RELAXED
        try (MDLRXNV2000Reader reader = new MDLRXNV2000Reader(
                this.getClass().getResourceAsStream("ethylesterification_countsLineHasAgents_countsLineMismatchMolfiles.rxn"))) {
            IReaction reaction = reader.read(SilentChemObjectBuilder.getInstance().newReaction());
            assertThat(reaction.getReactants().getAtomContainerCount()).isEqualTo(2);
            assertThat(reaction.getProducts().getAtomContainerCount()).isEqualTo(2);
            assertThat(reaction.getAgents().getAtomContainerCount()).isEqualTo(1);
        }
    }

    @Test
    void countsLineHasNoAgents_countsLineMatchesMolfiles_modeRelaxed_test() throws Exception {
        // default mode of MDLRXNV2000Reader is RELAXED
        try (MDLRXNV2000Reader reader = new MDLRXNV2000Reader(
                this.getClass().getResourceAsStream("ethylesterification_countsLineHasNoAgents_countsLineMatchesMolfiles.rxn"))) {
            IReaction reaction = reader.read(SilentChemObjectBuilder.getInstance().newReaction());
            assertThat(reaction.getReactants().getAtomContainerCount()).isEqualTo(2);
            assertThat(reaction.getProducts().getAtomContainerCount()).isEqualTo(2);
            assertThat(reaction.getAgents().getAtomContainerCount()).isEqualTo(0);
        }
    }

    @Test
    void countsLineHasNoAgents_countsLineMismatchesMolfiles_modeRelaxed_test() throws Exception {
        // default mode of MDLRXNV2000Reader is RELAXED
        try (MDLRXNV2000Reader reader = new MDLRXNV2000Reader(
                this.getClass().getResourceAsStream("ethylesterification_countsLineHasNoAgents_countsLineMismatchMolfiles.rxn"))) {
            IReaction reaction = reader.read(SilentChemObjectBuilder.getInstance().newReaction());
            assertThat(reaction.getReactants().getAtomContainerCount()).isEqualTo(2);
            assertThat(reaction.getProducts().getAtomContainerCount()).isEqualTo(2);
            assertThat(reaction.getAgents().getAtomContainerCount()).isEqualTo(1);
        }
    }

    @Test
    void countsLineHasNoAgents_countsLineMatchesMolfiles_modeStrict_test() throws Exception {
        try (MDLRXNV2000Reader reader = new MDLRXNV2000Reader(
                this.getClass().getResourceAsStream("ethylesterification_countsLineHasNoAgents_countsLineMatchesMolfiles.rxn"),
                Mode.STRICT)) {
            IReaction reaction = reader.read(SilentChemObjectBuilder.getInstance().newReaction());
            assertThat(reaction.getReactants().getAtomContainerCount()).isEqualTo(2);
            assertThat(reaction.getProducts().getAtomContainerCount()).isEqualTo(2);
            assertThat(reaction.getAgents().getAtomContainerCount()).isEqualTo(0);
        }
    }

    @Test
    void countsLineHasAgents_countsLineMatchesMolfiles_modeStrict_test() throws Exception {
        try (MDLRXNV2000Reader reader = new MDLRXNV2000Reader(
                this.getClass().getResourceAsStream("ethylesterification_countsLineHasAgents_countsLineMatchesMolfiles.rxn"),
                Mode.STRICT)) {
            assertThatThrownBy(() -> reader.read(SilentChemObjectBuilder.getInstance().newReaction()))
                    .isInstanceOf(CDKException.class)
                    .hasMessage("RXN files uses agent count extension. This is not supported in mode STRICT");
        }
    }

    @Test
    void countsLineHasAgents_countsLineMismatchMolfiles_modeStrict_test() throws Exception {
        try (MDLRXNV2000Reader reader = new MDLRXNV2000Reader(
                this.getClass().getResourceAsStream("ethylesterification_countsLineHasNoAgents_countsLineMismatchMolfiles.rxn"),
                Mode.STRICT)) {
            assertThatThrownBy(() -> reader.read(SilentChemObjectBuilder.getInstance().newReaction()))
                    .isInstanceOf(CDKException.class)
                    .hasMessage("Agents are not supported in mode STRICT. Found 5 molecular entities, but there are only 4 molecular entities declared on the counts line.");
        }
    }

    @Test
    void optionalSdfSeparator() throws Exception {
        String dummyRecord = "ethanol\n" +
                       "  Mrv1810 09251921392D          \n" +
                       "\n" +
                       "  3  2  0  0  0  0            999 V2000\n" +
                       "    1.9520   -1.1270    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                       "    1.2375   -0.7145    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                       "    2.6664   -0.7145    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                       "  1  2  1  0  0  0  0\n" +
                       "  1  3  1  0  0  0  0\n" +
                       "M  END\n" +
                       "$$$$\n";
        String sb = "$RXN\n" +
                "Test\n\n\n  2  1\n" +
                "$MOL\n" +
                dummyRecord +
                "$MOL\n" +
                dummyRecord +
                "$MOL\n" +
                dummyRecord;

        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        try (MDLRXNV2000Reader reader = new MDLRXNV2000Reader(new StringReader(sb))) {
            IReaction rxn = reader.read(bldr.newInstance(IReaction.class));
            assertThat(rxn.getReactants().getAtomContainerCount()).isEqualTo(2);
            assertThat(rxn.getProducts().getAtomContainerCount()).isEqualTo(1);
            assertThat(rxn.getReactants().getAtomContainer(0).getAtomCount()).isEqualTo((3));
            assertThat(rxn.getReactants().getAtomContainer(1).getAtomCount()).isEqualTo((3));
        }
    }
}
