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
import java.io.PrintStream;
import java.net.URLDecoder;
import net.sf.okapi.applications.rainbow.Input;
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
import util.Log;
import util.LogOutputStream;

public class CommandLine2 {

	private String appRootFolder;
	private LanguageManager lm;
	private Project prj;
	private FilterConfigurationMapper fcMapper;
	private PluginsManager pm;
	private ExecutionContext context;

	public void execute(String sharedFolder, String pipelineFile, String inputFile, boolean export) throws RainbowError {
		try {
			PrintStream ps = new PrintStream(new LogOutputStream("RAINBOW: "));
			System.setOut(ps);
			System.setErr(ps);
			initialize(sharedFolder);
			parseArguments(inputFile, export);
			launchPipeline(pipelineFile);
		}
		catch (Exception e) {
			Log.err(e);
			throw new RainbowError(e.getMessage());
		}
	}

	private void parseArguments(String inputFile, boolean export) throws OkapiException {
		// Creates default project
		FormatManager fm = new FormatManager();
		fm.load(null); // TODO: implement real external file, for now it's hard-coded
		prj = new Project(lm);
		prj.setInputRoot(0, appRootFolder, true);
		prj.setInputRoot(1, appRootFolder, true);
		prj.setInputRoot(2, appRootFolder, true);

		File f = new File(inputFile);
		String[] res = fm.guessFormat(f.getAbsolutePath());
		prj.getList(0).clear();
		prj.setInputRoot(0, Util.getDirectoryName(f.getAbsolutePath()), true);
		prj.addDocument(0, f.getAbsolutePath(), res[0], null, res[1], false);

		if (export) {
			Input inp = prj.getLastItem(0);
			inp.filterConfigId = "okf_rainbowkit-noprompt"; // avoid creating ManifestDialog in RainbowKitFilter
		}
	}

	private void initialize(String sharedFolder) throws Exception {
		// Get the location of the main class source
		File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
		appRootFolder = URLDecoder.decode(file.getAbsolutePath(), "utf-8");
		boolean fromJar = appRootFolder.endsWith(".jar");
		// Remove the JAR file if running an installed version
		if (fromJar) {
			appRootFolder = Util.getDirectoryName(appRootFolder);
		}    	// Remove the application folder in all cases
		appRootFolder = Util.getDirectoryName(appRootFolder);

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

		UtilitiesAccess utilitiesAccess = new UtilitiesAccess();
		new UtilitiesAccess().loadMenu(sharedFolder + File.separator + "rainbowUtilities.xml");

		context = new ExecutionContext();
		context.setApplicationName("Rainbow");
	}

	private void launchPipeline(String pipelineFile) {
		// Save any pending data
		fcMapper.setCustomConfigurationsDirectory(prj.getParametersFolder());
		fcMapper.updateCustomConfigurations();

		PipelineWrapper wrapper = new PipelineWrapper(fcMapper, appRootFolder, pm,
				prj.getProjectFolder(), prj.getInputRoot(0), null, null, context);

		wrapper.load(pipelineFile);
		wrapper.execute(prj);
	}
}
