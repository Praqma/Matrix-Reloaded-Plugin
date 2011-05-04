
package net.praqma.jenkins.plugin.reloaded;

import hudson.matrix.Combination;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to pass parameters from a form to a Run, given a uuid.
 * This is a singleton class.
 * 
 * @author wolfgang
 */
public class MatrixReloadedState {
    private static final MatrixReloadedState instance = new MatrixReloadedState();

    private MatrixReloadedState() {
    }

    public static MatrixReloadedState getInstance() {
        return instance;
    }

    /**
     * This is the class holding the information about a Matrix Reloaded state
     * retrieved from the form submit.
     * 
     * @author wolfgang
     */
    public class BuildState {
        /**
         * Determines whether a configuration should be reused or rebuild
         */
        Map<String, Boolean> configurations = new HashMap<String, Boolean>();

        public String uuid;

        public int rebuildNumber;

        BuildState(String uuid) {
            this.uuid = uuid;
        }

        /**
         * Add a configuration to the build state
         * 
         * @param config A String representing the
         *            {@link hudson.matrix.MatrixConfiguration} given as its
         *            {@link hudson.matrix.Combination}
         * @param reuse A boolean to determine whether to reuse the
         *            {@link hudson.model.Run} or not
         */
        public void addConfiguration(Combination combination, boolean reuse) {
            this.configurations.put(combination.toString(), reuse);
        }

        /**
         * Remove the {@link BuildState} object from the Map.
         */
        public void remove() {
            MatrixReloadedState.this.buildStates.remove(this.uuid);
        }

        /**
         * Remove a configuration from the build state
         * 
         * @param combination A {@link hudson.matrix.MatrixConfiguration} given
         *            as its {@link hudson.matrix.Combination}
         */
        public void removeConfiguration(Combination combination) {
            configurations.remove(combination.toString());

            /* Check if empty */
            if (configurations.isEmpty()) {
                remove();
            }
        }

        /**
         * Returns whether or not to reuse the {@link hudson.model.Run}
         * 
         * @param combination A {@link hudson.matrix.MatrixConfiguration} given
         *            as its {@link hudson.matrix.Combination}
         * @return A boolean determining whether or nor to reuse the
         *         {@link hudson.model.Run}
         */
        public boolean getConfiguration(Combination combination) {
            if (configurations.containsKey(combination.toString())) {
                return configurations.get(combination.toString());
            }

            return false;
        }

        public String toString() {
            Set<String> keys = configurations.keySet();
            Iterator<String> it = keys.iterator();

            StringBuffer sb = new StringBuffer();

            while (it.hasNext()) {
                String s = it.next();
                sb.append(s + ": " + configurations.get(s) + "\n");
            }

            return sb.toString();
        }

        public int size() {
            return configurations.size();
        }
    }

    /**
     * The data of the class
     */
    private Map<String, BuildState> buildStates = new HashMap<String, BuildState>();

    /**
     * Return a specific BuildState given a uuid
     * 
     * @param uuid The uuid
     * @return The BuildState
     */
    public BuildState getBuildState(String uuid) {
        if (!buildStates.containsKey(uuid)) {
            buildStates.put(uuid, new BuildState(uuid));
        }

        return buildStates.get(uuid);
    }

    public boolean exists(String uuid) {
        return buildStates.containsKey(uuid);
    }
}
