/*
 *  The MIT License
 *
 *  Copyright 2011 Praqma A/S.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package net.praqma.jenkins.plugin.reloaded;

import hudson.matrix.MatrixBuild;
import hudson.model.ParameterValue;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Cause.UpstreamCause;
import hudson.model.Project;
import hudson.model.Run;

import java.util.List;

public abstract class Util {

	
    /**
     * Convenience method for retrieving {@link ParameterValue}s.
     * 
     * @param pvs A list of {@link ParameterValue}s.
     * @param key The key of the {@link ParameterValue}.
     * @return The parameter or null
     */
    public static ParameterValue getParameterValue(List<ParameterValue> pvs, String key) {
        for (ParameterValue pv : pvs) {
            if (pv.getName().equals(key)) {
                return pv;
            }
        }

        return null;
    }
    
    public static RebuildAction getDownstreamRebuildActionFromMatrixBuild( MatrixBuild mbuild ) {
    	RebuildAction action = mbuild.getAction( RebuildAction.class );

    	/* If the action is null, this must either be a propagated downstream build or not applicable */
        if (action == null) {
            /* Get the upstream action, if it's not null and the rebuild downstream is set, clone it to the current build and continue  */
        	action = Util.getUpstreamRebuildAction( (AbstractBuild<?, ?>) mbuild );
            if (action != null && action.doRebuildDownstream()) {
            	return action;
            } else {
                return null;
            }
        } else {
        	return action;
        }
    }
    
    public static RebuildAction getUpstreamRebuildAction( AbstractBuild<?, ?> build ) {
    	UpstreamCause cause = (UpstreamCause) build.getCause( UpstreamCause.class );
    	if( cause != null ) {
	    	AbstractProject<?, ?> project = build.getProject();
	    	
	    	List<AbstractProject> projects = project.getUpstreamProjects();
	    	
	    	for( AbstractProject<?, ?> p : projects ) {
	    		if( cause.getUpstreamProject().equals( p.getDisplayName() ) ) {
	    			AbstractBuild<?, ?> origin = p.getBuildByNumber( cause.getUpstreamBuild() );
	    			return origin.getAction( RebuildAction.class );
	    		}
	    	}
    	}
    	
    	return null;
    }
}
