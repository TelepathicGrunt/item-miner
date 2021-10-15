Commission done by TelepathicGrunt
For: McBirken

start time: 10:18pm 10/13/2021

Mod details: specify a block to be mined and randomly spawns items based on progress.

### Checklist:

Add Useless Sword into dev environment for testing later on. (Finished)

Create a class to parse registry at mod startup and create a map of level to a set of items they can spawn. (Finished)

Create a config to specify which block is the item miner and who is the leader that the level is based on. (Finished)

Hook up capability onto player to hold mining progress. (Finished)

Hook up the code to make block unbreakable and no mining overlay but spawns blocks instead based on player progress. 
  If player is not the specified leader, spawn level 1 items always. (Finished)

Create gui element that shows the player's progress on screen. (Finished)

Create packet system so that server can send the leader's progress to everyone. (Finished)

Hook up gui progress bar to the packet on client side. (Finished)

Test and see if it all works. (Finished)


### Extra checklist:

improve config to specify any level and any items. (Finished)

### Version 2 checklist:

config to specify how many blocks needed to level up for each level.

config to do modded % rates and import all modded items

config to allow spawning mobs from spawn eggs right away.