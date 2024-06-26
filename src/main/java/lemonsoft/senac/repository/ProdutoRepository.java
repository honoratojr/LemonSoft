package lemonsoft.senac.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import lemonsoft.senac.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    public List<Produto> findByNomeContainingIgnoreCase(String nome);
    public Page<Produto> findByStatusTrue(Pageable pageable);
    
}
