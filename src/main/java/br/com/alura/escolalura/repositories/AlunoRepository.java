package br.com.alura.escolalura.repositories;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import br.com.alura.escolalura.codecs.AlunoCodec;
import br.com.alura.escolalura.models.Aluno;

@Repository
public class AlunoRepository {

    private MongoClient mongoClient;
    private MongoDatabase database;

    public void criarConexao() {
        Codec<Document> codec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
        AlunoCodec alunoCodec = new AlunoCodec(codec);

        CodecRegistry registry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromCodecs(alunoCodec)
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                                                          .codecRegistry(registry)
                                                          .build();

        this.mongoClient = MongoClients.create(settings);
        this.database = mongoClient.getDatabase("test");
    }

    public void salvar(Aluno aluno) {
        criarConexao();

        MongoCollection<Aluno> alunos = this.database.getCollection("alunos", Aluno.class);
        alunos.insertOne(aluno);

        this.mongoClient.close();
    }

    public List<Aluno> encontrarTodos() {
        criarConexao();

        MongoCollection<Aluno> alunos = this.database.getCollection("alunos", Aluno.class);
        MongoCursor<Aluno> resultado = alunos.find().iterator();
        List<Aluno> alunosEncontrados = new ArrayList<>();

        while (resultado.hasNext()) {
            Aluno aluno = resultado.next();
            alunosEncontrados.add(aluno);
        }

        this.mongoClient.close();

        return alunosEncontrados;
    }

};