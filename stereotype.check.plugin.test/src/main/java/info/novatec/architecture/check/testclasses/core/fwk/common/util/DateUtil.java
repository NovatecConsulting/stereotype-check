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
package info.novatec.architecture.check.testclasses.core.fwk.common.util;

import info.novatec.architecture.check.testclasses.core.fwk.common.util.copy.ObjectUtil;

/**
 * Valid class that implements the interface of the stereotype and imports a
 * class that has the postfix stereotype, which is the same string as the name
 * of the implemented interface. This constellation results in a solved bug,
 * that the class does not implements the interface. To get the full qualified
 * classname of {@link Util} all imports are scanned an the ObjectUtil class was
 * found, because the match is done by import.endsWith("Util") instead of
 * import.endsWith(".Util").
 * 
 * @author Volker Koch (NovaTec Consulting GmbH)
 *
 */
public class DateUtil implements Util {

	ObjectUtil util = new ObjectUtil();
}
