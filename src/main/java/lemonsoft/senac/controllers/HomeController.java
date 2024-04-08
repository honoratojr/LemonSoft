package lemonsoft.senac.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import lemonsoft.senac.model.Produto;
import lemonsoft.senac.repository.ImagemProdutoRepository;
import lemonsoft.senac.repository.ProdutoRepository;

@Controller
@RequestMapping("/lemonSoft")
public class HomeController {

    @Autowired
    ImagemProdutoRepository imagemProdutoRepository;

    @Autowired
    ProdutoRepository produtoRepository;

    @RequestMapping("/")
    public String loginBackOffice(Model model) {
        return "index";
    }

    @GetMapping("/home")
    public ModelAndView homeProdutosCliente(@RequestParam(defaultValue = "0") int page) {
        int pageSize = 20;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        Page<Produto> produtosPage = this.produtoRepository.findByStatusTrue(pageable);
        List<Produto> produtos = produtosPage.getContent();

        int totalPages = produtosPage.getTotalPages();

        ModelAndView mv = new ModelAndView("client/index");
        mv.addObject("produtos", produtos);
        mv.addObject("currentPage", page);
        mv.addObject("totalPages", totalPages);
        return mv;
    }

    @GetMapping("/visualizar/{id}")
    public String VisualizarProdutoCli(@PathVariable("id") Long id, Model model) {
        Produto produto = produtoRepository.findById(id).orElseThrow(() -> 
        new IllegalArgumentException("Produto não encontrado: " + id));
        model.addAttribute("produto" ,produto);
        return "/client/ver-produto";
    }
    /*Pesquisar produto */
    @GetMapping("/resultado-pesquisa")
    public String pesquisarProduto(@RequestParam(required = false) String nome, Model model) {
        List<Produto> produtosPesquisa;
        if (nome == null || nome.trim().isEmpty()) {
            return "redirect:/lemonSoft/home";
        } else {
            produtosPesquisa = produtoRepository.findByNomeContainingIgnoreCase(nome);
        }
        model.addAttribute("produtosPesquisa", produtosPesquisa);
        return "client/pesquisa-index";
    }

    @GetMapping("/cadastrar")
    public String cadastroCliente() {
        return "/";
    }
    
    
}
