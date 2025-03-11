package market;

import java.util.ArrayList;
import java.util.HashMap;

public class CurrentMarketPublisher {
    private static CurrentMarketPublisher instance;
    private final HashMap<String, ArrayList<CurrentMarketObserver>> filters;

    private CurrentMarketPublisher() {
        filters = new HashMap<>();
    }

    public static CurrentMarketPublisher getInstance() {
        if (instance == null) {
            instance = new CurrentMarketPublisher();
        }
        return instance;
    }

    public void subscribeCurrentMarket(String symbol, CurrentMarketObserver observer) {
        if (!filters.containsKey(symbol)) {
            filters.put(symbol, new ArrayList<CurrentMarketObserver>());
        }
        filters.get(symbol).add(observer);
    }

    public void unSubscribeCurrentMarket(String symbol, CurrentMarketObserver observer) {
        if (!filters.containsKey(symbol)) { return; }

        ArrayList<CurrentMarketObserver> observers = filters.get(symbol);
        if (observers!=null){
            observers.remove(observer);
        }
    }

    public void acceptCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide) {
        if (!filters.containsKey(symbol)) { return; }

        ArrayList<CurrentMarketObserver> observers = filters.get(symbol);
        for (CurrentMarketObserver observer : observers) {
            observer.updateCurrentMarket(symbol, buySide, sellSide);
        }
    }
}