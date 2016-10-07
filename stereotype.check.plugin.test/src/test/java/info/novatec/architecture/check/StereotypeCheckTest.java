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

import info.novatec.architecture.check.testclasses.app1.main.bl.bs.SampleBsBean;
import info.novatec.architecture.check.testclasses.app1.main.bl.bs.SampleTest;
import info.novatec.architecture.check.testclasses.app1.main.bl.is.Sample2Interfaces2Is;
import info.novatec.architecture.check.testclasses.app1.main.bl.is.Sample2InterfacesIs;
import info.novatec.architecture.check.testclasses.app1.main.bl.is.SampleIs;
import info.novatec.architecture.check.testclasses.app1.main.bl.is.SampleIsWithPackageNames;
import info.novatec.architecture.check.testclasses.app1.main.bl.is.SampleNoStereotype;
import info.novatec.architecture.check.testclasses.app1.main.bl.is.SampleWithInnerClassIs;
import info.novatec.architecture.check.testclasses.app1.main.bl.is.SampleWithPackageNamesIs;
import info.novatec.architecture.check.testclasses.app1.main.bl.is.SampleWithoutAnnotationIs;
import info.novatec.architecture.check.testclasses.app1.main.bl.is.SampleWithoutInterfaceIs;
import info.novatec.architecture.check.testclasses.app1.main.bl.is.SampleWithoutPostfix;
import info.novatec.architecture.check.testclasses.app1.main.bl.is.tf.BaseTf;
import info.novatec.architecture.check.testclasses.app1.main.bl.is.tf.SampleApplicationScoped;
import info.novatec.architecture.check.testclasses.app1.main.bl.is.tf.SampleTf;
import info.novatec.architecture.check.testclasses.app1.main.bl.is.tf.SampleTfExtendsSampleNoTf;
import info.novatec.architecture.check.testclasses.app1.main.bl.is.tf.SampleTfExtendsSampleTf;
import info.novatec.architecture.check.testclasses.app1.main.bl.wrong.SampleWrongPackageIs;
import info.novatec.architecture.check.testclasses.app1.main.data.dto.SampleDto;
import info.novatec.architecture.check.testclasses.app1.main.data.dto.SampleDtoExtendsSampleDto;
import info.novatec.architecture.check.testclasses.app1.main.data.dto.SampleDtoExtendsSampleNoDto;
import info.novatec.architecture.check.testclasses.app1.main.data.dto.SampleNoBaseClassDto;
import info.novatec.architecture.check.testclasses.app1.main.data.entity.AdditionalOperation;
import info.novatec.architecture.check.testclasses.app1.main.data.entity.CustomerConcern;
import info.novatec.architecture.check.testclasses.app1.main.data.entity.SampleEntity;
import info.novatec.architecture.check.testclasses.app1.main.data.entity.SampleEntityWithoutBaseclass;
import info.novatec.architecture.check.testclasses.app1.main.data.entity.common.BaseEntity;
import info.novatec.architecture.check.testclasses.app1.main.init.SampleIf;
import info.novatec.architecture.check.testclasses.app1.main.init.SampleNoStartupIf;
import info.novatec.architecture.check.testclasses.app1.main.ul.App1TypedViewStereotype;
import info.novatec.architecture.check.testclasses.app1.main.ul.wt.test.SampleApp1View;
import info.novatec.architecture.check.testclasses.app1.main.ul.wt.test.SampleCoreView;
import info.novatec.architecture.check.testclasses.app1.shared.bl.bs.SampleBs;
import info.novatec.architecture.check.testclasses.app1.shared.bl.bs.SampleNoInterfaceBs;
import info.novatec.architecture.check.testclasses.core.fwk.common.bl.is.IntegrationService;
import info.novatec.architecture.check.testclasses.core.fwk.common.bl.is.IntegrationServiceStereotype;
import info.novatec.architecture.check.testclasses.core.fwk.common.ul.ViewStereotype;
import info.novatec.architecture.check.testclasses.core.fwk.common.util.DateUtil;
import info.novatec.architecture.check.testclasses.core.fwk.common.util.Util;
import info.novatec.architecture.check.testclasses.core.fwk.main.ts.VersionTsBean;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * Tests to validate stereotypes.
 *
 */
public class StereotypeCheckTest extends AbstractStereotypeCheckTest {

