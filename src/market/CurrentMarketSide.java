package market;
import exception.*;
import prices.*;

public class CurrentMarketSide {
    private Price price;
    private int volume;

    public CurrentMarketSide(Price price, int volume) throws InvalidParameterException {
        setPrice(price);
        setVolume(volume);
    }

    private void setPrice(Price price) throws InvalidParameterException {
        if (price == null){
            throw new InvalidParameterException("CurrentMarketSide(Price price, ...): Price Object cannot be null.");
        }

        this.price = price;
    }

    private void setVolume(int volume) throws InvalidParameterException {
        if (volume < 0){
            throw new InvalidParameterException("CurrentMarketSide(Int Vol, ...): Int Volume can't be less than 0.");
        }

        this.volume = volume;
    }

    public String toString() {
        return String.format("%sx%d", this.price.toString(), this.volume);
    }
}