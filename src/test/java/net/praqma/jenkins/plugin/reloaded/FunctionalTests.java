package net.praqma.jenkins.plugin.reloaded;

import hudson.matrix.Axis;
import hudson.matrix.AxisList;
import hudson.matrix.Combination;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.model.Result;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.Hudson;
import hudson.model.ParametersAction;
import hudson.tasks.BuildTrigger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.jvnet.hudson.test.HudsonTestCase;

import hudson.plugins.downstream_ext.DownstreamTrigger;
import hudson.plugins.downstream_ext.MatrixTrigger;

public class FunctionalTests extends HudsonTestCase {

	private AxisList axes = null;

	public MatrixProject initialize( String title ) throws IOException {
		axes = new AxisList( new Axis( "dim1", "1", "2" ), new Axis( "dim2", "a", "b" ) );

		MatrixProject mp = createMatrixProject( "matrix-reloaded-test-" + title );
		mp.setAxes( axes );

		return mp;
	}

	public void test() throws IOException, InterruptedException, ExecutionException {
		MatrixProject mp = initialize( "basic" );

		/* Run first build */
		MatrixBuild mb = mp.scheduleBuild2( 0, new Cause.UserCause() ).get();

		/* Reload it */
		/* Create form elements */
		Map<String, String[]> form = new HashMap<String, String[]>();

		/* Base build is 1 */
		form.put( "MRP::NUMBER", new String[] { "1" } );
		form.put( "MRP::dim1=1,dim2=b", new String[] { "0" } );
		form.put( "MRP::dim1=2,dim2=a", new String[] { "0" } );

		MatrixReloadedAction mra = new MatrixReloadedAction();
		RebuildAction raction = mra.getRebuildAction( form );

		MatrixBuild mb2 = mp.scheduleBuild2( 0, new Cause.UserCause(), raction ).get();

		RebuildAction raction2 = (RebuildAction) mb2.getAction( RebuildAction.class );

		/* Test base builds */
		assertEquals( 1, raction2.getBaseBuildNumber() );
		assertEquals( mb, mb2.getBaseBuild() );

		/* Not rebuild */
		Map<String, String> r1 = new HashMap<String, String>();
		r1.put( "dim1", "1" );
		r1.put( "dim2", "a" );
		Combination combination1a = new Combination( r1 );
		
		/* Rebuild */
		Map<String, String> r2 = new HashMap<String, String>();
		r2.put( "dim1", "1" );
		r2.put( "dim2", "b" );
		Combination combination1b = new Combination( r2 );

		assertEquals( 1, mb2.getRun( combination1a ).getNumber() );
		assertEquals( 2, mb2.getRun( combination1b ).getNumber() );
		
		System.out.println( "1A: " + mb2.getRun( combination1a ).getNumber() );
		System.out.println( "1B: " + mb2.getRun( combination1b ).getNumber() );
	}
	
	
//	public void testDownstream() throws IOException, InterruptedException, ExecutionException {
//		System.out.println( "1: " );
//		MatrixProject parentProject = initialize( "parent" );
//		System.out.println( "2: " );
//		MatrixProject childProject = initialize( "child" );
//
//		System.out.println( "Setting upstream: " );
//		List<AbstractProject> downstreams = new ArrayList<AbstractProject>();
//		downstreams.add( childProject );
//		//BuildTrigger bt = new BuildTrigger( downstreams, Result.SUCCESS );
//                BuildTrigger bt = new BuildTrigger( parentProject.getName(), true );
//		//parentProject.getBuildTriggerUpstreamProjects().add( childProject );
//		//childProject.getBuildTriggerUpstreamProjects().add( parentProject );
//		//childProject.addTrigger( bt );
//		
//                childProject.getPublishersList().add(bt);
//                
//                /*
//                DownstreamTrigger dt = new DownstreamTrigger(childProject.getName(), Result.SUCCESS, false, false, DownstreamTrigger.Strategy.AND_LOWER, MatrixTrigger.ONLY_PARENT);
//                parentProject.getPublishersList().add(dt);
//                System.out.println( "----> " + dt );
//                DownstreamTrigger dt2 = new DownstreamTrigger(parentProject.getName(), Result.SUCCESS, false, false, DownstreamTrigger.Strategy.AND_LOWER, MatrixTrigger.ONLY_PARENT);
//                childProject.getPublishersList().add(dt2);
//                System.out.println( "----> " + dt2 );
//                * */
//                
//                hudson.rebuildDependencyGraph();
//                
//		/* Run first builds */
//		System.out.println( "Running: " );
//		MatrixBuild parentBuild1 = parentProject.scheduleBuild2( 0, new Cause.UserCause() ).get();
//
//		/* Get child build 1 */
//		System.out.println( "DOWNSTREAM: " );
//		System.out.println( parentBuild1.getDownstreamBuilds() );
//		
//		
//		
////		/* Reload it */
////		/* Create form elements */
////		Map<String, String[]> form = new HashMap<String, String[]>();
////
////		/* Base build is 1 */
////		form.put( "MRP::NUMBER", new String[] { "1" } );
////		form.put( "MRP::dim1=1,dim2=b", new String[] { "0" } );
////		form.put( "MRP::dim1=2,dim2=a", new String[] { "0" } );
////
////		MatrixReloadedAction mra = new MatrixReloadedAction();
////		RebuildAction raction = mra.getRebuildAction( form );
////
////		MatrixBuild mb2 = parentProject.scheduleBuild2( 0, new Cause.UserCause(), raction ).get();
////
////		RebuildAction raction2 = (RebuildAction) mb2.getAction( RebuildAction.class );
////
////		/* Test base builds */
////		assertEquals( 1, raction2.getBaseBuildNumber() );
////		assertEquals( parentBuild1, mb2.getBaseBuild() );
////
////		/* Not rebuild */
////		Map<String, String> r1 = new HashMap<String, String>();
////		r1.put( "dim1", "1" );
////		r1.put( "dim2", "a" );
////		Combination combination1a = new Combination( r1 );
////		
////		/* Rebuild */
////		Map<String, String> r2 = new HashMap<String, String>();
////		r2.put( "dim1", "1" );
////		r2.put( "dim2", "b" );
////		Combination combination1b = new Combination( r2 );
////
////		assertEquals( 1, mb2.getRun( combination1a ).getNumber() );
////		assertEquals( 2, mb2.getRun( combination1b ).getNumber() );
////		
////		System.out.println( "1A: " + mb2.getRun( combination1a ).getNumber() );
////		System.out.println( "1B: " + mb2.getRun( combination1b ).getNumber() );
//	}

}
