package br.com.alura.escolalura.codecs;

import java.time.LocalDate;
import java.time.ZoneId;

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

        Document document = new Document();

        document.put("_id", id);
        document.put("nome", nome);
        document.put("data_nascimento", dataNascimento);
        document.put("curso", new Document().append("nome", curso.getNome()));

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

        Document curso = (Document) document.get("curso");
        if (curso != null) {
            String nomeCurso = curso.getString("nome");
            aluno.setCurso(new Curso(nomeCurso));
        }

        return aluno;
    }

}
