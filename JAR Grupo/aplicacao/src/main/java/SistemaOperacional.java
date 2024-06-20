import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.sistema.Sistema;
import com.github.britooo.looca.api.util.Conversor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class SistemaOperacional {

    Looca looca = new Looca();
    Sistema sistema = looca.getSistema();

    private String data_inicializacao;

    private String tempo_atividade;

    public SistemaOperacional(String data_inicializacao, String tempo_atividade) {
        this.data_inicializacao = data_inicializacao;
        this.tempo_atividade = tempo_atividade;
    }

    public SistemaOperacional() {
    }

    public void coletarDadosDeSistemaOperacional(JdbcTemplate con, JdbcTemplate conWin, Integer idServidor, Integer idServidorNuvem) {

        // Obtendo a data e hora da última inicialização do sistema
        LocalDateTime dataInicializacao = LocalDateTime.ofInstant(Instant.parse(sistema.getInicializado().toString()), ZoneId.systemDefault());

        // Obtendo apenas a parte da data
        LocalDate dataSemHora = dataInicializacao.toLocalDate();

        // Criando um formato de data para exibir apenas a data
        DateTimeFormatter formatoDataConsole = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Criando um formato de data para guardar no banco
        DateTimeFormatter formatoDataBanco = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        setData_inicializacao(dataSemHora.format(formatoDataBanco));

        // Formatando a data para exibir apenas a data no formato desejado
        String dataFormatadaConsole = dataSemHora.format(formatoDataConsole);

        setTempo_atividade(Conversor.formatarSegundosDecorridos(sistema.getTempoDeAtividade()).replace("days", "dias"));

        System.out.println("\nSISTEMA");

        System.out.println("Data da ultima inicializacao: " + dataFormatadaConsole);
        System.out.println("Tempo de Atividade: " + tempo_atividade);


        con.update("INSERT INTO SistemaOperacionalRegistro (data_inicializacao, tempo_atividade, fk_servidor) VALUES (?, ?, ?)",
                data_inicializacao,
                tempo_atividade, idServidor);

        conWin.update("INSERT INTO SistemaOperacionalRegistro (data_inicializacao, tempo_atividade, fk_servidor) VALUES (?, ?, ?)",
                data_inicializacao,
                tempo_atividade, idServidorNuvem);

    }

    @Override
    public String toString() {
        return "|" +
                "data_inicializacao = " + data_inicializacao + '\'' +
                ", tempo_atividade = " + tempo_atividade + '\'' +
                '|';
    }

    public String getData_inicializacao() {
        return data_inicializacao;
    }

    public void setData_inicializacao(String data_inicializacao) {
        this.data_inicializacao = data_inicializacao;
    }

    public String getTempo_atividade() {
        return tempo_atividade;
    }

    public void setTempo_atividade(String tempo_atividade) {
        this.tempo_atividade = tempo_atividade;
    }
}