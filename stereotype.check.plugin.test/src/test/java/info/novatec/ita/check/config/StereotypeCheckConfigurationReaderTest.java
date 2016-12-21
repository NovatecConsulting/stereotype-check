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
import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

import info.novatec.ita.check.AbstractStereotypeCheckTest;
import info.novatec.ita.check.StereotypeCheck;
import info.novatec.ita.check.config.AnnotationConfiguration;
import info.novatec.ita.check.config.StereotypeCheckConfiguration;
import info.novatec.ita.check.config.StereotypeCheckReader;
import info.novatec.ita.check.config.StereotypeCondition;
import info.novatec.ita.check.config.StereotypeConfiguration;
import info.novatec.ita.check.config.StereotypeIdentifier;
import info.novatec.ita.check.testclasses.app1.main.bl.is.SampleIs;
import info.novatec.ita.check.testclasses.app1.main.data.entity.AdditionalOperation;
import info.novatec.ita.check.testclasses.app1.main.ul.wt.test.SampleApp1View;
import info.novatec.ita.check.testclasses.app1.main.ul.wt.test.SampleContainsTransformerView;
import info.novatec.ita.check.testclasses.app1.shared.bl.bs.SampleApiBs;

/**
 * Tests to read a configuration file for the stereotype check.
 *
 */
public class StereotypeCheckConfigurationReaderTest extends AbstractStereotypeCheckTest {

