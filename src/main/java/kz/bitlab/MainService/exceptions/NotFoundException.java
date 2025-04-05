package kz.bitlab.MainService.exceptions;


public class NotFoundException extends RuntimeException{

    public NotFoundException(String message) {
        super(message);
    }
}
