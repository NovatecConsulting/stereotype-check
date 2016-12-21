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
package info.novatec.ita.check.testclasses.app1.main.ul.wt.test;

import javax.inject.Inject;

import info.novatec.ita.check.testclasses.app1.main.bl.is.tf.SampleTf;
import info.novatec.ita.check.testclasses.core.fwk.common.ul.View;
import info.novatec.ita.check.testclasses.core.fwk.common.ul.ViewStereotype;

/**
 * This view injects a transformer which is not allowed in the central file
 * stereotype-dependency-allowed-in-override-fail.xml but the project
 * specific file stereotype-dependency-allowed-in-override-fail-override.xml
 * allows this. This configuration is not allowed.
 * 
 * @author Volker Koch (NovaTec Consulting GmbH)
 */
@ViewStereotype
public class SampleContainsTransformerView implements View {

	@Inject
	SampleTf sampleTf;
}
