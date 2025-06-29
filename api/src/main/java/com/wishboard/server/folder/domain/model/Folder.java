package com.wishboard.server.folder.domain.model;

import com.wishboard.server.common.domain.AuditingTimeEntity;
import com.wishboard.server.user.domain.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "folders")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Folder extends AuditingTimeEntity {

	@Id
	@Column(name = "folder_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "folder_name", length = 512)
	private String folderName = "empty";

	private Folder(User user, String folderName) {
		this.user = user;
		this.folderName = folderName;
	}

	public static Folder newInstance(User user, String folderName) {
		return new Folder(user, folderName);
	}

	public void updateFolderName(String folderName) {
		if (this.folderName.equals(folderName)) {
			return;
		}
		this.folderName = folderName;
	}
}
