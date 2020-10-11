public class Ingredient {
    public String ingredientName;
    public int ingredientID;
    public String picID;
    public double calories;
    public double carbs;
    public double protein;
    public double fat;
    public String unit;

    public Ingredient(String name, int ID, String picID, double calories, double carbs, double protein, double fat, String unit) {
        this.ingredientName = name;
        this.ingredientID = ID;
        this.picID = picID;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.unit = unit;
    }
}