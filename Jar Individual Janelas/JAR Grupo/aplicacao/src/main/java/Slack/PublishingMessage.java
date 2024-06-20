package Slack;

import Componentes.TipoComponente;
import Conexao.Conexao;
import com.slack.api.methods.SlackApiException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import com.slack.api.Slack;
import org.springframework.jdbc.core.JdbcTemplate;

public class PublishingMessage {

    //    Conexão
    static Conexao conexao = new Conexao();
    static JdbcTemplate con = conexao.getConexaoDoBanco();
    JdbcTemplate conWin = conexao.getConexaoDBWIN();

    //    Cpu Registro

    public static void publishMessageCpuRegistro(String id) {
        var client = Slack.getInstance().methods();
        var logger = LoggerFactory.getLogger("my-awesome-slack-app");

        // Query SQL para selecionar a utilização do Cpu
        String sql = "SELECT c.id_componente, c.nome, c.total_gib, c.data_registro, tc.tipo AS tipo_componente, \n" +
                "s.nome AS nome_servidor FROM Componente c \n" +
                "JOIN TipoComponente tc ON c.fk_tipo_componente = tc.id_tipo_componente \n" +
                "JOIN Servidor s ON c.fk_servidor = s.id_servidor \n" +
                "WHERE tc.tipo = 'CPU';";

        // Parâmetros da consulta
        String pMin = "SELECT parametro_min FROM ConfiguracaoAlerta \n" +
                "WHERE fk_tipo_componente = (SELECT id_tipo_componente FROM TipoComponente WHERE tipo = 'CPU');";

        String pMax = "SELECT parametro_max FROM ConfiguracaoAlerta \n" +
                "WHERE fk_tipo_componente = (SELECT id_tipo_componente FROM TipoComponente WHERE tipo = 'CPU');";


        String texto = "";

        try {
            // Executando a consulta e armazenando o resultado em uma variável
            Double resultado = con.queryForObject(sql, Double.class);
            Double parametroMin = con.queryForObject(pMin, Double.class);
            Double parametroMax = con.queryForObject(pMax, Double.class);

            if (resultado > parametroMax) {
                texto = "Erro: utilização do Cpu acima de " + parametroMax;
            } else if (resultado < parametroMin) {
                texto = "Erro: utilização do Cpu abaixo de " + parametroMin;
            }
        } catch (Exception e) {
            logger.error("Erro ao consultar o banco de dados: {}", e.getMessage(), e);
            texto = "Erro ao consultar o banco de dados.";
        }

        try {
            // Call the chat.postMessage method using the built-in WebClient
            String finalTexto = texto;
            var result = client.chatPostMessage(r -> r
                    // The token you used to initialize your app
                    .token("xoxb-7153877952561-7260686794097-gSYiS0Gpds6yrIGREsxV4RKt")
                    .channel(id)
                    .text(finalTexto)
            );
            // Print result, which includes information about the message (like TS)
            logger.info("result {}", result);
        } catch (IOException | SlackApiException e) {
            logger.error("error: {}", e.getMessage(), e);
        }
    }


    //    Memoria Registro

