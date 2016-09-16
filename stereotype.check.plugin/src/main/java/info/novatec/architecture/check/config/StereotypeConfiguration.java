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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * This class holds the configuration of a stereotype from the stereotype check
 * configuration file.
 * 
 * @author Volker Koch (volker.koch@novatec-gmbh.de)
 */
public class StereotypeConfiguration {

	/** The id of the stereotype used as reference in the dependencies */
	private StereotypeIdentifier id = null;

	/**
	 * The package name. Must be given as valid regular expression conforming
	 * with {@link Pattern}
	 */
	private String packageName = null;

	/** The condition for the package name. */
	private StereotypeCondition packageNameCondition = null;

	/** The postfix of the classname. */
	private String postfix = null;

	/** The condition for the postfix. */
	private StereotypeCondition postfixCondition;

	/** The configurations of the annotations. */
	private final Set<AnnotationConfiguration> annotationConfigs = new HashSet<>();

	/** The interfaces that are implemented by the stereotype directly. */
	private final Set<String> interfaceNames = new HashSet<>();

	/** the condition for the interfaces. */
	private StereotypeCondition interfaceNameCondition = null;

	/** The classes that a stereotype can extend from. */
	private final Set<String> baseClassNames = new HashSet<>();

	/** The condition for the baseclasses. */
	private StereotypeCondition baseClassNameCondition = null;

	/**
	 * Map containing information about which part of the central configuration
	 * file can be override by a project specific configuration file. Key is the
	 * {@link #id} of the stereotype. The value is a Map with with key name of
	 * the configuration (e.g. postfix). The value is if overrideable. If no key
	 * in this Map then it is not overrideable.
	 */
	private final Map<StereotypeIdentifier, Map<ConfigurationName, Boolean>> allowOverride = new HashMap<StereotypeIdentifier, Map<ConfigurationName, Boolean>>();

	/**
	 * @return the names of the annotations as concatenated string.
	 */
	public String getAnnotationNamesAsString() {
		List<String> annotationNames = new ArrayList<>();
		for (AnnotationConfiguration annotationConfiguration : getAnnotationConfigs()) {
			annotationNames.addAll(annotationConfiguration.getAnnotationNames());
		}
		Collections.sort(annotationNames);
		return StringUtils.join(annotationNames, " or ");
	}

	/**
	 * Add a new config for annotations.
	 * 
	 * @param config
	 *            new annotation config.
	 */
	void addAnnotationConfig(AnnotationConfiguration config) {
		getAnnotationConfigs().add(config);
	}

	/**
	 * Add an full qualified interface name
	 * 
	 * @param interfaceName
	 *            the interface name.
	 */
	void addInterfaceName(String interfaceName) {
		if (interfaceName != null) {
			this.getInterfaceNames().add(interfaceName);
		}
	}

	/**
	 * Add an full qualified base class name
	 * 
	 * @param baseClassName
	 *            the base class name.
	 */
	void addBaseClassName(String baseClassName) {
		if (baseClassName != null) {
			this.baseClassNames.add(baseClassName);
		}
	}

	/**
	 * Check if any of the conditions is sufficient.
	 * 
	 * @return true if any of the conditions is sufficient.
	 */
	public boolean hasSufficientCondition() {
		boolean hasSufficientAnnotation = false;
		for (AnnotationConfiguration annotationConfiguration : getAnnotationConfigs()) {
			hasSufficientAnnotation |= annotationConfiguration
					.getAnnotationNameCondition() == StereotypeCondition.sufficient;
		}
		return hasSufficientAnnotation || StereotypeCondition.sufficient == getBaseClassNameCondition()
				|| StereotypeCondition.sufficient == getInterfaceNameCondition()
				|| StereotypeCondition.sufficient == getPackageNameCondition()
				|| StereotypeCondition.sufficient == getPostfixCondition();
	}

	/**
	 * @param hasPostfix
	 *            true if the class has the postfix
	 * @return true if the class has the postfix and die condition is
	 *         sufficient.
	 */
	public boolean isPostfixConditionSufficent(boolean hasPostfix) {
		if (getPostfixCondition() != null) {
			return getPostfixCondition().isSufficent(hasPostfix);
		}
		return false;
	}

	/**
	 * @param hasPostfix
	 *            true if the class has the interface name
	 * @return true if the class has the interface name and die condition is
	 *         sufficient.
	 */
	public boolean isInterfaceNameSufficent(boolean hasInterface) {
		if (getInterfaceNameCondition() != null) {
			return getInterfaceNameCondition().isSufficent(hasInterface);
		}
		return false;
	}

	/**
	 * @param hasPostfix
	 *            true if the class has the base class
	 * @return true if the class has the base class and die condition is
	 *         sufficient.
	 */
	public boolean isBaseClassnameSufficent(boolean hasBaseClass) {
		if (getBaseClassNameCondition() != null) {
			return getBaseClassNameCondition().isSufficent(hasBaseClass);
		}
		return false;
	}

	/**
	 * @param hasPostfix
	 *            true if the class has the package
	 * @return true if the class has the package and die condition is
	 *         sufficient.
	 */
	public boolean isPackageNameSufficent(boolean isInPackage) {
		if (getPackageNameCondition() != null) {
			return getPackageNameCondition().isSufficent(isInPackage);
		}
		return false;
	}

