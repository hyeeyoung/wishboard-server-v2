package com.wishboard.server.domain.folder;

import java.util.ArrayList;
import java.util.List;

import com.wishboard.server.domain.common.AuditingTimeEntity;
import com.wishboard.server.domain.item.Item;
import com.wishboard.server.domain.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

	@OneToMany(mappedBy = "folder", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Item> items = new ArrayList<>();

	private Folder(User user, String folderName) {
		this.user = user;
		this.folderName = folderName;
	}

	public static Folder newInstance(User user, String folderName) {
		return new Folder(user, folderName);
	}
}
