package br.com.alura.escolalura.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
        List<Aluno> alunos = alunoRepository.encontrarTodos();
        model.addAttribute("alunos", alunos);
        return "aluno/listar";
    }

}
