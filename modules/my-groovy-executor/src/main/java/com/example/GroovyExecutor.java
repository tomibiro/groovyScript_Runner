package com.example;

import groovy.lang.GroovyShell;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component(
		immediate = true,
		service = GroovyExecutor.class
)
public class GroovyExecutor {

	private static final String SCRIPT_PATH = "c:\\git\\bundles\\7.4.3.125-ga125\\scripts\\myscript.groovy";

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
			GroovyShell shell = new GroovyShell();
			shell.evaluate(scriptContent);
			System.out.println("Groovy script finished.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
