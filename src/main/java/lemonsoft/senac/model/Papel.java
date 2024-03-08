package lemonsoft.senac.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Data
@Entity
public class Papel {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String papel;

    @ManyToMany(mappedBy = "papeis", fetch = FetchType.EAGER)
    private List<Usuario> usuarios;

    public Papel() {}

    public Papel(String papel) {
        this.papel = papel;
    }
    
}
