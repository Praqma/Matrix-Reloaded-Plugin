package net.praqma.jenkins.plugin.mrp;

import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.model.Run;
import hudson.model.listeners.RunListener;

public class MatrixReloadedListener extends RunListener<Run>
{
	public MatrixReloadedListener()
	{
		super( Run.class );
	}
	
	public void onStarted( Run run, TaskListener listener)
	{
		if( run instanceof AbstractBuild )
		{
			AbstractBuild build = (AbstractBuild)run;
		}
	}

}
