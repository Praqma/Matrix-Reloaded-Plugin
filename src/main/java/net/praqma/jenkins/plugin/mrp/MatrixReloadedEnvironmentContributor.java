package net.praqma.jenkins.plugin.mrp;

import java.io.IOException;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentContributor;
import hudson.model.TaskListener;
import hudson.model.Run;

@Extension
public class MatrixReloadedEnvironmentContributor extends EnvironmentContributor
{
	public void buildEnvironmentFor( Run r, EnvVars envs, TaskListener listener ) throws IOException, InterruptedException
	{
		if( r.getRedoRun() != null )
		{
			envs.put( Definitions.rebuildJobVarName, r.getRedoRun().number + "" );
		}
	}
}
