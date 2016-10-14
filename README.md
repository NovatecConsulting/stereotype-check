# Stereotype Checker Readme


## Status
[![Build Status](https://travis-ci.org/NovaTecConsulting/stereotype-check.svg?branch=master)](https://travis-ci.org/NovaTecConsulting/stereotype-check)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

## Installation of Eclipse Checkstyle Plugin

In order to get the stereotype checker plugin running in eclipse, an appropriate checkstyle plugin version has to be installed. 

* Therefore download the correct update-site from [Sourceforge](https://sourceforge.net/projects/eclipse-cs/files/Eclipse%20Checkstyle%20Plug-in/) 
* Add the local update site. "Help -> Install new Software -> Add"
    - Name: Local Eclipse Checkstyle Update Site
    - Click "Archive" and choose the downloaded zip file
	
## Build the Stereotype Check Plugin
* Build the plugin and create an update site using Maven: `mvn clean install`

## Install the Stereotype Check Plugin

To be able to check the project with checkstyle, the stereotype checker must be installed to the current eclipse installation. Follow these steps to achieve this:
* Install the plugin from there via "Help -> Install new Software"
	- Select Add.. annd then Local...
	- Select the directory relative to the project root `./stereotype.check.repository/target/repository`
	- Select the created plugin and install it.

## Configure the Stereotype check Plugin

Activate checkstyle for your project e.g. the "stereotype check.plugin.test" which contains many classes with problems.
* Open the properties of your project and select Checkstyle
* Add a new or change your Local Check Configuration
* In the list of the known modules select "Other/Stereotype Check" and press Add...
* You have to provide a configuration file. If the project is "stereotype check.plugin.test" you can use "${config_loc}/checkstyle-stereotype.xml"
* TODO description of the configuration file

--SNIPPET FROM StereotypeCondition
All possible conditions a part (annotation, postfix etc.) of a stereotype
definition can have. If a part of a stereotype definition is marked as
sufficient, this means that if a class conforms to this part, the class
belongs to the stereotype. In this case the class must have all other parts.

E.g. If a class has a sufficient postfix Dto and the stereotype has necessary
interface Pojo, the class must implement this interface. If a class
implements the interface Pojo but has a postfix Other the class does not
belong to this stereotype.

A stereotype definition must have one sufficient condition and one of the
sufficient conditions must be the postfix or package-name. This is necessary
to check the dependencies by analyzing the full qualified classname of an
import.
 --END



## Debug the Eclipse Plugin

Sometimes it is helpfull to be able to debug the eclipse plugin. Some prerequisites have to be fulfilled to do this:

* Install the Eclipse Plug-In Development Tools if not already done
    - "Help -> Install new Software"
    - Choose "The Eclipse Project Updates - http://download.eclipse.org/eclipse/updates/4.5" or add it, if not present
    - Choose "Eclipse Plug-in Development Environment" and install it

To actually debug the plugin select "Debug as -> Eclipse Application" from the context menu of the "stereotype.check.plugin" project


