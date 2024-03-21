package lemonsoft.senac.model;

import java.io.Serializable;
import java.util.List;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_user")
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, message = "O nome deve ter no mínimo 3 carateres")
    private String nome;

    @CPF(message = "CPF inválido")
    @Size(min = 11, message = "Deve conter 11 números")
    private String cpf;

    
    @Email(message = "Email inválido")
    @NotEmpty(message = "O campo deve ser preenchido.")
    private String email;

    @NotEmpty(message = "A senha deve ser informada")
	@Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;

    @Transient
    private String confirmarSenha;
    
    private boolean ativo = Boolean.TRUE;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_papel", 
    joinColumns = @JoinColumn(name = "usuario_id"), 
    inverseJoinColumns = @JoinColumn(name = "papel_id"))
	private List<Papel> papeis;

}
