package com.example;

import com.liferay.portal.kernel.util.AggregateClassLoader;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

@Component(
		immediate = true,
		service = GroovyExecutor.class
)
public class GroovyExecutor {

	//this searches the path inside the Liferay container. Modify this path if you want to populate script file locally
	private static final String SCRIPT_PATH = "/mnt/liferay/scripts/";

	@Activate
	public void scriptLoader() throws IOException {
		System.out.println("GroovyExecutor OSGi module started");

		Path scriptPath = Paths.get(SCRIPT_PATH);

		if (Files.list(scriptPath).findAny().isEmpty()) {
			System.out.println("No groovy script found at location: " + SCRIPT_PATH);
			return;
		}

		try {
			Files.list(scriptPath)
					.filter(Files::isRegularFile)
					.filter(path -> path.toString().endsWith(".groovy"))
					.forEach(path -> {
						try {
							String content = _readScript(path);
							_executeScript(_createBundleObjects(), content);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


	private String _readScript(Path scriptPath) throws IOException {
		return new String(Files.readAllBytes(scriptPath));
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

	private Map<String, Object> _createBundleObjects (){
		Map<String, Object> bundleObjects = Collections.singletonMap("variable", "string");

		return bundleObjects;
	}
}
