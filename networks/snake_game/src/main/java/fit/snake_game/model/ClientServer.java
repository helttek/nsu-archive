package fit.snake_game.model;

import fit.snake_game.Main;
import fit.snake_game.model.events.EventListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class ClientServer extends NetNode implements EventListener {
    private final GameModel gameModel;

    private final long PING_PERIOD;

    private InetAddress serverIp;
    private int serverPort;

    private Timer pingTimerTask;


    public ClientServer(long PING_PERIOD, Semaphore semaphore) throws IOException {
        gameModel = GameModel.getInstance();
        this.PING_PERIOD = PING_PERIOD;
        gameModel.getNetEventManager().subscribe(this, "receivedPacket");
    }

    public InetAddress getServerIp() {
        return serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    private void setServerIp(InetAddress serverIp) {
        this.serverIp = serverIp;
    }

    private void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void update(String eventType, DatagramPacket receivedPacket) {
        try {
            SnakesProto.GameMessage gameMessage = SnakesProto.GameMessage.parseFrom(Arrays.copyOf(receivedPacket.getData(), receivedPacket.getLength()));

            if (gameMessage.hasState()) {
                SnakesProto.GameMessage.StateMsg stateMsg = gameMessage.getState();

                if (gameModel.isChangeServer()) {
                    gameModel.getSnakes().addAll(stateMsg.getState().getSnakesList());
                    gameModel.getFood().addAll(stateMsg.getState().getFoodsList());
                    gameModel.getPlayers().addAll(stateMsg.getState().getPlayers().getPlayersList());

                    gameModel.startServerLogic();
                    gameModel.sendNewServerToEveryone();
//                    System.out.println("Telling everyone that I'm the new server");
                    gameModel.setChangeServer(false);
                    gameModel.changeDeputy();
                    gameModel.getClientServer().setServerIp(InetAddress.getByName("localhost"));
                    gameModel.getClientServer().setServerPort(Main.port);
                }
                gameModel.getEventManager().notify("gameState", stateMsg.getState().getSnakesList(),
                        stateMsg.getState().getFoodsList(), stateMsg.getState().getPlayers().getPlayersList());

                sendConfirmation(receivedPacket.getAddress(), receivedPacket.getPort(), receivedPacket);
            } else if (gameMessage.hasRoleChange()) {
                SnakesProto.GameMessage.RoleChangeMsg roleChangeMsg = gameMessage.getRoleChange();

                if (roleChangeMsg.hasReceiverRole() && !roleChangeMsg.hasSenderRole()) {
                    gameModel.setNodeRole(roleChangeMsg.getReceiverRole());
                }

                if (roleChangeMsg.hasReceiverRole() && roleChangeMsg.hasSenderRole()) {
                    if (roleChangeMsg.getSenderRole() == SnakesProto.NodeRole.MASTER &&
                            roleChangeMsg.getReceiverRole() == SnakesProto.NodeRole.DEPUTY) {
                        gameModel.setNodeRole(roleChangeMsg.getSenderRole());
                        gameModel.setChangeServer(true);
                    } else if (roleChangeMsg.getSenderRole() == SnakesProto.NodeRole.MASTER &&
                            roleChangeMsg.getReceiverRole() == SnakesProto.NodeRole.NORMAL) {
                        if (gameModel.getClientServer() != null) {

                            gameModel.getClientServer().setServerIp(receivedPacket.getAddress());
                            gameModel.getClientServer().setServerPort(receivedPacket.getPort());
                        }
                    }
                }
            } else if (gameMessage.hasAck()) {
                gameModel.clientConfirmMessageWithSeq(gameMessage.getMsgSeq());

                if (gameMessage.getMsgSeq() == 0) {
                    serverIp = receivedPacket.getAddress();
                    serverPort = receivedPacket.getPort();

                    startPingTimerTask();
                }
            } else if (gameMessage.hasError()) {
                SnakesProto.GameMessage.ErrorMsg errorMsg = gameMessage.getError();
                gameModel.getEventManager().notify("errorMessage", errorMsg.getErrorMessage());

                sendConfirmation(receivedPacket.getAddress(), receivedPacket.getPort(), receivedPacket);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startPingTimerTask() {
        pingTimerTask = new Timer();
        pingTimerTask.schedule(new PingTimerTask(), 0, PING_PERIOD);
    }

    public void stopPingTimerTask() {
        pingTimerTask.cancel();
        pingTimerTask.purge();
    }

    public class PingTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                gameModel.sendMessage(serverIp, serverPort, createPingMsg());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        private SnakesProto.GameMessage createPingMsg() {
            SnakesProto.GameMessage.Builder gameMessage = SnakesProto.GameMessage.newBuilder();

            gameMessage.setPing(SnakesProto.GameMessage.PingMsg.newBuilder().build());
            gameMessage.setMsgSeq(gameModel.getClientServerMsgCount());

            gameModel.increaseClientServerMsgCount();

            return gameMessage.build();
        }
    }
}
