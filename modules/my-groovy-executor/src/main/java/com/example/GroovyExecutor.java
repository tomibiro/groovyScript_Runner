package com.example;

import com.liferay.portal.kernel.exception.PortalException;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
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

	private static final String SCRIPT_PATH = "c:\\git\\bundles\\7.4.3.125-ga125\\scripts\\create_user.groovy";

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

			_executeGroovyScript(_createBinding(), scriptContent);

			System.out.println("Groovy script finished.");

		} catch (IOException | PortalException e) {
			e.printStackTrace();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void _executeGroovyScript(
			Binding binding, String script)
			throws Exception {

		GroovyShell groovyShell = new GroovyShell(binding);

		Script compiledScript = groovyShell.parse(script);

		//compiledScript.setBinding(new Binding(inputObjects));

		compiledScript.run();
	}

	private Binding _createBinding() throws PortalException {

		Binding binding = new Binding();

		binding.setVariable("user", com.liferay.portal.kernel.service.UserLocalServiceUtil.getUserById(20123));

		return binding;
	}
}
