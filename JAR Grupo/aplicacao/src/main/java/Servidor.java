import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Servidor {

    private Integer id_servidor;

    private String nome;

    private String host_name;

    private String data_cadastro;

    private List<ProcessoAplicacao> dadosDeProcessos;

    private List<RedeAplicacao> dadosDeRede;

    private List<SistemaOperacional> dadosDeSistemaOperacional;

    public Servidor(Integer id_servidor, String nome, String host_name, String data_cadastro) {
        this.id_servidor = id_servidor;
        this.nome = nome;
        this.host_name = host_name;
        this.data_cadastro = data_cadastro;
        this.dadosDeProcessos = new ArrayList<>();
        this.dadosDeRede = new ArrayList<>();
        this.dadosDeSistemaOperacional = new ArrayList<>();
    }

    public Servidor() {
    }

    public void buscarDadosDoServidor(JdbcTemplate con, Integer idServidor) {

        String sqlServidor = "SELECT * FROM Servidor WHERE id_servidor = ?";
        Servidor servidor = con.queryForObject(sqlServidor, new Object[]{idServidor}, new BeanPropertyRowMapper<>(Servidor.class));

        if (servidor != null) {
            this.id_servidor = servidor.getId_servidor();
            this.nome = servidor.getNome();
            this.host_name = servidor.getHost_name();
            this.data_cadastro = servidor.getData_cadastro();

            // Buscando dadosDeProcessos
            String sqlProcessos = "SELECT * FROM ProcessoRegistro WHERE fk_servidor = ?";
            this.dadosDeProcessos = con.query(sqlProcessos, new Object[]{idServidor}, new BeanPropertyRowMapper<>(ProcessoAplicacao.class));

            // Buscando dados de rede
            String sqlRede = "SELECT * FROM RedeRegistro WHERE fk_servidor = ?";
            this.dadosDeRede = con.query(sqlRede, new Object[]{idServidor}, new BeanPropertyRowMapper<>(RedeAplicacao.class));

            // Buscando dados do sistema operacional
            String sqlSO = "SELECT * FROM SistemaOperacionalRegistro WHERE fk_servidor = ?";
            this.dadosDeSistemaOperacional = con.query(sqlSO, new Object[]{idServidor}, new BeanPropertyRowMapper<>(SistemaOperacional.class));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nome: ").append(nome).append("\n");
        sb.append("Hostname: ").append(host_name).append("\n");
        sb.append("Data de Cadastro: ").append(data_cadastro).append("\n");

        sb.append("\nDados de Processos:\n");
        for (ProcessoAplicacao processo : dadosDeProcessos) {
            sb.append(processo).append("\n");
        }

        sb.append("\nDados de Rede:\n");
        for (RedeAplicacao rede : dadosDeRede) {
            sb.append(rede).append("\n");
        }

        sb.append("\nDados do Sistema Operacional:\n");
        for (SistemaOperacional so : dadosDeSistemaOperacional) {
            sb.append(so).append("\n");
        }

        return sb.toString();
    }

    public Integer getId_servidor() {
        return id_servidor;
    }

    public void setId_servidor(Integer id_servidor) {
        this.id_servidor = id_servidor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public String getData_cadastro() {
        return data_cadastro;
    }

    public void setData_cadastro(String data_cadastro) {
        this.data_cadastro = data_cadastro;
    }

    public List<ProcessoAplicacao> getProcessos() {
        return dadosDeProcessos;
    }

    public void setProcessos(List<ProcessoAplicacao> dadosDeProcessos) {
        this.dadosDeProcessos = Servidor.this.dadosDeProcessos;
    }

    public List<RedeAplicacao> getDadosDeRede() {
        return dadosDeRede;
    }

    public void setDadosDeRede(List<RedeAplicacao> dadosDeRede) {
        this.dadosDeRede = dadosDeRede;
    }

    public List<SistemaOperacional> getDadosDeSistemaOperacional() {
        return dadosDeSistemaOperacional;
    }

    public void setDadosDeSistemaOperacional(List<SistemaOperacional> dadosDeSistemaOperacional) {
        this.dadosDeSistemaOperacional = dadosDeSistemaOperacional;
    }
}
