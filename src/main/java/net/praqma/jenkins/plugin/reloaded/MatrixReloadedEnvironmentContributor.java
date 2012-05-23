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

import java.io.IOException;

import hudson.EnvVars;
import hudson.Extension;
import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixBuild;
import hudson.model.EnvironmentContributor;
import hudson.model.TaskListener;
import hudson.model.Run;

@Extension
public class MatrixReloadedEnvironmentContributor extends EnvironmentContributor {
    public void buildEnvironmentFor(Run r, EnvVars envs, TaskListener listener) throws IOException,
            InterruptedException {

    	if( r instanceof MatrixRun ) {
    		System.out.println( "MATRIX RUN!" );
    		MatrixBuild mb = ((MatrixRun)r).getParentBuild();
	    	System.out.println( mb.getActions() );
	    	Util.addActionToRun( mb );
	    	RebuildAction action = mb.getAction( RebuildAction.class );
	    	System.out.println( "Trying to put for " + action );
	        if( action == null ) {
	        	return;
	        }
	
	        if (action.getBaseBuildNumber() > 0) {
	        	System.out.println( "PUTTING " + action.getBaseBuildNumber() );
	            envs.put(Definitions.__REBUILD_VAR_NAME,action.getBaseBuildNumber() + "");
	        }
    	}
    }
}
