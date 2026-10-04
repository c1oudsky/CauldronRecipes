# Cauldron Recipes (1.7.10)
This mod allows you to use cauldron for simple recipes that use water, in turn give something and may grant a bonus on all water used for one recipe.
Examples:
wash color off wool and colored glass blocks and panes with water (1/3 bucket for glass and 2/3 for wool)!
Wash color off terracotta with lava (1/3 of bucket)!
Burn wool in lava in cauldron to get 2 string (1/3 of bucket)!
Wash dirt in water to see if any wheat seeds wash off it (full bucket spent)!

Each cauldron saves data about stored fluid and bound recipe (if it could be/needed to be bound). Any registered in Forge fluid is supported, meaning even modded ones if you like!
How, you say? Well, it has CraftTweaker support, so you can add your custom recipes with following syntax:
```zenscript
# Syntax:
mods.cauldronrecipes.addRecipe(IIngredient input, IItemStack output, IItemStack bonus, (optional) float bonuschance, (optional) boolean clustered, (optional) ILiquidStack fluid)
mods.cauldronrecipes.addRecipe(IIngredient input, IItemStack output, int waterUsed, (optional) ILiquidStack fluid)

# Removes added recipes - in case if you want to disable any of the mod's default recipes
mods.cauldronrecipes.removeRecipe(IIngredient input)
```
note: clustered parameter (false by default) - if true, spawns bonus itemstack in full quantity if the probability check passed, and spawns none if not. default behavior (e.g. false) rather rolls probability for each item of the whole stack separately so players get more equally dispersed results (more frequent average results rather than either none or all).
Of course, you can't (and wouldn't need to) set it if you don't use bonus chance parameter.
If you set bonus and no regular ouput then the recipe is considered compound - the player will have to put full amount of required item as 1 per click, and fluid height in cauldron will not go lower until full amount is put - then it gets emptied and result output is given.