	/**
	 * The {@link SampleIs} is a valid integration service.
	 * 
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testOK() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(SampleIs.class), expected);
	}

	/**
	 * The {@link SampleEntity} is a valid entity.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testOKWithBaseclass() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(SampleEntity.class), expected);
	}

	/**
	 * The {@link SampleNoBaseClassDto} has no baseclass for entiies
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testFailWithoutBaseclass() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = 
				"Stereotype dto: does not extend info.novatec.architecture.check.testclasses.core.fwk.api.data.dto.Pojo";
		verify(main, getPath(SampleNoBaseClassDto.class), expected);
	}

	/**
	 * The {@link SampleEntity} is a valid dto.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testOKWithoutAnnotation() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(SampleDto.class), expected);
	}

	/**
	 * The {@link SampleWithPackageNamesIs} is a not a valid integration
	 * service. The integration service interface is specified by full
	 * classname.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testOKWithPackageNames() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(SampleWithPackageNamesIs.class), expected);
	}

	/**
	 * The {@link SampleIsWithPackageNames} is a not a valid integration
	 * service. The integration service interface is specified by full
	 * classname.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testMissingWithPackageNames() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Stereotype integrationservice: missing postfix Is";
		verify(main, getPath(SampleIsWithPackageNames.class), expected);
	}

	/**
	 * The class {@link SampleNoStereotype} is in the same package as
	 * integrationservices but is not an integration service which is allowed
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testNoStereotype() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(SampleNoStereotype.class), expected);
	}

	/**
	 * The InnerClass of {@link SampleWithInnerClassIs} should not throw an
	 * error.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testWithInnerClass() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(SampleWithInnerClassIs.class), expected);
	}

	/**
	 * The {@link SampleWithoutAnnotationIs} is not annotated by
	 * {@link IntegrationServiceStereotype}.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testWithoutAnnotation() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Stereotype integrationservice: missing annotation info.novatec.architecture.check.testclasses.core.fwk.common.bl.is.IntegrationServiceStereotype";
		verify(main, getPath(SampleWithoutAnnotationIs.class), expected);
	}

	/**
	 * The class {@link SampleWithoutInterfaceIs} does not implement
	 * {@link IntegrationService}.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testWithoutInterface() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Stereotype integrationservice: does not implement info.novatec.architecture.check.testclasses.core.fwk.common.bl.is.IntegrationService";
		verify(main, getPath(SampleWithoutInterfaceIs.class), expected);
	}

	/**
	 * The classes {@link Sample2InterfacesIs} and {@link Sample2Interfaces2Is}
	 * can implement more interface than the interface
	 * {@link IntegrationService}.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testWith2Interfaces() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(Sample2InterfacesIs.class), expected);
		verify(main, getPath(Sample2Interfaces2Is.class), expected);
	}

	/**
	 * The {@link SampleWithoutPostfix} has no Postfix Is.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testWithoutPostfix() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Stereotype integrationservice: missing postfix Is";
		verify(main, getPath(SampleWithoutPostfix.class), expected);
	}

	/**
	 * The {@link SampleWrongPackageIs} is not in package *.main.bl.is.*
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testWrongPackage() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Stereotype integrationservice: is not in package ^([a-z]+[a-z0-9\\.]*)\\.main(\\.[a-z][a-z0-9]*)*\\.bl\\.is(\\.[a-z][a-z0-9]*)*$";
		verify(main, getPath(SampleWrongPackageIs.class), expected);
	}

	/**
	 * The{@link SampleTf} is a valid transformer.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testOKTf() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(SampleTf.class), expected);
	}

	/**
	 * The {@link SampleApplicationScoped} is a not a transformer but uses the
	 * annotation {@link ApplicationScoped}.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testOKApplicationScoped() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(SampleApplicationScoped.class), expected);
	}

	/**
	 * The annotation {@link IntegrationServiceStereotype} is ignored.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testAnnotationClass() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(IntegrationServiceStereotype.class), expected);
	}

	/**
	 * The view stereotype can be annotated with either
	 * {@link App1TypedViewStereotype} or {@link ViewStereotype}.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testTwoPossibleAnnotations() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(SampleApp1View.class), expected);
		verify(main, getPath(SampleCoreView.class), expected);
	}

	/**
	 * The init function stereotype must be annotated with three annotations.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testTwoSeparatAnnotations() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(SampleIf.class), expected);
	}

	/**
	 * The init function stereotype must be annotated with three annotations bu
	 * one is missing.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testTwoSeparatAnnotationsNoStartup() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Stereotype initfunction: missing annotation javax.ejb.LocalBean or javax.ejb.Singleton or javax.ejb.Startup";
		verify(main, getPath(SampleNoStartupIf.class), expected);
	}

	/**
	 * The technical service bean must be annotated with {@link Interceptors}
	 * and one of three possible others.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testAnnotationsAndOr() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(VersionTsBean.class), expected);
	}

	/**
	 * Check stereotype for interfaces.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testInterfaceOk() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(SampleBs.class), expected);
	}

	/**
	 * Check stereotype for interfaces, but has no base interface.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testInterfaceNoInterface() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Stereotype businessservice: does not extend info.novatec.architecture.check.testclasses.core.fwk.api.bl.bs.BusinessService";
		verify(main, getPath(SampleNoInterfaceBs.class), expected);
	}

	/**
	 * Because checkstyle only analyses source code, an indirect extension from
	 * a base class cannot be validated. All classes that can be extended have
	 * to be in the configuraton if the stereotype has no suffix.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testThreePossibleBaseclasses() throws Exception {
		DefaultConfiguration main = createDefaultConfig();

		String expectedFail = "Stereotype entity: does not extend info.novatec.architecture.check.testclasses.app1.main.data.entity.CustomerConcern or info.novatec.architecture.check.testclasses.app1.main.data.entity.common.BaseEntity or info.novatec.architecture.check.testclasses.core.fwk.api.data.dto.Pojo";
		verify(main, getPath(SampleEntityWithoutBaseclass.class), expectedFail);
		String[] expected = new String[] {};
		verify(main, getPath(BaseEntity.class), expected);
		verify(main, getPath(CustomerConcern.class), expected);
		verify(main, getPath(AdditionalOperation.class), expected);
	}

	/**
	 * If the stereotype has a postfix and a class A extends from another class
	 * B of the same stereotype, A must not have the implements/extends
	 * statement in its own file, because for B this check is already done.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testBaseClassIsStereotypeViaBaseClass() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(SampleDtoExtendsSampleDto.class), expected);
	}

	/**
	 * The baseclass of dto {@link SampleDtoExtendsSampleNoDto} is no dto.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testBaseClassIsNotStereotypeViaBaseClass() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Stereotype dto: does not extend info.novatec.architecture.check.testclasses.core.fwk.api.data.dto.Pojo";
		verify(main, getPath(SampleDtoExtendsSampleNoDto.class), expected);
	}

	/**
	 * The base interface of {@link SampleTfExtendsSampleTf} is a transformer.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testBaseClassIsStereotypeViaInterface() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(SampleTfExtendsSampleTf.class), expected);
	}

	/**
	 * The base interface of {@link SampleTfExtendsSampleNoTf} is not a
	 * transformer.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */

