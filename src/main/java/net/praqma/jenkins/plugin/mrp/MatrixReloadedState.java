package net.praqma.jenkins.plugin.mrp;

import java.util.HashMap;
import java.util.Map;

public class MatrixReloadedState
{
	private static final MatrixReloadedState instance = new MatrixReloadedState();
	
	private MatrixReloadedState(){	}
	
	public static MatrixReloadedState getInstance()
	{
		return instance;
	}
	
	public class BuildState
	{
		Map<String, Boolean> configurations = new HashMap<String, Boolean>();
		public String uuid;
		public int rebuildNumber;
		
		BuildState( String uuid )
		{
			this.uuid = uuid;
		}
		
		public void addConfiguration( String config, boolean reuse )
		{
			this.configurations.put( config, reuse );
		}
		
		public void remove()
		{
			MatrixReloadedState.this.buildStates.remove( this.uuid );
		}
		
		public boolean getConfiguration( String config )
		{
			if( configurations.containsKey( config ) )
			{
				return configurations.get( config );
			}
			
			return false;
		}
	}

	Map<String, BuildState> buildStates = new HashMap<String, BuildState>();

	public BuildState getBuildState( String uuid )
	{
		
		if( !buildStates.containsKey( uuid ) )
		{
			buildStates.put( uuid, new BuildState( uuid ) );
		}
		
		return buildStates.get( uuid );
	}
		
	//public void remove
	
}
