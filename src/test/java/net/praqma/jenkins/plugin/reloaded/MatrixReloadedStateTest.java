
package net.praqma.jenkins.plugin.reloaded;

import hudson.matrix.Combination;
import net.praqma.jenkins.plugin.reloaded.MatrixReloadedState;
import net.praqma.jenkins.plugin.reloaded.MatrixReloadedState.BuildState;

import org.jvnet.hudson.test.HudsonTestCase;

public class MatrixReloadedStateTest extends HudsonTestCase {
    public void testInstance() {
        MatrixReloadedState mrs = MatrixReloadedState.getInstance();

        assertNotNull(mrs);
    }

    public void testBuildState() {
        BuildState bs = MatrixReloadedState.getInstance().getBuildState("test");

        assertNotNull(bs);

        bs.remove();
    }

    public void testConfigurations() {
        BuildState bs = MatrixReloadedState.getInstance().getBuildState("test");

        assertNotNull(bs);

        bs.rebuildNumber = 1;
        bs.addConfiguration(Combination.fromString("a=1"), false);
        bs.addConfiguration(Combination.fromString("a=2"), true);

        assertFalse(bs.getConfiguration(Combination.fromString("a=1")));
        assertTrue(bs.getConfiguration(Combination.fromString("a=2")));
        assertFalse(bs.getConfiguration(Combination.fromString("a=3")));
        assertEquals(1, bs.rebuildNumber);

        bs.remove();
    }

    public void testBuildStateBranch() {
        BuildState bs = MatrixReloadedState.getInstance().getBuildState("test");

        assertNotNull(bs);

        /* Try to reach the other branch, where test IS defined */
        BuildState bs2 = MatrixReloadedState.getInstance().getBuildState("test");
        assertNotNull(bs2);

        bs.remove();
        bs2.remove();
    }

    public void testToString() {
        BuildState bs = MatrixReloadedState.getInstance().getBuildState("test");

        bs.addConfiguration(Combination.fromString("c1=1"), true);
        bs.addConfiguration(Combination.fromString("c2=2"), false);

        String s = bs.toString();

        assertEquals("c1=1: true\nc2=2: false\n", s);

        bs.remove();
    }

    public void testRemove() {
        BuildState bs = MatrixReloadedState.getInstance().getBuildState("test");

        bs.addConfiguration(Combination.fromString("c1=1"), true);
        bs.addConfiguration(Combination.fromString("c2=2"), false);

        bs.removeConfiguration(Combination.fromString("c1=1"));
        bs.removeConfiguration(Combination.fromString("c2=2"));

        assertEquals(0, bs.size());
        assertFalse(MatrixReloadedState.getInstance().exists("test"));
    }
}
