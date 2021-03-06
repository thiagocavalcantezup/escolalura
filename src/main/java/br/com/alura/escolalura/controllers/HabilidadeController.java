package br.com.alura.escolalura.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.alura.escolalura.models.Aluno;
import br.com.alura.escolalura.models.Habilidade;
import br.com.alura.escolalura.repositories.AlunoRepository;

@Controller
public class HabilidadeController {

    private final AlunoRepository alunoRepository;

    public HabilidadeController(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    @GetMapping("/alunos/{alunoId}/habilidades/nova")
    public String novo(@PathVariable String alunoId, Model model) {
        Aluno aluno = alunoRepository.encontrarPorId(alunoId);

        model.addAttribute("aluno", aluno);
        model.addAttribute("habilidade", new Habilidade());

        return "habilidade/nova";
    }

    @PostMapping("/alunos/{alunoId}/habilidades")
    public String criar(@PathVariable String alunoId, @ModelAttribute Habilidade habilidade) {
        Aluno aluno = alunoRepository.encontrarPorId(alunoId);
        aluno.adicionar(habilidade);

        alunoRepository.salvar(aluno);

        return "redirect:/alunos";
    }

}
