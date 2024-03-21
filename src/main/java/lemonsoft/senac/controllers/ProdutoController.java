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
    private ImagemProdutoRepository imgRepository;
    
    @GetMapping("/lista")
    public ModelAndView listarProdutos(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));

        Page<Produto> produtosPage = this.produtoRepository.findAll(pageable);
        List<Produto> produtos = produtosPage.getContent();

        int totalPages = produtos.isEmpty() || produtos.size() < 10 ? 1 : produtosPage.getTotalPages();

        ModelAndView mv = new ModelAndView("/estoque/listar-produtos");
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

    @PostMapping("/cadastro")
    public String cadastroProduto(@RequestParam("arquivo") MultipartFile[] files, RedirectAttributes attributes, @Valid Produto produto, BindingResult result) {
        
        if (result.hasErrors()) {
            return "estoque/criar-produto";
        }

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
                if(!Files.exists(uploadPath)) {
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
    
    @PostMapping("produto/edita")
    public String edita(@RequestParam("arquivo") MultipartFile[] files, @Valid Produto produto, BindingResult result) {
        if (result.hasErrors()) {}

        List<ImagemProduto> imagensEntities = produto.getImagens();

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            if (file.isEmpty()) {
                continue; 
            }

            try {
                ImagemProduto imagemProduto = imgRepository .findByNomeArquivoContaining(produto.getId() + "-" + (i + 1));
                String nomeOriginal = file.getOriginalFilename();
                String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
                String nomeArquivo = produto.getId() + "-" + (i + 1) + extensao;
                String uploadDir = "src/main/resources/static/img/produtos/";
                Path uploadPath = Paths.get(uploadDir);
                Path destino = uploadPath.resolve(nomeArquivo);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                if (!file.getOriginalFilename().trim().equals("") && imagemProduto != null) {

                    Files.write(destino, file.getBytes());

                    // Update existing image entity
                    imagemProduto.setNomeArquivo(nomeArquivo);
                    imagemProduto.setOrdenacao(produto.getImagens().get(i).getOrdenacao());
                    imagemProduto.setPrincipal(produto.getImagens().get(i).isPrincipal());
                    imagemProduto.setProduto(produto);
                    imgRepository.save(imagemProduto);
                } else {

                    Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

                    ImagemProduto imgEntity = new ImagemProduto();
                    imgEntity.setNomeArquivo(nomeArquivo);
                    imgEntity.setOrdenacao(produto.getImagens().get(i).getOrdenacao());
                    imgEntity.setPrincipal(produto.getImagens().get(i).isPrincipal());
                    imgEntity.setProduto(produto);
                    imagensEntities.add(imgEntity);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        produto.setImagens(imagensEntities);
        produtoRepository.save(produto);
        return "redirect:/backoffice/produtos";
    }

    @PostMapping("/ativar-desativar/{id}")
    public String ativoDesativo(@PathVariable Long id) {
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
        return "/admin/usuarios-pesquisa";
    }
    
}
