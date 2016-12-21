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
 * The possible elements of a {@link StereotypeConfiguration} are defined by the
 * checkstyle-stereotype.xsd as subelement of the stereotype tag.
 */
public enum ConfigurationName {
	postfix, baseclassname, baseclass, interfacename, INTERFACE, ANNOTATIONNAME, ANNOTATION;
}