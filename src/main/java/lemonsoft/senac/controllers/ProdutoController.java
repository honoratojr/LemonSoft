package lemonsoft.senac.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import jakarta.validation.Valid;
import lemonsoft.senac.model.ImagemProduto;
import lemonsoft.senac.model.Produto;
import lemonsoft.senac.repository.ImagemProdutoRepository;
import lemonsoft.senac.repository.ProdutoRepository;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    ProdutoRepository produtoRepository;

    @Autowired
    ImagemProdutoRepository imgRepository;

    @GetMapping("/lista")
    public ModelAndView listarProdutos(@RequestParam(defaultValue = "0") int page) {
        int pageSize = 10;
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
    public String novoProduto(Produto produto, Model model) {
        model.addAttribute("produto", new Produto());
        return "estoque/criar-produto";
    }

    @SuppressWarnings("null")

    @PostMapping("/cadastro")
    public String cadastrarProduto(@RequestParam("arquivo") MultipartFile[] files, RedirectAttributes attributes,
            @Valid Produto produto, BindingResult result) {
        if (result.hasErrors()) {
            return "estoque/criar-produto";
        }
        produtoRepository.save(produto);

        List<ImagemProduto> imagensEntities = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            if (file.isEmpty()) {
                continue; // Ignora arquivos vazios
            }
            String nomeOriginal = file.getOriginalFilename();
            String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
            String nomeArquivo = produto.getId() + "-" + (i + 1) + extensao;

            try {
                String uploadDir = "src/main/resources/static/img/produtos/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path destino = uploadPath.resolve(nomeArquivo);
                Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

                ImagemProduto imgEntity = new ImagemProduto();
                imgEntity.setNomeArquivo(nomeArquivo);
                imgEntity.setOrdenacao(produto.getImagens().get(i).getOrdenacao());
                imgEntity.setPrincipal(produto.getImagens().get(i).isPrincipal());
                imgEntity.setProduto(produto);
                imagensEntities.add(imgEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        produto.setImagens(imagensEntities);
        produtoRepository.save(produto);
        attributes.addFlashAttribute("mensagem", "Novo produto adicionado com sucesso!");
        return "redirect:/produto/lista";
    }

    @GetMapping("/editar/{id}")
    public String editarProdutoForm(@PathVariable Long id, Model model) {
        Produto produto = produtoRepository.findById(id).orElse(null);
        if (produto == null) {
            return "redirect:/produto/lista";
        }
        model.addAttribute("produto", produto);
        return "estoque/editar-produto";
    }

    @SuppressWarnings("null")

    @PostMapping("/editar/{id}")
    public String editarProduto(@RequestParam("arquivo") MultipartFile[] files, RedirectAttributes attributes,
            @Valid Produto produto, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return "estoque/editar-produto";
        }
        Produto produtoExistente = produtoRepository.findById(id).orElse(null);
        if (produtoExistente == null) {
            result.rejectValue("produtoNulo", "Erro: produto não foi encontrado.");
            return "redirect:/produto/lista";
        }
        produtoRepository.save(produtoExistente);

        List<ImagemProduto> imagensEntities = new ArrayList<>();

        try {
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];

                if (file.isEmpty()) {
                    continue;
                }

                ImagemProduto imagemProduto;
                if (i < produtoExistente.getImagens().size()) {

                    imagemProduto = produtoExistente.getImagens().get(i);
                } else {

                    imagemProduto = new ImagemProduto();
                    imagemProduto.setProduto(produtoExistente);
                    produtoExistente.getImagens().add(imagemProduto);
                }

                String nomeOriginal = file.getOriginalFilename();
                String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
                String nomeArquivo = produtoExistente.getId() + "-" + (i + 1) + extensao;
                String uploadDir = "src/main/resources/static/img/produtos/";
                Path destino = Paths.get(uploadDir).resolve(nomeArquivo);

                Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

                imagemProduto.setNomeArquivo(nomeArquivo);

                imagensEntities.add(imagemProduto);
            }

            produtoExistente.setImagens(imagensEntities);
            produtoRepository.save(produto);
            attributes.addFlashAttribute("mensagem", "Alterações salvas com sucesso!");
            } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/produto/lista";
    }

    @PostMapping("/ativar-desativar/{id}")
    public String ativoDesativo(@PathVariable Long id) {
        Produto produto = produtoRepository.findById(id).orElse(null);
        if (produto != null) {
            produto.setStatus(!produto.isStatus());
            produtoRepository.save(produto);
        }
        return "redirect:/produto/lista";
    }

    @GetMapping("/pesquisar-produto")
    public String pesquisarProduto(@RequestParam(required = false) String nome, Model model) {
        List<Produto> produtosEncontrados;
        if (nome == null || nome.trim().isEmpty()) {
            produtosEncontrados = produtoRepository.findAll();
        } else {
            produtosEncontrados = produtoRepository.findByNomeContainingIgnoreCase(nome);
        }
        model.addAttribute("produtosEncontrados", produtosEncontrados);
        return "estoque/produtos-pesquisa";
    }

    @GetMapping("/detalhes/{id}")
    public String detalhesProduto(@PathVariable("id") Long id, Model model) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
        model.addAttribute("produto", produto);
        return "/estoque/detalhes-produto";
    }

}
