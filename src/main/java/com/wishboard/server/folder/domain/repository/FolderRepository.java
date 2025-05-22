package com.wishboard.server.folder.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wishboard.server.folder.domain.model.Folder;
import com.wishboard.server.user.domain.model.User;

public interface FolderRepository extends JpaRepository<Folder, Long>, FolderRepositoryCustom {
	Optional<Folder> findFolderByIdAndUserId(Long id, Long userId);

	Optional<Folder> findByUserIdAndFolderName(Long userId, String folderName);

	Page<Folder> findAllByUserId(Long userId, Pageable pageable);

	List<Folder> findAllByUserIdOrderByCreatedAtDesc(Long userId);

	void deleteAllByUserId(Long userId);
}
