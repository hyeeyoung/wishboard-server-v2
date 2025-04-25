package com.wishboard.server.domain.folder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wishboard.server.domain.folder.Folder;
import com.wishboard.server.domain.user.User;

public interface FolderRepository extends JpaRepository<Folder, Long>, FolderRepositoryCustom {
	Optional<Folder> findFolderByIdAndUser(Long id, User user);

	Optional<Folder> findByUserAndFolderName(User user, String folderName);

	List<Folder> findAllByUserOrderByCreatedAtDesc(User user);
}
