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
package info.novatec.architecture.check.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Test;

import info.novatec.architecture.check.AbstractStereotypeCheckTest;

/**
 * Tests to check the override behavior of base classes.
 * 
 * @author Volker Koch (NovaTec Consulting GmbH)
 *
 */
public class BaseClassOverrideTest extends AbstractStereotypeCheckTest {

	/**
	 * Checks that a baseclassnname is overriden by {@link OverrideMode#replace}
	 * .
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution.
	 */
	@Test
	public void basclassnameIsOverridenByReplace() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader
				.read(new File("src/test/resources/stereotype-allowoverride.xml"));
		StereotypeConfiguration entity = config.getStereotypeConfig().get(StereotypeIdentifier.of("entity"));
		assertThat(entity).isNotNull();
		assertThat(entity.getBaseClassNames()).containsExactly(
				"info.novatec.architecture.check.testclasses.app1.main.data.entity.common.BaseEntity",
				"info.novatec.architecture.check.testclasses.app1.main.data.entity.CustomerConcern");
	}

	/**
	 * Checks that a baseclassnname is overriden by {@link OverrideMode#extend}.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution.
	 */
	@Test
	public void basclassnameIsOverridenByExtend() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader
				.read(new File("src/test/resources/stereotype-allowoverride-extend.xml"));
		StereotypeConfiguration entity = config.getStereotypeConfig().get(StereotypeIdentifier.of("entity"));
		assertThat(entity).isNotNull();
		assertThat(entity.getBaseClassNames()).containsExactly(
				"info.novatec.architecture.check.testclasses.app1.main.data.entity.common.BaseEntity",
				"info.novatec.architecture.check.testclasses.app1.main.data.entity.CustomerConcern",
				"info.novatec.architecture.check.testclasses.core.fwk.api.data.dto.Pojo");
	}

	/**
	 * Checks that a baseclass is overriden by {@link OverrideMode#replace}.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution.
	 */
	@Test
	public void baseclassIsOverridenByReplace() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader
				.read(new File("src/test/resources/stereotype-allowoverride.xml"));
		StereotypeConfiguration entity2 = config.getStereotypeConfig().get(StereotypeIdentifier.of("entity2"));
		assertThat(entity2).isNotNull();
		assertThat(entity2.getBaseClassNames()).containsExactly(
				"info.novatec.architecture.check.testclasses.app1.main.data.entity.common.BaseEntity2");
	}

	/**
	 * Checks that a baseclass is overriden by {@link OverrideMode#replace}.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution.
	 */
	@Test
	public void baseclassIsOverridenByExtend() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader
				.read(new File("src/test/resources/stereotype-allowoverride-extend.xml"));
		StereotypeConfiguration entity2 = config.getStereotypeConfig().get(StereotypeIdentifier.of("entity2"));
		assertThat(entity2).isNotNull();
		assertThat(entity2.getBaseClassNames()).containsExactly(
				"info.novatec.architecture.check.testclasses.app1.main.data.entity.common.BaseEntity2",
				"info.novatec.architecture.check.testclasses.core.fwk.api.data.dto.Pojo2");
	}

	/**
	 * Checks that a baseclass that is overrideable is not overriden by
	 * {@link OverrideMode#replace}.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution.
	 */
	@Test
	public void baseclassIsNotOverriden() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader
				.read(new File("src/test/resources/stereotype-allowoverride.xml"));
		StereotypeConfiguration entityNotOverriden = config.getStereotypeConfig()
				.get(StereotypeIdentifier.of("entityNotOverriden"));
		assertThat(entityNotOverriden).isNotNull();
		assertThat(entityNotOverriden.getBaseClassNames())
				.containsExactly("info.novatec.architecture.check.testclasses.core.fwk.api.data.dto.Pojo3");
	}
}
