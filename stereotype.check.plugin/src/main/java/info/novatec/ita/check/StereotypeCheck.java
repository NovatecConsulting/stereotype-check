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

import java.io.File;
import java.util.logging.Logger;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import info.novatec.ita.check.config.StereotypeCheckConfiguration;
import info.novatec.ita.check.config.StereotypeCheckReader;

/**
 * Checks if the class conforms to a stereotype. If it is a stereotype it
 * checks, if the class has dependencies to other stereotypes, that are allowed.
 * 
 * @author Volker Koch (volker.koch@novatec-gmbh.de)
 */
public class StereotypeCheck extends Check {

	private static final Logger logger = Logger.getLogger(StereotypeCheck.class.getCanonicalName());

	/** Information about the class actually checked by this class. */
	private ClassInfo currentClass;

	/** The filename of the the central configuration file. */
	private String filename = null;

	/** The central configuration read from {@link #filename}. */
	private StereotypeCheckConfiguration stereotypeCheckConfig;

	private StereotypeCheckValidator validator = new StereotypeCheckValidator(this);

	/**
	 * Inner classes are ignored. While the parser parses an inner class this
	 * attribute is set to true.
	 */
	private boolean isInnerClass = false;

	/**
	 * @return the tokens that are delivered from the parser to this class.
	 */
	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.ABSTRACT, TokenTypes.PACKAGE_DEF, TokenTypes.INTERFACE_DEF, TokenTypes.CLASS_DEF,
				TokenTypes.ANNOTATION, TokenTypes.IMPORT, TokenTypes.IMPLEMENTS_CLAUSE, TokenTypes.EXTENDS_CLAUSE,
				TokenTypes.TYPE };
	}

	/**
	 * Set the filename of the configuration.
	 * 
	 * @param filename
	 *            the filename of the configuration.
	 */
	public void setFile(String filename) {
		this.filename = filename;
	}

	/** {@inheritDoc} */
	@Override
	public void init() {
		super.init();

		if (this.filename == null) {
			throw new IllegalArgumentException("No Property 'file' defined for Check " + getClass().getName());
		}

		File file = new File(this.filename);
		if (!file.exists()) {
			throw new IllegalArgumentException("File defined in property 'file' of Check " + getClass().getName()
					+ " does not exist " + file.getAbsolutePath());
		}
		this.stereotypeCheckConfig = StereotypeCheckReader.read(file);
	}

	/** {@inheritDoc} */
	@Override
	public void beginTree(DetailAST aRootAST) {
		super.beginTree(aRootAST);
		this.currentClass = new ClassInfo();
	}

	/** {@inheritDoc} */
	@Override
	public void finishTree(DetailAST ast) {
		super.finishTree(ast);
		validator.validate(ast, currentClass);
	}

	/** {@inheritDoc} */
	@Override
	public void visitToken(DetailAST ast) {
		logAstDetails(ast);
		if (this.isInnerClass) {
			// ignore Inner classes
			return;
		}
		int tokenType = ast.getType();

		switch (tokenType) {
		case TokenTypes.PACKAGE_DEF:
			addPackagename(ast);
			break;
		case TokenTypes.IMPORT:
			addImport(ast);
			break;
		case TokenTypes.ABSTRACT:
			setAbstractType(ast);
			break;
		case TokenTypes.CLASS_DEF:
			if (this.currentClass.getClassName() != null) {
				this.isInnerClass = true;
				return;
			}
			setClassType(ast);
			break;
		case TokenTypes.ANNOTATION_DEF:
			setAnnotationType(ast);
			break;
		case TokenTypes.INTERFACE_DEF:
			setInterfaceType(ast);
			break;
		case TokenTypes.ANNOTATION:
			addAnnotation(ast);
			break;
		case TokenTypes.EXTENDS_CLAUSE:
			addBaseClass(ast);
			break;
		case TokenTypes.IMPLEMENTS_CLAUSE:
			addInterface(ast);
			break;
		case TokenTypes.TYPE:
			addImportFromFullQualifiedType(ast);
			break;
		default:
			break;
		}
	}

	private void addImportFromFullQualifiedType(DetailAST ast) {
		if (isClassType(ast)) {
			String typeName = FullIdent.createFullIdent(ast.getLastChild()).getText();
			if (typeName.indexOf(".") == -1) {
				if (!this.currentClass.hasImport(typeName)) {
					String currentImport = this.currentClass.getPackageName() + "." + typeName;
					this.currentClass.addImport(currentImport, ast);
					logger.fine("import: " + currentImport);
				}
			} else {
				this.currentClass.addImport(typeName, ast);
				logger.fine("import: " + typeName);
			}
		}
	}

	private void addInterface(DetailAST ast) {
		DetailAST interfaceChild = ast.getFirstChild();
		while (interfaceChild != null) {
			logger.fine("interfaceChild.getType(): " + interfaceChild.getType());
			if (interfaceChild.getType() == TokenTypes.IDENT) {
				String interfacename = interfaceChild.getText();
				this.currentClass.addInterface(interfacename);
				logger.fine("implements: " + interfacename);
			} else if (interfaceChild.getType() == TokenTypes.DOT) {
				String interfacename = FullIdent.createFullIdent(interfaceChild).getText();
				this.currentClass.addInterface(interfacename);
				logger.fine("implements: " + interfacename);
			}
			interfaceChild = interfaceChild.getNextSibling();
		}
	}

	private void addBaseClass(DetailAST ast) {
		DetailAST baseClassChild = ast.getFirstChild();
		while (baseClassChild != null) {
			if (baseClassChild.getType() == TokenTypes.IDENT) {
				String baseClassName = baseClassChild.getText();
				this.currentClass.addBaseClass(baseClassName);
				logger.fine("extends: " + baseClassName);
			} else if (baseClassChild.getType() == TokenTypes.DOT) {
				String baseClassName = FullIdent.createFullIdent(baseClassChild).getText();
				this.currentClass.addBaseClass(baseClassName);
				logger.fine("extends: " + baseClassName);
			}
			baseClassChild = baseClassChild.getNextSibling();
		}
	}

	private void addImport(DetailAST ast) {
		DetailAST nameAST;
		FullIdent full;
		nameAST = ast.getLastChild().getPreviousSibling();
		full = FullIdent.createFullIdent(nameAST);
		String currentImport = full.getText();
		this.currentClass.addImport(currentImport, ast);
		logger.fine("import: " + currentImport);
	}

	private void addAnnotation(DetailAST ast) {
		DetailAST annotationCild = ast.getFirstChild().getNextSibling();
		String annotation = FullIdent.createFullIdent(annotationCild).getText();
		this.currentClass.addAnnotation(annotation);
		logger.fine("annotation: " + annotation);
	}

	private void setClassType(DetailAST ast) {
		DetailAST classToken = ast.findFirstToken(TokenTypes.IDENT);
		String className = classToken.getText();
		this.currentClass.setClassName(className, ast);
		logger.fine("class name: " + className);
	}

	private void setAbstractType(DetailAST ast) {
		if (ast.getParent().getParent().getType() == TokenTypes.CLASS_DEF) {
			this.currentClass.setAbstract(true);
			logger.fine("abstract class");
		}
	}

	private void setInterfaceType(DetailAST ast) {
		DetailAST intToken = ast.findFirstToken(TokenTypes.IDENT);
		String interfaceName = intToken.getText();
		this.currentClass.setInterfaceName(interfaceName, ast);
		logger.fine("interface name: " + interfaceName);
	}

	private void setAnnotationType(DetailAST ast) {
		DetailAST annToken = ast.findFirstToken(TokenTypes.IDENT);
		String annotationName = annToken.getText();
		this.currentClass.setClassName(annotationName, ast);
		logger.fine("annotation name: " + annotationName);
	}

	private void addPackagename(DetailAST ast) {
		DetailAST nameAST;
		FullIdent full;
		nameAST = ast.getLastChild().getPreviousSibling();
		full = FullIdent.createFullIdent(nameAST);
		String packageName = full.getText();
		this.currentClass.setPackageName(packageName);
		logger.fine("package name: " + packageName);
	}

	/** {@inheritDoc} */
	@Override
	public void leaveToken(DetailAST ast) {
		int tokenType = ast.getType();
		if (tokenType == TokenTypes.CLASS_DEF) {
			this.isInnerClass = false;
		}
	}

	private void logAstDetails(DetailAST ast) {
		logger.finest("=================================== visiting " + ast);
		logger.finest("ast.getText() " + ast.getText());
		logger.finest("ast.getType() " + TokenTypes.getTokenName(ast.getType()));
		logger.finest("ast.getLineNo() " + ast.getLineNo());
	}

	private boolean isClassType(DetailAST ast) {
		DetailAST firstChild = ast.getFirstChild();
		// In the declaration of a type, we get the type name from the childs of
		// the type. If there are no childs or the child not of the right types
		// the type declaration does not contain a classname
		if (firstChild != null
				&& (firstChild.getType() == TokenTypes.IDENT || firstChild.getType() == TokenTypes.DOT)) {
			return true;
		}
		return false;
	}

	/**
	 * @return the configuration
	 */
	StereotypeCheckConfiguration getConfig() {
		return stereotypeCheckConfig;
	}

	/**
	 * @see StereotypeCheck#log(DetailAST, String, Object...)
	 */
	void addError(DetailAST ast, String message, Object[] parameters) {
		log(ast, message, parameters);
	}

}
