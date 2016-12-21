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

import java.util.HashSet;
import java.util.Set;

/**
 * This class holds the configuration of the annotations for a stereotype. One
 * (or more) of the annotations in this configuration must be used in the class
 * that belongs to a stereotype.
 * 
 * @author Volker Koch (volker.koch@novatec-gmbh.de)
 */
public class AnnotationConfiguration {

	/** The full qualified names of the annotations a stereotype must have. */
	private Set<String> annotationNames = new HashSet<>();

	/** Are the annotations sufficient or necessary for the stereotype. */
	private StereotypeCondition annotationNameCondition;

	/**
	 * @return The full qualified names of the annotations a stereotype must
	 *         have.
	 */
	public Set<String> getAnnotationNames() {
		return annotationNames;
	}

	/**
	 * Add a full qualified name of an annotation a stereotype must have.
	 * 
	 * @param annotationName
	 *            full qualified name of an annotation, if null nothing is
	 *            added.
	 */
	void addAnnotationName(String annotationName) {
		if (annotationName != null) {
			this.annotationNames.add(annotationName);
		}
	}

	/**
	 * @return the condition for the annotations.
	 */
	public StereotypeCondition getAnnotationNameCondition() {
		return annotationNameCondition;
	}

	/**
	 * Set the condition of the annotations.
	 * 
	 * @param annotationNameCondition
	 *            the condition
	 */
	void setAnnotationnameCondition(StereotypeCondition annotationNameCondition) {
		this.annotationNameCondition = annotationNameCondition;
	}

	/**
	 * Check if the annotation is present is in the class, is the annotation
	 * sufficient.
	 * 
	 * @param hasAnnotation
	 *            is the annotation present
	 * @return true if the condition is sufficient and the annotation is
	 *         present.
	 */
	public boolean isAnnotationNameSufficent(boolean hasAnnotation) {
		if (annotationNameCondition != null) {
			return annotationNameCondition.isSufficent(hasAnnotation);
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotationNameCondition == null) ? 0 : annotationNameCondition.hashCode());
		result = prime * result + ((annotationNames == null) ? 0 : annotationNames.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnnotationConfiguration other = (AnnotationConfiguration) obj;
		if (annotationNameCondition != other.annotationNameCondition)
			return false;
		if (annotationNames == null) {
			if (other.annotationNames != null)
				return false;
		} else if (!annotationNames.equals(other.annotationNames))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AnnotationConfiguration [annotationNames=" + annotationNames + ", annotationNameCondition="
				+ annotationNameCondition + "]";
	}

}
