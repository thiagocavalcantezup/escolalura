package br.com.alura.escolalura.repositories;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import br.com.alura.escolalura.codecs.AlunoCodec;
import br.com.alura.escolalura.models.Aluno;
import br.com.alura.escolalura.models.ClassificacaoAluno;

@Repository
public class AlunoRepository {

    private MongoClient mongoClient;
    private MongoDatabase database;

    public void abrirConexao() {
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

    private void fecharConexao() {
        this.mongoClient.close();
    }

    public void salvar(Aluno aluno) {
        abrirConexao();

        MongoCollection<Aluno> alunos = this.database.getCollection("alunos", Aluno.class);

        if (aluno.getId() == null) {
            alunos.insertOne(aluno);
        } else {
            alunos.updateOne(
                Filters.eq("_id", aluno.getId()), new Document().append("$set", aluno)
            );
        }

        fecharConexao();
    }

    public List<Aluno> encontrarTodos() {
        abrirConexao();

        MongoCollection<Aluno> alunos = this.database.getCollection("alunos", Aluno.class);
        MongoCursor<Aluno> resultado = alunos.find().iterator();
        List<Aluno> alunosEncontrados = criarLista(resultado);

        fecharConexao();

        return alunosEncontrados;
    }

    public List<Aluno> encontrarTodosPorNome(String nome) {
        abrirConexao();

        MongoCollection<Aluno> alunos = this.database.getCollection("alunos", Aluno.class);
        MongoCursor<Aluno> resultado = alunos.find(Filters.eq("nome", nome)).iterator();
        List<Aluno> alunosEncontrados = criarLista(resultado);

        fecharConexao();

        return alunosEncontrados;
    }

    public List<Aluno> encontrarTodosPorClassificacaoNotaDeCorte(ClassificacaoAluno classificacao,
                                                                 Double notaDeCorte) {
        abrirConexao();

        MongoCollection<Aluno> alunos = this.database.getCollection("alunos", Aluno.class);
        Bson filter;

        if (classificacao.equals(ClassificacaoAluno.REPROVADO)) {
            filter = Filters.lt("notas", notaDeCorte);
        } else {
            filter = Filters.gte("notas", notaDeCorte);
        }

        MongoCursor<Aluno> resultado = alunos.find(filter).iterator();
        List<Aluno> alunosEncontrados = criarLista(resultado);

        fecharConexao();

        return alunosEncontrados;
    }

    private List<Aluno> criarLista(MongoCursor<Aluno> resultado) {
        List<Aluno> lista = new ArrayList<>();

        while (resultado.hasNext()) {
            lista.add(resultado.next());
        }

        return lista;
    }

    public Aluno encontrarPorId(String id) {
        abrirConexao();

        MongoCollection<Aluno> alunos = this.database.getCollection("alunos", Aluno.class);
        Aluno aluno = alunos.find(Filters.eq("_id", new ObjectId(id))).first();

        fecharConexao();

        return aluno;
    }

};
