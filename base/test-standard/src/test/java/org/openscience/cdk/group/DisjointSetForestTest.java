/* Copyright (C) 2012  Gilleain Torrance <gilleain.torrance@gmail.com>
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.group;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author maclean
 */
class DisjointSetForestTest {

    @Test
    void constructorTest() {
        int n = 10;
        DisjointSetForest forest = new DisjointSetForest(n);
        Assertions.assertNotNull(forest);
    }

    @Test
    void getTest() {
        int n = 10;
        DisjointSetForest forest = new DisjointSetForest(n);
        for (int i = 0; i < n; i++) {
            Assertions.assertEquals(-1, forest.get(i));
        }
    }

    @Test
    void getRootTest() {
        int n = 2;
        DisjointSetForest forest = new DisjointSetForest(n);
        forest.makeUnion(0, 1);
        Assertions.assertEquals(0, forest.getRoot(1));
    }

    @Test
    void makeUnionTest() {
        int n = 2;
        DisjointSetForest forest = new DisjointSetForest(n);
        forest.makeUnion(0, 1);
        Assertions.assertEquals(0, forest.get(1));
    }

    @Test
    void getSetsTest() {
        int n = 6;
        DisjointSetForest forest = new DisjointSetForest(n);
        forest.makeUnion(0, 1);
        forest.makeUnion(2, 3);
        forest.makeUnion(4, 5);
        int[][] sets = forest.getSets();
        int[][] expected = new int[][]{{0, 1}, {2, 3}, {4, 5}};
        String failMessage = "Expected " + Arrays.deepToString(expected) + " but was " + Arrays.deepToString(sets);
        Assertions.assertTrue(Arrays.deepEquals(expected, sets), failMessage);
    }

}
