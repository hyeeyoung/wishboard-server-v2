package com.wishboard.server.item.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wishboard.server.folder.domain.model.Folder;
import com.wishboard.server.item.domain.model.Item;
import com.wishboard.server.user.domain.model.User;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
	@EntityGraph(attributePaths = {"images"})
	Optional<Item> findById(Long id);

	@EntityGraph(attributePaths = {"images"})
	List<Item> findAllByUserId(Long userId);

	void deleteAllByUserId(Long userId);

	List<Item> findAllByFolderId(Long folderId);
}
