/*===========================================================================
  Copyright (C) 2008-2013 by the Okapi Framework contributors
-----------------------------------------------------------------------------
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
===========================================================================*/

package net.sf.okapi.applications.rainbow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URLDecoder;

import net.sf.okapi.applications.rainbow.lib.FormatManager;
import net.sf.okapi.applications.rainbow.lib.LanguageManager;
import net.sf.okapi.applications.rainbow.pipeline.PipelineWrapper;
import net.sf.okapi.common.ExecutionContext;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.exceptions.OkapiException;
import net.sf.okapi.common.filters.DefaultFilters;
import net.sf.okapi.common.filters.FilterConfigurationMapper;
import net.sf.okapi.common.plugins.PluginsManager;

public class CommandLine2 {

	private String appRootFolder;
	public String sharedFolder;
	private LanguageManager lm;
	private Project prj;
	private UtilityDriver ud;
	private FilterConfigurationMapper fcMapper;
	private UtilitiesAccess utilitiesAccess;
	private BatchLog log;
	private String pipelineFile;
	private PluginsManager pm;
	private PrintStream ps = null;
	private ExecutionContext context;
	private File logFile;
	
	public int execute (String[] args)
	{
		try {
			if( !setLogFile(args) ) {
				logFile  = new File(System.getProperty("user.home")+"/rainbowBatchLog.txt");	
			}			  
			ps = new PrintStream(new FileOutputStream(logFile));
			System.setOut(ps);
			System.setErr(ps);
			
			printBanner();
			initialize();
			if ( !parseArguments(args) ) {
				return 1;
			}
			
			launchPipeline();
		}
		catch ( Throwable e ) {
			e.printStackTrace();
			return 1;
		}
		finally {
			if ( ps != null ) ps.close();
		}
		if (( log != null ) && ( log.getErrorCount() > 0 )) {
			return 1;
		}
		else {
			return 0;
		}
	}
	
	/**
	 * Parse the command line and check for the -log argument
	 * @return True if found and False if not found.
	 */
	private boolean setLogFile (String[] args) {
		String arg;
		for ( int i=0; i<args.length; i++ ) {
			arg = args[i];
			if ( "-log".equals(arg) ) { // log file
				logFile  = new File(nextArg(args, ++i));
				return true;
			}			
		}
		return false;
	}
	
	/**
	 * Parse the command line.
	 * @return True to execute something, false if error or exit immediately.
	 * @throws Exception 
	 */
	private boolean parseArguments (String[] args) throws Exception {
		String arg;
		boolean continueAfter = false;
		
		// Creates default project
		FormatManager fm = new FormatManager();
		fm.load(null); // TODO: implement real external file, for now it's hard-coded
		prj = new Project(lm);
		prj.setInputRoot(0, appRootFolder, true);
		prj.setInputRoot(1, appRootFolder, true);
		prj.setInputRoot(2, appRootFolder, true);
		boolean setOutSearch = false;
		int inpList = -1;
		pipelineFile = null;
		
		for ( int i=0; i<args.length; i++ ) {
			arg = args[i];
			if ( "-p".equals(arg) ) { // Load a project //$NON-NLS-1$
				prj.load(nextArg(args, ++i));
			}
			else if ( "-log".equals(arg) ) { // Log file
				//--just to prevent invalid argument--
				++i; // Make sure we eat the log file path too
			}
			else if ( "-se".equals(arg) ) { // Source encoding //$NON-NLS-1$
				prj.setSourceEncoding(nextArg(args, ++i));
			}
			else if ( "-te".equals(arg) ) { // Target encoding //$NON-NLS-1$
				prj.setTargetEncoding(nextArg(args, ++i));
			}
			else if ( "-sl".equals(arg) ) { // Source language //$NON-NLS-1$
				prj.setSourceLanguage(LocaleId.fromString(nextArg(args, ++i)));
			}
			else if ( "-tl".equals(arg) ) { // Target language //$NON-NLS-1$
				prj.setTargetLanguage(LocaleId.fromString(nextArg(args, ++i)));
			}
			else if (( "-ir".equals(arg) ) || ( "-ir0".equals(arg) )) { // Input root list 0 //$NON-NLS-1$ //$NON-NLS-2$
				prj.setInputRoot(0, nextArg(args, ++i), true);
			}
			else if ( "-pd".equals(arg) ) { //$NON-NLS-1$
				prj.setCustomParametersFolder(nextArg(args, ++i));
				prj.setUseCustomParametersFolder(true);
			}
			else if ( "-rd".equals(arg) ) { //$NON-NLS-1$
				// Use the project file name to set the root directory
				prj.setPath(Util.ensureSeparator(nextArg(args, ++i), false)+"project.rnb");
			}
			else if ( "-pln".equals(arg) ) {
				pipelineFile = nextArg(args, ++i);
				continueAfter = true;
			}
			else if ( "-fc".equals(arg) ) { //$NON-NLS-1$
				Input inp = prj.getLastItem(inpList);
				if ( inp == null ) { 
					throw new OkapiException(Res.getString("CommandLine.fsBeforeInputError")); //$NON-NLS-1$
				}
				else {
					inp.filterConfigId = nextArg(args, ++i);
				}
			}
			else if ( "-o".equals(arg) ) { // Output file //$NON-NLS-1$
				File f = new File(nextArg(args, ++i));
				prj.setOutputRoot(Util.getDirectoryName(f.getAbsolutePath()));
				prj.setUseOutputRoot(true);
				prj.pathBuilder.setUseExtension(false);
				prj.pathBuilder.setUseReplace(true);
				prj.pathBuilder.setReplace(Util.getFilename(f.getAbsolutePath(), true));
				setOutSearch = true;
			}
			else if ( !arg.startsWith("-") ) { // Input file //$NON-NLS-1$
				if ( ++inpList > 2 ) {
					throw new OkapiException(Res.getString("CommandLine.tooManyInput")); //$NON-NLS-1$
				}
				File f = new File(arg);
				String[] res = fm.guessFormat(f.getAbsolutePath());
				prj.inputLists.get(inpList).clear();
				prj.setInputRoot(inpList, Util.getDirectoryName(f.getAbsolutePath()), true);
				prj.addDocument(inpList, f.getAbsolutePath(), res[0], null, res[1], false);
			}
			else {
				log.error(Res.getString("CommandLine.invalidCommand")+args[i]); //$NON-NLS-1$
				continueAfter = false;
			}
			
			// Sets the search part of the output builder if an output path was specified.
			if ( setOutSearch ) {
				Input inp = prj.getLastItem(0);
				if ( inp == null ) { 
					throw new OkapiException(Res.getString("CommandLine.noInput")); //$NON-NLS-1$
				}
				else {
					prj.pathBuilder.setSearch(inp.relativePath);
				}
			}
		}
		return continueAfter;
	}
	
