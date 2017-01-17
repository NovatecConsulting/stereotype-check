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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

/**
 * This class reads the configuration of the stereotype check from a
 * configuration file. The schema is defined by the checkstyle-stereotype.xsd.
 * In addition to the checks done by validating against the XSD, this class
 * checks if there is a cycle in the dependencies.
 * 
 * @author Volker Koch (volker.koch@novatec-gmbh.de)
 *
 */
public class StereotypeCheckReader {

	private static final String CHECKSTYLE_STEREOTYPE_XSD = "checkstyle-stereotype.xsd";
	private static final String CHECKSTYLE_STEREOTYPE_OVERRIDE_XSD = "checkstyle-stereotype-override.xsd";
	private static final Logger logger = Logger.getLogger(StereotypeCheckReader.class.getCanonicalName());

	/**
	 * This delegate reader is used to do validation and reading the
	 * configuration in one step.
	 * 
	 * @author Volker Koch (volker.koch@novatec-gmbh.de)
	 *
	 */
	private static final class StereotypeCheckConfigurationReader extends StreamReaderDelegate {

		/** XML-TAG allowoverride */
		private static final String ALLOWOVERRIDE = "allowoverride";
		/** The names of all packages belonging to the application. */
		private Set<String> applicationPackageNames = new HashSet<String>();
		/**
		 * The set of regular expressions of classes that are excluded from the
		 * check.
		 */
		private final Set<Pattern> excludedClasses = new HashSet<>();
		/** The dependencies. */
		private Map<StereotypeIdentifier, DependencyConfiguration> dependencies = new HashMap<>();
		/** The stereotypes. */
		private Map<StereotypeIdentifier, StereotypeConfiguration> configs = new HashMap<>();
		/** Temporary field holding the actual read stereotype. */
		private StereotypeConfiguration config = null;
		/**
		 * Temporary field holding the additional stereotype config if there is
		 * one.
		 */
		private StereotypeConfiguration additionCfg = null;
		/** Temporary field holding the actual read annotations. */
		private AnnotationConfiguration annotationConfig = null;
		/** The additional config previously read. */
		private StereotypeCheckConfiguration additionalCheckCfg;
		private Boolean cfgIsOverridable;

		/**
		 * Creates a delegate reader to to reading and validation in one step.
		 * 
		 * @param reader
		 *            The original reader
		 * @param additionalCheckCfg
		 *            The additional config previously read.
		 */
		private StereotypeCheckConfigurationReader(XMLStreamReader reader,
				StereotypeCheckConfiguration additionalCheckCfg) {
			super(reader);
			this.additionalCheckCfg = additionalCheckCfg;
		}

		/** {@inheritDoc} */
		@Override
		public int next() throws XMLStreamException {
			int event = super.next();

			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				String localName = getLocalName();
				if ("applicationpackage".equals(localName)) {
					addApplicationPackage();
				} else if ("dependency".equals(localName)) {
					addDependency();
				} else if ("stereotype".equals(localName)) {
					createStereotype();
				} else if ("postfix".equals(localName)) {
					addPostfix();
				} else if ("package".equals(localName)) {
					addPackageName();
				} else if ("annotation".equals(localName)) {
					createAnnotation();
				} else if ("annotationname".equals(localName)) {
					addAnnotationName();
				} else if ("interface".equals(localName)) {
					addInterface();
				} else if ("interfacename".equals(localName)) {
					addInterfaceName();
				} else if ("baseclass".equals(localName)) {
					addBaseClass();
				} else if ("baseclassname".equals(localName)) {
					addBaseClassName();
				} else if ("excludedclasses".equals(localName)) {
					addExcludedClasses();
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				String localName2 = getLocalName();
				if ("stereotype".equals(localName2)) {
					addStereotype();
				} else if ("annotation".equals(localName2)) {
					addAnnotation();
				} else if ("interface".equals(localName2) || "baseclass".equals(localName2)) {
					cfgIsOverridable = false;
				} else if ("stereotypes".equals(localName2)) {
					mergeDependencies();
					checkDependencyCycle();
				}
				break;
			}
			return event;
		}

