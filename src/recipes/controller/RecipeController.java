package recipes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import recipes.entity.Recipe;
import recipes.entity.impl.UserDetailsImpl;
import recipes.repository.RecipeRepository;
import recipes.service.RecipeService;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class RecipeController {

    @Autowired
    RecipeService recipeService;

    @Autowired
    RecipeRepository recipeRepository;

    @GetMapping("/recipe/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable("id") Long id) {
        if (recipeService.getRecipe(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(recipeService.getRecipe(id), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @GetMapping("/recipe/search")
    public ResponseEntity<List<Recipe>> getRecipeBasedOnCategoryOrName(@RequestParam @Pattern(regexp = ("name|category")) Map<String, String> params) {
        if (params.size() == 1) {

            if (params.containsKey("category")) {
                return new ResponseEntity<>(recipeService.getRecipesByCategory(params.get("category")), HttpStatus.OK);
            }

            if (params.containsKey("name")) {
                return new ResponseEntity<>(recipeService.getRecipesByName(params.get("name")), HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @PostMapping("/recipe/new")
    public ResponseEntity<String> addRecipe(@RequestBody @Valid Recipe recipe) {
        return new ResponseEntity<>(recipeService.saveRecipe(recipe), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @PutMapping("/recipe/{id}")
    public ResponseEntity<?> updateRecipe(@PathVariable("id") Long id, @RequestBody @Valid Recipe recipe) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();

        if (recipeRepository.existsById(id) && !Objects.equals(recipeRepository.findById(id).get().getUserId(), currentUser.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return recipeService.updateRecipe(id, recipe) ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/recipe/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable("id") Long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();

        if (recipeRepository.existsById(id) && !Objects.equals(recipeRepository.findById(id).get().getUserId(), currentUser.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return recipeService.deleteRecipe(id) ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}