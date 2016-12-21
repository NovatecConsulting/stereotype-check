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

/** Primary key of a {@link StereotypeConfiguration}. */
public class StereotypeIdentifier {

	/** The ID. Can be null if not read from configuration file */
	private String id;

	/**
	 * Creates a new primary key for a {@link StereotypeConfiguration}.
	 * 
	 * @param id
	 *            The id.
	 * @return The new key.
	 */
	public static StereotypeIdentifier of(String id) {
		return new StereotypeIdentifier(id);
	}

	private StereotypeIdentifier(String id) {
		this.id = id;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof StereotypeIdentifier)) {
			return false;
		}
		return id != null && id.equals(((StereotypeIdentifier) obj).id);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return id;
	}
}