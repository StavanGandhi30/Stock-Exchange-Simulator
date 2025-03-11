package user;

import java.util.*;
import dto.TradableDTO;
import market.*;

public class User implements CurrentMarketObserver {

    private String userId;
    private final TreeMap<String, TradableDTO> tradables;
    private final HashMap<String, CurrentMarketSide[]> currentMarkets;

    public User(String userId){
        validateUserId(userId);

        this.tradables = new TreeMap<>();
        this.currentMarkets = new HashMap<>();

    }

    private void validateUserId(String userId) throws IllegalArgumentException {
        if (!(userId.matches("^[a-zA-Z]+$") && userId.length() == 3)) {
            throw new IllegalArgumentException("User code must be 3 letters, no spaces, no numbers, no special characters.");
        }

        this.userId = userId;
    }

    public void updateTradable (TradableDTO o) {
        if (o == null || o.tradableId() == null || o.tradableId().isEmpty()) {
            return;
        }

        this.tradables.put(o.tradableId(), o);
    }

    public String getUserId() {
        return this.userId;
    }

    public TreeMap<String, TradableDTO> getTradables() {
        return tradables;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(String.format("\tUser Id: %s\n", userId));

        for (String tradable: tradables.keySet()){
            TradableDTO tDTO = tradables.get(tradable);
            out.append(String.format("\t\tProduct: %s, Price: %s, OriginalVolume: %d, RemainingVolume: %d, CancelledVolume: %d, FilledVolume: %d, User: %s, Side: %s, Id: %s\n",
                    tDTO.product(), tDTO.price(), tDTO.originalVolume(), tDTO.remainingVolume(), tDTO.cancelledVolume(), tDTO.filledVolume(), tDTO.user(), tDTO.side(), tDTO.tradableId()));
        }

        return String.format("%s\n", out);
    }

    @Override
    public void updateCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide) {
        currentMarkets.put(symbol, new CurrentMarketSide[] {buySide, sellSide});
    }

    public String getCurrentMarkets() {
        StringBuilder out = new StringBuilder();

        for (String currentMarketSymbol: currentMarkets.keySet()){
            CurrentMarketSide[] marketSides = currentMarkets.get(currentMarketSymbol);

            out.append(String.format("%s\t%s - %s\n", currentMarketSymbol, marketSides[0].toString(), marketSides[1].toString()));
        }

        return (out.toString());
    }
}
