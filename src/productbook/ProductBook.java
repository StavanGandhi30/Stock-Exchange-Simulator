package productbook;

import exception.*;
import globalconstants.*;
import interfaces.*;
import dto.*;
import quotes.*;
import exception.*;
import market.*;
import prices.*;
import static globalconstants.BookSide.*;

import java.util.regex.Pattern;

public class ProductBook {
    private String product;
    private final ProductBookSide buySide;
    private final ProductBookSide sellSide;

    public ProductBook(String product) throws InvalidParameterException, DataValidationException {
        setProduct(product);

        this.buySide = new ProductBookSide(BUY);
        this.sellSide = new ProductBookSide(SELL);
    }

    private void setProduct(String product) throws InvalidParameterException, DataValidationException {
        if (!Pattern.matches("[A-Z0-9.]{1,5}", product)) {
            throw new InvalidParameterException("Product must be 1-5 alphanumeric characters with an optional period.");
        }

        this.product = product;
    }

    public TradableDTO add(Tradable t) throws InvalidPriceException, InvalidParameterException, DataValidationException{
        if (t == null || t.getSide() == null) {
            throw new InvalidParameterException("add(Tradable t): Tradable and Tradable side cannot be null.");
        }
//        System.out.println("**ADD: " + t);

        TradableDTO dto = (t.getSide() == BUY) ? buySide.add(t) : sellSide.add(t);

        tryTrade();
        updateMarket();
        return dto;
    }

    public TradableDTO[] add(Quote qte) throws InvalidPriceException, InvalidParameterException, DataValidationException {
        if (qte == null) {
            throw new InvalidParameterException("add(Quote qte): Quote cannot be null.");
        }

        removeQuotesForUser(qte.getUser());

        TradableDTO buyDTO = buySide.add(qte.getQuoteSide(BUY));
        TradableDTO sellDTO = sellSide.add(qte.getQuoteSide(SELL));

//        System.out.println("**ADD: " + qte.getQuoteSide(BUY));
//        System.out.println("**ADD: " + qte.getQuoteSide(SELL));

        tryTrade();
        return new TradableDTO[] {buyDTO, sellDTO};
    }

    public TradableDTO cancel(BookSide side, String orderId) throws InvalidPriceException, InvalidParameterException, DataValidationException {
        if (side == null) {
            throw new InvalidParameterException("cancel(BookSide side, ...): side cannot be null.");
        }
        if (orderId == null) {
            throw new InvalidParameterException("cancel(..., String orderId): orderId cannot be null.");
        }

        TradableDTO dto = (side == BUY) ? buySide.cancel(orderId) : sellSide.cancel(orderId);

        updateMarket();

        return dto;
    }

    public TradableDTO[] removeQuotesForUser(String userName) throws InvalidPriceException, InvalidParameterException, DataValidationException {
        if (userName == null) {
            throw new InvalidParameterException("removeQuotesForUser(String userName): userName cannot be null.");
        }

        TradableDTO buyDTO = buySide.removeQuotesForUser(userName);
        TradableDTO sellDTO = sellSide.removeQuotesForUser(userName);

        updateMarket();

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

    public void tryTrade() throws DataValidationException, InvalidParameterException {
        while (true) {
            Price buyPrice = buySide.topOfBookPrice();
            Price sellPrice = sellSide.topOfBookPrice();
            int totalToTrade = Math.min(buySide.topOfBookVolume(), sellSide.topOfBookVolume());

            if (buyPrice == null || sellPrice == null || buyPrice.compareTo(sellPrice) < 0 || totalToTrade <= 0) { return; }

            buySide.tradeOut(buyPrice, totalToTrade);
            sellSide.tradeOut(sellPrice, totalToTrade);
        }
    }

    public void updateMarket() throws InvalidPriceException, InvalidParameterException, DataValidationException{
        CurrentMarketTracker.getInstance().updateMarket(product, buySide.topOfBookPrice(), buySide.topOfBookVolume(), sellSide.topOfBookPrice(), sellSide.topOfBookVolume());
    }

    @Override
    public String toString() {
        return (String.format("--------------------------------------------\nProduct Book: %s\n%s%s--------------------------------------------",
                this.product, this.buySide.toString(), this.sellSide.toString()));
    }
}
