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

/**
 * All possible conditions a part (annotation, postfix etc.) of a stereotype
 * definition can have. If a part of a stereotype definition is marked as
 * sufficient, this means that if a class conforms to this part, the class
 * belongs to the stereotype. In this case the class must have all other parts.
 * 
 * E.g. If a class has a sufficient postfix Dto and the stereotype has necessary
 * interface Pojo, the class must implement this interface. If a class
 * implements the interface Pojo but has a postfix Other the class does not
 * belong to this stereotype.
 * 
 * A stereotype definition must have one sufficient condition and one of the
 * sufficient conditions must be the postfix or package-name. This is necessary
 * to check the dependencies by analyzing the full qualified classname of an
 * import.
 * 
 * @author Volker Koch (volker.koch@novatec-gmbh.de)
 *
 */
public enum StereotypeCondition {

	sufficient, necessary;

	/**
	 * @param condition
	 *            the condition
	 * @return true if the condition is sufficient and the condition is true.
	 */
	public boolean isSufficent(boolean condition) {
		if (this == sufficient) {
			return condition;
		}
		return false;
	}
}
