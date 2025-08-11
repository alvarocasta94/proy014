package es.cic.curso25.proy011.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String mensaje) {
        super(mensaje);
    }

    public NotFoundException(String mensaje, Throwable error) {
        super(mensaje, error);
    }
}
