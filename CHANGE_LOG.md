# INFO
This log contains changes made to the project. Each entry contains changed made after the last version but before the number was changed. Any changes made after a number change are considered part of the next release. This is regardless if versions are still being released with that version number attached. 

If this is a problem, use exact build numbers to track changes. As each build logs the git-hash it was created from to better understand changes made.

# Versions
## 5.0.#
### Runtime Changes

### Development Changes

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


