import Logs.Logs;
import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.processos.Processo;
import com.github.britooo.looca.api.group.processos.ProcessoGrupo;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProcessoAplicacao {

    Looca looca = new Looca();
    ProcessoGrupo grupoProcesso = looca.getGrupoDeProcessos();

    private Integer pid;

    private String nome;

    private Double uso_cpu;

    private Double uso_memoria;

    Logs log = new Logs();

    public ProcessoAplicacao(Integer pid, String nome, Double uso_cpu, Double uso_memoria) {
        this.pid = pid;
        this.nome = nome;
        this.uso_cpu = uso_cpu;
        this.uso_memoria = uso_memoria;
    }

    public ProcessoAplicacao() {
    }

    public void coletarDadosDeProcessos(JdbcTemplate con, JdbcTemplate conWin, Integer idServidor, Integer idServidorNuvem) {

        System.out.println("\nPROCESSOS");

        // 5 PRINCIPAIS PROCESSOS COM USOCPU ACIMA DE 50%
        List<Processo> listaProcessos = grupoProcesso.getProcessos();

        // Ordena a lista de processos com base no uso de CPU, do maior para o menor
        listaProcessos.sort((p1, p2) -> Double.compare(p2.getUsoCpu(), p1.getUsoCpu()));

        // Pega os 5 primeiros processos com maior uso de CPU
        List<Processo> top5Processos = listaProcessos.subList(0, Math.min(5, listaProcessos.size()));

        for (Processo processo : top5Processos) {
            if (processo.getUsoCpu() >= 50.0) {
                setNome(processo.getNome());
                setPid(processo.getPid());
                setUso_cpu(processo.getUsoCpu());
                setUso_memoria(processo.getUsoMemoria());

                System.out.println("PID: " + pid);
                System.out.println("Nome:" + nome);
                System.out.println("Uso de CPU: " + String.format("%.1f", uso_cpu));
                System.out.println("Uso de Memoria: " + String.format("%.1f", uso_memoria));

                // Verifica se o processo já existe no banco de dados
                Integer count = con.queryForObject("SELECT COUNT(*) FROM ProcessoRegistro WHERE pid = ? AND fk_servidor = ?",
                        Integer.class, pid, idServidor);
                Integer count2 = conWin.queryForObject("SELECT COUNT(*) FROM ProcessoRegistro WHERE pid = ? AND fk_servidor = ?",
                        Integer.class, pid, idServidorNuvem);

                String data;
                log.setSistemaOperacional(looca.getSistema().getSistemaOperacional());
                log.setHostName(looca.getRede().getParametros().getHostName());
                data = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
                log.setData(data);

                // Obter a data atual para o nome do arquivo de log
                String dataArquivo = new SimpleDateFormat("yyyyMMdd").format(new Date());
                String nomeArquivoLog = ".\\" + dataArquivo + ".txt";

                File logFile = new File(nomeArquivoLog);
                boolean isNewFile = !logFile.exists();

                if (count != null && count > 0 && count2 != null && count2 > 0) {
                    // Atualiza o registro existente
                    con.update("UPDATE ProcessoRegistro SET nome = ?, uso_cpu = ?, uso_memoria = ? WHERE pid = ? AND fk_servidor = ?",
                            nome, String.format("%.1f", uso_cpu).replace(",", "."), uso_memoria, pid, idServidor);
                    conWin.update("UPDATE ProcessoRegistro SET nome = ?, uso_cpu = ?, uso_memoria = ? WHERE pid = ? AND fk_servidor = ?",
                            nome, String.format("%.1f", uso_cpu).replace(",", "."), uso_memoria, pid, idServidorNuvem);
                    con.update("UPDATE ProcessoRegistro SET nome = ?, uso_cpu = ?, uso_memoria = ? WHERE pid = ? AND fk_servidor = ?",
                            nome, String.format("%.1f", uso_cpu).replace(",", "."), uso_memoria, pid, idServidor);

                    log.setMensagem("Registro existente atualizado com sucesso");

                    System.out.println(log.toString().replace("idMaquina: null\n", "").replace("\t", ""));

                    try (FileWriter writer = new FileWriter(nomeArquivoLog, true)) {
                        if (isNewFile) {
                            writer.write("===Início do Log===\n");
                            writer.write("Hostname: " + log.getHostName() + "\n");
                            writer.write("Sistema Operacional: " + log.getSistemaOperacional() + "\n\n");
                        }
                        writer.write("[" + data + "] Processo: " + log.getMensagem() + "\n");
                    } catch (IOException u) {
                        System.out.println("Erro ao gerar log" + u.getMessage());
                    }

                } else {
                    // Insere um novo registro
                    con.update("INSERT INTO ProcessoRegistro (pid, nome, uso_cpu, uso_memoria, fk_servidor) VALUES (?, ?, ?, ?, ?)",
                            pid, nome, String.format("%.1f", uso_cpu).replace(",", "."), uso_memoria, idServidor);
                    conWin.update("INSERT INTO ProcessoRegistro (pid, nome, uso_cpu, uso_memoria, fk_servidor) VALUES (?, ?, ?, ?, ?)",
                            pid, nome, String.format("%.1f", uso_cpu).replace(",", "."), uso_memoria, idServidorNuvem);
                }
            }
        }

    }

    @Override
    public String toString() {
        return "|" +
                "pid = " + pid +
                ", nome = " + nome + '\'' +
                ", uso_cpu = " + uso_cpu +
                ", uso_memoria = " + uso_memoria +
                '|';
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getUso_cpu() {
        return uso_cpu;
    }

    public void setUso_cpu(Double uso_cpu) {
        this.uso_cpu = uso_cpu;
    }

    public Double getUso_memoria() {
        return uso_memoria;
    }

    public void setUso_memoria(Double uso_memoria) {
        this.uso_memoria = uso_memoria;
    }
}
