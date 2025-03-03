import Interface.Tradable;
import prices.Price;
import GlobalConstants.BookSide;
import java.util.regex.Pattern;
import DTO.TradableDTO;
import Exception.InvalidParameterException;

class Order implements Tradable {
    private String user;
    private String product;
    private Price price;
    private BookSide side;
    private int originalVolume;
    private int remainingVolume;
    private int cancelledVolume = 0;
    private int filledVolume = 0;
    private final String id;

    public Order(String user, String product, Price price, int originalVolume, BookSide side) throws InvalidParameterException  {

        if (price == null){
            throw new InvalidParameterException("Order(..., Price price, ...): Price Object cannot be null.");
        }
        if (side == null){
            throw new InvalidParameterException("Order(..., BookSide side): BookSide Object cannot be null.");
        }
        if (originalVolume <= 0 || originalVolume >= 10000){
            throw new InvalidParameterException("Order(..., int originalVolume, ...): Original volume must be between 1 and 9999.");
        }

        setUser(user);
        try {
            setProduct(product);
        } catch (Exception.InvalidParameterException e) {
            throw new RuntimeException(e);
        }
        this.price = price;
        this.side = side;
        this.originalVolume = originalVolume;
        this.remainingVolume = originalVolume;
        this.id = user + product + price + System.nanoTime();
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
        return String.format("%s %s side order for %s: Price: %s, Orig Vol: %d, Rem Vol: %d, Fill Vol: %d, CXL Vol: %d, ID: %s",
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

    private void setUser(String user) throws InvalidParameterException {
        if (Pattern.matches("[a-zA-Z]{3}", user)){
            this.user = user;
        }

        throw new InvalidParameterException("Order(String user, ...): User code must be 3 letters, no spaces, no numbers, no special characters.");
    }

    private void setProduct(String product) throws InvalidParameterException {
        if (Pattern.matches("[A-Z0-9.]{1,5}", product)) {
            this.product = product;
        }
        throw new InvalidParameterException("Order(..., String product, ...): Product must be 1-5 alphanumeric characters with an optional period.");
    }
}

