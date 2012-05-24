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

import hudson.Extension;
import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixBuild;
import hudson.model.*;
import hudson.model.listeners.RunListener;

/**
 * The onStarted sets the base build of the run.<br>
 * The onCompleted registers the {@link Action}s to the side panel of the matrix project.
 * 
 * @author wolfgang
 */
@Extension
public class MatrixReloadedListener extends RunListener<Run> {

    public MatrixReloadedListener() {
        super(Run.class);
    }

    /**
     * Determine if the run is matrix reloaded or its parent is. Set the base build accordingly.
     */
    @Override
    public void onStarted(Run run, TaskListener listener) {

    	/* Only if this is a matrix build and NOT the first run! */
        if (run instanceof MatrixBuild && run.number > 1) {
        	RebuildAction action = run.getAction( RebuildAction.class );

        	/* If the action is null, this must either be a propagated downstream build or not applicable */
            if (action == null) {
                /* Get the upstream action, if it's not null and the rebuild downstream is set, clone it to the previous build and continue  */
            	action = Util.getUpstreamRebuildAction( (AbstractBuild<?, ?>) run );
                if (action != null && action.doRebuildDownstream()) {
                	action = action.clone( run.number - 1 );
                	run.addAction( action );
                } else {
                    return;
                }
            }

            /* Set the base build for the build */
            MatrixBuild mb = (MatrixBuild) run;
            MatrixBuild base = mb.getProject().getBuildByNumber(action.getBaseBuildNumber());
            ((MatrixBuild) run).setBaseBuild(base);
        }
    }

    /**
     * Add the Matrix Reloaded link to the build context, this will enable matrix reload a previous build from the menu.<br>
     * This is done for all matrix builds/runs
     */
    @Override
    public void onCompleted(Run run, TaskListener listener) {
		/*
		 * Test for MatrixBuild and add to context
		 */
        if (run instanceof MatrixBuild) {
            AbstractBuild<?, ?> build = (AbstractBuild<?, ?>) run;

            MatrixReloadedAction action = new MatrixReloadedAction();
            build.getActions().add(action);
        }

        /*
         * Test for MatrixRun and add to context
         */
        if (run instanceof MatrixRun) {
            AbstractBuild<?, ?> build = (AbstractBuild<?, ?>) run;

            MatrixReloadedAction action = new MatrixReloadedAction(((MatrixRun) run).getParent().getCombination().toString());
            build.getActions().add(action);
        }
    }
}
