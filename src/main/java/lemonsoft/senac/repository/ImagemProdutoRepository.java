package lemonsoft.senac.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import lemonsoft.senac.model.ImagemProduto;

public interface ImagemProdutoRepository extends JpaRepository<ImagemProduto, Long>{
    
    ImagemProduto findByNomeArquivoContaining(String nomeArquivo);
    
    Optional<ImagemProduto> findByProduto_IdAndNomeArquivo(int produtoId, String nomeArquivo);
    
}