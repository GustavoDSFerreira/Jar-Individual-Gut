import Componentes.Disco;
import Componentes.MemoriaAplicacao;
import Componentes.ProcessadorAplicacao;
import Conexao.Conexao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.github.britooo.looca.api.core.Looca;
import Logs.Logs;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main {

    public static void main(String[] args) {
        Conexao conexao = new Conexao();
        JdbcTemplate con = conexao.getConexaoDoBanco();
        JdbcTemplate conWin = conexao.getConexaoDBWIN();
        Timer timer = new Timer();

        Looca looca = new Looca();
        Logs log = new Logs();

        Disco disco = new Disco();
        MemoriaAplicacao memoria = new MemoriaAplicacao();
        ProcessadorAplicacao processador = new ProcessadorAplicacao();
        ProcessoAplicacao processo = new ProcessoAplicacao();
        SistemaOperacional sistema = new SistemaOperacional();
        RedeAplicacao rede = new RedeAplicacao();
        JanelaAplicacao janela = new JanelaAplicacao();

        String hostName = getHostName();
        logData(log, looca);

        Integer idServidorLocal = null;
        Integer idServidorNuvem = null;
        Integer idEmpresaLocal = null;

        try {
            Map<String, Object> empresaNuvem = conWin.queryForMap(
                    "SELECT e.id_empresa, e.cnpj, e.nome FROM Empresa e " +
                            "JOIN Servidor s ON e.id_empresa = s.fk_empresa " +
                            "WHERE s.host_name = ?", hostName);

            idEmpresaLocal = getEmpresaLocal(con, empresaNuvem);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERRO AO BUSCAR OU INSERIR EMPRESA.");
        }

        try {
            idServidorNuvem = conWin.queryForObject("SELECT id_servidor FROM Servidor WHERE host_name = ?", Integer.class, hostName);
            System.out.println("\nIDNuvem Servidor: " + idServidorNuvem);

            Servidor servidorNuvem = conWin.queryForObject(
                    "SELECT id_servidor, nome, host_name, fk_empresa FROM Servidor WHERE id_servidor = ?",
                    new BeanPropertyRowMapper<>(Servidor.class),
                    idServidorNuvem);

            idServidorLocal = getServidorLocal(con, servidorNuvem, idEmpresaLocal);
        } catch (Exception e) {
            System.out.println("SERVIDOR NÃO ENCONTRADO NA NUVEM OU ERRO NA INSERÇÃO.");
            e.printStackTrace();
        }

        coletaDadosFixos(memoria, processador, disco, con, conWin, idServidorLocal, idServidorNuvem);

        Integer finalIdServidor = idServidorLocal;
        Integer finalIdServidorNuvem = idServidorNuvem;

        timer.schedule(new TimerTask() {
            public void run() {
                coletaDadosDinamicos(memoria, processador, disco, processo, sistema, rede, janela, con, conWin, finalIdServidor, finalIdServidorNuvem);
            }
        }, 50, 10000);

        exibirQRCode("/qrcode.png");
    }

    private static String getHostName() {
        String hostName = "";
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            hostName = inetAddress.getHostName();
            System.out.println("HOSTNAME: " + hostName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("ERRO NA CAPTURA DO HOSTNAME");
        }
        return hostName;
    }

    private static void logData(Logs log, Looca looca) {
        String data;
        log.setSistemaOperacional(looca.getSistema().getSistemaOperacional());
        log.setHostName(looca.getRede().getParametros().getHostName());
        data = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        log.setData(data);
        log.setMensagem("O ID do servidor foi capturado com sucesso");

        System.out.println(log.toString().replace("idMaquina: null\n", "").replace("\t", ""));

        String dataArquivo = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String nomeArquivoLog = dataArquivo + ".txt";
        try (FileWriter writer = new FileWriter(nomeArquivoLog, true)) {
            String logString = log.toString().replace("idMaquina: null\n", "").replace("\t", "");
            writer.write(logString);
        } catch (IOException u) {
            System.out.println("Erro ao gerar log" + u.getMessage());
        }
    }

    private static Integer getEmpresaLocal(JdbcTemplate con, Map<String, Object> empresaNuvem) {
        Integer idEmpresaLocal = null;
        try {
            idEmpresaLocal = con.queryForObject(
                    "SELECT id_empresa FROM Empresa WHERE cnpj = ?", Integer.class, empresaNuvem.get("cnpj"));
        } catch (Exception e) {
            // Empresa não encontrada no banco local
        }

        if (idEmpresaLocal == null) {
            con.update(
                    "INSERT INTO Empresa (cnpj, nome) VALUES (?, ?)",
                    empresaNuvem.get("cnpj"),
                    empresaNuvem.get("nome")
            );
            System.out.println("Empresa inserida no banco local.");

            idEmpresaLocal = con.queryForObject(
                    "SELECT id_empresa FROM Empresa WHERE cnpj = ?", Integer.class, empresaNuvem.get("cnpj"));
        } else {
            System.out.println("Empresa já existe no banco local.");
        }
        return idEmpresaLocal;
    }

    private static Integer getServidorLocal(JdbcTemplate con, Servidor servidorNuvem, Integer idEmpresaLocal) {
        Integer idServidorLocal = null;
        try {
            idServidorLocal = con.queryForObject("SELECT id_servidor FROM Servidor WHERE host_name = ?", Integer.class, servidorNuvem.getHost_name());
            System.out.println("\nIDLocal Servidor: " + idServidorLocal);
        } catch (Exception e) {
            con.update(
                    "INSERT INTO Servidor (nome, host_name, fk_empresa) VALUES (?, ?, ?)",
                    servidorNuvem.getNome(),
                    servidorNuvem.getHost_name(),
                    idEmpresaLocal
            );
            System.out.println("Servidor inserido no banco local.");

            idServidorLocal = con.queryForObject(
                    "SELECT id_servidor FROM Servidor WHERE host_name = ?", Integer.class, servidorNuvem.getHost_name());
        }
        return idServidorLocal;
    }

    private static void coletaDadosFixos(MemoriaAplicacao memoria, ProcessadorAplicacao processador, Disco disco, JdbcTemplate con, JdbcTemplate conWin, Integer idServidorLocal, Integer idServidorNuvem) {
        memoria.coletarDadosFixos(con, conWin, idServidorLocal, idServidorNuvem);
        processador.coletarDadosFixos(con, conWin, idServidorLocal, idServidorNuvem);
        disco.coletarDadosFixos(con, conWin, idServidorLocal, idServidorNuvem);
    }

    private static void coletaDadosDinamicos(MemoriaAplicacao memoria, ProcessadorAplicacao processador, Disco disco, ProcessoAplicacao processo, SistemaOperacional sistema, RedeAplicacao rede, JanelaAplicacao janela, JdbcTemplate con, JdbcTemplate conWin, Integer idServidorLocal, Integer idServidorNuvem) {
        if (idServidorLocal != null) {
            memoria.coletarDadosDinamicos(con, conWin, idServidorLocal, idServidorNuvem);
            processador.coletarDadosDinamicos(con, conWin, idServidorLocal, idServidorNuvem);
            disco.coletarDadosDinamicos(con, conWin, idServidorLocal, idServidorNuvem);
            processo.coletarDadosDeProcessos(con, conWin, idServidorLocal, idServidorNuvem);
            sistema.coletarDadosDeSistemaOperacional(con, conWin, idServidorLocal, idServidorNuvem);
            rede.coletarDadosDeRede(con, conWin, idServidorLocal, idServidorNuvem);
            janela.coletarDadosDeJanelas();
        }
    }

    private static void exibirQRCode(String imagePath) {
        JFrame frame = new JFrame("QR Code");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);


        java.net.URL imageURL = Main.class.getResource(imagePath);
        if (imageURL != null) {
            ImageIcon icon = new ImageIcon(imageURL);
            JLabel label = new JLabel(icon);

            frame.getContentPane().add(label);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } else {
            System.err.println("Imagem não encontrada: " + imagePath);
        }
    }
}
