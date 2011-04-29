package net.praqma.jenkins.plugin.mrp;

import org.jvnet.hudson.test.HudsonTestCase;

public class MatrixReloadedActionTest extends HudsonTestCase
{
	public void testGetDisplayName()
	{
		MatrixReloadedAction mra = new MatrixReloadedAction();
		
		assertEquals( mra.getDisplayName(), "Matrix Reloaded" );
	}
	
	public void testGetIconFileName()
	{
		MatrixReloadedAction mra = new MatrixReloadedAction();
		
		assertEquals( mra.getIconFileName(), "/plugin/mrp/images/matrix_small.png" );
	}
	
	public void testGetUrlName()
	{
		MatrixReloadedAction mra = new MatrixReloadedAction();
		
		assertEquals( mra.getUrlName(), "matrix-reloaded" );
	}
	
	public void testGetChecked1()
	{
		MatrixReloadedAction mra = new MatrixReloadedAction();
		
		assertNull( mra.getChecked() );
	}
	
	public void testGetChecked2()
	{
		MatrixReloadedAction mra = new MatrixReloadedAction( "test" );
		
		assertNotNull( mra.getChecked() );
		assertEquals( mra.getChecked(), "test" );
	}
}
