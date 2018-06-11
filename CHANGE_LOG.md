# INFO
This log contains changes made to the project. Each entry contains changed made after the last version but before the number was changed. Any changes made after a number change are considered part of the next release. This is regardless if versions are still being released with that version number attached. 

If this is a problem, use exact build numbers to track changes. As each build logs the git-hash it was created from to better understand changes made.

# Versions
## 5.0.#
### Runtime Changes
### Development Changes

## 5.0.7
### Runtime Changes
* Added: ability to switch end cap input/output state
* Added: geometry to end caps for lower edge bars

* Changed: inserter to output towards player when placed

* Fixed: end cap arrow rendering wrong direction
* Fixed: items getting destroyed in end cap
            minor issue caused by end cap using wrong slot ID (2 rather than 1)
            Another patch will be added to prevent issues like this
* Fixed: inserter setting with up or down rotation when placed
* Fixed: inserter being off by a few degrees when told to face a side
* Fixed: inserter facing wrong direction when first placed
* Fixed: visual rotation snap when loading world or placing inserter

### Development Changes

## 5.0.6
### Runtime Changes
* Added: simple model for end cap pipe belt
* Added: logic for end cap pipe belt
* Added: logic to change input/output of junction and intersection pipe belts
* Fixed: crash due to empty belt state list
* Fixed: crash loading pipe belt with local state data


### Development Changes
* Rewrote: pipe belt state list to use a hashmap
* Rewrote: pipe belt state to seperate global from local data
* Moved: pipe belt NBT keys to state final fields


## 5.0.5 - 6/9/2018
### Runtime Changes
* Added: Item render for inserter arm
* Added: Ability to insert items into pipe belt with inserter from several sides.
         Previous only supported inserting from back in the same way pipes worked.
* Added: Improved inserter handling for node based tiles
* Added: End cap pipe belt 

* Fixed: Crash getting collision box for rail block (actual fix in VoltzEngine)
* Fixed: pipe belt pick block

### Development Changes
* Added: IInserterAccess to allow controling how robotic arms add and remove items.
* Abstracted: belt pipe item render code into helper class. Allows any machine to render fake items in the world.


## 5.0.4
### Runtime Changes
### Development Changes
TODO Go through old commits to make change log


