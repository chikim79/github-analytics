package com.csca5028.demo.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GHRepoRepository extends JpaRepository<GHRepo, Long> {

}
