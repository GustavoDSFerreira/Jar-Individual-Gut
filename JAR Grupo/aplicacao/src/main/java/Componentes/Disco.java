package Componentes;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.DiscoGrupo;
import com.github.britooo.looca.api.group.discos.Volume;
import com.github.britooo.looca.api.util.Conversor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class Disco extends Componente {

    Looca looca = new Looca();
    DiscoGrupo grupoDisco = looca.getGrupoDeDiscos();
    List<Volume> volumeDiscos = grupoDisco.getVolumes();

    private String nome;

    private Long total;

    private Long uso;

    public Disco(TipoComponente tipo, String nome, Long total, Long uso) {
        super(tipo);
        this.nome = nome;
        this.total = total;
        this.uso = uso;
    }

    public Disco() {

    }

    @Override
    public Integer getIdTipoComponente() {

        setTipo(TipoComponente.DISCO);

        return getTipo().getId_tipo_componente();
    }

    @Override
    public void coletarDadosFixos(JdbcTemplate con, JdbcTemplate conWin, Integer idServidor, Integer idServidorNuvem) {
        Integer id_tipo_componente = getIdTipoComponente();

        for (Volume volume : volumeDiscos) {
            setNome(volume.getNome());
            setTotal(volume.getTotal());

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

            // Inserir o componente se ele não existir
            if (id_componente == null) {
                con.update(
                        "INSERT INTO Componente (nome, total_gib, fk_tipo_componente, fk_servidor) VALUES (?, ?, ?, ?)",
                        nome,
                        Conversor.formatarBytes(total).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                        id_tipo_componente, idServidor
                );

                // Atualizar id_componente após a inserção
                id_componente = con.queryForObject(
                        "SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ? AND nome = ?",
                        Integer.class, idServidor, id_tipo_componente, nome
                );
            }

            if (id_componente_nuvem == null) {
                conWin.update(
                        "INSERT INTO Componente (nome, total_gib, fk_tipo_componente, fk_servidor) VALUES (?, ?, ?, ?)",
                        nome,
                        Conversor.formatarBytes(total).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                        id_tipo_componente, idServidorNuvem
                );

                // Atualizar id_componente_nuvem após a inserção
                id_componente_nuvem = conWin.queryForObject(
                        "SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ? AND nome = ?",
                        Integer.class, idServidorNuvem, id_tipo_componente, nome
                );
            }
        }
    }

    @Override
    public void coletarDadosDinamicos(JdbcTemplate con, JdbcTemplate conWin, Integer idServidor, Integer idServidorNuvem) {

        Integer id_tipo_componente = getIdTipoComponente();

        for (Volume volume : volumeDiscos) {
            setUso(volume.getTotal() - volume.getDisponivel());
            setNome(volume.getNome());

            Integer id_componente = null;
            Integer id_componente_nuvem = null;

            // Pegando ID do Componente
            try {
                id_componente = con.queryForObject(
                        "SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ? AND nome = ?",
                        Integer.class, idServidor, id_tipo_componente, nome);
            } catch (EmptyResultDataAccessException e) {
                // Se não encontrar resultado, id_componente permanece null
            } catch (Exception e) {
                throw new RuntimeException("Erro ao consultar componente", e);
            }

            try {
                id_componente_nuvem = conWin.queryForObject(
                        "SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ? AND nome = ?",
                        Integer.class, idServidorNuvem, id_tipo_componente, nome);
            } catch (EmptyResultDataAccessException e) {
                // Se não encontrar resultado, id_componente_nuvem permanece null
            } catch (Exception e) {
                throw new RuntimeException("Erro ao consultar componente na nuvem", e);
            }

            System.out.println("\nDISCOS");
            System.out.println("Nome: " + nome);
            System.out.println("Em Uso: " + Conversor.formatarBytes(uso));

            // Verificando se os IDs dos componentes foram encontrados antes de inserir no registro
            if (id_componente != null) {
                con.update("INSERT INTO Registro (uso, fk_componente) VALUES (?, ?)",
                        Conversor.formatarBytes(uso).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                        id_componente);
            } else {
                System.out.println("Componente não encontrado para o servidor " + idServidor + " e volume " + nome);
            }

            if (id_componente_nuvem != null) {
                conWin.update("INSERT INTO Registro (uso, fk_componente) VALUES (?, ?)",
                        Conversor.formatarBytes(uso).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                        id_componente_nuvem);
            } else {
                System.out.println("Componente na nuvem não encontrado para o servidor " + idServidorNuvem + " e volume " + nome);
            }
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getUso() {
        return uso;
    }

    public void setUso(Long uso) {
        this.uso = uso;
    }
}

