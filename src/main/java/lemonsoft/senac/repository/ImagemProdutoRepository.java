package lemonsoft.senac.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import lemonsoft.senac.model.ImagemProduto;

public interface ImagemProdutoRepository extends JpaRepository<ImagemProduto, Long>{
    
    ImagemProduto findByNomeArquivoContaining(String nomeArquivo);
    
}