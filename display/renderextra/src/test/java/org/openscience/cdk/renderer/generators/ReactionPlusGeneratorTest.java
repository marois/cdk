/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer.generators;

import java.awt.Rectangle;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.renderer.elements.IRenderingElement;

/**
 */
public class ReactionPlusGeneratorTest extends AbstractGeneratorTest {

    private ReactionPlusGenerator generator;

    @Override
    protected Rectangle getCustomCanvas() {
        return null;
    }

    @BeforeEach
    @Override
    void setup() {
        super.setup();
        model.registerParameters(new ReactionSceneGenerator());
        model.registerParameters(new BasicBondGenerator());
        this.generator = new ReactionPlusGenerator();
        model.registerParameters(generator);
        super.setTestedGenerator(generator);
    }

    @Test
    void testEmptyReaction() {
        IReaction singleReaction = super.builder.newInstance(IReaction.class);

        // nothing should be made
        IRenderingElement root = generator.generate(singleReaction, model);
        List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
        Assertions.assertEquals(0, elements.size());
    }

}
