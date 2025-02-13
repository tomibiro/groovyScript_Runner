package com.example;

import com.liferay.portal.kernel.exception.PortalException;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import javax.portlet.PortletRequest;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component(
		immediate = true,
		service = GroovyExecutor.class
)
public class GroovyExecutor {

	/*@Reference
	private UserLocalService userLocalService;

	@Reference
	private CompanyLocalService companyLocalService;

	@Reference
	private GroupLocalService groupLocalService;

	@Reference
	private LayoutLocalService layoutLocalService;*/

	@Reference
	private PortletRequest portletRequest;

	private static final String SCRIPT_PATH = "c:\\git\\bundles\\master\\scripts\\create_user.groovy";

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

		Object user = portletRequest.getAttribute(PortletRequest.USER_INFO);

		/*long companyId = PortalUtil.getCompanyId(request);
		Company company = companyLocalService.getCompany(companyId);*/
		//User user = userLocalService.getUserById(PortalUtil.getUserId(request));
		/*Group group = groupLocalService.getGroup(companyId, "Guest");
		Layout layout = layoutLocalService.getLayouts(group.getGroupId(), false).get(0);
		ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = new ThemeDisplay();
		themeDisplay.setUser(user);
		themeDisplay.setCompany(company);
		themeDisplay.setScopeGroupId(group.getGroupId());

		binding.setVariable("company", company);
		binding.setVariable("user", user);
		binding.setVariable("themeDisplay", themeDisplay);
		binding.setVariable("serviceContext", serviceContext);
		binding.setVariable("group", group);
		binding.setVariable("layout", layout);
		binding.setVariable("request", request);
		binding.setVariable("response", response);
*/

		binding.setVariable("userinfo", user);

		return binding;
	}
}
