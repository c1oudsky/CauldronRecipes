# Cauldron Recipes (1.7.10)
This mod allows you to use cauldron for simple recipes that use water, in turn give something and may grant a bonus on all water used for one recipe.
Examples: TODO

It has CraftTweaker support, so you can add your custom recipes:
```zenscript
# Syntax:
mods.cauldronrecipes.addRecipe(IIngredient input, IItemStack output, IItemStack bonus, (optional) float bonuschance, (optional) boolean clustered)
mods.cauldronrecipes.addRecipe(IIngredient input, IItemStack output, int waterUsed)
mods.cauldronrecipes.removeRecipe(IIngredient input)
```
note: clustered parameter (false by default) - if true, spawns bonus itemstack in full quantity if the probability check passed, and spawns none if not. default behavior (e.g. false) rather rolls probability for each item of the whole stack separately so players get more equally dispersed results (more frequent average results rather than either none or all).
Of course, you can't (and wouldn't need to) set it if you don't use bonus chance parameter.
