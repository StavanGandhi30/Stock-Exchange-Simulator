package ProductBook;

import GlobalConstants.BookSide;
import Interface.Tradable;
import DTO.TradableDTO;
import Quotes.Quote;
import java.util.regex.Pattern;
import Exception.InvalidParameterException;
import static GlobalConstants.BookSide.*;

public class ProductBook {
    private final String product;
    private final ProductBookSide buySide;
    private final ProductBookSide sellSide;

    public ProductBook(String product) throws InvalidParameterException {
        if (!Pattern.matches("[A-Z0-9.]{1,5}", product)) {
            throw new InvalidParameterException("Product must be 1-5 alphanumeric characters with an optional period.");
        }
        this.product = product;
        this.buySide = new ProductBookSide(BUY);
        this.sellSide = new ProductBookSide(SELL);
    }

    public TradableDTO add(Tradable t) throws InvalidParameterException{
        if (t == null || t.getSide() == null) {
            throw new InvalidParameterException("add(Tradable t): Tradable and Tradable side cannot be null.");
        }
        System.out.println("**ADD: " + t);

        TradableDTO dto = (t.getSide() == BUY) ? buySide.add(t) : sellSide.add(t);

        tryTrade();
        return dto;
    }

    public TradableDTO[] add(Quote qte) throws InvalidParameterException {
        if (qte == null) {
            throw new InvalidParameterException("add(Quote qte): Quote cannot be null.");
        }
        removeQuotesForUser(qte.getUser());

        TradableDTO buyDTO = buySide.add(qte.getQuoteSide(BUY));
        TradableDTO sellDTO = sellSide.add(qte.getQuoteSide(SELL));

        System.out.println("**ADD: " + qte.getQuoteSide(BUY));
        System.out.println("**ADD: " + qte.getQuoteSide(SELL));

        tryTrade();
        return new TradableDTO[] {buyDTO, sellDTO};
    }

    public TradableDTO cancel(BookSide side, String orderId) throws InvalidParameterException {
        if (side == null) {
            throw new InvalidParameterException("cancel(BookSide side, ...): side cannot be null.");
        }
        if (orderId == null) {
            throw new InvalidParameterException("cancel(..., String orderId): orderId cannot be null.");
        }

        return (side == BUY) ? buySide.cancel(orderId) : sellSide.cancel(orderId);
    }

    public TradableDTO[] removeQuotesForUser(String userName) throws InvalidParameterException {
        if (userName == null) {
            throw new InvalidParameterException("removeQuotesForUser(String userName): userName cannot be null.");
        }

        TradableDTO buyDTO = buySide.removeQuotesForUser(userName);
        TradableDTO sellDTO = sellSide.removeQuotesForUser(userName);

        return new TradableDTO[] {buyDTO, sellDTO};
    }

    public String getTopOfBookString(BookSide side) {
        if (side == BUY) {
            if (buySide.topOfBookPrice() == null){
                return ("Top of BUY book: $0.00 x 0");
            }
            return String.format("Top of BUY book: %s x %s", buySide.topOfBookPrice().toString(), buySide.topOfBookVolume());
        } else {
            if (sellSide.topOfBookPrice() == null){
                return ("Top of SELL book: $0.00 x 0");
            }
            return String.format("Top of SELL book: %s x %s", sellSide.topOfBookPrice().toString(), sellSide.topOfBookVolume());
        }
    }

    public void tryTrade() {
        if (buySide.topOfBookPrice() == null || sellSide.topOfBookPrice() == null) { return; }

        int totalToTrade = (buySide.topOfBookPrice().compareTo(sellSide.topOfBookPrice()) > 0) ?
            buySide.topOfBookVolume() : sellSide.topOfBookVolume();

        while (totalToTrade > 0) {
            if (buySide.topOfBookPrice() == null || sellSide.topOfBookPrice() == null) { return; }
            if (sellSide.topOfBookPrice().compareTo(buySide.topOfBookPrice()) > 0) { return; }

            int toTrade = Math.min(buySide.topOfBookVolume(), sellSide.topOfBookVolume());

            buySide.tradeOut(buySide.topOfBookPrice(), toTrade);
            sellSide.tradeOut(buySide.topOfBookPrice(), toTrade);

            totalToTrade -= toTrade;
        }
    }

    @Override
    public String toString() {
        return (String.format("--------------------------------------------\nProduct Book: %s\n%s%s--------------------------------------------",
                this.product, this.buySide.toString(), this.sellSide.toString()));
    }
}
