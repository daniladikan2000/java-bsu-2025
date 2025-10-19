package ProductionLines;

import Products.ElectronicProduct;
import Products.Product;

import java.util.List;

public class ElectronicsLine extends ProductionLine<ElectronicProduct> {
    public ElectronicsLine(String lineId, List<ElectronicProduct> products, double efficiency) {
        super(lineId, products, efficiency);
    }

    @Override
    public boolean canProduce(Product product) {
        return product instanceof ElectronicProduct;
    }
}
