package net.praqma.jenkins.plugin.mrp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import hudson.matrix.Axis;
import hudson.matrix.AxisList;
import hudson.matrix.Combination;
import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.model.Result;

import org.junit.BeforeClass;
import org.junit.Test;
import org.jvnet.hudson.test.HudsonTestCase;

public class MatrixReloadedListenerTest extends HudsonTestCase
{
	private AxisList axes = null;
	private Combination c = null;
	
	@BeforeClass
	public void init()
	{
		
	    axes = new AxisList( new Axis( "dim1", "1", "2", "3" ), 
	    		             new Axis( "dim2", "a", "b", "c" ) 
	                       );
	                       
	    
        Map<String,String> r = new HashMap<String, String>();
        r.put( "dim1", "1" );
        r.put( "dim2", "a" );
        c = new Combination( r );
	}
	
	public void test1() throws IOException, InterruptedException, ExecutionException
	{
		init();
		
		MatrixProject mp = createMatrixProject( "test" );
		mp.setAxes( axes );
		//mp.addAction( new MatrixReloadedAction() );
		
		MatrixBuild mb = mp.scheduleBuild2( 0 ).get();
		MatrixRun mr = mb.getRun( c );
		Result r = mr.getResult();
		
		System.out.println( "---->" + r.toString() );
		
		assertNotNull( mb );
	}
}
