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
package info.novatec.ita.check;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

import info.novatec.ita.check.config.StereotypeConfiguration;

/**
 * Contains Information about the parsed class, that is used by the
 * {@link StereotypeCheck}.
 * 
 * @author Volker Koch (volker.koch@novatec-gmbh.de)
 */
class ClassInfo {

	/** The name of the class (without package). */
	private String className;

	/** The package of the class. */
	private String packageName;

	/**
	 * The explicit imports in the import section of the class.
	 * <p/>
	 * The key is the name of the import.
	 * <p/>
	 * The value is the position in the class file used for the error marker in
	 * the IDE.
	 */
	private Map<String, DetailAST> imports = new HashMap<String, DetailAST>();
	/**
	 * The class level annotations as defined in the java-file. May or may not
	 * contain packagenames.
	 */
	private List<String> annotations = new ArrayList<String>();

	/**
	 * Annotations with full qualified names. Derived from {@link #imports} and
	 * {@link #annotations}.
	 */
	private List<String> annotationsWithPackage = null;

	/**
	 * The names of the interface the class directly implements as defined in
	 * the java-file. May or may not contain packagenames.
	 */
	private List<String> interfaces = new ArrayList<String>();
	/**
	 * Interfaces with full qualified names. Derived from {@link #interfaces}
	 * and {@link #annotations}.
	 */
	private List<String> interfacesWithPackage = null;

	/**
	 * The names of the interface the class directly implements as defined in
	 * the java-file. May or may not contain packagenames.
	 */
	private List<String> baseClasses = new ArrayList<String>();
	/**
	 * Base classes with full qualified names. Derived from {@link #imports} and
	 * {@link #baseClasses}.
	 */
	private List<String> baseClassesWithPackage = null;

	/**
	 * The position in the java-file for the class definition used for the error
	 * marker in the IDE.
	 */
	private DetailAST classAst;

	/** Is the class an interface. */
	private boolean isInterface = false;

	/** Is the class abstract. */
	private boolean isAbstract;

	/**
	 * Get the annotations as full qualified class names.
	 * 
	 * @return The annotations as full qualified class names.
	 */
	private List<String> getAnnotationsWithPackage() {
		if (annotationsWithPackage == null) {
			annotationsWithPackage = buildPackageNames(annotations);
		}
		return annotationsWithPackage;
	}

	/**
	 * Get the interfaces as full qualified class names.
	 * 
	 * @return The interfaces as full qualified class names.
	 */
	private List<String> getInterfacesWithPackage() {
		if (interfacesWithPackage == null) {
			interfacesWithPackage = buildPackageNames(interfaces);
		}
		return interfacesWithPackage;
	}

	/**
	 * Get the baseClasses as full qualified class names.
	 * 
	 * @return The baseClasses as full qualified class names.
	 */
	private List<String> getBaseClassesWithPackage() {
		if (baseClassesWithPackage == null) {
			baseClassesWithPackage = buildPackageNames(baseClasses);
		}
		return baseClassesWithPackage;
	}

	private List<String> buildPackageNames(Collection<String> typenames) {
		List<String> typesWithPackage = new ArrayList<String>();
		for (String name : typenames) {
			if (name.contains(".")) {
				// If the typename has a dot we assume, that it is full
				// qualified.
				typesWithPackage.add(name);
			} else {
				// search for the Class in the imports
				boolean hasImport = false;
				for (String importedClass : imports.keySet()) {
					if (importedClass.endsWith("." + name)) {
						typesWithPackage.add(importedClass);
						hasImport = true;
						break;
					}
				}
				if (!hasImport) {
					// If the import is missing, the class must be in the
					// same package
					typesWithPackage.add(packageName + "." + name);
				}
			}
		}
		return typesWithPackage;
	}

	/**
	 * Does the className ends with the postfix
	 * 
	 * @param postfix
	 *            the postfix
	 * @return true if the className ends with the postfix
	 */
	public boolean hasPostfix(String postfix) {
		if (className != null && postfix != null) {
			return className.endsWith(postfix);
		}
		return false;
	}

	/**
	 * Is the class annotated with the given annotation
	 * 
	 * @param annotationNames
	 *            the annotation
	 * @return true if annotated
	 */
	public boolean hasAnnotation(Set<String> annotationNames) {
		boolean result = false;
		for (String annotationName : annotationNames) {
			result |= this.getAnnotationsWithPackage().contains(annotationName);
		}
		return result;
	}

	/**
	 * Does the class directly implements one of the interfaces
	 * 
	 * @param interfaceNames
	 *            the interfaces
	 * @return true if one of the given interfaces is implemented by this class
	 *         directly
	 */
	public boolean hasInterface(Set<String> interfaceNames) {
		boolean result = false;
		for (String interfaceName : interfaceNames) {
			result |= this.getInterfacesWithPackage().contains(interfaceName);
		}
		return result;
	}

	/**
	 * Does the class directly extend one of the baseclasses
	 * 
	 * @param baseclassNames
	 *            the baseclasses
	 * @return true if one of the given baseclasses is subclassed by this class
	 *         directly
	 */
	public boolean hasBaseClass(Set<String> baseclassNames) {
		boolean result = false;
		for (String baseclassName : baseclassNames) {
			result |= this.getBaseClassesWithPackage().contains(baseclassName);
		}
		return result;
	}