	/**
	 * The base interface of {@link SampleTfExtendsSampleNoTf} is not a
	 * transformer.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	public void testBaseClassIsNotStereotypeViaInterface() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String expected = "Stereotype transformer: does not implement org.apache.commons.collections4.Transformer";
		verify(main, getPath(SampleTfExtendsSampleNoTf.class), expected);
	}

	/**
	 * If the class is abstract but fulfills some condition of a stereotype, no
	 * errors occur.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testAbstractClass() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(BaseTf.class), expected);
	}

	/**
	 * Ignore all classes that ends to Test.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testTestClassNotExcluded() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(SampleTest.class), expected);
	}
	
	/**
	 * Ignore all classes that ends to Test.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testTestClassExcluded() throws Exception {
		DefaultConfiguration main = createConfig("src/test/resources/stereotypeWithExcludedClass.xml");
		final String[] expected = {"Stereotype businessservicebean: missing annotation javax.ejb.Stateless or javax.interceptor.Interceptors", "Stereotype businessservicebean: missing postfix BsBean"};
		verify(main, getPath(SampleTest.class), expected);
	}

	/**
	 * The {@link SampleBsBean} is a valid business service bean.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testBsBean() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(SampleBsBean.class), expected);
	}

	/**
	 * The interface {@link Util} is in the same package as {@link DateUtil},
	 * which is an util stereotype.
	 * 
	 * @throws Exception
	 *             in case of an unexpected test execution
	 */
	@Test
	public void testInterfaceInSamePackage() throws Exception {
		DefaultConfiguration main = createDefaultConfig();
		final String[] expected = {};
		verify(main, getPath(DateUtil.class), expected);
	}

}
