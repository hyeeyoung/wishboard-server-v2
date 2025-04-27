package com.wishboard.server.domain.item.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wishboard.server.domain.item.Item;
import com.wishboard.server.domain.user.User;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
	@EntityGraph(attributePaths = {"images"})
	Optional<Item> findById(Long id);
	
	@EntityGraph(attributePaths = {"images"})
	List<Item> findAllByUser(User user);

	void deleteAllByUser(User user);
}
