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
package info.novatec.ita.check.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Test;

import info.novatec.ita.check.AbstractStereotypeCheckTest;
import info.novatec.ita.check.config.OverrideMode;
import info.novatec.ita.check.config.StereotypeCheckConfiguration;
import info.novatec.ita.check.config.StereotypeCheckReader;
import info.novatec.ita.check.config.StereotypeConfiguration;
import info.novatec.ita.check.config.StereotypeIdentifier;

/**
 * Tests to check the override behavior of annotations.
 * 
 * @author Volker Koch (NovaTec Consulting GmbH)
 *
 */
public class AnnotationOverrideTest extends AbstractStereotypeCheckTest {

	/**
	 * Checks that a annotationname is overridden by {@link OverrideMode#replace}
	 * .
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void annotationnameIsOverriddenByReplace() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader
				.read(new File("src/test/resources/stereotype-allowoverride.xml"));

		StereotypeConfiguration view = config.getStereotypeConfig().get(StereotypeIdentifier.of("view"));
		assertThat(view).isNotNull();
		assertThat(view.getAnnotationNamesAsString())
				.isEqualTo("info.novatec.ita.check.testclasses.app1.main.ul.App1TypedViewStereotype");
	}

	/**
	 * Checks that a annotation is overridden by {@link OverrideMode#replace}.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution.
	 */
	@Test
	public void annotationIsOverriddenByReplace() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader
				.read(new File("src/test/resources/stereotype-allowoverride.xml"));
		StereotypeConfiguration view2 = config.getStereotypeConfig().get(StereotypeIdentifier.of("view2"));
		assertThat(view2).isNotNull();
		assertThat(view2.getAnnotationNamesAsString())
				.isEqualTo("info.novatec.ita.check.testclasses.app1.main.ul.App1TypedViewStereotype2");
	}

	/**
	 * Checks that a annotation that is overrideable is not overridden.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution.
	 */
	@Test
	public void annotationIsNotOverridden() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader
				.read(new File("src/test/resources/stereotype-allowoverride.xml"));
		StereotypeConfiguration viewNotOverridden = config.getStereotypeConfig()
				.get(StereotypeIdentifier.of("viewNotOverridden"));
		assertThat(viewNotOverridden).isNotNull();
		assertThat(viewNotOverridden.getAnnotationNamesAsString())
				.isEqualTo("info.novatec.ita.check.testclasses.core.fwk.common.ul.ViewStereotype3");
	}

}
