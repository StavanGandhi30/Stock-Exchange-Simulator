package quotes;

import prices.Price;
import globalconstants.BookSide;
import java.util.regex.Pattern;
import exception.InvalidParameterException;

public class Quote {
    private String user;
    private String product;
    private final QuoteSide buyside;
    private final QuoteSide sellside;

    public Quote(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume, String userName) throws InvalidParameterException  {
        setUserName(userName);
        setSymbol(symbol);

        this.buyside = new QuoteSide(userName, symbol, buyPrice, buyVolume, BookSide.BUY);
        this.sellside = new QuoteSide(userName, symbol, sellPrice, sellVolume, BookSide.SELL);
    }

    private void setUserName(String userName) throws InvalidParameterException {
        if (!Pattern.matches("[a-zA-Z]{3}", userName)) {
            throw new InvalidParameterException("Quote(..., String userName): User code must be 3 letters, no spaces, no numbers, no special characters.");
        }

        this.user = userName;
    }

    private void setSymbol(String symbol) throws InvalidParameterException {
        if (!Pattern.matches("[A-Z0-9.]{1,5}", symbol)) {
            throw new InvalidParameterException("Quote(String symbol, ...): Product must be 1-5 alphanumeric characters with an optional period.");
        }

        this.product = symbol;
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
