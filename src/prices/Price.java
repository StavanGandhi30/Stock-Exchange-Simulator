package prices;

import java.util.Objects;
import Exception.InvalidPriceException;

public class Price implements Comparable<Price>{
    private final int cents;

    public Price(int x) {
        this.cents = x;
    }

    public Price add(Price p) throws InvalidPriceException {
        if (p != null) {
            return new Price(cents + p.cents);
        }
        throw new InvalidPriceException("Cannot add null to a Price object");
    }

    public Price subtract(Price p) throws InvalidPriceException {
        if (p != null) {
            return new Price(cents - p.cents);
        }
        throw new InvalidPriceException("Cannot subtract null to a Price object");
    }

    public Price multiply(int multiplier) {
        return new Price(cents * multiplier);
    }

    public Boolean isNegative() {
        return (this.cents < 0);
    }

    public Boolean greaterOrEqual(Price p) throws InvalidPriceException {
        if (p != null) {
            return !(this.cents < p.cents);
        }
        throw new InvalidPriceException("Cannot check greater than or equal with null");
    }

    public Boolean lessOrEqual(Price p) throws InvalidPriceException {
        if (p != null) {
            return !(this.cents > p.cents);
        }
        throw new InvalidPriceException("Cannot check less than or equal with a null");
    }

    public Boolean greaterThan(Price p) throws InvalidPriceException {
        if (p != null) {
            return (this.cents > p.cents);
        }
        throw new InvalidPriceException("Cannot check greater than with a null");
    }

    public Boolean lessThan(Price p) throws InvalidPriceException {
        if (p != null) {
            return (this.cents < p.cents);
        }
        throw new InvalidPriceException("Cannot check less than with a null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price p = (Price) o;
        return (this.cents == p.cents);
    }

    @Override
    public int compareTo(Price p) {
        if (p != null) {
            return (cents - p.cents);
        }
        return (-1);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cents);
    }

    @Override
    public String toString() {
        return String.format("$%,.2f",cents / 100.0);
    }
}
