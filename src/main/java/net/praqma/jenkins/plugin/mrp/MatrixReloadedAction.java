package net.praqma.jenkins.plugin.mrp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import net.sf.json.JSONArray;
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
	
    public AbstractBuild<?, ?> getBuild() {
        return build;
    }
    
    public String getString()
    {
    	return "MY OWN STRING";
    }
	
	public void doConfigSubmit( StaplerRequest req, StaplerResponse rsp ) throws ServletException, IOException, InterruptedException
	{
		AbstractBuild<?, ?> build = req.findAncestorObject(AbstractBuild.class);
		
		List<ParameterValue> values = new ArrayList<ParameterValue>();
		
        JSONObject formData = req.getSubmittedForm();
        Iterator<?> it = formData.keys();
        
        /* Generate the parameters */
        while( it.hasNext() )
        {
        	String key = (String)it.next();
        	System.out.println( "[WOLLE] key=" + key ); 
        	/* Check the field */
        	if( key.startsWith( "mrp::" ) )
        	{
        		String[] vs = key.split( "::", 2 );
        		
        		String value = formData.getString( key );
        		boolean checked = formData.getBoolean( key );
        		
        		System.out.println( "[WOLLE] key=" + key + ", value=" + value );
        		
        		boolean reuse = false;
        		
        		/**/
        		if( vs.length > 1 && checked )
        		{
        			System.out.println( "[WOLLE] YAY" );
        			reuse = true;
        		}
        		
        		/* Create the parameter */
        		if( vs.length > 1 )
        		{
        			System.out.println( "[WOLLE] creating parameter" );
        			values.add( new BooleanParameterValue( key, reuse ) );
        		}
        	}
        	
        	if( key.equals( "MRP::NUMBER" ) )
        	{
        		String value = formData.getString( key );
        		values.add( new StringParameterValue( "MRP_NUMBER", value ) );
        	}
        }
        
        //System.out.println( "[WOLLE] OWNER=" + paramDefprop.getOwner().getDisplayName() );
        System.out.println( "[WOLLE] OWNER=" + build.getProject().getDisplayName() );
        System.out.println( "[WOLLE] SIZE OF: " + values.size() );
        
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