		public void mergeDependencies() {
			if (additionalCheckCfg != null) {
				for (StereotypeIdentifier id : additionalCheckCfg.getDependencies().keySet()) {
					DependencyConfiguration dependencyConfiguration = dependencies.get(id);
					DependencyConfiguration dependencyConfigurationAdditional = additionalCheckCfg.getDependencies()
							.get(id);
					dependencyConfiguration.merge(dependencyConfigurationAdditional);
				}
			}
		}

		private void addBaseClassName() {
			if (additionCfg == null || !cfgIsOverridable
					|| additionCfg.getOverrideMode(ConfigurationName.baseclass) == OverrideMode.extend) {
				config.addBaseClassName(getAttributeValue("name"));
			}
		}

		private void addBaseClass() {
			cfgIsOverridable = cfgIsOverridable(getAttributeValue(ALLOWOVERRIDE), ConfigurationName.baseclass,
					config);
			if (additionCfg == null) {
				OverrideMode overrideMode = OverrideMode.valueOfWithDefault(getAttributeValue("overridemode"));
				config.setOverrideMode(ConfigurationName.baseclass, overrideMode);
			}
			if (additionCfg != null && cfgIsOverridable) {
				config.setBaseClassNameCondition(additionCfg.getBaseClassNameCondition());
				config.getBaseClassNames().addAll(additionCfg.getBaseClassNames());
				if (additionCfg.getOverrideMode(ConfigurationName.baseclass) == OverrideMode.extend) {
					config.addBaseClassName(getAttributeValue("name"));
				}
			} else {
				config.addBaseClassName(getAttributeValue("name"));
				config.setBaseClassNameCondition(StereotypeCondition.valueOf(getAttributeValue("condition")));
			}
		}

		private void addInterfaceName() {
			if (additionCfg == null || !cfgIsOverridable
					|| additionCfg.getOverrideMode(ConfigurationName.INTERFACE) == OverrideMode.extend) {
				config.addInterfaceName(getAttributeValue("name"));
			}
		}

		private void addInterface() {
			cfgIsOverridable = cfgIsOverridable(getAttributeValue(ALLOWOVERRIDE), ConfigurationName.INTERFACE,
					config);
			if (additionCfg == null) {
				OverrideMode overrideMode = OverrideMode.valueOfWithDefault(getAttributeValue("overridemode"));
				config.setOverrideMode(ConfigurationName.INTERFACE, overrideMode);
			}
			if (additionCfg != null && cfgIsOverridable) {
				config.setInterfaceNameCondition(additionCfg.getInterfaceNameCondition());
				config.getInterfaceNames().addAll(additionCfg.getInterfaceNames());
				if (additionCfg.getOverrideMode(ConfigurationName.INTERFACE) == OverrideMode.extend) {
					config.addInterfaceName(getAttributeValue("name"));
				}
			} else {
				config.addInterfaceName(getAttributeValue("name"));
				config.setInterfaceNameCondition(StereotypeCondition.valueOf(getAttributeValue("condition")));
			}
		}

		private void addAnnotationName() {
			if (additionCfg == null || !cfgIsOverridable) {
				annotationConfig.addAnnotationName(getAttributeValue("name"));
			}
		}

		private void createAnnotation() {
			annotationConfig = new AnnotationConfiguration();
			cfgIsOverridable = cfgIsOverridable(getAttributeValue(ALLOWOVERRIDE), ConfigurationName.ANNOTATION,
					config);
			if (additionCfg != null && cfgIsOverridable) {
				for (AnnotationConfiguration annotationCfg : additionCfg.getAnnotationConfigs()) {
					annotationConfig.setAnnotationnameCondition(annotationCfg.getAnnotationNameCondition());
					annotationConfig.getAnnotationNames().addAll(annotationCfg.getAnnotationNames());
				}
			} else {
				annotationConfig.addAnnotationName(getAttributeValue("name"));
				annotationConfig
						.setAnnotationnameCondition(StereotypeCondition.valueOf(getAttributeValue("condition")));
			}
		}

