package Quotes;

import prices.Price;
import GlobalConstants.BookSide;
import java.util.regex.Pattern;
import Exception.InvalidParameterException;

public class Quote {
    private final String user;
    private final String product;
    private final QuoteSide buyside;
    private final QuoteSide sellside;

    public Quote(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume, String userName) throws InvalidParameterException  {
        if (!Pattern.matches("[a-zA-Z]{3}", userName)) {
            throw new InvalidParameterException("Quote(..., String userName): User code must be 3 letters, no spaces, no numbers, no special characters.");
        }
        if (!Pattern.matches("[A-Z0-9.]{1,5}", symbol)) {
            throw new InvalidParameterException("Quote(String symbol, ...): Product must be 1-5 alphanumeric characters with an optional period.");
        }

        this.user = userName;
        this.product = symbol;
        this.buyside = new QuoteSide(userName, symbol, buyPrice, buyVolume, BookSide.BUY);
        this.sellside = new QuoteSide(userName, symbol, sellPrice, sellVolume, BookSide.SELL);
    }

    public QuoteSide getQuoteSide(BookSide sideIn) throws InvalidParameterException {
        if (sideIn == null) {
            throw new InvalidParameterException("getQuoteSide(BookSide sideIn): Invalid BookSide value.");
        }
        return ((sideIn == BookSide.BUY) ? this.buyside : this.sellside);
    }

    public String getSymbol() {
        return this.product;
    }

    public String getUser() {
        return this.user;
    }
}