    public static void publishMessageMemoriaRegistro(String id) {
        var client = Slack.getInstance().methods();
        var logger = LoggerFactory.getLogger("my-awesome-slack-app");

        // Query SQL para selecionar a utilização do Memoria
        String sql = "SELECT c.id_componente, c.nome, c.total_gib, c.data_registro, tc.tipo AS tipo_componente, \n" +
                "s.nome AS nome_servidor FROM Componente c \n" +
                "JOIN TipoComponente tc ON c.fk_tipo_componente = tc.id_tipo_componente \n" +
                "JOIN Servidor s ON c.fk_servidor = s.id_servidor \n" +
                "WHERE tc.tipo = 'MEMORIA';";

        // Parâmetros da consulta
        String pMin = "SELECT parametro_min FROM ConfiguracaoAlerta \n" +
                "WHERE fk_tipo_componente = (SELECT id_tipo_componente FROM TipoComponente WHERE tipo = 'MEMORIA');";

        String pMax = "SELECT parametro_max FROM ConfiguracaoAlerta \n" +
                "WHERE fk_tipo_componente = (SELECT id_tipo_componente FROM TipoComponente WHERE tipo = 'MEMORIA');";


        String texto = "";

        try {
            // Executando a consulta e armazenando o resultado em uma variável
            Integer resultado = con.queryForObject(sql, Integer.class);
            Double parametroMin = con.queryForObject(pMin, Double.class);
            Double parametroMax = con.queryForObject(pMax, Double.class);

            if (resultado > parametroMax) {
                texto = "Erro: utilização do memória acima de " + parametroMax;
            } else if (resultado < parametroMin) {
                texto = "Erro: utilização do memória abaixo de " + parametroMin;
            }
        } catch (Exception e) {
            logger.error("Erro ao consultar o banco de dados: {}", e.getMessage(), e);
            texto = "Erro ao consultar o banco de dados.";
        }

        try {
            // Call the chat.postMessage method using the built-in WebClient
            String finalTexto = texto;
            var result = client.chatPostMessage(r -> r
                    // The token you used to initialize your app
                    .token("xoxb-7153877952561-7260686794097-gSYiS0Gpds6yrIGREsxV4RKt")
                    .channel(id)
                    .text(finalTexto)
            );
            // Print result, which includes information about the message (like TS)
            logger.info("result {}", result);
        } catch (IOException | SlackApiException e) {
            logger.error("error: {}", e.getMessage(), e);
        }
    }


    //    Disco Registro

    public static void publishMessageDiscoRegistro(String id) {
        var client = Slack.getInstance().methods();
        var logger = LoggerFactory.getLogger("my-awesome-slack-app");

        // Query SQL para selecionar a utilização do Disco
        String sql = "SELECT c.id_componente, c.nome, c.total_gib, c.data_registro, tc.tipo AS tipo_componente, \n" +
                "s.nome AS nome_servidor FROM Componente c \n" +
                "JOIN TipoComponente tc ON c.fk_tipo_componente = tc.id_tipo_componente \n" +
                "JOIN Servidor s ON c.fk_servidor = s.id_servidor \n" +
                "WHERE tc.tipo = 'DISCO';";


        // Parâmetros da consulta
        String pMin = "SELECT parametro_min FROM ConfiguracaoAlerta \n" +
                "WHERE fk_tipo_componente = (SELECT id_tipo_componente FROM TipoComponente WHERE tipo = 'DISCO');";

        String pMax = "SELECT parametro_max FROM ConfiguracaoAlerta \n" +
                "WHERE fk_tipo_componente = (SELECT id_tipo_componente FROM TipoComponente WHERE tipo = 'DISCO');";


        String texto = "";

        try {
            // Executando a consulta e armazenando o resultado em uma variável
            Integer resultado = con.queryForObject(sql, Integer.class);
            Double parametroMin = con.queryForObject(pMin, Double.class);
            Double parametroMax = con.queryForObject(pMax, Double.class);

            if (resultado > parametroMax) {
                texto = "Erro: utilização do disco acima de " + parametroMax;
            } else if (resultado < parametroMin) {
                texto = "Erro: utilização do disco abaixo de " + parametroMin;
            }
        } catch (Exception e) {
            logger.error("Erro ao consultar o banco de dados: {}", e.getMessage(), e);
            texto = "Erro ao consultar o banco de dados.";
        }
        try {
            // Call the chat.postMessage method using the built-in WebClient
            String finalTexto = texto;
            var result = client.chatPostMessage(r -> r
                    // The token you used to initialize your app
                    .token("xoxb-7153877952561-7260686794097-gSYiS0Gpds6yrIGREsxV4RKt")
                    .channel(id)
                    .text(finalTexto)
            );
            // Print result, which includes information about the message (like TS)
            logger.info("result {}", result);
        } catch (IOException | SlackApiException e) {
            logger.error("error: {}", e.getMessage(), e);
        }
    }
}


