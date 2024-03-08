package lemonsoft.senac.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lemonsoft.senac.model.Usuario;
import lemonsoft.senac.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    public void salvarUsuario(Usuario usuario) {
        
    }

    
    
}
