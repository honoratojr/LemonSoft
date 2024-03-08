package lemonsoft.senac.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import lemonsoft.senac.model.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
   
   Usuario findByEmail(String email);

   public List<Usuario> findByNomeContainingIgnoreCase(String nome);

   @Query("select j from Usuario j where j.email = :email and j.senha = :senha")
   public Usuario buscarLogin(String email, String senha);

}
