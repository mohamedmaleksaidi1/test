package com.speeda.api;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface TokenSessionRepository extends JpaRepository<TokenSession, Long> {

}
