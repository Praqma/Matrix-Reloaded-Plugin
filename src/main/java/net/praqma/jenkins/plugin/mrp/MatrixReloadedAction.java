package net.praqma.jenkins.plugin.mrp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import net.praqma.jenkins.plugin.mrp.MatrixReloadedState.BuildState;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixBuild;
import hudson.model.AbstractBuild;
import hudson.model.BooleanParameterValue;
import hudson.model.Action;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.Hudson;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.StringParameterValue;

public class MatrixReloadedAction implements Action
{
	private AbstractBuild<?, ?> build;
	private String checked = null;
	
	enum BuildType
	{
		MATRIXBUILD,
		MATRIXRUN,
		UNKNOWN
	}
	
	public MatrixReloadedAction(){}
	
	public MatrixReloadedAction( String checked )
	{
		this.checked = checked;
	}

	public String getDisplayName()
	{
		return "Matrix Reloaded";
	}

	public String getIconFileName()
	{
		//return "matrix_small.png";
		return "/plugin/mrp/images/matrix_small.png";
	}

	public String getUrlName()
	{
		return "matrix-reloaded";
	}
	
	public AbstractBuild<?, ?> getBuild()
	{
		return build;
	}
    
    public String getPrefix()
    {
    	return Definitions.prefix;
    }
    
    public String getChecked()
    {
    	return this.checked;
    }
	
	public void doConfigSubmit( StaplerRequest req, StaplerResponse rsp ) throws ServletException, IOException, InterruptedException
	{
		AbstractBuild<?, ?> mbuild = req.findAncestorObject(AbstractBuild.class);
		AbstractBuild<?, ?> build = null;
		
		BuildType type;
		
		if( req.findAncestor( MatrixBuild.class ) != null )
		{
			type = BuildType.MATRIXBUILD;
			build = mbuild;
		}
		else if( req.findAncestor( MatrixRun.class ) != null )
		{
			type = BuildType.MATRIXRUN;
			build = ((MatrixRun)mbuild).getParentBuild();
		}
		else
		{
			type = BuildType.UNKNOWN;
		}
		
		List<ParameterValue> values = new ArrayList<ParameterValue>();
		
        JSONObject formData = req.getSubmittedForm();
        //System.out.println( "[WOLLE] formDAta=" + formData.toString( 2 ) );
        //JSONArray runRedos = (JSONArray)formData.get( Definitions.prefix + "run" );
        
        //System.out.println( "[WOLLE] formREDO=" + runRedos.toString( 2 ) );
        //Iterator<?> it = runRedos.keys();
        Iterator<?> it = formData.keys();
        //runRedos.
        //formData.accumulate( Definitions.prefix + "run", value );
        
        
        System.out.println( "[MRP] The MATRIX RELOADED FORM has been submitted" );
        
        /* UUID */
        String uuid = build.getProject().getDisplayName() + "_" + build.getNumber() + "_" + System.currentTimeMillis();
        BuildState bs = MatrixReloadedState.getInstance().getBuildState( uuid );
        
        /* Generate the parameters */
        while( it.hasNext() )
        //for( int i = 0 ; i < runRedos.size() ; ++i )
        {
        	String key = (String)it.next();
        	//System.out.println( "[WOLLE] " + i + " + " + runRedos.getBoolean( i ) );
        	//System.out.println( "[WOLLE] " + key + " + " + formData.getBoolean( key ) );
        	 
        	/* Check the field */
        	
        	if( key.startsWith( Definitions.prefix ) )
        	{
        		String[] vs = key.split( Definitions.delimiter, 2 );
        		try
        		{
	        		boolean checked = formData.getBoolean( key );
	        		
	        		boolean rebuild = false;
	        		
	        		/**/
	        		if( vs.length > 1 && checked )
	        		{
	        			rebuild = true;
	        		}
	        		
	        		/* Create the parameter */
	        		if( vs.length > 1 )
	        		{
	        			//values.add( new BooleanParameterValue( key, rebuild ) );
	        			bs.addConfiguration( vs[1], rebuild );
	        		}
        		}
        		catch( JSONException e )
        		{
        			/* No-op, not the parameter we were looking for. */
        		}
        	}
        	
        	if( key.equals( Definitions.prefix + "NUMBER" ) )
        	{
        		String value = formData.getString( key );
        		//values.add( new StringParameterValue( key, value ) );
        		try
        		{
        			bs.rebuildNumber = Integer.parseInt( value );
        		}
        		catch( NumberFormatException w )
        		{
        			bs.rebuildNumber = 0;
        		}
        	}
        }
        
        //System.out.println( "[WOLLE] OWNER=" + build.getProject().getDisplayName() );
        //System.out.println( "[WOLLE] SIZE OF: " + values.size() );
        
        /* Get the parameters, if any and add them to the build */
        ParametersDefinitionProperty paramDefprop = build.getProject().getProperty(ParametersDefinitionProperty.class);
        if( paramDefprop != null )
        {
        	List<ParameterDefinition> defs = paramDefprop.getParameterDefinitions();
        	for( ParameterDefinition pd : defs )
        	{
        		if( !pd.getName().startsWith( Definitions.prefix ) )
        		{
        			values.add( pd.getDefaultParameterValue() );
        		}        		
        	}
        }
        
        values.add( new StringParameterValue( "uuid", uuid ) );
        
        Hudson.getInstance().getQueue().schedule( 
        		build.getProject(), 0, new ParametersAction(values), new CauseAction(new Cause.UserCause())
        );
        
        
        if( type.equals( BuildType.MATRIXRUN ) )
        {
        	rsp.sendRedirect( "../../../" );
        }
        else
        {
        	rsp.sendRedirect( "../../" );
        }
	}

}