	/**
	 * Read a file where the project specific configuration allows a dependency
	 * which is not allowed in the central configuration.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void dependencyAllowedInOverrideFail() throws Exception {
		DefaultConfiguration main = createConfig(
				"src/test/resources/stereotype-dependency-allowed-in-override-fail.xml");
		final String[] expected = {};
		try {
			verify(main, getPath(SampleContainsTransformerView.class), expected);
			fail("CheckstyleException should have been thrown");
		} catch (CheckstyleException ex) {
			assertThat(ex.getCause()).isNotNull().isInstanceOf(IllegalArgumentException.class);
			assertThat(ex.getCause().getMessage()).contains(
					"The additional configuration for from dependency transformer contains a dependency to transformer that is not part of the central configuration.");
		}
	}

	/**
	 * If the stereotype configuration file is not valid against the XSD.
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test(expected = CheckstyleException.class)
	public void invalidXMLConfigurationFileThrowsCheckstyleException() throws IOException, Exception {
		DefaultConfiguration main = createCheckConfig(TreeWalker.class);
		final DefaultConfiguration checkConfig = createCheckConfig(StereotypeCheck.class);
		checkConfig.addAttribute("file", "src/test/resources/stereotype_invalidXML.xml");
		main.addChild(checkConfig);
		final String[] expected = { "" };
		try {
			verify(main, getPath(SampleIs.class), expected);
		} catch (CheckstyleException ex) {
			assertThat(ex.getMessage()).contains("Attribute 'to' must appear on element 'dependency'");
			throw ex;
		}
	}

	/**
	 * If the stereotype configuration file is not valid against the XSD.
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@Test(expected = CheckstyleException.class)
	public void duplicateStereotypeConfigurationThrowsCheckstyleException() throws IOException, Exception {
		DefaultConfiguration main = createCheckConfig(TreeWalker.class);
		final DefaultConfiguration checkConfig = createCheckConfig(StereotypeCheck.class);
		checkConfig.addAttribute("file", "src/test/resources/duplicate-stereotype.xml");
		main.addChild(checkConfig);
		final String[] expected = { "" };
		try {
			verify(main, getPath(SampleIs.class), expected);
		} catch (CheckstyleException ex) {
			assertThat(ex.getMessage()).contains("There are multiple occurrences of ID value 'producer'");
			throw ex;
		}
	}

	/**
	 * Successfully read a valid configuration file.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void readValidConfigurationFile() throws Exception {
		DefaultConfiguration main = createCheckConfig(TreeWalker.class);
		final DefaultConfiguration checkConfig = createCheckConfig(StereotypeCheck.class);
		checkConfig.addAttribute("file", "src/test/resources/stereotype.xml");
		main.addChild(checkConfig);
		final String[] expected = {};
		verify(main, getPath(SampleIs.class), expected);
	}

	/**
	 * Read a file with a cycle in the dependency definition.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void dependencyCycle() throws Exception {
		DefaultConfiguration main = createCheckConfig(TreeWalker.class);
		final DefaultConfiguration checkConfig = createCheckConfig(StereotypeCheck.class);
		checkConfig.addAttribute("file", "src/test/resources/stereotypeCycle.xml");
		main.addChild(checkConfig);
		final String[] expected = {};
		try {
			verify(main, getPath(SampleIs.class), expected);
			fail("CheckstyleException should have been thrown");
		} catch (CheckstyleException ex) {
			assertThat(ex).hasCauseExactlyInstanceOf(IllegalArgumentException.class);
			assertThat(ex.getCause()).hasMessageContaining("There is a cycle in dependency-configuration:")
					.hasMessageContaining("dto").hasMessageContaining("view").hasMessageContaining("transformer");

		}
	}

	/**
	 * A configuration file must be specified to execute the stereotype check.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void noPropertyFileDefined() throws Exception {
		DefaultConfiguration main = createCheckConfig(TreeWalker.class);
		final DefaultConfiguration checkConfig = createCheckConfig(StereotypeCheck.class);
		main.addChild(checkConfig);
		final String[] expected = {};
		try {
			verify(main, getPath(SampleIs.class), expected);
			fail("CheckstyleException");
		} catch (CheckstyleException ex) {
			assertThat(ex).hasCauseExactlyInstanceOf(IllegalArgumentException.class);
			assertThat(ex.getCause())
					.hasMessage("No Property 'file' defined for Check info.novatec.ita.check.StereotypeCheck");
		}
	}

	/**
	 * Checks if the configuration file exists.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void fileStereotypeXmlNotExist() throws Exception {
		DefaultConfiguration main = createCheckConfig(TreeWalker.class);
		final DefaultConfiguration checkConfig = createCheckConfig(StereotypeCheck.class);
		checkConfig.addAttribute("file", "src/test/resources/stereotypeNotExist.xml");
		main.addChild(checkConfig);
		final String[] expected = {};
		try {
			verify(main, getPath(SampleIs.class), expected);
			fail("CheckstyleException");
		} catch (CheckstyleException ex) {
			assertThat(ex).hasCauseExactlyInstanceOf(IllegalArgumentException.class);
			assertThat(ex.getCause()).hasMessage(
					"File defined in property 'file' of Check info.novatec.ita.check.StereotypeCheck does not exist src/test/resources/stereotypeNotExist.xml");
		}
	}

	/**
	 * Checks if a stereotype has minimum one sufficient condition.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void fileStereotypeXmlNotMinimumSufficient() throws Exception {
		try {
			StereotypeCheckReader.read(new File("src/test/resources/stereotypeNotMinimumSufficient.xml"));
			fail("IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
			assertThat(ex).hasMessageContaining(
					"There must be one sufficient condition to idenitfy the stereotype: integrationservice");
		}
	}

	/**
	 * Checks that a stereotype can use more than one other stereotype.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void fileDependencyDuplicateFrom() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader
				.read(new File("src/test/resources/dependencyDuplicateFrom.xml"));

		Set<StereotypeIdentifier> dependencies = config.getDependencies()
				.get(StereotypeIdentifier.of("integrationservice")).getAllowedToDependencies();

		assertThat(dependencies).contains(StereotypeIdentifier.of("transformer"),
				StereotypeIdentifier.of("transformer2"));
	}

	/**
	 * Check that a stereotype can be annotated with either annotation a or b.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void fileLoadApp1Stereotype() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader.read(new File("src/test/resources/stereotype.xml"));
		StereotypeConfiguration view = config.getStereotypeConfig().get(StereotypeIdentifier.of("view"));

		assertThat(view).isNotNull();
		assertThat(view.getAnnotationConfigs()).hasSize(1);
		assertThat(view.getAnnotationConfigs()).flatExtracting("annotationNames").contains(
				"info.novatec.ita.check.testclasses.core.fwk.common.ul.ViewStereotype",
				"info.novatec.ita.check.testclasses.app1.main.ul.App1TypedViewStereotype");
	}

	/**
	 * Check that a stereotype can be extended by class a or b.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void fileLoadBaseclasses() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader.read(new File("src/test/resources/stereotype.xml"));
		StereotypeConfiguration entity = config.getStereotypeConfig().get(StereotypeIdentifier.of("entity"));

		assertThat(entity).isNotNull();
		assertThat(entity.getBaseClassNames()).containsExactlyInAnyOrder(
				"info.novatec.ita.check.testclasses.core.fwk.api.data.dto.Pojo",
				"info.novatec.ita.check.testclasses.app1.main.data.entity.common.BaseEntity",
				"info.novatec.ita.check.testclasses.app1.main.data.entity.CustomerConcern");
	}

	/**
	 * Checks that a class must be annotated with more than one annotation.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void fileTwoSeparatedAnnotations() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader.read(new File("src/test/resources/stereotype.xml"));
		StereotypeConfiguration initFunction = config.getStereotypeConfig()
				.get(StereotypeIdentifier.of("initfunction"));

		AnnotationConfiguration startupAnnotation = new AnnotationConfiguration();
		startupAnnotation.setAnnotationnameCondition(StereotypeCondition.necessary);
		startupAnnotation.addAnnotationName("javax.ejb.Startup");

		AnnotationConfiguration singeltonAnnotation = new AnnotationConfiguration();
		singeltonAnnotation.setAnnotationnameCondition(StereotypeCondition.necessary);
		singeltonAnnotation.addAnnotationName("javax.ejb.Singleton");

		AnnotationConfiguration localBeanAnnotation = new AnnotationConfiguration();
		localBeanAnnotation.setAnnotationnameCondition(StereotypeCondition.necessary);
		localBeanAnnotation.addAnnotationName("javax.ejb.LocalBean");

		assertThat(initFunction).isNotNull();
		assertThat(initFunction.getAnnotationConfigs()).containsExactlyInAnyOrder(startupAnnotation,
				singeltonAnnotation, localBeanAnnotation);

	}

	@Test
	public void checkEntityOverrideOk() throws Exception {
		DefaultConfiguration main = createCheckConfig(TreeWalker.class);
		final DefaultConfiguration checkConfig = createCheckConfig(StereotypeCheck.class);
		checkConfig.addAttribute("file", "src/test/resources/stereotype-allowoverride.xml");
		main.addChild(checkConfig);
		final String[] expected = {};
		verify(main, getPath(AdditionalOperation.class), expected);
	}

	@Test
	public void checkEntityOverrideFail() throws Exception {
		DefaultConfiguration main = createCheckConfig(TreeWalker.class);
		final DefaultConfiguration checkConfig = createCheckConfig(StereotypeCheck.class);
		checkConfig.addAttribute("file", "src/test/resources/stereotype-allowoverride-fail.xml");
		main.addChild(checkConfig);
		String expectedFail = "Stereotype entity: does not extend info.novatec.ita.check.testclasses.core.fwk.api.data.dto.Pojo";
		verify(main, getPath(AdditionalOperation.class), expectedFail);
	}

	@Test
	public void checkInterfaceOverrideOk() throws Exception {
		DefaultConfiguration main = createCheckConfig(TreeWalker.class);
		final DefaultConfiguration checkConfig = createCheckConfig(StereotypeCheck.class);
		checkConfig.addAttribute("file", "src/test/resources/stereotype-allowoverride.xml");
		main.addChild(checkConfig);
		final String[] expected = {};
		verify(main, getPath(SampleApiBs.class), expected);
	}

	@Test
	public void checkInterfaceOverrideFail() throws Exception {
		DefaultConfiguration main = createCheckConfig(TreeWalker.class);
		final DefaultConfiguration checkConfig = createCheckConfig(StereotypeCheck.class);
		checkConfig.addAttribute("file", "src/test/resources/stereotype-allowoverride-fail.xml");
		main.addChild(checkConfig);
		final String expectedFail = "Stereotype businessservice: does not extend info.novatec.ita.check.testclasses.core.fwk.api.bl.bs.BusinessService";
		verify(main, getPath(SampleApiBs.class), expectedFail);
	}

	@Test
	public void checkAnnotationOverrideOk() throws Exception {
		DefaultConfiguration main = createCheckConfig(TreeWalker.class);
		final DefaultConfiguration checkConfig = createCheckConfig(StereotypeCheck.class);
		checkConfig.addAttribute("file", "src/test/resources/stereotype-allowoverride.xml");
		main.addChild(checkConfig);
		final String[] expected = {};
		verify(main, getPath(SampleApp1View.class), expected);
	}

	@Test
	public void checkAnnotationOverrideFail() throws Exception {
		DefaultConfiguration main = createCheckConfig(TreeWalker.class);
		final DefaultConfiguration checkConfig = createCheckConfig(StereotypeCheck.class);
		checkConfig.addAttribute("file", "src/test/resources/stereotype-allowoverride-fail.xml");
		main.addChild(checkConfig);
		final String expectedFail = "Stereotype view: missing annotation info.novatec.ita.check.testclasses.core.fwk.common.ul.ViewStereotype";
		verify(main, getPath(SampleApp1View.class), expectedFail);
	}
}
