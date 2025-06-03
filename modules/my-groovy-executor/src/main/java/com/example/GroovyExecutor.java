package com.example;

import com.liferay.portal.kernel.deploy.auto.AutoDeployDir;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

@Component(
		immediate = true,
		service = GroovyExecutor.class
)
public class GroovyExecutor extends Thread {
	//this searches the path inside the Liferay container. Modify this path if you want to populate script file locally
	private static final String SCRIPT_PATH = "/mnt/liferay/scripts/";
	private boolean _schedulerStarted;
	private long _scheduledScanTime = 3000;
	private static final Log _log = LogFactoryUtil.getLog(GroovyExecutor.class);

	@Activate
	public void scriptLoader() throws IOException {
		_log.info("GroovyExecutor OSGi module is started");
		_schedulerStarted = true;
		_log.info("Scheduler for scripts is started");
		this.start();
	}

	@Deactivate
	public void deactivate() {
		_log.info("Deactivation of the module is done");
		_stopThread();
	}

	private String _readScript(File file) throws IOException {
		_log.info("Reading the script: " + file.getName());
		return Files.readString(file.toPath(), StandardCharsets.UTF_8);
	}

	private void _executeScript(Map<String, Object> inputObjects, String script) {
		Class<?> clazz = GroovyExecutor.class;

		Thread newThread = new Thread(this);
		_log.info("Start the groovy shell");
		GroovyShell groovyShell;
		try {
			groovyShell = new GroovyShell(
					AggregateClassLoader.getAggregateClassLoader(
							clazz.getClassLoader(), newThread.getContextClassLoader(),
							GroovyShell.class.getClassLoader())
			);
		} catch (Exception exception){
			exception.printStackTrace();
			_log.error("Groovy shell threw an exception");
			return;
		} finally {
			newThread.interrupt();
		}

		Script compiledScript = groovyShell.parse(script);
		compiledScript.setBinding(new Binding(inputObjects));
		_log.info("Run the script: " + script);
		compiledScript.run();
		_log.info("Script finished: " + script);
	}

	private boolean _deleteFile(File file) {
		_log.info("Script file has deleted: " + file.getName());
		return file.delete();
	}

	private Map<String, Object> _createBundleObjects() {
		Map<String, Object> bundleObjects = Collections.singletonMap("variable", "string");
		return bundleObjects;
	}

	private void _scanDirectory() throws IOException {
		File[] files = Paths.get(SCRIPT_PATH).toFile().listFiles();

		if (files == null || files.length == 0) {
			_log.info("No files in watched directory");
			return;
		}

		for (File file : files) {
			String fileName = file.getName();
			_log.info("File found: " + file.getName());

			if (file.isFile() && fileName.endsWith("groovy")) {
				String script = _readScript(file);
				_log.info("Executing groovy script: " + file.getName());
				_executeScript(_createBundleObjects(), script);
				_deleteFile(file);
			}
			else {
				_log.info("It is not a groovy file: " + file.getName());
			}
		}
	}

	@Override
	public void run() {
		while (_schedulerStarted) {
			try {
				_scanDirectory();
				_log.info("Wait for :"+_scheduledScanTime);
				Thread.sleep(_scheduledScanTime);
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
				_log.error("Groovy script execution interrupted. Stopping the thread");
				_stopThread();
			}
		}
	}

	private void _stopThread(){
		_schedulerStarted = false;
		Thread.currentThread().interrupt();
	}
}

