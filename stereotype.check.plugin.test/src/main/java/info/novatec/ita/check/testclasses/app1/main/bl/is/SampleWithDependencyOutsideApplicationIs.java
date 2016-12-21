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
package info.novatec.ita.check.testclasses.app1.main.bl.is;

import info.novatec.ita.check.testclasses.core.fwk.common.bl.is.IntegrationService;
import info.novatec.ita.check.testclasses.core.fwk.common.bl.is.IntegrationServiceStereotype;
import info.novatec.ita.check.testoutsideapplication.jaxws.handler.WSSUsernameTokenProducer;

/**
 * This valid class has a dependency to a class, which may be a stereotype, but
 * the class is not in a package that belongs to the checked application.
 * 
 * @author Volker Koch (NovaTec Consulting GmbH)
 *
 */
@IntegrationServiceStereotype
public class SampleWithDependencyOutsideApplicationIs implements
		IntegrationService {

	public WSSUsernameTokenProducer getProducer() {
		return null;
	}
}
