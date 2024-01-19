package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.util;

/*-
 * #%L
 * mcck-core-EXAMPLES-springboot-rest-hibernate
 * %%
 * Copyright (C) 2024 Michael I. Calderero
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.exception.AppCustomException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;

public class DbHelper {

	@Resource
	private EntityManager entityMgr;

	@Resource
	private DataSource dataSrc;

	public DbHelper() {
		// noop
	}

	public int countBookEntityAuditRecords() {
		return Integer.valueOf(entityMgr.createNativeQuery("select count(*) from my_schema.book_a").getSingleResult().toString());
	}

	public int countRevisionEntityRecords() {
		return Integer.valueOf(entityMgr.createNativeQuery("select count(*) from my_schema.revinfo").getSingleResult().toString());
	}

	public void loadAuditEnversData() {
		try (Connection conn = dataSrc.getConnection()) {
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("test_data/audit_envers_data.sql"));
			conn.commit();
		} catch (SQLException e) {
			throw new AppCustomException(e);
		}
	}
}
