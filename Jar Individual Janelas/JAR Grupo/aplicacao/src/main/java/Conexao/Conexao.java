package Conexao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class Conexao {

    private JdbcTemplate conexaoDoBanco;
    private JdbcTemplate conexaoDBWIN;

    //Construtor de Configuração  do Banco
    public Conexao() {
        BasicDataSource dataSource = new BasicDataSource();

        //Drive de Conexão com o Banco
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost/sentinel_system");
        dataSource.setUsername("root");
        dataSource.setPassword("Guto12##");

        conexaoDoBanco = new JdbcTemplate(dataSource);

        BasicDataSource dataSourceSQLServer = new BasicDataSource();

        dataSourceSQLServer.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSourceSQLServer.setUrl("jdbc:sqlserver://3.213.47.135;databaseName=sentinel_system;trustServerCertificate=true");
        dataSourceSQLServer.setUsername("sa");
        dataSourceSQLServer.setPassword("@Thigas844246");

        conexaoDBWIN = new JdbcTemplate(dataSourceSQLServer);

    }

    //Responsavel por retornar a conexão do Banco
    public JdbcTemplate getConexaoDoBanco() {

        return conexaoDoBanco;
    }

    public JdbcTemplate getConexaoDBWIN() {
        return conexaoDBWIN;
    }
}
