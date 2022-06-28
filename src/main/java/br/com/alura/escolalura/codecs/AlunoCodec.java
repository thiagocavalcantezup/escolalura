package br.com.alura.escolalura.codecs;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import br.com.alura.escolalura.models.Aluno;
import br.com.alura.escolalura.models.Curso;
import br.com.alura.escolalura.models.Habilidade;

public class AlunoCodec implements CollectibleCodec<Aluno> {

    private final Codec<Document> codec;

    public AlunoCodec(Codec<Document> codec) {
        this.codec = codec;
    }

    @Override
    public boolean documentHasId(Aluno aluno) {
        return aluno.getId() != null;
    }

    @Override
    public Aluno generateIdIfAbsentFromDocument(Aluno aluno) {
        return documentHasId(aluno) ? aluno : aluno.criarId();
    }

    @Override
    public BsonValue getDocumentId(Aluno aluno) {
        if (!documentHasId(aluno)) {
            throw new IllegalStateException("Esse documento não possui id.");
        }

        return new BsonString(aluno.getId().toHexString());
    }

    @Override
    public void encode(BsonWriter writer, Aluno aluno, EncoderContext context) {
        ObjectId id = aluno.getId();
        String nome = aluno.getNome();
        LocalDate dataNascimento = aluno.getDataNascimento();
        Curso curso = aluno.getCurso();
        List<Habilidade> habilidades = aluno.getHabilidades();

        Document document = new Document();

        document.put("_id", id);
        document.put("nome", nome);
        document.put("data_nascimento", dataNascimento);
        document.put("curso", new Document().append("nome", curso.getNome()));

        List<Document> habilidadesDocument = new ArrayList<>();
        for (Habilidade habilidade : habilidades) {
            habilidadesDocument.add(
                new Document().append("nome", habilidade.getNome())
                              .append("nivel", habilidade.getNivel())
            );
        }
        document.put("habilidades", habilidadesDocument);

        codec.encode(writer, document, context);
    }

    @Override
    public Class<Aluno> getEncoderClass() {
        return Aluno.class;
    }

    @Override
    public Aluno decode(BsonReader reader, DecoderContext context) {
        Document document = codec.decode(reader, context);
        Aluno aluno = new Aluno();

        aluno.setId(document.getObjectId("_id"));
        aluno.setNome(document.getString("nome"));
        aluno.setDataNascimento(
            document.getDate("data_nascimento")
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
        );

        Document curso = document.get("curso", Document.class);
        if (curso != null) {
            String nomeCurso = curso.getString("nome");
            aluno.setCurso(new Curso(nomeCurso));
        }

        List<Document> habilidadesDocument = document.getList(
            "habilidades", Document.class, new ArrayList<>()
        );
        List<Habilidade> habilidades = new ArrayList<>();
        for (Document habilidadeDocument : habilidadesDocument) {
            habilidades.add(
                new Habilidade(
                    habilidadeDocument.getString("nome"), habilidadeDocument.getString("nivel")
                )
            );
        }
        aluno.setHabilidades(habilidades);

        return aluno;
    }

}
