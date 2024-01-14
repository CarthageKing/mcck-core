package org.carthageking.mc.mcck.core.EXAMPLES.sbrb.dao.entity;

import java.util.Objects;

import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.service.AppCustomRevisionListener;
import org.carthageking.mc.mcck.core.EXAMPLES.sbrb.util.AppCustomConstants;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// This will be our revision entity to be used by Envers. Rather than
// extend from DefaultRevisionEntity class, we chose to reimplement it
// completely so that we can specify the custom column names
@Entity
@Table(name = "revinfo")
// annotation below is required in order to indicate to Envers that this
// is the entity to be used as the revision table. If not present, this
// will be treated as any other table and Envers will autogenerate its own
// revision table definition.
// we also specify the listener class that will populate our custom fields.
@RevisionEntity(AppCustomRevisionListener.class)
public class AppCustomRevisionEntity implements java.io.Serializable {

	private static final long serialVersionUID = -6495046933812945376L;

	@Id
	@GeneratedValue
	@Column(name = "rev_id")
	@RevisionNumber
	private int id;

	@Column(name = "rev_tstmp")
	@RevisionTimestamp
	private long timestamp;

	@Column(name = "rev_username", length = AppCustomConstants.MAX_USERNAME_LENGTH)
	private String username;

	public AppCustomRevisionEntity() {
		// noop
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, timestamp, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AppCustomRevisionEntity)) {
			return false;
		}
		AppCustomRevisionEntity other = (AppCustomRevisionEntity) obj;
		return id == other.id && timestamp == other.timestamp && Objects.equals(username, other.username);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[id=" + id + ", timestamp=" + timestamp + ", username=" + username + "]";
	}
}
