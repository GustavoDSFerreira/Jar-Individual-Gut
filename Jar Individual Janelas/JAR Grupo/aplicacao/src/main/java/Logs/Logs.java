package Logs;

public class Logs {
    private String sistemaOperacional;
    private String hostName;
    private String data;
    private String mensagem;
    private Integer errorCode;
    private String statusError;

    public Logs(String sistemaOperacional, String hostName, String data, String mensagem, Integer errorCode, String statusError) {
        this.sistemaOperacional = sistemaOperacional;
        this.hostName = hostName;
        this.data = data;
        this.mensagem = mensagem;
        this.errorCode = errorCode;
        this.statusError = statusError;
    }

    public Logs() {

    }

    public String getSistemaOperacional() {
        return sistemaOperacional;
    }

    public void setSistemaOperacional(String sistemaOperacional) {
        this.sistemaOperacional = sistemaOperacional;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getStatusError() {
        return statusError;
    }

    public void setStatusError(String statusError) {
        this.statusError = statusError;
    }

    @Override
    public String toString() {
        return """
                ===Início do Log===
                Hostname: %s
                Sistema Operacional: %s
                                
                [%s] Processo: %s
                """.formatted(hostName, sistemaOperacional, data, mensagem);
    }
}
