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

import java.util.logging.Logger;

import net.praqma.jenkins.plugin.reloaded.MatrixReloadedState.BuildState;

import hudson.Extension;
import hudson.matrix.MatrixConfiguration;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.matrix.listeners.MatrixBuildListener;
import hudson.model.AbstractProject;
import java.util.List;

@Extension
public class MatrixReloadedBuildListener extends MatrixBuildListener {

    private static Logger logger = Logger.getLogger(MatrixReloadedBuildListener.class.getName());

    public boolean doBuildConfiguration(MatrixBuild b, MatrixConfiguration c) {
        BuildState bs = Util.getBuildStateFromRun(b);
        if (bs == null) {
            //Jenkins 13514
            List<AbstractProject> ps = b.getProject().getUpstreamProjects();
            for (AbstractProject p : ps) {
                if (p instanceof MatrixProject) {
                    BuildState state = Util.getBuildStateFromRun(p.getLastBuild());
                    if (state != null && state.downstreamConfig) {
                        b.getActions().addAll(p.getActions());
                        bs = Util.getBuildStateFromRun(p.getLastBuild());
                    }
                }
            }

            if (bs == null) {

                logger.severe("I didn't get");
                return true;
            }
        }

        logger.severe("I got " + bs);

        return bs.getConfiguration(c.getCombination());
    }
}