	/**
	 * Is the classname an annotation or interface or baseclass referenced in
	 * this stereotype.
	 * 
	 * @param classNameWithPackage
	 *            the full qualified classname
	 * @return true if the classname an annotation or interface or baseclass
	 *         referenced in this stereotype.
	 */
	public boolean isPartOfStereotype(String classNameWithPackage) {
		for (AnnotationConfiguration anConfig : getAnnotationConfigs()) {
			if (anConfig.getAnnotationNames().contains(classNameWithPackage)) {
				return true;
			}
		}
		if (getBaseClassNames().contains(classNameWithPackage)) {
			return true;
		}
		if (getInterfaceNames().contains(classNameWithPackage)) {
			return true;
		}
		return false;
	}

	/**
	 * Check if override is allowed
	 * 
	 * @param configId
	 *            The {@link #id} of a stereotype.
	 * @param cfgName
	 *            The name of the configuration that can be overriden (e.g.
	 *            postfix)
	 * @return true if an override is allowed for the given stereotype at the
	 *         given part of the configuration.
	 */
	public Boolean getAllowOverride(StereotypeIdentifier configId, ConfigurationName cfgName) {
		Map<ConfigurationName, Boolean> map = this.allowOverride.get(configId);
		if (map != null) {
			if (map.get(cfgName) != null) {
				return map.get(cfgName);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Set that override is allowed.
	 * 
	 * @param configId
	 *            The {@link #id} of a stereotype to be override.
	 * @param cfgName
	 *            The name of the configuration that can be overriden (e.g.
	 *            postfix)
	 * @param allowOverride
	 *            if override is allowed.
	 * @throws IllegalArgumentException
	 *             if there is already a override definition for the given
	 *             configId.
	 */
	void setAllowOverride(StereotypeIdentifier configId, ConfigurationName cfgName, Boolean allowOverride)
			throws IllegalArgumentException {
		if (this.allowOverride.containsKey(configId)) {
			throw new IllegalArgumentException(cfgName + " already found for " + configId + " error!");
		} else if (!this.allowOverride.containsKey(configId)) {
			Map<ConfigurationName, Boolean> tempMap = new HashMap<ConfigurationName, Boolean>();
			tempMap.put(cfgName, allowOverride);
			this.allowOverride.put(configId, tempMap);
		}
	}

	/**
	 * @return the id
	 */
	public StereotypeIdentifier getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	void setId(StereotypeIdentifier id) {
		this.id = id;
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @param packageName
	 *            the packageName to set
	 */
	void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * @return the packageNameCondition
	 */
	public StereotypeCondition getPackageNameCondition() {
		return packageNameCondition;
	}

	/**
	 * @param packageNameCondition
	 *            the packageNameCondition to set
	 */
	void setPackageNameCondition(StereotypeCondition packageNameCondition) {
		this.packageNameCondition = packageNameCondition;
	}

	/**
	 * @return the postfix
	 */
	public String getPostfix() {
		return postfix;
	}

	/**
	 * @param postfix
	 *            the postfix to set
	 */
	void setPostfix(String postfix) {
		this.postfix = postfix;
	}

	/**
	 * @return the postfixCondition
	 */
	public StereotypeCondition getPostfixCondition() {
		return postfixCondition;
	}

	/**
	 * @param postfixCondition
	 *            the postfixCondition to set
	 */
	void setPostfixCondition(StereotypeCondition postfixCondition) {
		this.postfixCondition = postfixCondition;
	}

	/**
	 * @return the annotationConfigs
	 */
	public Set<AnnotationConfiguration> getAnnotationConfigs() {
		return annotationConfigs;
	}

	/**
	 * @return the interfaceNames
	 */
	public Set<String> getInterfaceNames() {
		return interfaceNames;
	}

	/**
	 * @return the interfaceNameCondition
	 */
	public StereotypeCondition getInterfaceNameCondition() {
		return interfaceNameCondition;
	}

	/**
	 * @param interfaceNameCondition
	 *            the interfaceNameCondition to set
	 */
	void setInterfaceNameCondition(StereotypeCondition interfaceNameCondition) {
		this.interfaceNameCondition = interfaceNameCondition;
	}

	/**
	 * @return the baseClassNames
	 */
	public Set<String> getBaseClassNames() {
		return baseClassNames;
	}

	/**
	 * @return the baseclassnameCondition
	 */
	public StereotypeCondition getBaseClassNameCondition() {
		return baseClassNameCondition;
	}

	/**
	 * @param baseClassNameCondition
	 *            the baseclassnameCondition to set
	 */
	void setBaseClassNameCondition(StereotypeCondition baseClassNameCondition) {
		this.baseClassNameCondition = baseClassNameCondition;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "StereotypeConfiguration [id=" + id + ", packageName=" + packageName + ", packageNameCondition="
				+ packageNameCondition + ", postfix=" + postfix + ", postfixCondition=" + postfixCondition
				+ ", annotationConfigs=" + annotationConfigs + ", interfaceNames=" + interfaceNames
				+ ", interfaceNameCondition=" + interfaceNameCondition + ", baseClassNames=" + baseClassNames
				+ ", baseClassNameCondition=" + baseClassNameCondition + ", allowOverride=" + allowOverride + "]";
	}

}
