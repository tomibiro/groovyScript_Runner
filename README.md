The "groovy script runner" is reponsible to act as an osgi module and this is for running groovy scripts in a GroovyShell and execute them inside the Liferay DXP/portal.
The main goal is to populate dummy data with groovy scripts to running Liferay containers in Docker. (local usage is also possible)

The usage of the module:

1, start a Liferay bundle localy or in Docker container.

2, modify the build.gradle file with the Liferay version to create a compatible build.

3, modify the SCRIPT_PATH to the dedicated groovy script path if you would like to run localy.

4, build the osgi module with ./gradlew build

5, you will find the created .jar file in \modules\my-groovy-executor\build\libs\

10, copy this jar to the Liferay's \deploy folder or to the mounted container's folder (e.g. \groovy_script_runner folder in the docker config)
