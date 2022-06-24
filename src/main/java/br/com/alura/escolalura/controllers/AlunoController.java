package br.com.alura.escolalura.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.alura.escolalura.models.Aluno;

@Controller
public class AlunoController {

    @GetMapping("/alunos/novo")
    public String novo(Model model) {
        model.addAttribute("aluno", new Aluno());
        return "aluno/novo";
    }

    @PostMapping("/alunos")
    public String criar(@ModelAttribute Aluno aluno) {
        System.out.println("Aluno para salvar: " + aluno);
        return "redirect:/";
    }

}
