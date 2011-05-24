package net.praqma.jenkins.plugin.reloaded;

import net.praqma.jenkins.plugin.reloaded.MatrixReloadedState.BuildState;

import hudson.Extension;
import hudson.matrix.MatrixConfiguration;
import hudson.matrix.MatrixBuild;
import hudson.matrix.listeners.MatrixBuildListener;

@Extension
public class MatrixReloadedBuildListener extends MatrixBuildListener{
	
	public boolean doBuildConfiguration(MatrixBuild b, MatrixConfiguration c)
	{
        BuildState bs = Util.getBuildStateFromRun(b);
        if( bs == null ) {
        	return true;
        }
        
        return bs.getConfiguration(c.getCombination());
	}
}
