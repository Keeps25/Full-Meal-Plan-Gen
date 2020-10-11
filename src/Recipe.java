import java.util.ArrayList;

public class Recipe {
    public String recipeName;
    public int recipeID;
    public String picID;
    public ArrayList<Integer> ingredientsIDList;
    public ArrayList<Double> ingredientsQuantity;

    public Recipe(String name, int ID, String picID, ArrayList<Integer> ingredientIDList, ArrayList<Double> ingredientsQuantity) {
        this.recipeName = name;
        this.recipeID = ID;
        this.picID = picID;
        this.ingredientsIDList = ingredientIDList;
        this.ingredientsQuantity = ingredientsQuantity;
    }
}