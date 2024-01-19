package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.service;

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

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;

import jakarta.annotation.Resource;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;

@Component
public class AuditCleanerService extends AbstractStatefulRunnable {

	private static final int DEFAULT_BATCH_SIZE = 100;

	@Resource
	private TransactionOperations txOper;

	@Resource
	private EntityManagerFactory entityMgrFactory;

	@Resource
	private EntityManager entityMgr;

	private EnversService enversSvc;
	private long recordValidityMillis = Duration.ofHours(1).toMillis();
	private List<Class<?>> auditedEntityClassesLst;
	private String auditedEntityRevisionColumnName;
	private String[] revisionEntityInfos;

	public AuditCleanerService() {
		// noop
	}

	@Override
	protected void afterPropertiesSet() throws Exception {
		Set<EntityType<?>> entities = entityMgr.getMetamodel().getEntities();
		auditedEntityClassesLst = getAuditedEntityClasses(entities);
		Class<?> revisionEntityClass = getRevisionEntityClass(entities);
		ServiceRegistry svcRegistry = entityMgrFactory.unwrap(SessionFactoryServiceRegistry.class);
		enversSvc = svcRegistry.getService(EnversService.class);
		auditedEntityRevisionColumnName = enversSvc.getConfig().getRevisionFieldName();
		revisionEntityInfos = getRevisionEntityIdColumnName(revisionEntityClass);
		super.afterPropertiesSet(); // this must be the last
	}

	@Override
	protected void beforeDestroy() throws Exception {
		super.beforeDestroy(); // this must be the first
	}

	@Override
	protected String getName() {
		return getClass().getSimpleName();
	}

	@Scheduled(cron = "${app_custom.scheduling.audit_cleaner_svc_sched}")
	@Override
	public void run() {
		super.run();
	}

	@Override
	protected void runIt(String finalName, long timeNowMillis) throws Exception {
		long oldestAllowedRecordAgeMillis = timeNowMillis - recordValidityMillis;
		getLog().info("Records older than {} will be removed", new Timestamp(timeNowMillis));

		txOper.execute(status -> {
			List<Long> expiredRevIds = getExpiredRevisionIds(oldestAllowedRecordAgeMillis, revisionEntityInfos);

			while (!expiredRevIds.isEmpty()) {
				for (Class<?> entityClazz : auditedEntityClassesLst) {
					Table tblAnnotation = entityClazz.getAnnotation(Table.class);
					String auditTableName = enversSvc.getConfig().getAuditEntityName(tblAnnotation.name());
					Query deleteAuditedEntityQuery = entityMgr.createNativeQuery("delete from {h-schema}" + auditTableName + " a where a." + auditedEntityRevisionColumnName + " in :theIds");
					deleteAuditedEntityQuery.setParameter("theIds", expiredRevIds);
					deleteAuditedEntityQuery.executeUpdate();
				}
				Query deleteExpiredRevisionsQuery = entityMgr.createNativeQuery("delete from {h-schema}" + revisionEntityInfos[0] + " a where a." + revisionEntityInfos[1] + " in :theIds");
				deleteExpiredRevisionsQuery.setParameter("theIds", expiredRevIds);
				deleteExpiredRevisionsQuery.executeUpdate();
				expiredRevIds = getExpiredRevisionIds(oldestAllowedRecordAgeMillis, revisionEntityInfos);
			}
			return null;
		});
	}

	@SuppressWarnings("unchecked")
	private List<Long> getExpiredRevisionIds(long oldestAllowedRecordAgeMillis, String[] revisionEntityInfos) {
		Query selectExpiredRevisionsQuery = entityMgr.createNativeQuery("select b."
			+ revisionEntityInfos[1] + " from {h-schema}" + revisionEntityInfos[0] + " b "
			+ "where b." + revisionEntityInfos[2] + " < :refTimestamp limit :batchSize");
		selectExpiredRevisionsQuery.setParameter("refTimestamp", oldestAllowedRecordAgeMillis);
		selectExpiredRevisionsQuery.setParameter("batchSize", DEFAULT_BATCH_SIZE);
		return selectExpiredRevisionsQuery.getResultList();
	}

	private String[] getRevisionEntityIdColumnName(Class<?> clazz) {
		String[] infos = { null, null, null };
		// get the table name
		Table tblAnnot = clazz.getAnnotation(Table.class);
		infos[0] = tblAnnot.name();
		// we need to have the annotations on the getter methods rather on the fields so
		// that they will be easier to access
		for (Method m : clazz.getMethods()) {
			if (m.getName().startsWith("get") && m.getParameterCount() < 1) {
				if (null != m.getAnnotation(RevisionNumber.class)) {
					Column colAnnot = m.getAnnotation(Column.class);
					infos[1] = colAnnot.name();
				} else if (null != m.getAnnotation(RevisionTimestamp.class)) {
					Column colAnnot = m.getAnnotation(Column.class);
					infos[2] = colAnnot.name();
				}
			}
		}
		return infos;
	}

	private Class<?> getRevisionEntityClass(Set<EntityType<?>> entities) {
		for (EntityType<?> et : entities) {
			Class<?> clazz = et.getJavaType();
			if (null != clazz.getAnnotation(RevisionEntity.class)) {
				return clazz;
			}
		}
		return null;
	}

	private List<Class<?>> getAuditedEntityClasses(Set<EntityType<?>> entities) {
		List<Class<?>> lst = new ArrayList<>();
		for (EntityType<?> et : entities) {
			Class<?> clazz = et.getJavaType();
			if (null != clazz.getAnnotation(Audited.class)) {
				lst.add(clazz);
			}
		}
		return lst;
	}
}
