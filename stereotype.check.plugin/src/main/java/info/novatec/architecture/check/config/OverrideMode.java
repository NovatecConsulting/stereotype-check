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

/**
 * If an annotation or a base class or an interface is marked with allowoveride in the central
 * configuration file, a project specific configuration file can define how
 * the override is done.
 * 
 * @author Volker Koch (volker.koch@novatec-gmbh.de)
 *
 */
public enum OverrideMode {
	/**
	 * The original annotation is ignored and only the annotations from the
	 * local file are used.
	 */
	replace,
	/** the original annotation must not be defined again in the local file. */
	extend;

	/**
	 * Get the {@link OverrideMode} from its name. If the name is null the
	 * {@link OverrideMode#replace} is returned.
	 * 
	 * @param name
	 *            The name of the {@link OverrideMode}.
	 * @return An {@link OverrideMode}.
	 */
	public static OverrideMode valueOfWithDefault(String name) {
		if (name != null) {
			return valueOf(name);
		}
		return replace;
	}
}
