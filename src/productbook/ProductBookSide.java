package productbook;

import globalconstants.*;
import interfaces.*;
import prices.*;
import dto.*;
import java.util.*;
import exception.*;
import user.*;

import static globalconstants.BookSide.*;

public class ProductBookSide {
    private BookSide side;
    private final TreeMap<Price, ArrayList<Tradable>> bookEntries;

    public ProductBookSide(BookSide side) throws InvalidParameterException, DataValidationException {
        setSide(side);

        this.bookEntries = new TreeMap<>(Comparator.reverseOrder());
    }

    private void setSide(BookSide side) throws InvalidParameterException, DataValidationException {
        if (side == null) {
            throw new InvalidParameterException("ProductBookSide(BookSide side): Side cannot be null");
        }

        this.side = side;
    }

    public TradableDTO add(Tradable o) throws InvalidParameterException, DataValidationException {
        if (o == null) {
            throw new InvalidParameterException("add(Tradable o): Tradable cannot be null");
        }
        Price price = o.getPrice();
        if (!bookEntries.containsKey(price)) {
            bookEntries.put(price, new ArrayList<>());
        }
        bookEntries.get(price).add(o);

        TradableDTO dto = new TradableDTO(o);
        UserManager.getInstance().updateTradable(o.getUser(), dto);

        return dto;
    }

    public TradableDTO cancel(String tradableId) throws InvalidParameterException, DataValidationException {
        if (tradableId == null) {
            throw new InvalidParameterException("cancel(String tradableId): tradableId cannot be null");
        }

        for (Map.Entry<Price, ArrayList<Tradable>> entry : bookEntries.entrySet()) {
            ArrayList<Tradable> tradables = entry.getValue();

            Iterator<Tradable> iterator = tradables.iterator();
            while (iterator.hasNext()) {
                Tradable t = iterator.next();
                if (t.getId().equals(tradableId)) {
//                    System.out.println("**CANCEL: " + t);

                    t.setCancelledVolume(t.getCancelledVolume() + t.getRemainingVolume());
                    t.setRemainingVolume(0);
                    iterator.remove();

                    if (tradables.isEmpty()) {
                        bookEntries.remove(entry.getKey());
                    }

                    TradableDTO dto = t.makeTradableDTO();
                    UserManager.getInstance().updateTradable(t.getUser(), dto);

                    return dto;
                }
            }
        }
        return null;
    }

    public TradableDTO removeQuotesForUser(String userName) throws InvalidParameterException, DataValidationException {
        if (userName == null || userName.isEmpty()) {
            throw new DataValidationException("User name cannot be null or empty.");
        }

        for (Price price : bookEntries.keySet()) {
            for (Tradable t : bookEntries.get(price)) {
                if (t.getUser().equals(userName)) {
                    TradableDTO cancelledDTO = cancel(t.getId());

                    UserManager.getInstance().updateTradable(t.getUser(), cancelledDTO);

                    return cancelledDTO;
                }
            }
        }

        return null;
    }

    public Price topOfBookPrice() {
        if (bookEntries.isEmpty()) { return null; }

        return (side == BUY) ? bookEntries.firstKey() : bookEntries.lastKey();
    }

    public int topOfBookVolume(){
        if (bookEntries.isEmpty()) { return 0; }

        Price price = (side == BUY) ? bookEntries.firstKey() : bookEntries.lastKey();
        ArrayList<Tradable> tradables = bookEntries.get(price);

        int totalVolume = 0;
        for (Tradable t : tradables) {
            totalVolume += t.getRemainingVolume();
        }

        return totalVolume;
    }

    public void tradeOut(Price price, int vol) throws InvalidParameterException, DataValidationException {
        Price topPrice = topOfBookPrice();
        if (topPrice == null || topPrice.compareTo(price) > 0 ) { return; }

        ArrayList<Tradable> atPrice = bookEntries.get(topPrice);
        if (atPrice == null || atPrice.isEmpty()) { return; }

        if (vol >= getBookVolume(atPrice)) {
            tradeOutInFull(atPrice);
        } else {
            tradeOutInPartial(atPrice, vol);
        }

        atPrice.removeIf(t -> t.getRemainingVolume() == 0);
        if (atPrice.isEmpty()) { bookEntries.remove(topPrice); }
    }

    private void tradeOutInFull(ArrayList<Tradable> atPrice) throws DataValidationException {
        for (Tradable t : atPrice) {
            int originalRemainingVol = t.getRemainingVolume();
            t.setFilledVolume(t.getFilledVolume() + originalRemainingVol);
            t.setRemainingVolume(0);

            System.out.printf("\t\tFULL FILL (%s %d) %s %s order: %s at %s, Orig Vol: %d, Rem Vol: %d, Fill Vol: %d, CXL Vol: %d, ID: %s%n",
                    t.getSide(), originalRemainingVol, t.getUser(), t.getSide(), t.getProduct(), t.getPrice(), t.getOriginalVolume(), t.getRemainingVolume(), t.getFilledVolume(), t.getCancelledVolume(), t.getId());

            UserManager.getInstance().updateTradable(t.getUser(), t.makeTradableDTO());
        }

        bookEntries.remove(this.topOfBookPrice());
    }

    private void tradeOutInPartial(ArrayList<Tradable> atPrice, int vol) throws DataValidationException {
        int remainder = vol;
        int totalVolAtPrice = getBookVolume(atPrice);

        for (Tradable t : atPrice) {
            double ratio = (double) t.getRemainingVolume() / totalVolAtPrice;
            int toTrade = (int) Math.ceil(vol * ratio);
            toTrade = Math.min(toTrade, remainder);

            t.setFilledVolume(t.getFilledVolume() + toTrade);
            t.setRemainingVolume(t.getRemainingVolume() - toTrade);

            System.out.printf("\t\tPARTIAL FILL (%s %d) %s %s order: %s at %s, Orig Vol: %d, Rem Vol: %d, Fill Vol: %d, CXL Vol: %d, ID: %s%n",
                    t.getSide(), t.getFilledVolume(), t.getUser(), t.getSide(), t.getProduct(), t.getPrice(), t.getOriginalVolume(), t.getRemainingVolume(), t.getFilledVolume(), t.getCancelledVolume(), t.getId());

            remainder -= toTrade;

            UserManager.getInstance().updateTradable(t.getUser(), new TradableDTO(t));
        }
    }

    private int getBookVolume(ArrayList<Tradable> atPrice){
        int totalVol = 0;
        for (Tradable t : atPrice) {
            totalVol += t.getRemainingVolume();
        }
        return totalVol;
    }

    @Override
    public String toString(){
        StringBuilder returnStr = new StringBuilder(String.format("Side: %s\n", this.side));

        if (bookEntries.isEmpty()) { return (returnStr + "\t\t<Empty>\n"); }

        for (Map.Entry<Price, ArrayList<Tradable>> entry : ((this.side == BUY) ? bookEntries.entrySet(): bookEntries.descendingMap().entrySet())) {
            returnStr.append(String.format("\t\t%s\n", entry.getKey().toString()));

            for (Tradable val : entry.getValue()) {
                returnStr.append(String.format("\t\t\t\t%s\n", val.toString()));
            }
        }

        return returnStr.toString();
    }
}
