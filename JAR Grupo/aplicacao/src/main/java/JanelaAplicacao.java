import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.janelas.Janela;
import com.github.britooo.looca.api.group.janelas.JanelaGrupo;

import java.awt.*;
import java.util.List;

public class JanelaAplicacao {

    Looca looca = new Looca();
    JanelaGrupo grupoJanela = looca.getGrupoDeJanelas();

    private Long pid;

    private Long janelaId;

    private String titulo;

    private String comando;

    private Rectangle localizacaoETamanho;

    private Boolean isVisivel;

    public JanelaAplicacao(Long pid, Long janelaId, String titulo, String comando, Rectangle localizacaoETamanho, Boolean isVisivel) {
        this.pid = pid;
        this.janelaId = janelaId;
        this.titulo = titulo;
        this.comando = comando;
        this.localizacaoETamanho = localizacaoETamanho;
        this.isVisivel = isVisivel;
    }

    public JanelaAplicacao() {
    }

    public void coletarDadosDeJanelas() {

        System.out.println("JANELAS\n");

        System.out.println("Quantidade de Janelas Abertas: " + grupoJanela.getTotalJanelasVisiveis());

        List<Janela> janelas = grupoJanela.getJanelas();

        for (Janela janela : janelas) {

            setPid(janela.getPid());
            setJanelaId(janela.getJanelaId());
            setTitulo(janela.getTitulo());
            setComando(janela.getComando());
            setLocalizacaoETamanho(janela.getLocalizacaoETamanho());
            setVisivel(janela.isVisivel());

            System.out.println("\nPID: " + getPid());
            System.out.println("PID Janela: " + getJanelaId());
            System.out.println("Titulo: " + getTitulo());
            System.out.println("Comando :");
            System.out.println("Localização e Tamanho: " + getLocalizacaoETamanho());
            System.out.println("Janela Visível : " + getVisivel());

        }

    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Long getJanelaId() {
        return janelaId;
    }

    public void setJanelaId(Long janelaId) {
        this.janelaId = janelaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public Rectangle getLocalizacaoETamanho() {
        return localizacaoETamanho;
    }

    public void setLocalizacaoETamanho(Rectangle localizacaoETamanho) {
        this.localizacaoETamanho = localizacaoETamanho;
    }

    public Boolean getVisivel() {
        return isVisivel;
    }

    public void setVisivel(Boolean visivel) {
        isVisivel = visivel;
    }
}
