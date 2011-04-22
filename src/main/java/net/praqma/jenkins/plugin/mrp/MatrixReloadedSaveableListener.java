package net.praqma.jenkins.plugin.mrp;

import java.io.IOException;

import hudson.Extension;
import hudson.XmlFile;
import hudson.model.Run;
import hudson.model.Saveable;
import hudson.model.TaskListener;
import hudson.model.listeners.SaveableListener;

@Extension
public class MatrixReloadedSaveableListener extends SaveableListener
{
	/*
	@Override
	public void onChange( Saveable o, XmlFile file )
	{
		System.out.println( "[WOLLE] XMLFILE=" + file.toString() );
		try
		{
			System.out.println( "[WOLLE] " + file.asString() );
		}
		catch( IOException e )
		{
			System.out.println( "[WOLLE] I could not get as string... " + e.getMessage() );
		}
	}
	*/
}
