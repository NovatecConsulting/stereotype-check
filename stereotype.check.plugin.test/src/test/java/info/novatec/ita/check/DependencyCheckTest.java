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
package info.novatec.ita.check;

import java.io.IOException;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

import info.novatec.ita.check.testclasses.app1.main.bl.bo.SampleBo;
import info.novatec.ita.check.testclasses.app1.main.bl.bo.SampleReferencingAttributeBo;
import info.novatec.ita.check.testclasses.app1.main.bl.bo.SampleReferencingParameterBo;
import info.novatec.ita.check.testclasses.app1.main.bl.bo.SampleReferencingReturnBo;
import info.novatec.ita.check.testclasses.app1.main.bl.bo.SampleReferencingVarBo;
import info.novatec.ita.check.testclasses.app1.main.bl.bo.sub.SampleReferencingAttributeOtherPackageBo;
import info.novatec.ita.check.testclasses.app1.main.bl.bo.sub.SampleReferencingParameterOtherPackageBo;
import info.novatec.ita.check.testclasses.app1.main.bl.bo.sub.SampleReferencingReturnOtherPackageBo;
import info.novatec.ita.check.testclasses.app1.main.bl.bo.sub.SampleReferencingVarOtherPackageBo;
import info.novatec.ita.check.testclasses.app1.main.bl.is.DependencyIs;
import info.novatec.ita.check.testclasses.app1.main.bl.is.SampleWithDependencyOutsideApplicationIs;
import info.novatec.ita.check.testclasses.app1.main.bl.is.tf.SampleTf;
import info.novatec.ita.check.testclasses.app1.main.bl.is.tf.SampleWithWrongDepencencyTf;
import info.novatec.ita.check.testclasses.app1.main.ul.wt.test.SampleContainsTransformerView;
import info.novatec.ita.check.testclasses.app1.main.ul.wt.test.SampleViewContainsView;

/**
 * Tests to check allowed and disallowed dependencies between stereotypes.
 *
 */
public class DependencyCheckTest extends AbstractStereotypeCheckTest {
	/**
	 * The view {@link SampleViewContainsView} injects another view which is
	 * allowed in the central file stereotype-dependency-not-allowed.xml but not
	 * in the project specific file
	 * stereotype-dependency-not-allowed-override.xml
	 * 
	 * @throws Exception
	 */
	@Test
	public void disallowDependencyInOverride() throws Exception {
		DefaultConfiguration main = createConfig(
				"src/test/resources/stereotype-dependency-not-allowed-in-override.xml");
		final String[] expected = {
				"Disallowed dependency from stereotype view to stereotype view: info.novatec.ita.check.testclasses.app1.main.ul.wt.test.SampleCoreView" };
		verify(main, getPath(SampleViewContainsView.class), expected);
	}

	/**
	 * The view {@link SampleContainsTransformerView} injects a transformer
	 * which is allowed in the central file
	 * stereotype-dependency-not-allowed.xml and in the project specific file
	 * stereotype-dependency-not-allowed-override.xml
	 * 
	 * @throws Exception
	 */
	@Test
	public void allowedDependencyInOverride() throws Exception {
		DefaultConfiguration main = createConfig(
				"src/test/resources/stereotype-dependency-not-allowed-in-override.xml");
		final String[] expected = {};
		verify(main, getPath(SampleContainsTransformerView.class), expected);
	}

