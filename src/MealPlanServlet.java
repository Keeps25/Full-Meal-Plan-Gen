import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javax.servlet.*;
import javax.servlet.http.*;

public class MealPlanServlet extends HttpServlet {
    HashMap<Integer, Ingredient> ingredients = new HashMap<>();
    HashMap<Integer, Recipe> recipes = new HashMap<>();
    public void init() throws ServletException {
        if (recipes.isEmpty() || ingredients.isEmpty()) {
        Connection connection = null;
        //Keys are Ingredient and Recipe IDs respectively
        //Values are the Ingredient or Recipe object itself
            try {
                String driver = "com.mysql.jdbc.Driver";
                Class.forName(driver);

                //Connect to database
                String server = "localhost";
                String path = "test";
                String url = "jdbc:mysql://" + server + "/" + path;
                String user = "bobbarker";
                String pass = "password123";
                connection = DriverManager.getConnection(url, user, pass);
                System.out.println("Successfully connected to database!");


            } catch (ClassNotFoundException e) {
                System.out.println("Couldn't find database driver " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("An error occurred. Could not connect to database " + e.getMessage());
            }

            try {
                Statement statement = connection.createStatement();
                ResultSet results = statement.executeQuery("SELECT * FROM ingredient");
                //Loop through recipes, add to HashMap
                while (results.next()) {
                    String name = results.getString(0);
                    int ID = results.getInt(1);
                    String picID = results.getString(2);
                    double calories = results.getDouble(3);
                    double carbs = results.getDouble(4);
                    double proteins = results.getDouble(5);
                    double fats = results.getDouble(6);

                    Ingredient curr = new Ingredient(name, ID, picID, calories, carbs, proteins, fats);
                    ingredients.put(ID, curr);
                }

                results = statement.executeQuery("SELECT * FROM recipe");
                //Looping through recipes, add to HashMap
                while (results.next()) {
                    String name = results.getString(0);
                    int ID = results.getInt(1);
                    String picID = results.getString(2);

                    //Parse integer IDs
                    String ingredientsIDsList = results.getString(3);
                    ArrayList<Integer> IDlist = parseString(ingredientsIDsList);
                    //Parse double Quantities
                    String ingredientsQuantitiesList = results.getString(4);
                    ArrayList<Double> quantitiesList = parseString2(ingredientsQuantitiesList);

                    Recipe recipe = new Recipe(name, ID, picID, IDlist, quantitiesList);
                    recipes.put(ID, recipe);
                }

            } catch (Exception e) {
                System.out.println("Couldn't retrieve data successfully" + e.getMessage());
            }

            System.out.println("Recipes and Ingredients have been retrieved from server");
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        int calories = Integer.parseInt(request.getParameter("calories"));

        String json = "{";
        json += sendRecipes(ingredients, recipes, calories);
        json += "\n}";

        out.write(json);

        out.close();
    }

    private static ArrayList<Integer> parseString(String list) {
        ArrayList<Integer> finalList = new ArrayList<>();
        String num = "";
        int i = 0;
        while (i < list.length()) {
            char ch = list.charAt(i);
            if (Character.isDigit(ch)) {
                num += ch;
                i++;
                while(i < list.length() && Character.isDigit(list.charAt(i))) {
                    ch = list.charAt(i);
                    num += ch;
                    i++;
                    //Now i should be pointing to a comma or end of list
                }
                finalList.add(Integer.parseInt(num));
            }
            num = "";
            i++;
        }

        return finalList;
    }

    private static ArrayList<Double> parseString2(String list) {
        ArrayList<Double> finalList = new ArrayList<>();
        String num = "";
        int i = 0;
        while (i < list.length()) {
            char ch = list.charAt(i);
            if (Character.isDigit(ch) || ch == '.') {
                num += ch;
                i++;
                while(i < list.length() && (Character.isDigit(list.charAt(i)) || list.charAt(i) == '.')) {
                    ch = list.charAt(i);
                    num += ch;
                    i++;
                    //Now i should be pointing to a comma or end of list
                }
                finalList.add(Double.parseDouble(num));
            }
            num = "";
            i++;
        }

        return finalList;
    }

    private static String sendRecipes(HashMap<Integer, Ingredient> ingredients, HashMap<Integer, Recipe> recipes, int totalCal) {
        String result = "";
        int recipesSize = recipes.size();
        Random r = new Random();
        String[] days =  {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" , "Sunday"};
        String[] times =  {"Breakfast", "Lunch", "Dinner"};
        for (int day = 0; day < 7; day++){
            if (day != 0) {
                result += (",");
            }
            result += ("\"" + days[day]);
            result += ("\": {");
            int caloriesLeft = totalCal;
            for (int time = 0; time < 3; time++) {
                int timeout = 0;
                int currentCal = 0;
                Recipe randomRecipe = null;
                while ((currentCal == 0 || currentCal > caloriesLeft) && timeout < 10){
                    currentCal = 0;
                    randomRecipe = recipes.get(r.nextInt(recipesSize) + 1);
                    for (int y = 0; y < randomRecipe.ingredientsIDList.size(); y++){
                        int ingredientID = randomRecipe.ingredientsIDList.get(y);
                        currentCal += ingredients.get(ingredientID).calories * randomRecipe.ingredientsQuantity.get(y);
                    }
                    timeout++;
                }
                if (time != 0) {
                    result += (",");
                }
                result += ("\"" + times[time]);
                result += ("\": {");
                result += jsonMaker(ingredients, randomRecipe, currentCal);
                result += ("}");
                caloriesLeft -= currentCal;
            }
            result += ("}");
        }
        return result;
    }

    private static String jsonMaker(HashMap<Integer, Ingredient> ingredients, Recipe recipe, int totalCalories) {
        String json = "";
        json += ("\t\"name\": \"");
        json += (recipe.recipeName);
        json += ("\",\n\t\"picture_id\": \"");
        json += recipe.picID;
        json += ("\",\n\t\"calories\": \"");
        json += totalCalories;
        json += ("\",\n\t\"ingredients\": [");
        for (int i = 0; i < recipe.ingredientsIDList.size(); i++) {
            if( i!= 0){json += ",";}
            json += "\n\t\t{";
            json += "\n\t\t\t\"name\": \"" + ingredients.get(recipe.ingredientsIDList.get(i)).ingredientName;
            json += "\",\n\t\t\t\"quantity\": \"" + recipe.ingredientsQuantity.get(i);
            json += "\"\n\t\t}";
        }
        json += "\n]";
        return json;
    }
}