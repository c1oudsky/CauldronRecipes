import mods.cauldronrecipes.addRecipe;
# Converts any vanilla wooden planks to oak ones for 1/3 of lava bucket
addRecipe(<minecraft:planks:*>, <minecraft:planks>, 1, <liquid:lava>);

# Adding 10 wooden slabs of any type (but same for one recipe)
# into full water cauldron gives one cobblestone block
mods.cauldronrecipes.addRecipe(<ore:slabWood>*10, null, <minecraft:cobblestone>);

# Adding 5 wooden slabs of any type (but same for one recipe)
# into full lava cauldron converts it to water
mods.cauldronrecipes.addRecipe(<ore:slabWood>*5, <liquid:lava>, <liquid:water>);

# Removes default wool recipe with water (it is the
# default liquid if not defined with 2nd argument)
mods.cauldronrecipes.removeRecipe(<minecraft:wool:*>);
