package ProductBook;

import java.util.*;
import java.util.regex.Pattern;
import DTO.*;
import Interface.*;
import Quotes.*;
import Exception.*;
import User.*;

import static GlobalConstants.BookSide.*;


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
        } catch (InvalidParameterException error) {
            throw new DataValidationException(error.getMessage());
        }
    }

    public ProductBook getProductBook(String symbol) throws DataValidationException {
        if (symbol==null || !ProductBooks.containsKey(symbol)) {
            throw new DataValidationException("");
        }

        return ProductBooks.get(symbol);
    }

    private static <K, V> K getRandomKey(TreeMap<K, V> treeMap) {
        List<K> keys = new ArrayList<>(treeMap.keySet());
        return keys.get(new Random().nextInt(keys.size()));
    }

    public String getRandomProduct() throws DataValidationException {
        if (ProductBooks.isEmpty()) {
            throw new DataValidationException("");
        }

        return getRandomKey(ProductBooks);
    }

    public TradableDTO addTradable(Tradable o) throws DataValidationException {
        if (o==null) {
            throw new DataValidationException("");
        }

        TradableDTO tDTO = new TradableDTO(o);
        this.addProduct(o.getProduct());
        UserManager.getInstance().updateTradable(o.getUser(), tDTO);

        return tDTO;
    }

    public TradableDTO[] addQuote(Quote q) throws DataValidationException, InvalidParameterException {
        if (q==null) {
            throw new DataValidationException("");
        }

        String symbol = q.getSymbol();
        String user = q.getUser();

        ProductBook productBook = ProductBooks.get(symbol);
        productBook.removeQuotesForUser(user);

        TradableDTO buySide = addTradable(q.getQuoteSide(BUY));
        TradableDTO sellSide = addTradable(q.getQuoteSide(SELL));


        return new TradableDTO[]{buySide, sellSide};
    }

    public TradableDTO cancel(TradableDTO o) throws DataValidationException {
        if (o==null) {
            throw new DataValidationException("");
        }

        try {
            return ProductBooks.get(o.product()).cancel(o.side(), o.tradableId());
        } catch (InvalidParameterException e) {
            System.out.printf("failure to cancel: %s\n", e.getMessage());
            return null;
        }
    }

    public TradableDTO[] cancelQuote(String symbol, String user) throws DataValidationException, InvalidParameterException {
        if (symbol==null) {
            throw new DataValidationException("");
        }

        if (user==null) {
            throw new DataValidationException("");
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
        return "ProductManager().toString()\n";
    }
}
