package Products;

public record ElectronicProduct(String id, String name, int productionTime) implements Product {
    @Override
    public String getCategory() {
        return "Electronic";
    }
}
