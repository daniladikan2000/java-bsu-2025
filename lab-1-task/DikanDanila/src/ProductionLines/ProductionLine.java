package ProductionLines;

import java.util.List;
import java.util.ArrayList;

import Products.Product;

public abstract class ProductionLine<T extends Product> {
    protected String lineId;
    protected List<T> products;
    protected double efficiency;

    ProductionLine(String lineId, List<T> products, double efficiency) {
        if (efficiency < 0.0 || efficiency > 1.0) {
            throw new IllegalArgumentException(
                    String.format("Efficiency must be between 0.0 and 1.0, but was: %.2f", efficiency)
            );
        }

        this.lineId = lineId;
        this.products = new ArrayList<>(products);
        this.efficiency = efficiency;
    }

    public abstract boolean canProduce(Product product);

    public String getLineId() {
        return lineId;
    }

    public List<T> getProducts() {
        return products;
    }

    public double getEfficiency() {
        return efficiency;
    }
}
