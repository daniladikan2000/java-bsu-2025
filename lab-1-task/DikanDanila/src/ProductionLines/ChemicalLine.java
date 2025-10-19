package ProductionLines;

import Products.ChemicalProduct;
import Products.Product;
import java.util.List;

public class ChemicalLine extends ProductionLine<ChemicalProduct> {
    public ChemicalLine(String lineId, List<ChemicalProduct> products, double efficiency) {
        super(lineId, products, efficiency);
    }

    @Override
    public boolean canProduce(Product product) {
        return product instanceof ChemicalProduct;
    }
}