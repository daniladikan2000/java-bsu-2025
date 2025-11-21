package Products;

public record MechanicalProduct(String id, String name, int productionTime) implements Product {
    @Override
    public String getCategory() {
        return "Mechanical";
    }
}
