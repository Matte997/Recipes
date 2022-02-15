package recipes.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import recipes.entity.Recipe;
import recipes.entity.impl.UserDetailsImpl;
import recipes.repository.RecipeRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    public String saveRecipe(Recipe recipe) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();

        recipe.setUserId(currentUser.getId());
        recipe.setDate(LocalDateTime.now());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", recipeRepository.save(recipe).getId());

        return jsonObject.toString();
    }

    public Recipe getRecipe(Long recipeId) {
        return recipeRepository.findById(recipeId).orElse(null);
    }

    public List<Recipe> getRecipesByName(String name) {
        return recipeRepository.findByNameContainingIgnoreCaseOrderByDateDesc(name);
    }

    public List<Recipe> getRecipesByCategory(String category) {
        return recipeRepository.findByCategoryIgnoreCaseOrderByDateDesc(category);
    }

    public Boolean deleteRecipe(Long recipeId) {
        if (recipeRepository.existsById(recipeId)) {
            recipeRepository.deleteById(recipeId);

            return true;
        }

        return false;
    }

    public Boolean updateRecipe(Long recipeId, Recipe recipe) {
        if (recipeRepository.existsById(recipeId)) {
            Recipe retrieveRecipe = recipeRepository.findById(recipeId).orElse(null);

            recipe.setId(retrieveRecipe.getId());
            recipe.setDate(LocalDateTime.now());
            recipe.setUserId(retrieveRecipe.getUserId());

            recipeRepository.save(recipe);

            return true;
        }

        return false;
    }
}