	private String nextArg (String[] args, int index) {
		if ( index >= args.length ) {
			throw new OkapiException(Res.getString("CommandLine.missingParameter")); //$NON-NLS-1$
		}
		return args[index];
	}
	
	private void printBanner () {
		System.out.println("-------------------------------------------------------------------------------"); //$NON-NLS-1$
		System.out.println(Res.getString("CommandLine.bannerApplication")); //$NON-NLS-1$
		System.out.println(Res.getString("CommandLine.bannerVersion")+getClass().getPackage().getImplementationVersion()); //$NON-NLS-1$
		System.out.println("-------------------------------------------------------------------------------"); //$NON-NLS-1$
	}
	
	private void initialize () throws Exception {
    	// Get the location of the main class source
    	File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
    	appRootFolder = URLDecoder.decode(file.getAbsolutePath(),"utf-8"); //$NON-NLS-1$
    	boolean fromJar = appRootFolder.endsWith(".jar");
    	// Remove the JAR file if running an installed version
    	if ( fromJar ) appRootFolder = Util.getDirectoryName(appRootFolder); //$NON-NLS-1$
    	// Remove the application folder in all cases
    	appRootFolder = Util.getDirectoryName(appRootFolder);

		log = new BatchLog();
		lm = new LanguageManager();
		lm.loadList(sharedFolder + File.separator + "languages.xml"); //$NON-NLS-1$
		
		// Set up the filter configuration mapper
		fcMapper = new FilterConfigurationMapper();
		// Get pre-defined configurations
		DefaultFilters.setMappings(fcMapper, false, true);
		// Discover and add plug-ins
		pm = new PluginsManager();
		pm.discover(new File(appRootFolder+File.separator+"dropins"), true);
		fcMapper.addFromPlugins(pm);

		utilitiesAccess = new UtilitiesAccess();
		utilitiesAccess.loadMenu(sharedFolder+File.separator+"rainbowUtilities.xml");
		
		context = new ExecutionContext();
		context.setApplicationName("Rainbow");
	}
	
	private void launchPipeline () {
		// Save any pending data
		fcMapper.setCustomConfigurationsDirectory(prj.getParametersFolder());
		fcMapper.updateCustomConfigurations();
		
		PipelineWrapper wrapper = new PipelineWrapper(fcMapper, appRootFolder, pm,
			prj.getProjectFolder(), prj.getInputRoot(0), null, null, context);

		wrapper.load(pipelineFile);
		wrapper.execute(prj);
	}
}
