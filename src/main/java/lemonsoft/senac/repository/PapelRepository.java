package lemonsoft.senac.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import lemonsoft.senac.model.Papel;

public interface PapelRepository extends JpaRepository<Papel, Long> {
    
    Papel findByPapel(String papel);
}
