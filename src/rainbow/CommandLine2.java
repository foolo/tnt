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
package rainbow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import net.sf.okapi.applications.rainbow.BatchLog;
import net.sf.okapi.applications.rainbow.Project;
import net.sf.okapi.applications.rainbow.UtilitiesAccess;

import net.sf.okapi.applications.rainbow.lib.FormatManager;
import net.sf.okapi.applications.rainbow.lib.LanguageManager;
import net.sf.okapi.applications.rainbow.pipeline.PipelineWrapper;
import net.sf.okapi.common.ExecutionContext;
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
	private FilterConfigurationMapper fcMapper;
	private UtilitiesAccess utilitiesAccess;
	private BatchLog log;
	public String pipelineFile;
	private PluginsManager pm;
	private PrintStream ps = null;
	private ExecutionContext context;
	public File logFile;
	public ArrayList<String> inputFiles;

	public int execute() {
		try {
			ps = new PrintStream(new FileOutputStream(logFile));
			System.setOut(ps);
			System.setErr(ps);

			initialize();
			if (!parseArguments()) {
				return 1;
			}

			launchPipeline();
		}
		catch (Throwable e) {
			e.printStackTrace();
			return 1;
		}
		finally {
			if (ps != null) {
				ps.close();
			}
		}
		if ((log != null) && (log.getErrorCount() > 0)) {
			return 1;
		}
		else {
			return 0;
		}
	}

	private boolean parseArguments() throws Exception {
		// Creates default project
		FormatManager fm = new FormatManager();
		fm.load(null); // TODO: implement real external file, for now it's hard-coded
		prj = new Project(lm);
		prj.setInputRoot(0, appRootFolder, true);
		prj.setInputRoot(1, appRootFolder, true);
		prj.setInputRoot(2, appRootFolder, true);

		if (inputFiles.size() > 3) {
			throw new OkapiException("Too many input files.");
		}

		for (int i = 0; i < inputFiles.size(); i++) {
			File f = new File(inputFiles.get(i));
			String[] res = fm.guessFormat(f.getAbsolutePath());
			prj.getList(i).clear();
			prj.setInputRoot(i, Util.getDirectoryName(f.getAbsolutePath()), true);
			prj.addDocument(i, f.getAbsolutePath(), res[0], null, res[1], false);
		}
		return true;
	}

	private void initialize() throws Exception {
		// Get the location of the main class source
		File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
		appRootFolder = URLDecoder.decode(file.getAbsolutePath(), "utf-8");
		boolean fromJar = appRootFolder.endsWith(".jar");
		// Remove the JAR file if running an installed version
		if (fromJar) {
			appRootFolder = Util.getDirectoryName(appRootFolder);
		}    	// Remove the application folder in all cases
		appRootFolder = Util.getDirectoryName(appRootFolder);

		log = new BatchLog();
		lm = new LanguageManager();
		lm.loadList(sharedFolder + File.separator + "languages.xml");

		// Set up the filter configuration mapper
		fcMapper = new FilterConfigurationMapper();
		// Get pre-defined configurations
		DefaultFilters.setMappings(fcMapper, false, true);
		// Discover and add plug-ins
		pm = new PluginsManager();
		pm.discover(new File(appRootFolder + File.separator + "dropins"), true);
		fcMapper.addFromPlugins(pm);

		utilitiesAccess = new UtilitiesAccess();
		utilitiesAccess.loadMenu(sharedFolder + File.separator + "rainbowUtilities.xml");

		context = new ExecutionContext();
		context.setApplicationName("Rainbow");
	}

	private void launchPipeline() {
		// Save any pending data
		fcMapper.setCustomConfigurationsDirectory(prj.getParametersFolder());
		fcMapper.updateCustomConfigurations();

		PipelineWrapper wrapper = new PipelineWrapper(fcMapper, appRootFolder, pm,
				prj.getProjectFolder(), prj.getInputRoot(0), null, null, context);

		wrapper.load(pipelineFile);
		wrapper.execute(prj);
	}
}
