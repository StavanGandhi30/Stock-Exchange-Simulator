package productbook;

import java.util.*;
import java.util.regex.Pattern;

import dto.*;
import interfaces.*;
import quotes.*;
import exception.*;
import user.*;

import static globalconstants.BookSide.*;


public class ProductManager {
    private static ProductManager instance;

    private final TreeMap<String, ProductBook> ProductBooks = new TreeMap<>();

    private ProductManager() {}

    public static ProductManager getInstance(){
        if (instance == null) {
            instance = new ProductManager();
        }
        return instance;
    }

    public void addProduct(String symbol) throws DataValidationException {
        try {
            ProductBooks.put(symbol, new ProductBook(symbol));
        } catch (Exception e) {
            throw new DataValidationException("Product must be 1-5 alphanumeric characters with an optional period.");
        }
    }

    public ProductBook getProductBook(String symbol) throws DataValidationException {
        if (symbol==null || !ProductBooks.containsKey(symbol)) {
            throw new DataValidationException("Product does not exist");
        }

        return ProductBooks.get(symbol);
    }

    public String getRandomProduct() throws DataValidationException {
        if (ProductBooks.isEmpty()) {
            throw new DataValidationException("ProductBook is empty!");
        }

        return ProductBooks.keySet().iterator().next();
    }

    public TradableDTO addTradable(Tradable o) throws DataValidationException, InvalidPriceException, InvalidParameterException {
        if (o == null) {
            throw new DataValidationException("Tradable passed in is null");
        }

        ProductBook productBook = getProductBook(o.getProduct());
        TradableDTO dto = productBook.add(o);
        UserManager.getInstance().updateTradable(o.getUser(), dto);
        return dto;
    }

    public TradableDTO[] addQuote(Quote q) throws InvalidPriceException, DataValidationException, InvalidParameterException {
        if (q==null) {
            throw new DataValidationException("Quote passed in is null");
        }

        ProductBook productBook = getProductBook(q.getSymbol());
        productBook.removeQuotesForUser(q.getUser());

        TradableDTO buySide = addTradable(q.getQuoteSide(BUY));
        TradableDTO sellSide = addTradable(q.getQuoteSide(SELL));

        return new TradableDTO[]{buySide, sellSide};
    }

    public TradableDTO cancel(TradableDTO o) throws InvalidPriceException, DataValidationException {
        if (o==null) {
            throw new DataValidationException("Failure to cancel.\n");
        }

        try {
            return ProductBooks.get(o.product()).cancel(o.side(), o.tradableId());
        } catch (InvalidParameterException e) {
            return null;
        }
    }

    public TradableDTO[] cancelQuote(String symbol, String user) throws InvalidPriceException, DataValidationException, InvalidParameterException {
        if (symbol==null) {
            throw new DataValidationException("Symbol passed in is null");
        }

        if (user==null) {
            throw new DataValidationException("User passed in is null");
        }

        if (!ProductBooks.containsKey(symbol)) {
            throw new DataValidationException("");
        }

        try {
            return ProductBooks.get(symbol).removeQuotesForUser(user);
        } catch (InvalidParameterException e){
            System.out.printf("failure to cancel: %s\n", e.getMessage());
            return null;
        }
    }

    @Override
    public String toString(){
        StringBuilder returnStr = new StringBuilder();

        for (Map.Entry<String, ProductBook> productBookEntries: ProductBooks.entrySet()){
            returnStr.append(productBookEntries.getValue().toString());
        }

        return returnStr.toString();
    }
}
