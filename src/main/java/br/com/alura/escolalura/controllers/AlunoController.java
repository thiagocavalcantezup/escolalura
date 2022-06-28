package br.com.alura.escolalura.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.alura.escolalura.models.Aluno;
import br.com.alura.escolalura.repositories.AlunoRepository;

@Controller
public class AlunoController {

    private final AlunoRepository alunoRepository;

    public AlunoController(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    @GetMapping("/alunos/novo")
    public String novo(Model model) {
        model.addAttribute("aluno", new Aluno());
        return "aluno/novo";
    }

    @PostMapping("/alunos")
    public String criar(@ModelAttribute Aluno aluno) {
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
    public String pesquisarNome() {
        return "aluno/pesquisar-nome";
    }

    @GetMapping("/alunos/pesquisar")
    public String pesquisar(@RequestParam String nome, Model model) {
        model.addAttribute("alunos", alunoRepository.encontrarTodosPorNome(nome));
        return "aluno/pesquisar-nome";
    }

}
