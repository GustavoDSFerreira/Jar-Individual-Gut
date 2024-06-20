package Componentes;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.processador.Processador;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class ProcessadorAplicacao extends Componente {

    Looca looca = new Looca();
    Processador processador = looca.getProcessador();

    private String nome;

    private Double uso;

    public ProcessadorAplicacao(TipoComponente tipo, String nome, Double uso) {
        super(tipo);
        this.nome = nome;
        this.uso = uso;
    }

    public ProcessadorAplicacao() {

    }

    @Override
    public Integer getIdTipoComponente() {
        setTipo(TipoComponente.CPU);

        return getTipo().getId_tipo_componente();
    }

    @Override
    public void coletarDadosFixos(JdbcTemplate con, JdbcTemplate conWin, Integer idServidor, Integer idServidorNuvem) {
        Integer id_tipo_componente = getIdTipoComponente();

        setNome(processador.getNome());

        Integer id_componente = null;
        Integer id_componente_nuvem = null;

        // Pegando ID do Componente
        try {
            id_componente = con.queryForObject(
                    "SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ? AND nome = ?",
                    Integer.class, idServidor, id_tipo_componente, nome);
        } catch (EmptyResultDataAccessException e) {
            id_componente = null;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar componente", e);
        }

        try {
            id_componente_nuvem = conWin.queryForObject(
                    "SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ? AND nome = ?",
                    Integer.class, idServidorNuvem, id_tipo_componente, nome);
        } catch (EmptyResultDataAccessException e) {
            id_componente_nuvem = null;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar componente na nuvem", e);
        }

        if (id_componente == null) {
            con.update(
                    "INSERT INTO Componente (nome, fk_tipo_componente, fk_servidor) VALUES (?, ?, ?)",
                    nome, id_tipo_componente, idServidor
            );

            // Atualizar id_componente após a inserção
            id_componente = con.queryForObject(
                    "SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ? AND nome = ?",
                    Integer.class, idServidor, id_tipo_componente, nome
            );
        }

        if (id_componente_nuvem == null) {
            conWin.update(
                    "INSERT INTO Componente (nome, fk_tipo_componente, fk_servidor) VALUES (?, ?, ?)",
                    nome, id_tipo_componente, idServidorNuvem
            );

            // Atualizar id_componente_nuvem após a inserção
            id_componente_nuvem = conWin.queryForObject(
                    "SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ? AND nome = ?",
                    Integer.class, idServidorNuvem, id_tipo_componente, nome
            );
        }
    }

    @Override
    public void coletarDadosDinamicos(JdbcTemplate con, JdbcTemplate conWin, Integer idServidor, Integer idServidorNuvem) {
        Integer id_tipo_componente = getIdTipoComponente();

        Integer id_componente = null;
        Integer id_componente_nuvem = null;

        // Pegando ID do Componente
        try {
            id_componente = con.queryForObject(
                    "SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ? AND nome = ?",
                    Integer.class, idServidor, id_tipo_componente, nome);
        } catch (EmptyResultDataAccessException e) {
            id_componente = null;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar componente", e);
        }

        try {
            id_componente_nuvem = conWin.queryForObject(
                    "SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ? AND nome = ?",
                    Integer.class, idServidorNuvem, id_tipo_componente, nome);
        } catch (EmptyResultDataAccessException e) {
            id_componente_nuvem = null;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar componente na nuvem", e);
        }

        setUso(processador.getUso());

        System.out.println("\nPROCESSADOR");
        System.out.println("Em Uso: " + String.format("%.1f", uso));

        if (id_componente != null) {
            con.update(
                    "INSERT INTO Registro (uso, fk_componente) VALUES (?, ?)",
                    String.format("%.1f", uso).replace(",", "."),
                    id_componente
            );
        } else {
            System.out.println("Componente não encontrado para o servidor " + idServidor);
        }

        if (id_componente_nuvem != null) {
            conWin.update(
                    "INSERT INTO Registro (uso, fk_componente) VALUES (?, ?)",
                    String.format("%.1f", uso).replace(",", "."),
                    id_componente_nuvem
            );
        } else {
            System.out.println("Componente na nuvem não encontrado para o servidor " + idServidorNuvem);
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getUso() {
        return uso;
    }

    public void setUso(Double uso) {
        this.uso = uso;
    }
}
