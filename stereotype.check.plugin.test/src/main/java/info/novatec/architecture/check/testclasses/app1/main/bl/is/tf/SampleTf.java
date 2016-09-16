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
package info.novatec.architecture.check.testclasses.app1.main.bl.is.tf;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.collections4.Transformer;

/**
 * The subclasses of this base class does not implement the interface defined by
 * the stereotype directly. The interface is specified by this base class. If
 * the sufficient postfix of the base class is the same as the postfix of the
 * subclass, the subclass can be sure, that the base class implements the
 * interface. Otherwise the base class will be invalid.
 * 
 * @author Volker Koch (NovaTec Consulting GmbH)
 */
@ApplicationScoped
public class SampleTf implements Transformer<Object, Object> {

	@Override
	public Object transform(Object input) {
		return null;
	}

}
