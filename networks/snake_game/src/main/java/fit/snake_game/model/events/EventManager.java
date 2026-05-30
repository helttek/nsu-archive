package fit.snake_game.model.events;

import java.net.DatagramPacket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import fit.snake_game.model.SnakesProto;
import fit.snake_game.model.announcement.IpAddress;


public class EventManager {
    private final Map<String, Set<EventListener>> listeners = new HashMap<>();

    public EventManager(String... operations) {
        for (String operation : operations) {
            listeners.put(operation, ConcurrentHashMap.newKeySet());
        }
    }

    public void subscribe(EventListener listener, String... eventTypes) {
        for (String eventType : eventTypes) {
            listeners.get(eventType).add(listener);
        }
    }

    public void unsubscribe(EventListener listener, String... eventTypes) {
        for (String eventType : eventTypes) {
            listeners.get(eventType).remove(listener);
        }
    }

    public void unsubscribeAll(String... eventTypes) {
        for (String eventType : eventTypes) {
            listeners.get(eventType).clear();
        }
    }

    public void notify(String eventType, Map<IpAddress, SnakesProto.GameMessage.AnnouncementMsg> availableGames) {
        Set<EventListener> gamesListeners = listeners.get(eventType);
        for (EventListener listener : gamesListeners) {
            listener.update(eventType, availableGames);
        }
    }

    public void notify(String eventType, List<SnakesProto.GameState.Snake> snakes, List<SnakesProto.GameState.Coord> food, List<SnakesProto.GamePlayer> players) {
        Set<EventListener> gameStateListeners = listeners.get(eventType);
        for (EventListener listener : gameStateListeners) {
            listener.update(eventType, snakes, food, players);
        }
    }

    public void notify(String eventType, DatagramPacket receivedPacket) {
        Set<EventListener> eventListeners = listeners.get(eventType);
        for (EventListener listener : eventListeners) {
            listener.update(eventType, receivedPacket);
        }
    }

    public void notify(String eventType, String message) {
        Set<EventListener> eventListeners = listeners.get(eventType);
        for (EventListener listener : eventListeners) {
            listener.update(eventType, message);
        }
    }
}