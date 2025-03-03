package com.example;

import com.liferay.portal.kernel.util.AggregateClassLoader;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

@Component(
		immediate = true,
		service = GroovyExecutor.class
)
public class GroovyExecutor {

	//this searches the path inside the Liferay container. Modify this path if you want to populate script file locally
	private static final String SCRIPT_PATH = "/mnt/liferay/scripts/myscript.groovy";

	@Activate
	public void activate() {
		System.out.println("GroovyExecutor OSGi module started");


		File scriptFile = new File(SCRIPT_PATH);
		if (!scriptFile.exists()) {
			System.out.println("No groovy script found at location: " + SCRIPT_PATH);
			return;
		}

		try {
			String scriptContent = new String(Files.readAllBytes(Paths.get(SCRIPT_PATH)));
			_executeScript(_createBundleObjects(), scriptContent);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Map<String, Object> _createBundleObjects (){
		Map<String, Object> bundleObjects = Collections.singletonMap("variable", "string");

		return bundleObjects;
	}

	private void _executeScript(Map<String, Object> inputObjects, String script) {
		Class<?> clazz = GroovyExecutor.class;

		Thread currentThread = Thread.currentThread();

		GroovyShell groovyShell = new GroovyShell(
				AggregateClassLoader.getAggregateClassLoader(
						clazz.getClassLoader(), currentThread.getContextClassLoader(),
						GroovyShell.class.getClassLoader())
		);

		Script compiledScript = groovyShell.parse(script);
		compiledScript.setBinding(new Binding(inputObjects));
		compiledScript.run();

		System.out.println("Groovy script finished.");
	}
}
