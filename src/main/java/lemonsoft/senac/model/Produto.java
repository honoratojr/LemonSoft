package lemonsoft.senac.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_produtos")
public class Produto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, message = "O nome deve ter no mínimo 3 carateres")
    private String nome;
    
    @NotNull(message = "O campo deve ser preenchido.")
    private BigDecimal preco;

    @NotNull(message = "Deve ser maior que 0.")
    private Integer quantidadeEstoque;

    @DecimalMin(value = "0.5", message = "Atribua uma avaliação.")
    private double avaliacao;
    
    @Column(length = 200)
    @NotEmpty(message = "O campo deve ser preenchido.")
    private String descricao;

    @Column(length = 2000)
    @NotEmpty(message = "Atribua uma descrição ao produto.")
    private String descricaoDetalhada;

    public boolean status = Boolean.TRUE;

    @OneToMany(mappedBy = "produto", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<ImagemProduto> imagens;
    
}
