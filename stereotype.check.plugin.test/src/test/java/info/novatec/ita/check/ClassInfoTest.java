/*******************************************************************************
 * Copyright 2016 NovaTec Consulting GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package info.novatec.ita.check;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Tests for {@link ClassInfo}.
 */
public class ClassInfoTest {
	@Test
	public void getFullClassNameWithPackage() {

		ClassInfo classInfo = new ClassInfo();
		classInfo.setClassName("Classname", null);
		classInfo.setPackageName("package1.package2");

		String fullClassName = classInfo.getFullClassName();

		assertThat(fullClassName).isEqualTo("package1.package2.Classname");
	}

	@Test
	public void getFullClassNameWithOutPackage() {

		ClassInfo classInfo = new ClassInfo();
		classInfo.setClassName("Classname", null);
		classInfo.setPackageName(null);

		String fullClassName = classInfo.getFullClassName();

		assertThat(fullClassName).isEqualTo("Classname");
	}
}
