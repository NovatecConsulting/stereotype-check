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
 * Tests to check the override behavior of interfaces.
 * 
 * @author Volker Koch (NovaTec Consulting GmbH)
 *
 */
public class InterfaceOverrideTest extends AbstractStereotypeCheckTest {

	/**
	 * Checks that a interface is overriden by {@link OverrideMode#replace}.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution.
	 */
	@Test
	public void interfaceIsOverridenByReplace() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader
				.read(new File("src/test/resources/stereotype-allowoverride.xml"));
		StereotypeConfiguration businessservice = config.getStereotypeConfig()
				.get(StereotypeIdentifier.of("businessservice"));
		assertThat(businessservice).isNotNull();
		assertThat(businessservice.getInterfaceNames())
				.containsExactly("info.novatec.architecture.check.testclasses.core.fwk.api.bl.bs.ApiBusinessService");
	}

	/**
	 * Checks that a interface is overriden by {@link OverrideMode#extend}.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution.
	 */
	@Test
	public void interfaceIsOverridenByExtend() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader
				.read(new File("src/test/resources/stereotype-allowoverride-extend.xml"));
		StereotypeConfiguration businessservice = config.getStereotypeConfig()
				.get(StereotypeIdentifier.of("businessservice"));
		assertThat(businessservice).isNotNull();
		assertThat(businessservice.getInterfaceNames()).containsExactly(
				"info.novatec.architecture.check.testclasses.core.fwk.api.bl.bs.ApiBusinessService",
				"info.novatec.architecture.check.testclasses.core.fwk.api.bl.bs.BusinessService");
	}

	/**
	 * Checks that a interfacenname is overriden by {@link OverrideMode#replace}
	 * .
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution.
	 */
	@Test
	public void interfacenameIsOverridenByReplace() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader
				.read(new File("src/test/resources/stereotype-allowoverride.xml"));
		StereotypeConfiguration businessservice2 = config.getStereotypeConfig()
				.get(StereotypeIdentifier.of("businessservice2"));
		assertThat(businessservice2).isNotNull();
		assertThat(businessservice2.getInterfaceNames())
				.containsExactly("info.novatec.architecture.check.testclasses.core.fwk.api.bl.bs.ApiBusinessService2");
	}

	/**
	 * Checks that a interfacenname is overriden by {@link OverrideMode#extend}.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution.
	 */
	@Test
	public void interfacenameIsOverridenByExtend() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader
				.read(new File("src/test/resources/stereotype-allowoverride-extend.xml"));
		StereotypeConfiguration businessservice2 = config.getStereotypeConfig()
				.get(StereotypeIdentifier.of("businessservice2"));
		assertThat(businessservice2).isNotNull();
		assertThat(businessservice2.getInterfaceNames()).containsExactlyInAnyOrder(
				"info.novatec.architecture.check.testclasses.core.fwk.api.bl.bs.ApiBusinessService2",
				"info.novatec.architecture.check.testclasses.core.fwk.api.bl.bs.BusinessService2");
	}

	/**
	 * Checks that a interface that is overrideable is not overriden.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution.
	 */
	@Test
	public void interfaceIsNotOverriden() throws Exception {
		StereotypeCheckConfiguration config = StereotypeCheckReader
				.read(new File("src/test/resources/stereotype-allowoverride.xml"));
		StereotypeConfiguration businessserviceNotOverriden = config.getStereotypeConfig()
				.get(StereotypeIdentifier.of("businessserviceNotOverriden"));
		assertThat(businessserviceNotOverriden).isNotNull();
		assertThat(businessserviceNotOverriden.getInterfaceNames())
				.containsExactly("info.novatec.architecture.check.testclasses.core.fwk.api.bl.bs.BusinessService3");
	}
}