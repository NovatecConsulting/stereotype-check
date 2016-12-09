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
package info.novatec.ita.check.testclasses.app1.main.data.entity;

import javax.persistence.Entity;

/**
 * This invalid class extends a class, which is not defined to be a base class
 * of the stereotype. The override mechanism also does not define the base class
 * as valid.
 * 
 * @author Volker Koch (NovaTec Consulting GmbH)
 *
 */
@Entity
public class AdditionalOperation extends CustomerConcern {

}