
FirmaNutrition - A cleaner TFC nutrition system
===============================================

FirmaNutrition is an addon for TFC that changes the nutrition system, making nutrient decay behave in a more continuous manner, and giving players more agency over how nutrition works.

Instead of having recently eaten meals stored and summing those up to get the current nutrition of the player like in regular TFC,
nutrients are directly added to the player and decay as your hunger bar gets lower.
This makes decay feel more realistic and prevents sudden large decreases in nutrition, while also encouraging higher-nutrition meals.


Config Settings
===============

There are two different options available for how this new decay system works: either a constant rate, or a dynamic rate proportional to the nutrition a player has.
Furthermore, the latter option also allows you to set a minimum proportion, below which decay should always remain constant.

Besides that, you can also set a maximum hidden nutrition, which will make reaching 100% average nutrition easier to get (and is technically required for this, since there aren't any meals in TFC with every nutrient type)


Further Additions
=================

- Impossible in regular TFC, but trivial with this nutrition system: You can set your nutrition with a command, by using `/tfc player @s set nutrition`
- Per-player and per-nutrient decay rates: Now you can fine-tune how fast different nutrients decay, by using `/tfc player @s set decay_rate`
- For addon devs: Easy access to this more advanced nutrition system, with the new `IAdditionalNutritionData` interface. Whether making your own nutrition system or just adding compatibility, this should give you access to everything you need
