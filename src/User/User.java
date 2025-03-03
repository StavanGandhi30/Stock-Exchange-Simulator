package User;

import java.util.*;
import DTO.TradableDTO;

public class User {

    private final String userId;
    private final TreeMap<String, TradableDTO> tradables;

    public User(String userId){
        this.userId = validateUserId(userId);
        this.tradables = new TreeMap<>();;
    }

    private String validateUserId(String userId) {
        if (userId.matches("^[a-zA-Z]+$") && userId.length() == 3) {
            return userId;
        }

        throw new IllegalArgumentException("");
    }

    public void updateTradable (TradableDTO o) {
        if (o == null || o.tradableId() == null || o.tradableId().isEmpty()) {
            return;
        }

        this.tradables.put(o.tradableId(), o);
    }

    public String getUserId() {
        return (this.userId);
    }

    public TreeMap<String, TradableDTO> getTradables() {
        return tradables;
    }

    @Override
    public String toString() {
        String out = String.format("\tUser Id: %s\n", userId);

        for (String tradable: tradables.keySet()){
            TradableDTO tDTO = tradables.get(tradable);
            out = String.format("%s\t\tProduct: %s, Price: %s, OriginalVolume: %d, RemainingVolume: %d, CancelledVolume: %d, FilledVolume: %d, User: %s, Side: %s, Id: %s\n",
                    out, tDTO.product(), tDTO.price(), tDTO.originalVolume(), tDTO.remainingVolume(), tDTO.cancelledVolume(), tDTO.filledVolume(), tDTO.user(), tDTO.side(), tDTO.tradableId());
        }

        return String.format("%s\n", out);
    }
}
