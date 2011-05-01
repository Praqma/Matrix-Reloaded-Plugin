package net.praqma.jenkins.plugin.mrp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import net.praqma.jenkins.plugin.mrp.MatrixReloadedState.BuildState;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixBuild;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.Hudson;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.StringParameterValue;

/**
 * The Matrix Reloaded Action class.
 * This enables the plugin to add the link action to the side panel.
 * @author wolfgang
 *
 */
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
    
    public void performConfig( AbstractBuild<?, ?> build, JSONObject formData )
    {
		List<ParameterValue> values = new ArrayList<ParameterValue>();
		
        Iterator<?> it = formData.keys();
        
        System.out.println( "[MRP] The MATRIX RELOADED FORM has been submitted" );
        System.out.println( "[WOLLE] FORM=" + formData.toString( 2 ) );
        
        /* UUID */
        String uuid = build.getProject().getDisplayName() + "_" + build.getNumber() + "_" + System.currentTimeMillis();
        BuildState bs = MatrixReloadedState.getInstance().getBuildState( uuid );
        
        /* Generate the parameters */
        while( it.hasNext() )
        {
        	String key = (String)it.next();

        	/* Check the fields of the form */
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
	        		
	        		/* Add parameter to the build state */
	        		if( vs.length > 1 )
	        		{
	        			bs.addConfiguration( vs[1], rebuild );
	        		}
        		}
        		catch( JSONException e )
        		{
        			/* No-op, not the field we were looking for. */
        		}
        	}
        	
        	/* The special form field, providing information about the build we decent from */
        	if( key.equals( Definitions.prefix + "NUMBER" ) )
        	{
        		String value = formData.getString( key );
        		try
        		{
        			bs.rebuildNumber = Integer.parseInt( value );
        		}
        		catch( NumberFormatException w )
        		{
        			/* If we can't parse the integer, the number is zero.
        			 * This will either make the new run fail or rebuild it
        			 * id rebuildIfMissing is set(not set actually) */
        			bs.rebuildNumber = 0;
        		}
        	}
        }
        
        /* TODO this only registers the default values of the parameters, this should work now */
        /* Get the parameters of the build, if any and add them to the build */
        //ParametersDefinitionProperty paramDefprop = build.getProject().getProperty(ParametersDefinitionProperty.class);
        ParametersAction actions = build.getAction( ParametersAction.class );
        if( actions != null )
        {
        	List<ParameterValue> list = actions.getParameters();
        	for( ParameterValue pv : list )
        	{
        		//if( !pv.getName().startsWith( Definitions.prefix ) )
        		if( !pv.getName().equals( "uuid" ) )
        		{
        			System.out.println( "[WOLLE] adding " + pv.getName() );
        			values.add( pv );
        		}        		
        	}
        }
        
        /* Add the UUID to the new build. */
        values.add( new StringParameterValue( "uuid", uuid ) );
        
        /* Schedule the MatrixBuild */
        Hudson.getInstance().getQueue().schedule( 
        		build.getProject(), 0, new ParametersAction(values), new CauseAction(new Cause.UserCause())
        );
        
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

        JSONObject formData = req.getSubmittedForm();
        System.out.println( formData.toString( 2 ) );
		performConfig( build, formData );
        
        /* Depending on where the form was submitted, the number
         * of levels to ... */
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
