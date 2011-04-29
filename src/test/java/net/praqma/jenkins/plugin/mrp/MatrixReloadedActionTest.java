package net.praqma.jenkins.plugin.mrp;

import hudson.matrix.Axis;
import hudson.matrix.AxisList;
import hudson.matrix.Combination;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.model.Cause;
import hudson.model.ParameterValue;
import hudson.model.ParameterDefinition;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.StringParameterDefinition;
import hudson.model.StringParameterValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import net.praqma.jenkins.plugin.mrp.MatrixReloadedAction.BuildType;
import net.praqma.jenkins.plugin.mrp.MatrixReloadedState.BuildState;
import net.sf.json.JSONObject;

import org.junit.BeforeClass;
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
	
	public void testBuildType()
	{
		BuildType bt = BuildType.MATRIXBUILD;
	}
	
	
	private AxisList axes = null;
	private Combination c = null;
	
	public void init()
	{
		
	    axes = new AxisList( new Axis( "dim1", "1", "2" ), 
	    		             new Axis( "dim2", "a", "b" ) 
	                       );
	                       
	    
        Map<String,String> r = new HashMap<String, String>();
        r.put( "dim1", "1" );
        r.put( "dim2", "a" );
        c = new Combination( r );
	}
	
	public void testForm() throws IOException, InterruptedException, ExecutionException
	{
		/* Create a previous build */
		init();
		
		MatrixProject mp = createMatrixProject( "test" );
		mp.setAxes( axes );
		List<ParameterDefinition> list = new ArrayList<ParameterDefinition>();
		list.add( new StringParameterDefinition( "Tetst", "wolle" ) );
		ParametersDefinitionProperty pdp = new ParametersDefinitionProperty( list );
		mp.addProperty( pdp );
		
		
		/* Create some parameters to test continuation of parameters from reused to new build */
		List<ParameterValue> values = new ArrayList<ParameterValue>();
		values.add( new StringParameterValue( "testParm", "value" ) );
		
		MatrixBuild mb = mp.scheduleBuild2( 0, new Cause.UserCause(), new ParametersAction( values ) ).get();
		
		/* Create form elements */
		JSONObject form = new JSONObject();
		
		form.element( "MRP::NUMBER", 1 );
		
		form.element( "MRP::dim1=1,dim2=a", false );
		form.element( "MRP::dim1=1,dim2=b", true );
		form.element( "MRP::dim1=2,dim2=a", true );
		form.element( "MRP::dim1=2,dim2=b", false );
		
		MatrixReloadedAction mra = new MatrixReloadedAction();
		mra.performConfig( mb, form );
		
	}
	
}
