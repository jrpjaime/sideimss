package mx.gob.imss.contadores.dto;

public class MensajeDto {
    private String mensaje;

    public MensajeDto(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
