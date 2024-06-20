package Componentes;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.util.Conversor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class MemoriaAplicacao extends Componente {

    Looca looca = new Looca();
    Memoria memoria = looca.getMemoria();

    private Long total;

    private Long uso;

    public MemoriaAplicacao(TipoComponente tipo, Long total, Long uso) {
        super(tipo);
        this.total = total;
        this.uso = uso;
    }

    public MemoriaAplicacao() {
    }

    @Override
    public Integer getIdTipoComponente() {

        setTipo(TipoComponente.MEMORIA);

        return getTipo().getId_tipo_componente();
    }

    @Override
    public void coletarDadosFixos(JdbcTemplate con, JdbcTemplate conWin, Integer idServidor, Integer idServidorNuvem) {
        Integer id_tipo_componente = getIdTipoComponente();
        setTotal(memoria.getTotal());

        Integer id_componente = null;
        Integer id_componente_nuvem = null;

        // Pegando ID do Componente
        try {
            id_componente = con.queryForObject("SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ?",
                    Integer.class, idServidor, id_tipo_componente);
        } catch (EmptyResultDataAccessException e) {
            // Se não encontrar resultado, id_componente permanece null
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar componente", e);
        }

        try {
            id_componente_nuvem = conWin.queryForObject("SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ?",
                    Integer.class, idServidorNuvem, id_tipo_componente);
        } catch (EmptyResultDataAccessException e) {
            // Se não encontrar resultado, id_componente_nuvem permanece null
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar componente na nuvem", e);
        }

        if (id_componente == null) {
            synchronized (this) {
                try {
                    id_componente = con.queryForObject("SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ?",
                            Integer.class, idServidor, id_tipo_componente);
                } catch (EmptyResultDataAccessException e) {
                    id_componente = null;
                }

                if (id_componente == null) {
                    con.update("INSERT INTO Componente (total_gib, fk_tipo_componente, fk_servidor) VALUES (?, ?, ?)",
                            Conversor.formatarBytes(total).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                            id_tipo_componente, idServidor);
                }
            }
        }

        if (id_componente_nuvem == null) {
            synchronized (this) {
                try {
                    id_componente_nuvem = conWin.queryForObject("SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ?",
                            Integer.class, idServidorNuvem, id_tipo_componente);
                } catch (EmptyResultDataAccessException e) {
                    id_componente_nuvem = null;
                }

                if (id_componente_nuvem == null) {
                    conWin.update("INSERT INTO Componente (total_gib, fk_tipo_componente, fk_servidor) VALUES (?, ?, ?)",
                            Conversor.formatarBytes(total).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                            id_tipo_componente, idServidorNuvem);
                }
            }
        }
    }

    @Override
    public void coletarDadosDinamicos(JdbcTemplate con, JdbcTemplate conWin, Integer idServidor, Integer idServidorNuvem) {

        Integer id_tipo_componente = getIdTipoComponente();

        Integer id_componente;
        Integer id_componente_nuvem;

        //Pegando ID  do Componente
        try {
            id_componente = con.queryForObject("SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ?", Integer.class, idServidor, id_tipo_componente);
            id_componente_nuvem = conWin.queryForObject("SELECT id_componente FROM Componente WHERE fk_servidor = ? AND fk_tipo_componente = ?", Integer.class, idServidorNuvem, id_tipo_componente);

        } catch (Exception e) {
            id_componente = null;
            id_componente_nuvem = null;
        }

        setUso(memoria.getEmUso());

        System.out.println("\nMEMORIA");

        System.out.println("Em Uso: " + Conversor.formatarBytes(uso));

        con.update("INSERT INTO Registro (uso, fk_componente) VALUES (?, ?)",
                Conversor.formatarBytes(uso).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                id_componente);

        conWin.update("INSERT INTO Registro (uso, fk_componente) VALUES (?, ?)",
                Conversor.formatarBytes(uso).replace("GiB", "").replace("MiB", "").replace("KiB", "").replace(",", "."),
                id_componente_nuvem);


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
