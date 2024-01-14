package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.dao.entity;

import java.sql.Timestamp;

import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.util.AppCustomConstants;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class AuditableEntity {

	@Column(name = "aud_created_by", length = AppCustomConstants.MAX_USERNAME_LENGTH)
	@CreatedBy
	private String createdBy;

	@Column(name = "aud_created_dt", nullable = false)
	@CreatedDate
	private Timestamp createdDateTime;

	@Column(name = "aud_last_mod_by", length = AppCustomConstants.MAX_USERNAME_LENGTH)
	@LastModifiedBy
	private String lastModifiedBy;

	@Column(name = "aud_last_mod_dt", nullable = false)
	@LastModifiedDate
	private Timestamp lastModifiedDateTime;

	public AuditableEntity() {
		// noop
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Timestamp createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Timestamp getLastModifiedDateTime() {
		return lastModifiedDateTime;
	}

	public void setLastModifiedDateTime(Timestamp lastModifiedDateTime) {
		this.lastModifiedDateTime = lastModifiedDateTime;
	}
}
