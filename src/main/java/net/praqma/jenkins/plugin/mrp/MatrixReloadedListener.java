package net.praqma.jenkins.plugin.mrp;

import java.io.IOException;
import java.util.List;

import net.praqma.jenkins.plugin.mrp.MatrixReloadedState.BuildState;

import hudson.EnvVars;
import hudson.Extension;
import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixBuild;
import hudson.model.AbstractBuild;
import hudson.model.BooleanParameterValue;
import hudson.model.JobProperty;
import hudson.model.ParameterValue;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.TaskListener;
import hudson.model.ParametersAction;
import hudson.model.Run;
import hudson.model.StringParameterValue;
import hudson.model.listeners.RunListener;

@Extension
public class MatrixReloadedListener extends RunListener<Run>
{
	public MatrixReloadedListener()
	{
		super( Run.class );
	}
	
	/**
	 * 
	 */
	@Override
	public void onStarted( Run run, TaskListener listener )
	{
		/* Parse ParametersAction */
        /* First try to find the correct parameter for the project */
		/* Test for MatrixBuild */
		if( run instanceof MatrixRun )
		{
			StringParameterValue uuid;
			try
			{
				List<ParameterValue> pvs = run.getActions( ParametersAction.class ).get(0).getParameters();
				uuid = (StringParameterValue)getParameterValue( pvs, "uuid" );
			}
			catch( ArrayIndexOutOfBoundsException e )
			{
				/* This is not a Matrix Reloaded instance */
				return;
			}
            
            MatrixRun mr = (MatrixRun)run;
                        
            //StringParameterValue mrpNumber = (StringParameterValue)getParameterValue( pvs, Definitions.prefix + "NUMBER" );
            
            
            
            /* This run is not related to a matrix reloaded build */
            if( uuid == null )
            {
            	return;
            }
            
            BuildState bs = MatrixReloadedState.getInstance().getBuildState( uuid.value );

            int mnumber = bs.rebuildNumber;
            run.setRedoRun( mnumber, bs.getConfiguration( mr.getParent().getCombination().toString() ) );
            
            //System.out.println( "[WOLLE] REDO=" + bs.getConfiguration( mr.getParent().getCombination().toString() ) );
		}
	}
	
	private ParameterValue getParameterValue( List<ParameterValue> pvs, String key )
	{
    	for( ParameterValue pv : pvs )
    	{
    		if( pv.getName().equals( key ) )
    		{
    			return pv;
    		}
    	}
    	
    	return null;
	}
	
	/**
	 * Add the Matrix Reloaded link to the build context
	 */
	@Override
	public void onCompleted( Run run, TaskListener listener )
	{
		/* Test for MatrixBuild */
		if( run instanceof MatrixBuild )
		{
			AbstractBuild build = (AbstractBuild)run;

			MatrixReloadedAction action = new MatrixReloadedAction();
			build.getActions().add( action );
		}
		
		/* Test for MatrixRun */
		if( run instanceof MatrixRun )
		{
			AbstractBuild<?, ?> build = (AbstractBuild<?, ?>)run;

			MatrixReloadedAction action = new MatrixReloadedAction( ( (MatrixRun)run ).getParent().getCombination().toString() );
			build.getActions().add( action );
		}
	}

}
