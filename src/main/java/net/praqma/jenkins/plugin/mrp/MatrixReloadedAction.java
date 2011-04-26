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
	
	public void doConfigSubmit( StaplerRequest req, StaplerResponse rsp ) throws ServletException, IOException, InterruptedException
	{
		AbstractBuild<?, ?> build = req.findAncestorObject(AbstractBuild.class);
		
		List<ParameterValue> values = new ArrayList<ParameterValue>();
		
        JSONObject formData = req.getSubmittedForm();
        Iterator<?> it = formData.keys();
        
        System.out.println( "[MRP] The MATRIX RELOADED FORM has been submitted" );
        
        /* UUID */
        String uuid = build.getProject().getDisplayName() + "_" + build.getNumber() + "_" + System.currentTimeMillis();
        BuildState bs = MatrixReloadedState.getInstance().getBuildState( uuid );
        
        /* Generate the parameters */
        while( it.hasNext() )
        {
        	String key = (String)it.next();
        	 
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
        
        /* Get the parameters, if any and  */
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
        
        
        
		rsp.sendRedirect( "../../" );
        
        /*
        JSONArray a = JSONArray.fromObject(formData.get("parameter"));
        
        for (Object o : a) {
            JSONObject jo = (JSONObject) o;
            String name = jo.getString("name");
            ParameterDefinition d = paramDefprop.getParameterDefinition(name);
            if (d == null) {
                throw new IllegalArgumentException("No such parameter definition: " + name);
            }
            ParameterValue parameterValue = d.createValue(req, jo);
            System.out.println( "[WOLLE] Parameter=" + name + "," + d.getName() );
        }
        */
	}

}