		private void addAnnotation() {
			config.addAnnotationConfig(annotationConfig);
			annotationConfig = null;
			cfgIsOverridable = null;
		}

		private void addStereotype() {
			if (config.getId() == null) {
				throw new IllegalArgumentException("No id defined for " + config.getId() + ": " + location(this));
			}
			if (!config.hasSufficientCondition() && additionalCheckCfg == null) {
				throw new IllegalArgumentException("There must be one sufficient condition to idenitfy the stereotype: "
						+ config.getId() + ": " + location(this));
			}
			configs.put(config.getId(), config);
			if (dependencies.get(config.getId()) == null) {
				dependencies.put(config.getId(), new DependencyConfiguration(config.getId()));
			}
			config = null;
		}

		private void addPackageName() {
			config.setPackageName(getAttributeValue("name"));
			config.setPackageNameCondition(StereotypeCondition.valueOf(getAttributeValue("condition")));
		}

		private void addPostfix() {
			config.setPostfix(getAttributeValue("name"));
			config.setPostfixCondition(StereotypeCondition.valueOf(getAttributeValue("condition")));
		}

		private void createStereotype() {
			config = new StereotypeConfiguration();
			config.setId(StereotypeIdentifier.of(getAttributeValue("id")));
			if (additionalCheckCfg != null) {
				additionCfg = additionalCheckCfg.getStereotypeConfig().get(config.getId());
			}
		}

		private void addDependency() {
			StereotypeIdentifier from = StereotypeIdentifier.of(getAttributeValue("from"));
			StereotypeIdentifier to = StereotypeIdentifier.of(getAttributeValue("to"));
			String allowedString = getAttributeValue( "allowed");

			DependencyConfiguration dependencyConfig = dependencies.get(from);
			if (dependencyConfig == null) {
				dependencyConfig = new DependencyConfiguration(from);
				dependencies.put(from, dependencyConfig);
			}
			dependencyConfig.addTo(to, allowedString);
		}

		private void addApplicationPackage() {
			applicationPackageNames.add(getAttributeValue("name"));
		}

		private void addExcludedClasses() {
			String regexp = getAttributeValue("regexp");
			excludedClasses.add(Pattern.compile(regexp));
		}

		/** Checks that there is no cycle over all depenencies. */
		private void checkDependencyCycle() {
			for (StereotypeIdentifier from : dependencies.keySet()) {
				List<StereotypeIdentifier> cycle = new ArrayList<>();
				checkDependencyCycleRecursive(cycle, from);
			}

		}

		/** Helper method to check if there is a dependency cycle. */
		private void checkDependencyCycleRecursive(List<StereotypeIdentifier> cycle, StereotypeIdentifier from) {
			DependencyConfiguration dependencyConfiguration = dependencies.get(from);
			if (dependencyConfiguration != null) {
				for (StereotypeIdentifier toDependency : dependencyConfiguration.getAllowedToDependencies()) {
					if (!toDependency.equals(from)) {
						List<StereotypeIdentifier> cycleInternal = new ArrayList<>(cycle);
						cycleInternal.add(from);
						if (cycle.contains(toDependency)) {
							throw new IllegalArgumentException("There is a cycle in dependency-configuration: "
									+ StringUtils.join(cycleInternal, ", "));
						}
						checkDependencyCycleRecursive(cycleInternal, toDependency);
					}
				}
			}
		}

		/**
		 * Helper method to get the line and column of the actual position in
		 * the file.
		 */
		private String location(XMLStreamReader reader) {
			Location location = reader.getLocation();
			return location != null ? location.getLineNumber() + ":" + location.getColumnNumber() : "";
		}

		private String getAttributeValue(String localName) {
			return getAttributeValue(null, localName);
		}

