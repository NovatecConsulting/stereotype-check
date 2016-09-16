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
package info.novatec.architecture.check;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import info.novatec.architecture.check.config.AnnotationConfiguration;
import info.novatec.architecture.check.config.StereotypeIdentifier;
import info.novatec.architecture.check.config.StereotypeCondition;
import info.novatec.architecture.check.config.StereotypeConfiguration;

/**
 * Validates the parsed information against the configuration
 * 
 * @author Volker Koch (volker.koch@novatec-gmbh.de)
 */
public class StereotypeCheckValidator {

	private static final Logger logger = Logger.getLogger(StereotypeCheckValidator.class.getCanonicalName());
	private StereotypeCheck check;

	/**
	 * Creates a validator belonging to the actual instance of the checker.
	 */
	StereotypeCheckValidator(StereotypeCheck check) {
		this.check = check;
	}

	/**
	 * Validates the parsed information against the configuration.
	 * 
	 * @param ast
	 *            the actual position in the parsing.
	 * @param classToCheck
	 *            the parsed information.
	 */
	void validate(DetailAST ast, ClassInfo classToCheck) {
		logger.fine("ClassInfo " + classToCheck);
		if (classToCheck.isExcluded(check.getConfig().getExcludedClasses())) {
			logger.fine("Class is a excluded by configuration.");
			return;
		}
		// check
		for (StereotypeConfiguration config : check.getConfig().getStereotypeConfig().values()) {
			boolean hasPostfix = classToCheck.hasPostfix(config.getPostfix());
			boolean hasInterface;
			boolean hasBaseClass;
			Set<String> interfaceNames = config.getInterfaceNames();
			if (classToCheck.isInterface()) {
				hasInterface = false;
				hasBaseClass = classToCheck.hasBaseClass(interfaceNames);
			} else {
				hasInterface = classToCheck.hasInterface(config.getInterfaceNames())
						|| classToCheck.extendsSameStereotype(config);
				hasBaseClass = classToCheck.hasBaseClass(config.getBaseClassNames())
						|| classToCheck.extendsSameStereotype(config);
			}
			boolean isInPackage = classToCheck.isInPackage(config.getPackageName());

			if (config.isPostfixConditionSufficent(hasPostfix)
					|| hasMinimumOneSufficientAnnotation(classToCheck, config)
					|| config.isInterfaceNameSufficent(hasInterface) || config.isBaseClassnameSufficent(hasBaseClass)
					|| config.isPackageNameSufficent(isInPackage)) {
				if (config.getPostfix() != null && !hasPostfix) {
					addError(classToCheck.getClassAst(),
							"Stereotype " + config.getId() + ": missing postfix " + config.getPostfix());
				}
				if (config.getAnnotationConfigs().size() > 0 && !hasAllAnnotations(classToCheck, config)) {
					addError(classToCheck.getClassAst(), "Stereotype " + config.getId() + ": missing annotation "
							+ config.getAnnotationNamesAsString());
				}
				if (!classToCheck.isInterface() && interfaceNames.size() > 0 && !hasInterface) {
					addError(classToCheck.getClassAst(),
							"Stereotype " + config.getId() + ": does not implement " + concatSorted(interfaceNames));
				}
				if (classToCheck.isInterface() && interfaceNames.size() > 0 && !hasBaseClass) {
					addError(classToCheck.getClassAst(),
							"Stereotype " + config.getId() + ": does not extend " + concatSorted(interfaceNames));
				}
				if (config.getBaseClassNames().size() > 0 && !hasBaseClass) {
					addError(classToCheck.getClassAst(), "Stereotype " + config.getId() + ": does not extend "
							+ concatSorted(config.getBaseClassNames()));
				}
				if (config.getPackageName() != null && !isInPackage) {
					addError(classToCheck.getClassAst(),
							"Stereotype " + config.getId() + ": is not in package " + config.getPackageName());
				}
				checkDependency(ast, classToCheck, config);
			}
		}
	}

	private boolean hasMinimumOneSufficientAnnotation(ClassInfo classToCheck, StereotypeConfiguration config) {
		boolean hasAllSufficientAnnotations = false;
		for (AnnotationConfiguration anConfig : config.getAnnotationConfigs()) {
			boolean hasSingleAnnotation = classToCheck.hasAnnotation(anConfig.getAnnotationNames());
			// you can have different annotations that can have different
			// conditions,
			// therefore you have to check if the class has minimum one
			// sufficient annotation
			hasAllSufficientAnnotations |= anConfig.isAnnotationNameSufficent(hasSingleAnnotation);
		}
		return hasAllSufficientAnnotations;
	}

	private boolean hasAllAnnotations(ClassInfo classToCheck, StereotypeConfiguration config) {
		boolean hasAllAnnotations = true;
		for (AnnotationConfiguration anConfig : config.getAnnotationConfigs()) {
			hasAllAnnotations &= classToCheck.hasAnnotation(anConfig.getAnnotationNames());
		}

		if (classToCheck.isAbstract()) {
			// annotations on abstract classes can be ignored
			hasAllAnnotations = true;
		}
		return hasAllAnnotations;
	}

	private String concatSorted(Set<String> strings) {
		List<String> sortedStrings = new ArrayList<>(strings);
		Collections.sort(sortedStrings);
		return StringUtils.join(sortedStrings, " or ");
	}

	private void checkDependency(DetailAST ast, ClassInfo classToCheck, StereotypeConfiguration config) {
		Set<StereotypeIdentifier> allowedDependencies = check.getConfig().getDependencies().get(config.getId()).getAllowedToDependencies();
		Set<StereotypeIdentifier> disallowedDependencies = new HashSet<StereotypeIdentifier>(
				check.getConfig().getStereotypeConfig().keySet());
		if (allowedDependencies != null) {
			disallowedDependencies.removeAll(allowedDependencies);
		}
		for (StereotypeIdentifier to : disallowedDependencies) {
			StereotypeConfiguration toConfig = check.getConfig().getStereotypeConfig().get(to);
			for (String importedClass : classToCheck.getImports()) {
				if ((check.getConfig().isInApplicationPackage(importedClass)
						|| check.getConfig().isPartOfAnyStereotype(importedClass)) && !toConfig.isPartOfStereotype(importedClass)) {
					if (toConfig.getPostfixCondition() == StereotypeCondition.sufficient
							&& importedClass.endsWith(toConfig.getPostfix())) {
						DetailAST importAst = classToCheck.getImportAst(importedClass);
						addError(importAst, "Disallowed dependency from stereotype " + config.getId()
								+ " to stereotype " + toConfig.getId() + ": " + importedClass);
					}
					if (toConfig.getPackageNameCondition() == StereotypeCondition.sufficient
							&& importedClass.startsWith(toConfig.getPackageName())) {
						DetailAST importAst = classToCheck.getImportAst(importedClass);
						addError(importAst, "Disallowed dependency from stereotype " + config.getId()
								+ " to stereotype " + toConfig.getId() + ": " + importedClass);
					}
				}
			}
		}
	}

	private void addError(DetailAST ast, String message, Object... parameters) {
		check.addError(ast, message, parameters);
	}
}
