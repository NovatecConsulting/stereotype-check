/**
 * 
 */
package info.novatec.architecture.check.testclasses.app1.main.ul.wt.test;

import javax.inject.Inject;

import info.novatec.architecture.check.testclasses.core.fwk.common.ul.View;
import info.novatec.architecture.check.testclasses.core.fwk.common.ul.ViewStereotype;

/**
 * This view injects another view which is allowed in the central file
 * stereotype-dependency-not-allowed.xml but not in the project specific file
 * stereotype-dependency-not-allowed-override.xml
 * 
 * @author Volker Koch (NovaTec Consulting GmbH)
 */
@ViewStereotype
public class SampleViewContainsView implements View{
	
	@Inject
	SampleCoreView otherView;
}
