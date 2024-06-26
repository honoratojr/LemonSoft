package lemonsoft.senac.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class ImagemProduto {  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nomeArquivo;

    @Lob
    @Column(columnDefinition="BLOB")
    private byte[] arquivo;

    private int ordenacao;

    private boolean principal;

    @ManyToOne
    @JoinColumn(name = "produto_id", foreignKey = @ForeignKey(name = "fk_imagem_produto"))
    private Produto produto;

    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNomeArquivo() {
        return nomeArquivo;
    }
    
    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }
    
    public int getOrdenacao() {
        return ordenacao;
    }
    
    public void setOrdenacao(int ordenacao) {
        this.ordenacao = ordenacao;
    }
    
    public boolean isPrincipal() {
        return principal;
    }
    
    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }
    
    public Produto getProduto() {
        return produto;
    }
    
    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public byte[] getArquivo() {
        return arquivo;
    }

    public void setArquivo(byte[] arquivo) {
        this.arquivo = arquivo;
    }

    @Override
    public String toString() {
        return "ImagemProduto{" +
                "id=" + id +
                ", nomeArquivo='" + nomeArquivo + '\'' +
                ", ordenacao=" + ordenacao +
                ", principal=" + principal +
                '}';
    }
}
