package net.praqma.jenkins.plugin.mrp;

import java.io.IOException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.Action;

public class MatrixReloadedAction implements Action
{

	public String getDisplayName()
	{
		return "Matrix Reloaded";
	}

	public String getIconFileName()
	{
		return "";
	}

	public String getUrlName()
	{
		return "matrix-reloaded";
	}
	
	public void doConfigSubmit( StaplerRequest req, StaplerResponse rsp ) throws ServletException, IOException, InterruptedException
	{
		
	}

}
