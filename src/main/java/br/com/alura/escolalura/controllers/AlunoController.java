package br.com.alura.escolalura.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.alura.escolalura.models.Aluno;
import br.com.alura.escolalura.models.ClassificacaoAluno;
import br.com.alura.escolalura.repositories.AlunoRepository;
import br.com.alura.escolalura.services.GeolocalizacaoService;

@Controller
public class AlunoController {

    private final AlunoRepository alunoRepository;
    private final GeolocalizacaoService geolocalizacaoService;

    public AlunoController(AlunoRepository alunoRepository,
                           GeolocalizacaoService geolocalizacaoService) {
        this.alunoRepository = alunoRepository;
        this.geolocalizacaoService = geolocalizacaoService;
    }

    @GetMapping("/alunos/novo")
    public String novo(Model model) {
        model.addAttribute("aluno", new Aluno());
        return "aluno/novo";
    }

    @PostMapping("/alunos")
    public String criar(@ModelAttribute Aluno aluno) {
        List<Double> coordinates;

        try {
            coordinates = geolocalizacaoService.obterCoordenadas(aluno.getContato());
        } catch (Exception e) {
            coordinates = new ArrayList<>();
            System.out.println("Endereço não localizado.");
            e.printStackTrace();
        }

        aluno.getContato().setCoordinates(coordinates);
        alunoRepository.salvar(aluno);
        return "redirect:/";
    }

    @GetMapping("/alunos")
    public String listar(Model model) {
        model.addAttribute("alunos", alunoRepository.encontrarTodos());
        return "aluno/listar";
    }

    @GetMapping("/alunos/{id}")
    public String visualizar(@PathVariable String id, Model model) {
        model.addAttribute("aluno", alunoRepository.encontrarPorId(id));
        return "aluno/visualizar";
    }

    @GetMapping("/alunos/pesquisar-nome")
    public String pesquisarNome(@RequestParam(required = false) String nome, Model model) {
        if (nome != null) {
            model.addAttribute("alunos", alunoRepository.encontrarTodosPorNome(nome));
        }

        return "aluno/pesquisar-nome";
    }

    @GetMapping("/alunos/pesquisar-nota")
    public String pesquisarNota(@RequestParam(required = false) ClassificacaoAluno classificacao,
                                @RequestParam(required = false) Double notaDeCorte, Model model) {
        if (classificacao != null && notaDeCorte != null) {
            model.addAttribute(
                "alunos",
                alunoRepository.encontrarTodosPorClassificacaoNotaDeCorte(
                    classificacao, notaDeCorte
                )
            );
        }

        return "aluno/pesquisar-nota";
    }

    @GetMapping("/alunos/pesquisar-geolocalizacao")
    public String pesquisarGeolocalizacao(@RequestParam(required = false) String alunoId,
                                          Model model) {
        if (alunoId != null) {
            Aluno aluno = alunoRepository.encontrarPorId(alunoId);
            List<Aluno> alunosProximos = alunoRepository.encontrarPorGeolocalizacao(aluno);
            model.addAttribute("alunosProximos", alunosProximos);
        }

        model.addAttribute("alunos", alunoRepository.encontrarTodos());

        return "aluno/pesquisar-geolocalizacao";
    }

}