	/**
	 * The integration service {@link DependencyIs} has a allowed dependency to
	 * transformer {@link SampleTf}.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void noDependencyViolations() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(DependencyIs.class), expected);
	}

	/**
	 * The transformer {@link SampleWithWrongDepencencyTf} has a disallowed
	 * dependency to integration service {@link DependencyIs}.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void dependencyViolation() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Disallowed dependency from stereotype transformer to stereotype integrationservice: "
				+ "info.novatec.ita.check.testclasses.app1.main.bl.is.DependencyIs";
		verify(main, getPath(SampleWithWrongDepencencyTf.class), expected);
	}

	/**
	 * The {@link SampleWithDependencyOutsideApplicationIs} has a dependency
	 * outside of the application package.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void noDependencyViolationsForOutsideApplication() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(SampleWithDependencyOutsideApplicationIs.class), expected);
	}

	/**
	 * The business operation {@link SampleReferencingAttributeBo} has a
	 * disallowed dependency to another business operation {@link SampleBo}
	 * referenced by a class member.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void dependencyViolationReferencingClassMember() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Disallowed dependency from stereotype businessoperation to stereotype businessoperation: "
				+ "info.novatec.ita.check.testclasses.app1.main.bl.bo.SampleBo";
		verify(main, getPath(SampleReferencingAttributeBo.class), expected);
	}

	/**
	 * The business operation {@link SampleReferencingReturnBo} has a disallowed
	 * dependency to another business operation {@link SampleBo} referenced by a
	 * method return.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void dependencyViolationReferencingReturnType() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = " Disallowed dependency from stereotype businessoperation to stereotype businessoperation: "
				+ "info.novatec.ita.check.testclasses.app1.main.bl.bo.SampleBo";
		verify(main, getPath(SampleReferencingReturnBo.class), expected);
	}

	/**
	 * The business operation {@link SampleReferencingParameterBo} has a
	 * disallowed dependency to another business operation {@link SampleBo}
	 * referenced by a method parameter.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void dependencyViolationReferencingMethodParameterType() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Disallowed dependency from stereotype businessoperation to stereotype businessoperation: "
				+ "info.novatec.ita.check.testclasses.app1.main.bl.bo.SampleBo";
		verify(main, getPath(SampleReferencingParameterBo.class), expected);
	}

	/**
	 * The business operation {@link SampleReferencingVarBo} has a disallowed
	 * dependency to another business operation {@link SampleBo} referenced by a
	 * local variable.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void dependencyViolationReferencingLocalVariable() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Disallowed dependency from stereotype businessoperation to stereotype businessoperation: "
				+ "info.novatec.ita.check.testclasses.app1.main.bl.bo.SampleBo";
		verify(main, getPath(SampleReferencingVarBo.class), expected);
	}

	/**
	 * The business operation {@link SampleReferencingAttributeOtherPackageBo}
	 * has a disallowed dependency to another business operation in another
	 * package {@link SampleBo} referenced by an attribute.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void dependencyViolationReferencingAttributeFullQualifiedClassMember() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Disallowed dependency from stereotype businessoperation to stereotype businessoperation: "
				+ "info.novatec.ita.check.testclasses.app1.main.bl.bo.SampleBo";
		verify(main, getPath(SampleReferencingAttributeOtherPackageBo.class), expected);
	}

	/**
	 * The business operation {@link SampleReferencingReturnOtherPackageBo} has
	 * a disallowed dependency to another business operation n another package
	 * {@link SampleBo} referenced by a method return.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void dependencyViolationReferencingReturnFullQualifiedClassMember() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Disallowed dependency from stereotype businessoperation to stereotype businessoperation: "
				+ "info.novatec.ita.check.testclasses.app1.main.bl.bo.SampleBo";
		verify(main, getPath(SampleReferencingReturnOtherPackageBo.class), expected);
	}

	/**
	 * The business operation {@link SampleReferencingParameterOtherPackageBo}
	 * has a disallowed dependency to another business operation in another
	 * package {@link SampleBo} referenced by a method parameter.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void dependencyViolationReferencingParameterFullQualifiedClassMember() throws IOException, Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Disallowed dependency from stereotype businessoperation to stereotype businessoperation: "
				+ "info.novatec.ita.check.testclasses.app1.main.bl.bo.SampleBo";
		verify(main, getPath(SampleReferencingParameterOtherPackageBo.class), expected);
	}

	/**
	 * The business operation {@link SampleReferencingVarOtherPackageBo} has a
	 * disallowed dependency to another business operation in another package
	 * {@link SampleBo} referenced by a local variable.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void dependencyViolationReferencingLocalVarFullQualifiedClassMember() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Disallowed dependency from stereotype businessoperation to stereotype businessoperation: "
				+ "info.novatec.ita.check.testclasses.app1.main.bl.bo.SampleBo";
		verify(main, getPath(SampleReferencingVarOtherPackageBo.class), expected);
	}
}
