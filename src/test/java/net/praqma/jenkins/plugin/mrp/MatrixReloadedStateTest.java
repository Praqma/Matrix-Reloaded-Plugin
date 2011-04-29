package net.praqma.jenkins.plugin.mrp;

import net.praqma.jenkins.plugin.mrp.MatrixReloadedState.BuildState;

import org.jvnet.hudson.test.HudsonTestCase;

public class MatrixReloadedStateTest extends HudsonTestCase
{
	public void testInstance()
	{
		MatrixReloadedState mrs = MatrixReloadedState.getInstance();
		
		assertNotNull( mrs );
	}
	
	public void testBuildState()
	{
		BuildState bs = MatrixReloadedState.getInstance().getBuildState( "test" );
		
		assertNotNull( bs );
		
		bs.rebuildNumber = 1;
		bs.addConfiguration( "a=1", false );
		bs.addConfiguration( "a=2", true );
		
		assertFalse( bs.getConfiguration( "a=1" ) );
		assertTrue( bs.getConfiguration( "a=2" ) );
		assertFalse( bs.getConfiguration( "a=3" ) );
		assertEquals( 1, bs.rebuildNumber );
		
		bs.remove();
	}
}
