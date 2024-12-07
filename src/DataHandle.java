
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.*;

public class DataHandle {
    private String folderPath = "./src/Recipes";
    private HashMap<String, Integer> RecipeList=new HashMap<>();
    private HashMap<String, String>RecipeText = new HashMap<>();
    public DataHandle(){
        File folder = new File(folderPath);
        setRecipe(folder);
    }
    private void setRecipe(File folder){
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.getName().endsWith(".txt")) {
                    Path path=Paths.get("./src/Recipes/"+file.getName());
                    try{
                        List<String> allLines = Files.readAllLines(path);
                        String Text="";
                        String Name=allLines.get(0);
                        String Time=allLines.get(1);
                        Name = Name.replace("레시피 이름: ", "").trim();
                        Time = Time.replace("조리 시간: ", "").replace("분", "").trim();
                        RecipeList.put(Name, Integer.parseInt(Time));
                        for(int i=0; i<allLines.size(); i++){
                            Text+=allLines.get(i)+"\n";
                        }
                        RecipeText.put(Name ,Text);
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public HashMap<String, Integer> getRecipe(){
        return RecipeList;
    }
    public HashMap<String, String> getRecipeText(){
        return RecipeText;
    }

    public static void main(String[] args) {
        DataHandle data = new DataHandle();
        HashMap<String, Integer> hashmap= data.getRecipe();
        System.out.println(hashmap);
        HashMap<String, String> hashmapt= data.getRecipeText();
        System.out.println(hashmapt);
    }
}


