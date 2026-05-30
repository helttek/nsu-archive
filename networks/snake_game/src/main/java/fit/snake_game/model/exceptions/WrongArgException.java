package fit.snake_game.model.exceptions;

public class WrongArgException extends RuntimeException {
    public WrongArgException(String message) {
        super(message);
    }
}