	/**
	 * Check by postfix comparision if the baseclass is from the same stereotype
	 * of this class. This can only be checked if the condition of the postfix
	 * is sufficient.
	 * 
	 * @param config
	 *            the stereotype configuration to check
	 * @return true if class and baseClass has the same stereotype
	 */
	public boolean extendsSameStereotype(StereotypeConfiguration config) {
		boolean result = false;
		if (config.getPostfix() != null && config.isPostfixConditionSufficent(true)) {
			for (String baseclassName : getBaseClasses()) {
				if (baseclassName != null) {
					result |= baseclassName.endsWith(config.getPostfix());
				}
			}
		}
		return result;
	}
	

	/**
	 * Checks if the class matches one of the regular expressions of the excluded classes.
	 * @param excludedClasses regular expressions of the excluded classes.
	 * @return true if it matches
	 */
	public boolean isExcluded(Set<Pattern> excludedClasses) {
		for (Pattern pattern : excludedClasses) {
			if (pattern.matcher(packageName+"."+className).matches()){
				return true;
			}
		}
		return false;
	}

	/**
	 * Is the class in the given package.
	 * 
	 * @param packageName
	 *            the package
	 * @return true if the class in the given package.
	 */
	public boolean isInPackage(String packageName) {
		return packageName != null && this.packageName.matches(packageName);
	}

	/**
	 * Add base class
	 * 
	 * @param baseClass
	 *            base class
	 */
	public void addBaseClass(String baseClass) {
		baseClasses.add(baseClass);
	}

	/**
	 * Add annotation
	 * 
	 * @param annotation
	 *            annotation
	 */
	public void addAnnotation(String annotation) {
		this.annotations.add(annotation);
	}

	/**
	 * Add import
	 * 
	 * @param currentImport
	 *            import
	 * @param ast
	 *            position in the java-file used for error markers in the IDE.
	 */
	public void addImport(String currentImport, DetailAST ast) {
		imports.put(currentImport, ast);
	}

	/**
	 * Add import
	 * 
	 * @param currentInterface
	 *            interface
	 */
	public void addInterface(String currentInterface) {
		interfaces.add(currentInterface);
	}

	/**
	 * Check if the class has a import for the type.
	 * 
	 * @param typeName
	 *            Type.
	 * @return true, if the class has a import for the type.
	 */
	public boolean hasImport(String typeName) {
		String toCheck = "." + typeName;
		for (String importClass : imports.keySet()) {
			if (importClass.endsWith(toCheck)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the position in java-file for the given import
	 *
	 * @param imp
	 *            the import.
	 * @return the psotion in java-file
	 */
	public DetailAST getImportAst(String imp) {
		return imports.get(imp);
	}

	/**
	 * @return true if the class is an interface.
	 */
	public boolean isInterface() {
		return isInterface;
	}

	/**
	 * @return true if the class is abstract.
	 */
	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	/**
	 * Sets {@link #interfaceName}.
	 *
	 * @param interfaceName
	 *            New value for {@link #interfaceName}
	 * @param ast
	 *            position in the java-file used for error markers in the IDE.
	 */
	public void setInterfaceName(String interfaceName, DetailAST ast) {
		setClassName(interfaceName, ast);
		this.isInterface = true;
	}

	/**
	 * Sets {@link #className}.
	 *
	 * @param className
	 *            New value for {@link #className}
	 * @param ast
	 *            position in the java-file used for error markers in the IDE.
	 */
	public void setClassName(String className, DetailAST ast) {
		this.className = className;
		this.classAst = ast;
	}

	/**
	 * Sets {@link #packageName}.
	 *
	 * @param packageName
	 *            New value for {@link #packageName}
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * Gets {@link #packageName}.
	 *
	 * @return {@link #packageName}
	 */
	public String getPackageName() {
		return this.packageName;
	}
	
	/**
	 * Get the class name including the package. 
	 * @return The class name including the package. 
	 */
	public String getFullClassName(){
		return StringUtils.defaultString(packageName, "")+"."+className;
	}

	/**
	 * Gets {@link #annotations}.
	 *
	 * @return {@link #annotations}
	 */
	public List<String> getAnnotations() {
		return this.annotations;
	}

	/**
	 * Gets {@link #interfaces}.
	 *
	 * @return {@link #interfaces}
	 */
	public List<String> getInterfaces() {
		return interfaces;
	}

	/**
	 * Gets {@link #imports}.
	 *
	 * @return {@link #imports}
	 */
	public Set<String> getImports() {
		return imports.keySet();
	}

	/**
	 * Gets {@link #className}.
	 *
	 * @return {@link #className}
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * Gets {@link #baseClasses}.
	 *
	 * @return {@link #baseClasses}
	 */
	public List<String> getBaseClasses() {
		return baseClasses;
	}

	/**
	 * Gets {@link #annotations}.
	 *
	 * @return {@link #annotations}
	 */
	public DetailAST getClassAst() {
		return classAst;
	}

	@Override
	public String toString() {
		return "ClassInfo [className=" + className + ", packageName=" + packageName + ", annotations=" + annotations
				+ ", imports=" + imports + ", interfaces=" + interfaces + ", interfacesWithPackage="
				+ interfacesWithPackage + ", baseClasses=" + baseClasses + ", baseClassesWithPackage="
				+ baseClassesWithPackage + ", annotationsWithPackage=" + annotationsWithPackage + ", classAst="
				+ classAst + ", isInterface=" + isInterface + ", isAbstract=" + isAbstract + "]";
	}


}
