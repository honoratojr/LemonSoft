package lemonsoft.senac.controllers;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lemonsoft.senac.model.Produto;
import lemonsoft.senac.repository.ProdutoRepository;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    ProdutoRepository produtoRepository;

    
    @GetMapping("/lista")
    public ModelAndView listarProdutos(@RequestParam(defaultValue = "0") int page) {
        int pageSize = 2;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        Page<Produto> produtosPage = this.produtoRepository.findAll(pageable);
        List<Produto> produtos = produtosPage.getContent();

        int totalPages = produtosPage.getTotalPages();

        ModelAndView mv = new ModelAndView("estoque/listar-produtos");
        mv.addObject("produtos", produtos);
        mv.addObject("currentPage", page);
        mv.addObject("totalPages", totalPages);
        return mv;
    }

    @GetMapping("/novo")
    public String novoProduto(Model model) {
        model.addAttribute("produto", new Produto());
        return "estoque/criar-produto";
    }
    
    @SuppressWarnings("null")
    @PostMapping("/salvar")
    public String salvarProduto(@Valid Produto produto, BindingResult result, Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "estoque/criar-produto";
        }
        produtoRepository.save(produto);
        attributes.addFlashAttribute("mensagem", "Novo produto adicionado com sucesso!");
        return "redirect:/produto/lista";
    }

    @PostMapping("/ativar-desativar/{id}")
    public String ativoDesativo(@PathVariable Long id) {
        @SuppressWarnings("null")//<- Essa linha foi gerada automaticamente. Verificar o que significa.
        Produto produto = produtoRepository.findById(id).orElse(null);
        if (produto != null) {
            produto.setStatus(!produto.isStatus());
            produtoRepository.save(produto);
        } return "redirect:/produto/lista";
    }

    @GetMapping("/pesquisar-produto")
    public String pesquisarProduto(@RequestParam(required = false) String nome, Model model) {
        List<Produto> produtosEncontrados;
        if(nome == null || nome.trim().isEmpty()){
            produtosEncontrados = produtoRepository.findAll();
        } else {
            produtosEncontrados = produtoRepository.findByNomeContainingIgnoreCase(nome);
        }
        model.addAttribute("produtosEncontrados", produtosEncontrados);
        return "estoque/produtos-pesquisa";
    }

    @GetMapping("/detalhes/{id}")
    public String detalhesProduto(@PathVariable("id") Long id, Model model) {
        Produto produto = produtoRepository.findById(id).orElseThrow(() -> 
        new IllegalArgumentException("Produto não encontrado: " + id));
        model.addAttribute("produto" ,produto);
        return "/estoque/detalhes-produto";
    }
    
    
}
