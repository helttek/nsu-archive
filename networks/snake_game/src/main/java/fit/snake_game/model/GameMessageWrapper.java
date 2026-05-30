package fit.snake_game.model;

import fit.snake_game.model.SnakesProto.GameMessage;

public class GameMessageWrapper {
    private final GameMessage gameMessage;

    private final int port;

    long timesSent;

    public GameMessageWrapper(GameMessage gameMessage, int port) {
        this.gameMessage = gameMessage;
        this.port = port;
        timesSent = 0;
    }

    public GameMessage getGameMessage() {
        return gameMessage;
    }

    public int getPort() {
        return port;
    }
}
