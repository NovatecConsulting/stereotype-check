/*******************************************************************************
 * Copyright 2016 NovaTec Consulting GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package info.novatec.architecture.check;

import static org.assertj.core.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Arrays;
import com.puppycrawl.tools.checkstyle.BaseFileSetCheckTestSupport;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.Configuration;

public class AbstractStereotypeCheckTest extends BaseFileSetCheckTestSupport {

	/**
	 * Get the project relative path to the .java file of the given class
	 * 
	 * @param clazz
	 *            class
	 * @return project relative path to the .java file
	 * 
	 */
	protected String getPath(Class<?> clazz) throws IOException {
		return new File("src/main/java/" + clazz.getName().replaceAll("\\.", "/") + ".java").getCanonicalPath();
	}

	/**
	 * Create a default configuration from ("src/test/resources/stereotype.xml")
	 * to execute to stereotype check checkstyle plugin.
	 * 
	 * @return The configuration.
	 */
	protected DefaultConfiguration createDefaultConfig() {
		return createConfig("src/test/resources/stereotype.xml");
	}

	/**
	 * Create a configuration to execute to stereotype check checkstyle plugin.
	 * 
	 * @param configFilename
	 *            The project relative path to the configuration file used by
	 *            the stereotype check.
	 * @return The configuration.
	 */
	private DefaultConfiguration createConfig(String configFilename) {
		DefaultConfiguration main = createCheckConfig(TreeWalker.class);
		final DefaultConfiguration checkConfig = createCheckConfig(StereotypeCheck.class);
		checkConfig.addAttribute("file", configFilename);
		main.addChild(checkConfig);
		return main;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.puppycrawl.tools.checkstyle.BaseCheckTestSupport#verify(com.
	 * puppycrawl.tools.checkstyle.Checker, java.io.File[], java.lang.String,
	 * java.lang.String[])
	 */
	protected void verify(Checker checker, File[] processedFiles, String messageFileName, String[] expectedErrorTexts)
			throws Exception {
		this.stream.flush();
		int numberOfErrors = checker.process(Arrays.asList(processedFiles));

		LineNumberReader errors = new LineNumberReader(
				new InputStreamReader(new ByteArrayInputStream(this.BAOS.toByteArray())));

		for (int i = 0; i < expectedErrorTexts.length; ++i) {
			String actual = errors.readLine();
			assertThat(actual).as("error message %s", i).contains(expectedErrorTexts[i]);
		}

		assertThat(numberOfErrors).as("got unexpected error count with error message: %s", errors.readLine())
				.isEqualTo(expectedErrorTexts.length);

		checker.destroy();
	}

	protected void verify(Configuration aConfig, String fileName, String expected) throws Exception {
		verify(createChecker(aConfig), fileName, fileName, expected);
	}

	protected void verify(Checker c, String processedFilename, String messageFileName, String expected)
			throws Exception {
		verify(c, new File[] { new File(processedFilename) }, messageFileName, expected);
	}

	protected void verify(Checker c, File[] processedFiles, String messageFileName, String expected) throws Exception {
		String[] expectedAsArray;
		if (expected == null) {
			expectedAsArray = new String[] {};
		} else {
			expectedAsArray = new String[] { expected };
		}
		verify(c, processedFiles, messageFileName, expectedAsArray);
	}

}
