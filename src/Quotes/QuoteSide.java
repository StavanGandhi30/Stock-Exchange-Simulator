package Quotes;

import DTO.TradableDTO;
import prices.Price;
import GlobalConstants.BookSide;
import java.util.regex.Pattern;
import Interface.Tradable;
import Exception.InvalidParameterException;

class QuoteSide implements Tradable {
    private final String user;
    private final String product;
    private final Price price;
    private final BookSide side;
    private final int originalVolume;
    private int remainingVolume;
    private int cancelledVolume = 0;
    private int filledVolume = 0;
    private final String id;

    public QuoteSide(String user, String product, Price price, int originalVolume, BookSide side) throws InvalidParameterException {
        if (!Pattern.matches("[a-zA-Z]{3}", user)){
            throw new InvalidParameterException("QuoteSide(String user, ...): User code must be 3 letters, no spaces, no numbers, no special characters.");
        }

        if (!Pattern.matches("[A-Z0-9.]{1,5}", product)) {
            throw new InvalidParameterException("QuoteSide(String product, ...): Product must be 1-5 alphanumeric characters with an optional period.");
        }
        if (price == null){
            throw new InvalidParameterException("QuoteSide(Price price, ...): Price Object cannot be null.");
        }
        if (side == null){
            throw new InvalidParameterException("QuoteSide(BookSide side, ...): BookSide cannot be null.");
        }
        if (originalVolume <= 0 || originalVolume >= 10000){
            throw new InvalidParameterException("QuoteSide(int originalVolume, ...): Original volume must be between 1 and 9999.");
        }

        this.user = user;
        this.product = product;
        this.price = price;
        this.side = side;
        this.originalVolume = originalVolume;
        this.remainingVolume = originalVolume;
        this.id = user + product + price.toString() + System.nanoTime();;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getRemainingVolume() {
        return this.remainingVolume;
    }

    @Override
    public void setCancelledVolume(int newVol) {
        this.cancelledVolume = newVol;
    }

    @Override
    public int getCancelledVolume() {
        return this.cancelledVolume;
    }

    @Override
    public void setRemainingVolume(int newVol) {
        this.remainingVolume = newVol;
    }

    @Override
    public Price getPrice() {
        return this.price;
    }

    @Override
    public void setFilledVolume(int newVol) {
        this.filledVolume = newVol;
    }

    @Override
    public int getFilledVolume() {
        return this.filledVolume;
    }

    @Override
    public BookSide getSide() {
        return this.side;
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public String getProduct() {
        return this.product;
    }

    @Override
    public int getOriginalVolume() {
        return this.originalVolume;
    }

    @Override
    public String toString() {
        return String.format("%s %s side quote for %s: Price: %s, Orig Vol: %d, Rem Vol: %d, Fill Vol: %d, CXL Vol: %d, ID: %s",
                user, side, product, price, originalVolume, remainingVolume, filledVolume, cancelledVolume, id);
    }

    @Override
    public TradableDTO makeTradableDTO() {
        return new TradableDTO(
                this.getUser(),
                this.getProduct(),
                this.getPrice(),
                this.getOriginalVolume(),
                this.getRemainingVolume(),
                this.getCancelledVolume(),
                this.getFilledVolume(),
                this.getSide(),
                this.getId()
        );
    }
}
