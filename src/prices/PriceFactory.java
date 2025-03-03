package prices;

import java.util.*;
import Exception.*;

public abstract class PriceFactory  {
    private static final Map<Integer, Price> priceCache = new HashMap<>();

    public static Price makePrice(int value) {
        if (!priceCache.containsKey(value)) {
            priceCache.put(value, new Price(value));
        }

        return priceCache.get(value);
    }

    public static Price makePrice(String value) throws InvalidPriceException {
        String strValue = value.trim().replace("$", "").replace(",", "");
        String[] strings = strValue.split("\\.");

        if (strValue.isEmpty() || strings.length > 2 || strValue.matches(".*[a-zA-Z].*")) {
            throw new InvalidPriceException("Invalid price String value: " + value);
        } else if (strings.length == 1){
            strValue = strings[0] + "00";
        } else if (strings[1].length() == 2) { //Checks for more or less than one decimal value
            strValue = strValue.trim().replace(".", "");
        } else {
            throw new InvalidPriceException("Invalid price String value: " + value);
        }

        return makePrice(Integer.parseInt(strValue));
    }
}