		/**
		 * Check whether a certain configuration element is overrideable or not
		 * e.g. the stereotype entity element
		 * 
		 * @param attrValue
		 *            the value of the allowoverride attribute
		 * @param cfgName
		 *            the name of the configuration
		 * @param config
		 *            the stereotype configuration
		 * @return whether is overrideable or not
		 * @throws IllegalArgumentException
		 */
		private Boolean cfgIsOverridable(String attrValue, ConfigurationName cfgName, StereotypeConfiguration config)
				throws IllegalArgumentException {
			Boolean cfgOverridable = config.getAllowOverride(cfgName);
			if (!cfgOverridable && attrValue != null) {
				config.setAllowOverride(cfgName, Boolean.valueOf(attrValue));
				return true;
			}
			return cfgOverridable;
		}

		/**
		 * @return the configuration read from the file.
		 */
		public StereotypeCheckConfiguration getConfig() {
			return new StereotypeCheckConfiguration(applicationPackageNames, excludedClasses, dependencies, configs);
		}
	}

	/**
	 * Read and validate the given file to a configuration for stereotype check.
	 * 
	 * @param file
	 *            The file to read.
	 * @param additionalCheckCfg
	 *            a previously read configuration which may override parts of
	 *            the configuration read by the file.
	 * @return the configuration.
	 * @throws XMLStreamException
	 * @throws IllegalArgumentException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static StereotypeCheckConfiguration read(File file, String checkstyleStereotypeXsd,
			StereotypeCheckConfiguration additionalCheckCfg)
			throws XMLStreamException, IllegalArgumentException, SAXException, IOException {

		//Validate with StreamSource because Stax Validation is not provided with every implementation of JAXP
		SchemaFactory schemafactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemafactory
				.newSchema(StereotypeCheckReader.class.getClassLoader().getResource(checkstyleStereotypeXsd));

		Validator validator = schema.newValidator();
		validator.validate(new StreamSource(file));

		//Parse with Stax
		XMLStreamReader reader = XMLInputFactory.newInstance()
				.createXMLStreamReader(new BufferedInputStream(new FileInputStream(file)));
		StereotypeCheckConfigurationReader delegate = new StereotypeCheckConfigurationReader(reader,
				additionalCheckCfg);
		
		while (delegate.hasNext()){
			delegate.next();
		}

		return delegate.getConfig();
	}

	/**
	 * Read and validate the given file to a configuration for stereotype check.
	 * If there is a project specific file, that overrides the central
	 * configuration given by the file, a merged configuration is created.
	 * 
	 * @param file
	 *            The central configuration file.
	 * @return the configuration.
	 */
	public static StereotypeCheckConfiguration read(File file) {
		StereotypeCheckConfiguration additionalCheckCfg = null;
		String filenameOverride = file.getAbsolutePath().replaceAll("(.*)(\\.xml)$", "$1-override$2");
		if (filenameOverride != null && new File(filenameOverride).exists()) {
			File stOverrideFile = new File(filenameOverride);
			try {
				logger.info("File to override the default checkstyle-stereotype.xml found: "
						+ stOverrideFile.getAbsolutePath());
				additionalCheckCfg = StereotypeCheckReader.read(stOverrideFile, CHECKSTYLE_STEREOTYPE_OVERRIDE_XSD,
						null);
				logger.info("A file to override the default checkstyle-stereotype.xml found and loaded");

			} catch (XMLStreamException | IllegalArgumentException | SAXException | IOException e) {
				throw new IllegalArgumentException(
						"File " + stOverrideFile.getAbsolutePath() + " is not valid: " + e.getMessage(), e);
			}
		}
		try {
			return read(file, CHECKSTYLE_STEREOTYPE_XSD, additionalCheckCfg);
		} catch (XMLStreamException | SAXException | IOException e) {
			throw new IllegalArgumentException("File " + file.getAbsolutePath() + " is not valid: " + e.getMessage(),
					e);
		}

	}

}
