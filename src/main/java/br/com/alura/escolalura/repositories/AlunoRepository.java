package br.com.alura.escolalura.repositories;

import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import br.com.alura.escolalura.models.Aluno;

@Repository
public class AlunoRepository {

    public void salvar(Aluno aluno) {
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("test");
        MongoCollection<Aluno> alunos = database.getCollection("alunos", Aluno.class);
        alunos.insertOne(aluno);
        mongoClient.close();
    }

}
