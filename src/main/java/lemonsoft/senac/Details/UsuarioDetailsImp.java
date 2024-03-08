package lemonsoft.senac.Details;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lemonsoft.senac.model.Usuario;

public class UsuarioDetailsImp implements UserDetails {

    private Usuario usuario;

    public UsuarioDetailsImp(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = usuario.getPapeis().stream().map(papel -> new SimpleGrantedAuthority("ROLE_" + papel.getPapel()))
        .collect(Collectors.toList());

        return authorities;
    }

    @Override
    public String getPassword() {
        return usuario.getSenha();
        
    }

    @Override
    public String getUsername() {
        return  usuario.getEmail();  
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
}
