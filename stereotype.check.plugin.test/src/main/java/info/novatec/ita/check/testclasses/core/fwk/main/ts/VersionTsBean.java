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
package info.novatec.ita.check.testclasses.core.fwk.main.ts;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.interceptor.Interceptors;

import info.novatec.ita.check.testclasses.core.fwk.common.exception.interceptor.BusinessServiceExceptionInterceptor;
import info.novatec.ita.check.testclasses.core.fwk.shared.VersionTs;

/**
 * This valid class is annotated with Interceptors and one of the needed other
 * annotations of the stereotype.
 * 
 * @author Volker Koch (NovaTec Consulting GmbH)
 *
 */
@Singleton
@Startup
@Interceptors(BusinessServiceExceptionInterceptor.class)
public class VersionTsBean implements VersionTs {

}
