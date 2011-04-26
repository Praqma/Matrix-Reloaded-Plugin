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
import hudson.model.ParameterValue;
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
            List<ParameterValue> pvs = run.getActions( ParametersAction.class ).get(0).getParameters();
            MatrixRun mr = (MatrixRun)run;
            
            //StringParameterValue mrpNumber = (StringParameterValue)getParameterValue( pvs, Definitions.prefix + "NUMBER" );
            StringParameterValue uuid = (StringParameterValue)getParameterValue( pvs, "uuid" );
            
            
            /* This run is not related to a matrix reloaded build */
            if( uuid == null )
            {
            	return;
            }
            
            BuildState bs = MatrixReloadedState.getInstance().getBuildState( uuid.value );

            int mnumber = bs.rebuildNumber;
            run.setRedoRun( mnumber, bs.getConfiguration( mr.getParent().getCombination().toString() ) );
            
            System.out.println( "[WOLLE] REDO=" + bs.getConfiguration( mr.getParent().getCombination().toString() ) );
			
            /*
	        try
	        {
	        	
	        	//List<ParametersAction> pvs2 = run.getActions( ParametersAction.class );
	            	            
	            //System.out.println( "[WOLLE] NAME OF: " + mr.getParent().getCombination().toString() );
	        	for( ParameterValue pv : pvs )
	        	{
	        		//listener.getLogger().println( "[MRP] * " + pv.getName() );
	        		
	        		if( pv.getName().startsWith( Definitions.prefix ) )
	        		{
	        			String[] vs = pv.getName().split( Definitions.delimiter, 2 );
	        			if( vs[1].equals( mr.getParent().getCombination().toString() ) )
	        			{
	        				//listener.getLogger().println( "[MRP] This configuration is a part of a rebuild" );
	        				BooleanParameterValue pvb = (BooleanParameterValue)pv;
	        				
	        				run.setRedoRun( mnumber, pvb.value ); // rebuild = pvb.value

							break;
	        			}
	        			
	        		}
	        	}
	        }
	        catch( IndexOutOfBoundsException e )
	        {
	        	listener.getLogger().println( "[MRP] No parameters found" );
	        }
	        */
	        
	        /* This not possible
	        if( pvs != null )
	        {
		        // Remove parameters?
				for( ParameterValue pv : pvs )
				{
					if( pv.getName().startsWith( Definitions.prefix ) )
					{
						pvs.remove( pv );
					}
				}
	        }
	        */
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
			AbstractBuild build = (AbstractBuild)run;

			MatrixReloadedAction action = new MatrixReloadedAction( ( (MatrixRun)run ).getParent().getCombination().toString() );
			build.getActions().add( action );
		}
	}

}
