package lemonsoft.senac.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lemonsoft.senac.model.Papel;
import lemonsoft.senac.model.Usuario;
import lemonsoft.senac.repository.PapelRepository;
import lemonsoft.senac.repository.UsuarioRepository;
import lemonsoft.senac.services.UsuarioService;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/admin")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    PapelRepository papelRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String loginBackOffice() {
        return "login-adm";
    }
    
    @PostMapping("/login")
    public ModelAndView login(@Valid Usuario usuario, BindingResult result, HttpSession session)  {
        ModelAndView mv = new ModelAndView();
        mv.addObject("usuario", new Usuario());
        if (result.hasErrors()) {
            mv.setViewName("login-adm");
        }
        Usuario userLogin = usuarioRepository.findByEmail(usuario.getEmail());

        if (userLogin == null || !new BCryptPasswordEncoder().matches(usuario.getSenha(), userLogin.getSenha())) {
            mv.addObject("mensagem", "Credenciais inválidas, tente novamente!");
        } else if (!userLogin.isAtivo()) {
            mv.addObject("mensagem", "Erro: Credenciais inativas!");
        } else {
            session.setAttribute("usuarioLogado", userLogin);
            mv.setViewName("/index");
        } return mv;
    }
    
    @GetMapping("/lista")
    public ModelAndView listarUsuarios() {
        List<Usuario> usuarios = this.usuarioRepository.findAll();
        ModelAndView mv = new ModelAndView("/admin/listar-usuarios");
        mv.addObject("usuarios", usuarios);
        return mv;
    }
    
    @GetMapping("/novo")
    public String criarUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("listaPapeis", papelRepository.findAll());
        return "/admin/criar-usuario";
    }
    
    @PostMapping("/salvar")
    public String salvarUsuario(@Valid Usuario usuario, BindingResult result, Model model, RedirectAttributes attributes, 
    @RequestParam(name = "papelId", required = false) Long papelId)/*Verificar essa linha, onde está passando os parâmetros de papeis. */{
        if (result.hasErrors()) {
            model.addAttribute("listaPapeis", papelRepository.findAll());
            model.addAttribute("papelIds", "Deve atribuir um grupo.");
            return "/admin/criar-usuario";
        }
        if (papelId != null) {
            Papel papel = papelRepository.findById(papelId).orElse(null);
            if (papel != null) {
                usuario.setPapeis(Arrays.asList(papel));
            }
        }
        //Verific se existe E-mail cadastrado
        Usuario usr = usuarioRepository.findByEmail(usuario.getEmail());
        if (usr != null) {
            model.addAttribute("emailExiste", "E-mail já cadastrado!");
            return "/admin/criar-usuario";
        }
        //verifica se as senhas coincidem
        if (!usuario.getSenha().equals(usuario.getConfirmarSenha())) {
            result.rejectValue("confirmarSenha", "error.usuario", "As senhas não coincidem!");
            model.addAttribute("listaPapeis", papelRepository.findAll());
            return "/admin/criar-usuario";
        }
        String hashSenha = new BCryptPasswordEncoder().encode(usuario.getSenha());
        usuario.setSenha(hashSenha);
        //Salva o usuario
        usuarioRepository.save(usuario);
        attributes.addFlashAttribute("mensagem", "Usuario salvo com sucesso!");
        return "redirect:/admin/lista";
    }
    //Esse end-point ative e desativa os usuário com apenas um clique no botão.
    @PostMapping("/ativar-desativar/{id}")
    public String ativoDesativo(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario != null) {
            usuario.setAtivo(!usuario.isAtivo());
            usuarioRepository.save(usuario);
        } return "redirect:/admin/lista";
    }
    
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable("id") long id, Model model) {
        Optional<Usuario> usuarioVelho = usuarioRepository.findById(id);
        if (!usuarioVelho.isPresent()) {
            throw new IllegalArgumentException("Usuário inválido: " + id);
        }
        Usuario usuario = usuarioVelho.get();
        List<Papel> todosPapeis = papelRepository.findAll();
        List<Long> papelIdsAssociados = usuario.getPapeis().stream().map(Papel::getId).collect(Collectors.toList());

        model.addAttribute("papelIdsAssociados", papelIdsAssociados);

        model.addAttribute("usuario", usuario);
        model.addAttribute("listaPapeis", todosPapeis);
        return "/admin/editar-usuario";
    }

    /*Editar usuários */
    @PostMapping("/editar/{id}")
    public String editarUsuario(@PathVariable("id") long id, @Valid Usuario usuario, BindingResult result, RedirectAttributes attributes, @RequestParam(name = "papelIds", required = false) List<Long> papelIds, Model model) {
        model.addAttribute("listaPapeis", papelRepository.findAll());
        if (result.hasErrors()) {
            model.addAttribute("papelIdsAssociados", papelIds);
            usuario.setId(id);
            return "/admin/editar-usuario";
        }
        if (papelIds != null) {
            usuario.setPapeis(new ArrayList<>());
            for (Long papelId : papelIds) {
                Papel papel = papelRepository.findById(papelId).orElse(null);
                if (papel != null) {
                    usuario.getPapeis().add(papel);
                }
            }
        }
        usuarioRepository.save(usuario);
        if (!usuario.getSenha().equals(usuario.getConfirmarSenha())) {
            result.rejectValue("confirmarSenha", "error.usuario", "As senhas não coincidem!");
            return "/admin/editar-usuario";
        }
        String hashSenha = new BCryptPasswordEncoder().encode(usuario.getSenha());
        usuario.setSenha(hashSenha);
        usuarioRepository.save(usuario);
        attributes.addFlashAttribute("mensagem", "Alterações salvas com sucesso!");
        return "redirect:/admin/lista";
    }
    /*Faz a pesquisa de usuários por nome. */
    @GetMapping("/pesquisar-usuario")
    public String pesquisarUsuario(@RequestParam(required = false) String nome, Model model) {
        List<Usuario> usuariosEncontrados;
        if(nome == null || nome.trim().isEmpty()){
            usuariosEncontrados = usuarioRepository.findAll();
        } else {
            usuariosEncontrados = usuarioRepository.findByNomeContainingIgnoreCase(nome);
        }
        model.addAttribute("usuariosEncontrados", usuariosEncontrados);
        return "/admin/usuarios-pesquisa";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/admin/login";
    }
    
}
