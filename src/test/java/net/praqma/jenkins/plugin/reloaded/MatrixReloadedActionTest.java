
package net.praqma.jenkins.plugin.reloaded;

import hudson.matrix.Axis;
import hudson.matrix.AxisList;
import hudson.matrix.Combination;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.model.AbstractBuild;
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

import net.praqma.jenkins.plugin.reloaded.MatrixReloadedAction;
import net.praqma.jenkins.plugin.reloaded.MatrixReloadedAction.BuildType;
import net.sf.json.JSONObject;

import org.jvnet.hudson.test.HudsonTestCase;

public class MatrixReloadedActionTest extends HudsonTestCase {
    public void testGetDisplayName() {
        MatrixReloadedAction mra = new MatrixReloadedAction();

        assertEquals(mra.getDisplayName(), Definitions.__DISPLAY_NAME);
    }

    public void testGetIconFileName() {
        MatrixReloadedAction mra = new MatrixReloadedAction();

        assertEquals(mra.getIconFileName(), Definitions.__ICON_FILE_NAME);
    }

    public void testGetUrlName() {
        MatrixReloadedAction mra = new MatrixReloadedAction();

        assertEquals(mra.getUrlName(), Definitions.__URL_NAME);
    }

    // public void testGetBuild() throws IOException, InterruptedException,
    // ExecutionException
    // {
    // /* Create a previous build */
    // init();
    //
    // MatrixProject mp = createMatrixProject( "test" );
    // mp.setAxes( axes );
    //
    // MatrixBuild mb = mp.scheduleBuild2( 0 ).get();
    //
    // MatrixReloadedAction action = mb.getAction( MatrixReloadedAction.class );
    // assertNotNull( action );
    // assertTrue( action.getBuild() instanceof AbstractBuild<?, ?> );
    // }

    public void testPrefix() {
        MatrixReloadedAction mra = new MatrixReloadedAction();

        assertEquals(mra.getPrefix(), Definitions.__PREFIX);
    }

    public void testGetChecked1() {
        MatrixReloadedAction mra = new MatrixReloadedAction();

        assertNull(mra.getChecked());
    }

    public void testGetChecked2() {
        MatrixReloadedAction mra = new MatrixReloadedAction("test");

        assertNotNull(mra.getChecked());
        assertEquals(mra.getChecked(), "test");
    }

    public void testBuildType() {
        BuildType bt = BuildType.MATRIXBUILD;
    }

    private AxisList axes = null;

    private Combination c = null;

    public void init() {

        axes = new AxisList(new Axis("dim1", "1", "2"), new Axis("dim2", "a", "b"));

        Map<String, String> r = new HashMap<String, String>();
        r.put("dim1", "1");
        r.put("dim2", "a");
        c = new Combination(r);
    }

    public void testForm() throws IOException, InterruptedException, ExecutionException {
        /* Create a previous build */
        init();

        MatrixProject mp = createMatrixProject("test");
        mp.setAxes(axes);
        List<ParameterDefinition> list = new ArrayList<ParameterDefinition>();
        list.add(new StringParameterDefinition("key", "value"));
        ParametersDefinitionProperty pdp = new ParametersDefinitionProperty(list);
        mp.addProperty(pdp);

        /*
         * Create some parameters to test continuation of parameters from reused
         * to new build
         */
        List<ParameterValue> values = new ArrayList<ParameterValue>();
        values.add(new StringParameterValue("key", "value"));

        MatrixBuild mb = mp.scheduleBuild2(0, new Cause.UserCause(), new ParametersAction(values))
                .get();

        /* Create form elements */
        JSONObject form = new JSONObject();

        form.element("MRP::NUMBER", 1);

        form.element("MRP::dim1=1,dim2=a", false);
        form.element("MRP::dim1=1,dim2=b", true);
        form.element("MRP::dim1=2,dim2=a", true);
        form.element("MRP::dim1=2,dim2=b", false);
        form.element("MRP::", false);

        MatrixReloadedAction mra = new MatrixReloadedAction();
        mra.performConfig(mb, form);
    }

    public void testFormNoParms() throws IOException, InterruptedException, ExecutionException {
        /* Create a previous build */
        init();

        MatrixProject mp = createMatrixProject("test");
        mp.setAxes(axes);

        MatrixBuild mb = mp.scheduleBuild2(0).get();

        /* Create form elements */
        JSONObject form = new JSONObject();

        form.element("MRP::NUMBER", 1);

        form.element("MRP::dim1=1,dim2=a", false);
        form.element("MRP::dim1=1,dim2=b", true);
        form.element("MRP::dim1=2,dim2=a", true);
        form.element("MRP::dim1=2,dim2=b", false);

        MatrixReloadedAction mra = new MatrixReloadedAction();
        mra.performConfig(mb, form);
    }

    public void testFormFalseParms() throws IOException, InterruptedException, ExecutionException {
        /* Create a previous build */
        init();

        MatrixProject mp = createMatrixProject("test");
        mp.setAxes(axes);

        MatrixBuild mb = mp.scheduleBuild2(0).get();

        /* Create form elements */
        JSONObject form = new JSONObject();

        form.element("MRP::NUMBER", 1);

        form.element("MRPFALSE1", false);
        form.element("MRPFALSE2", true);

        MatrixReloadedAction mra = new MatrixReloadedAction();
        mra.performConfig(mb, form);
    }

    public void testFormFalseNumberParm() throws IOException, InterruptedException,
            ExecutionException {
        /* Create a previous build */
        init();

        MatrixProject mp = createMatrixProject("test");
        mp.setAxes(axes);

        MatrixBuild mb = mp.scheduleBuild2(0).get();

        /* Create form elements */
        JSONObject form = new JSONObject();

        form.element("MRP::NUMBER", "fail");

        MatrixReloadedAction mra = new MatrixReloadedAction();
        mra.performConfig(mb, form);
    }

    public void testEnv() throws IOException, InterruptedException, ExecutionException {
        /* Create a previous build */
        init();

        MatrixProject mp = createMatrixProject("test");
        mp.setAxes(axes);

        MatrixBuild mb = mp.scheduleBuild2(0).get();

        MatrixReloadedEnvironmentContributor mrec = new MatrixReloadedEnvironmentContributor();
        mrec.buildEnvironmentFor(mb, mb.getEnvironment(null), null);
    }

}
