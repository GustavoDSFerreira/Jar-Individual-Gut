import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.rede.Rede;
import com.github.britooo.looca.api.group.rede.RedeInterface;
import com.github.britooo.looca.api.group.rede.RedeInterfaceGroup;
import com.github.britooo.looca.api.util.Conversor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class RedeAplicacao {

    Looca looca = new Looca();
    Rede rede = looca.getRede();
    RedeInterfaceGroup grupoInterfaces = rede.getGrupoDeInterfaces();

    private String endereco_ipv4;

    private String endereco_ipv6;

    private Long bytes_recebidos;

    private Long bytes_enviados;

    private Long pacotes_recebidos;

    private Long pacotes_enviados;

    public RedeAplicacao(String endereco_ipv4, String endereco_ipv6, Long bytes_recebidos, Long bytes_enviados, Long pacotes_recebidos, Long pacotes_enviados) {
        this.endereco_ipv4 = endereco_ipv4;
        this.endereco_ipv6 = endereco_ipv6;
        this.bytes_recebidos = bytes_recebidos;
        this.bytes_enviados = bytes_enviados;
        this.pacotes_recebidos = pacotes_recebidos;
        this.pacotes_enviados = pacotes_enviados;
    }

    public RedeAplicacao() {
    }

    public void coletarDadosDeRede(JdbcTemplate con, JdbcTemplate conWin, Integer idServidor, Integer idServidorNuvem) {

        System.out.println("\nREDE");

        List<RedeInterface> interfacesDeRede = grupoInterfaces.getInterfaces();

        for (RedeInterface redeInterface : interfacesDeRede) {
            if (!redeInterface.getEnderecoIpv4().isEmpty() && redeInterface.getPacotesEnviados() != 0 && redeInterface.getPacotesRecebidos() != 0) {
                // Utiliza os setters para armazenar os valores
                setEndereco_ipv4(String.join(", ", redeInterface.getEnderecoIpv4()));
                setEndereco_ipv6(String.join(", ", redeInterface.getEnderecoIpv6()));
                setBytes_recebidos(redeInterface.getBytesRecebidos());
                setBytes_enviados(redeInterface.getBytesEnviados());
                setPacotes_recebidos(redeInterface.getPacotesRecebidos());
                setPacotes_enviados(redeInterface.getPacotesEnviados());

                // Printar os valores no console
                System.out.println("Endereco IPv4: " + endereco_ipv4);
                System.out.println("Endereco IPv6: " + endereco_ipv6);
                System.out.println("Bytes Recebidos: " + Conversor.formatarBytes(bytes_recebidos));
                System.out.println("Bytes Enviados: " + Conversor.formatarBytes(bytes_enviados));
                System.out.println("Pacotes Recebidos: " + pacotes_recebidos);
                System.out.println("Pacotes Enviados: " + pacotes_enviados);

                // Inserir os valores no banco de dados
                con.update("INSERT INTO RedeRegistro (endereco_ipv4, endereco_ipv6, bytes_recebidos, bytes_enviados, pacotes_recebidos, pacotes_enviados, fk_servidor) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        endereco_ipv4.replaceAll("\\[|\\]", ""),
                        endereco_ipv6.replaceAll("\\[|\\]", ""),
                        Conversor.formatarBytes(bytes_recebidos).replace("MiB", "").replace(",", ".").replace("GiB", "").replace("KiB", ""),
                        Conversor.formatarBytes(bytes_enviados).replace("MiB", "").replace(",", ".").replace("GiB", "").replace("KiB", ""),
                        pacotes_recebidos,
                        pacotes_enviados,
                        idServidor);
                conWin.update("INSERT INTO RedeRegistro (endereco_ipv4, endereco_ipv6, bytes_recebidos, bytes_enviados, pacotes_recebidos, pacotes_enviados, fk_servidor) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        endereco_ipv4.replaceAll("\\[|\\]", ""),
                        endereco_ipv6.replaceAll("\\[|\\]", ""),
                        Conversor.formatarBytes(bytes_recebidos).replace("MiB", "").replace(",", ".").replace("GiB", "").replace("KiB", ""),
                        Conversor.formatarBytes(bytes_enviados).replace("MiB", "").replace(",", ".").replace("GiB", "").replace("KiB", ""),
                        pacotes_recebidos,
                        pacotes_enviados,
                        idServidorNuvem);
            }
        }
    }

    @Override
    public String toString() {
        return "|" +
                "endereco_ipv4 = " + endereco_ipv4 + '\'' +
                ", endereco_ipv6 = " + endereco_ipv6 + '\'' +
                ", bytes_recebidos = " + bytes_recebidos +
                ", bytes_enviados = " + bytes_enviados +
                ", pacotes_recebidos = " + pacotes_recebidos +
                ", pacotes_enviados = " + pacotes_enviados +
                '|';
    }

    // Getters e Setters
    public String getEndereco_ipv4() {
        return endereco_ipv4;
    }

    public void setEndereco_ipv4(String endereco_ipv4) {
        this.endereco_ipv4 = endereco_ipv4;
    }

    public String getEndereco_ipv6() {
        return endereco_ipv6;
    }

    public void setEndereco_ipv6(String endereco_ipv6) {
        this.endereco_ipv6 = endereco_ipv6;
    }

    public Long getBytes_recebidos() {
        return bytes_recebidos;
    }

    public void setBytes_recebidos(Long bytes_recebidos) {
        this.bytes_recebidos = bytes_recebidos;
    }

    public Long getBytes_enviados() {
        return bytes_enviados;
    }

    public void setBytes_enviados(Long bytes_enviados) {
        this.bytes_enviados = bytes_enviados;
    }

    public Long getPacotes_recebidos() {
        return pacotes_recebidos;
    }

    public void setPacotes_recebidos(Long pacotes_recebidos) {
        this.pacotes_recebidos = pacotes_recebidos;
    }

    public Long getPacotes_enviados() {
        return pacotes_enviados;
    }

    public void setPacotes_enviados(Long pacotes_enviados) {
        this.pacotes_enviados = pacotes_enviados;
    }
}
