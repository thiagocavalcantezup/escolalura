package br.com.alura.escolalura.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.alura.escolalura.models.Aluno;
import br.com.alura.escolalura.models.Nota;
import br.com.alura.escolalura.repositories.AlunoRepository;

@Controller
public class NotaController {

    private final AlunoRepository alunoRepository;

    public NotaController(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    @GetMapping("/alunos/{alunoId}/notas/nova")
    public String novo(@PathVariable String alunoId, Model model) {
        Aluno aluno = alunoRepository.encontrarPorId(alunoId);

        model.addAttribute("aluno", aluno);
        model.addAttribute("nota", new Nota());

        return "nota/nova";
    }

    @PostMapping("/alunos/{alunoId}/notas")
    public String criar(@PathVariable String alunoId, @ModelAttribute Nota nota) {
        Aluno aluno = alunoRepository.encontrarPorId(alunoId);
        aluno.adicionar(nota);

        alunoRepository.salvar(aluno);

        return "redirect:/alunos";
    }

}
