/* Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
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
package org.openscience.cdk.reaction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Tests for IReactionChain implementations.
 *
 */
class ReactionChainTest extends CDKTestCase {

    private final static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    /**
     *  Constructor for the ReactionEngineTest object.
     */
    ReactionChainTest() {
        super();
    }

    /**
     * Junit test.
     *
     * @throws Exception
     */
    @Test
    void testReactionChain() {
        IReactionSet chain = new ReactionChain();
        Assertions.assertNotNull(chain);
    }

    /**
     * Junit test.
     *
     * @throws Exception
     */
    @Test
    void testAddReaction_IReaction_int() {
        ReactionChain chain = new ReactionChain();
        IReaction reaction1 = builder.newInstance(IReaction.class);
        reaction1.setID("reaction1");
        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction1.setID("reaction2");
        IReaction reaction3 = builder.newInstance(IReaction.class);
        reaction1.setID("reaction3");
        chain.addReaction(reaction1, 0);
        chain.addReaction(reaction2, 1);
        chain.addReaction(reaction3, 2);

        Assertions.assertNotNull(chain);

    }

    /**
     * Junit test.
     *
     * @throws Exception
     */
    @Test
    void testGetReactionStep_IReaction() {
        ReactionChain chain = new ReactionChain();
        IReaction reaction1 = builder.newInstance(IReaction.class);
        reaction1.setID("reaction1");
        chain.addReaction(reaction1, 0);
        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction1.setID("reaction2");
        IReaction reaction3 = builder.newInstance(IReaction.class);
        reaction1.setID("reaction3");
        chain.addReaction(reaction1, 0);
        chain.addReaction(reaction2, 1);
        chain.addReaction(reaction3, 2);

        Assertions.assertEquals(1, chain.getReactionStep(reaction2));
    }

    /**
     * Junit test.
     *
     * @throws Exception
     */
    @Test
    void testGetReaction_int() {
        ReactionChain chain = new ReactionChain();
        IReaction reaction1 = builder.newInstance(IReaction.class);
        reaction1.setID("reaction1");
        chain.addReaction(reaction1, 0);
        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction1.setID("reaction2");
        IReaction reaction3 = builder.newInstance(IReaction.class);
        reaction1.setID("reaction3");
        chain.addReaction(reaction1, 0);
        chain.addReaction(reaction2, 1);
        chain.addReaction(reaction3, 2);

        Assertions.assertEquals(reaction2, chain.getReaction(1));

    }
}
