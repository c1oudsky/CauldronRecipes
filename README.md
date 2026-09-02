# Cauldron Recipes (1.7.10)
This mod allows you to use cauldron for simple recipes that use water, in turn give something and may grant a bonus on all water used for one recipe.
Examples: TODO

It has CraftTweaker support, so you can add your custom recipes:
```zenscript
# Syntax:
mods.cauldronrecipes.addRecipe(IIngredient input, IItemStack output, IItemStack bonus, (optional) float bonuschance)
mods.cauldronrecipes.addRecipe(IIngredient input, IItemStack output, int waterUsed)
mods.cauldronrecipes.removeRecipe(IIngredient input)
```
