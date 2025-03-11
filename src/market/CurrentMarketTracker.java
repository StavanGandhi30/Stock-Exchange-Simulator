package market;

import exception.*;
import prices.*;

public class CurrentMarketTracker {
    private static CurrentMarketTracker instance;

    private CurrentMarketTracker() {}

    public static CurrentMarketTracker getInstance() {
        if (instance == null) {
            instance = new CurrentMarketTracker();
        }
        return instance;
    }

    public void updateMarket(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) throws InvalidPriceException, InvalidParameterException {
        Price marketWidth = (buyPrice != null && sellPrice != null) ? sellPrice.subtract(buyPrice) : PriceFactory.makePrice(0);

        CurrentMarketSide buySide = new CurrentMarketSide((buyPrice == null) ? PriceFactory.makePrice(0) : buyPrice, buyVolume);
        CurrentMarketSide sellSide = new CurrentMarketSide((sellPrice == null) ? PriceFactory.makePrice(0) : sellPrice, sellVolume);

        System.out.println("*********** Current Market ***********");
        System.out.printf("* %s %s - %s [%s]%n", symbol, buySide, sellSide, marketWidth);
        System.out.println("**************************************");

        CurrentMarketPublisher.getInstance().acceptCurrentMarket(symbol, buySide, sellSide);
    }
}