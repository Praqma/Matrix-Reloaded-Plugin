package net.praqma.jenkins.plugin.mrp;

import java.io.IOException;
import java.util.List;

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
		//System.out.println( "[WOLLE] Started.... " + run.getDisplayName() );
		System.out.println( "[WOLLE] Started.... " + run.getParent().getDisplayName() );
		
		/* Parse ParametersAction */
        /* First try to find the correct parameter for the project */
		/* Test for MatrixBuild */
		if( run instanceof MatrixRun )
		{
			listener.getLogger().println( "[MRP] This is a matrix run" );
			
	        try
	        {
	        	
	        	List<ParametersAction> pvs2 = run.getActions( ParametersAction.class );
	        	System.out.println( "[WOLLE] SIZE OF PARMS2: " + pvs2.size() );
	        	
	            List<ParameterValue> pvs = run.getActions( ParametersAction.class ).get(0).getParameters();
	            System.out.println( "[WOLLE] SIZE OF PARMS: " + pvs.size() );
	            MatrixRun mr = (MatrixRun)run;
	            
	            StringParameterValue mrpNumber = (StringParameterValue)getParameterValue( pvs, "MRP_NUMBER" );
	            System.out.println( "[WOLLE] mrpNumnber=" + mrpNumber );
	            int mnumber = Integer.parseInt( mrpNumber.value );
	            	            
	            System.out.println( "[WOLLE] NAME: " + mr.getParent().getCombination().toString() );
	        	for( ParameterValue pv : pvs )
	        	{
	        		listener.getLogger().println( "[MRP] * " + pv.getName() );
	        		
	        		if( pv.getName().startsWith( "mrp::" ) )
	        		{
	        			String[] vs = pv.getName().split( "::", 2 );
	        			if( vs[1].equals( mr.getParent().getCombination().toString() ) )
	        			{
	        				listener.getLogger().println( "[MRP] There was a match! " );
	        				BooleanParameterValue pvb = (BooleanParameterValue)pv;
	        				
	        				/* Reusing */
	        				if( pvb.value )
	        				{
	        					run.setReuse( mnumber );
	        				}
	        				
	        				/*
	        				try
							{
								EnvVars ev = run.getEnvironment( listener );
								ev.put( "MRP_NUMBER", mrpNumber.value );
								listener.getLogger().println( "[MRP] Created MRP_NUMBER" );
							}
							catch( Exception e )
							{
								listener.getLogger().println( "[MRP] Unable to create MRP_NUMBER" );
							}
							*/
							
							/* We could break here... */
							break;
	        			}
	        			
	        		}
	        	}
	        }
	        catch( IndexOutOfBoundsException e )
	        {
	        	listener.getLogger().println( "[MRP] No parameters" );
	        }
		}

	}
	
	private ParameterValue getParameterValue( List<ParameterValue> pvs, String key )
	{
    	for( ParameterValue pv : pvs )
    	{
    		if( pv.getName().equals( key ) )
    		{
    			System.out.println( "[WOLLE] I GOT IT!!" );
    			return pv;
    		}
    	}
    	
    	System.out.println( "[WOLLE] I DIDNT GET IT!!" );
    	return null;
	}
	
	/**
	 * Add the Matrix Reloaded link to the build context
	 */
	@Override
	public void onCompleted( Run run, TaskListener listener )
	{
		System.out.println( "[WOLLE] Completed...." );
		
		/* Test for MatrixBuild */
		if( run instanceof MatrixBuild )
		{
			System.out.println( "[WOLLE] Yes, this is a matrix build! " + run.getParent().getDisplayName() + " " + run.getDisplayName() );
			
			AbstractBuild build = (AbstractBuild)run;

			MatrixReloadedAction action = new MatrixReloadedAction();
			build.getActions().add( action );
		}
		else
		{
			System.out.println( "[WOLLE] This was NOT a matrix build!" );
		}
		
		/* Test for MatrixRun */
		if( run instanceof MatrixRun )
		{
			
		}
	}

}
