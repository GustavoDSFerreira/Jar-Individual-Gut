package Componentes;

public enum TipoComponente {

    CPU(1),
    MEMORIA(2),
    DISCO(3);

    private final Integer id_tipo_componente;

    TipoComponente(Integer idTipoComponente) {
        this.id_tipo_componente = idTipoComponente;
    }

    public Integer getId_tipo_componente() {
        return id_tipo_componente;
    }
}
