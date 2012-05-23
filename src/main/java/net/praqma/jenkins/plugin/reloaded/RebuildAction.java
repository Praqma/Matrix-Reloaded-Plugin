package net.praqma.jenkins.plugin.reloaded;

import java.util.HashMap;
import java.util.Map;

import hudson.matrix.Combination;
import hudson.model.Action;

public class RebuildAction implements Action {

	/**
     * Determines whether a configuration should be reused or rebuild
     */
    private Map<String, Boolean> configurations = new HashMap<String, Boolean>();
	
    /**
     * The base build for this rebuild action
     */
	private int baseBuildNumber = 0;
	
	/**
	 * Determines whether to propagate matrix reloaded to downstream builds
	 */
	private boolean rebuildDownstream = false; 
	
	public RebuildAction() {
	}
	
	public void setBaseBuildNumber( int baseBuildNumber ) {
		this.baseBuildNumber = baseBuildNumber;
	}
	
	public void setRebuildDownstream( boolean b ) {
		this.rebuildDownstream = b;
	}
	
	public int getBaseBuildNumber() {
		return baseBuildNumber;
	}
	
	public boolean doRebuildDownstream() {
		return rebuildDownstream;
	}
	
	public RebuildAction clone( int baseBuildNumber ) {
		RebuildAction ra = new RebuildAction();
		ra.baseBuildNumber = baseBuildNumber;
		
		return ra;
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
     * Returns whether or not to rebuild the {@link hudson.model.Run} If the
     * combination is not in the database, the method returns true, meaning
     * the run will build.
     *
     * @param combination A {@link hudson.matrix.MatrixConfiguration} given
     * as its {@link hudson.matrix.Combination}
     * @return A boolean determining whether or nor to rebuild the
     *         {@link hudson.model.Run}
     */
    public boolean getConfiguration(Combination combination) {
        if (configurations.containsKey(combination.toString())) {
            return configurations.get(combination.toString());
        }

        return false;
    }
	
	public String getDisplayName() {
		return null;
	}

	public String getIconFileName() {
		return null;
	}

	public String getUrlName() {
		return null;
	}

}
