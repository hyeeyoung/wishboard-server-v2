package com.wishboard.server.folder.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wishboard.server.folder.domain.model.Folder;
import com.wishboard.server.user.domain.model.User;

public interface FolderRepository extends JpaRepository<Folder, Long>, FolderRepositoryCustom {
	Optional<Folder> findFolderByIdAndUser(Long id, User user);

	Optional<Folder> findByUserAndFolderName(User user, String folderName);

	Page<Folder> findAllByUser(User user, Pageable pageable);

	List<Folder> findAllByUserOrderByCreatedAtDesc(User user);

	void deleteAllByUser(User user);
}
