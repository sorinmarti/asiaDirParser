# AsiaDirParser

This is a collection of scripts to manipulate exported data from a very specific set of documents.

The script works with a configuration file (see example "asiaDir.conf"). The following example does the following:
* execute the function `updatePlaces`
* as input file use `res/places_in.tsv`
* as output file name (do not specify a filetype) use `res/places_out`
* generate `ALL` output types, meaning `TSV` and `JSON`
* The input file has a header line (titles which are not part of the data)...
* ...but it has no id column (first column with identifiers)
* The script will update missing geo data (lon, lat)...
* ...but it will not overwrite already present data.
* The script will sort all entries by ülace name...
* ...and it will save separate filtered files with incomplete/complete entries. 

```python
function.updatePlaces = TRUE
updatePlaces.file.in  = res/places_in.tsv
updatePlaces.file.out = res/places_out
updatePlaces.output.type = ALL
updatePlaces.hasHeaders = TRUE
updatePlaces.hasIdColumn = FALSE
updatePlaces.updateGeoData = TRUE
updatePlaces.forceReloadGeoData = FALSE
updatePlaces.sortByPlaceNames = TRUE
updatePlaces.saveFilteredLists = TRUE
```

There are the following functions:

| Function | Description|
| --------------------------------------- |------------- |
| Update PLACES file                      | Updates data in a TSV file, representing a place name dictionary. By parsing TGN ids, Longitude and Latitude values are added to the data. Can output the updated TSV file and/or a GeoJson version | 
| Check integrity of PLACES file          | Checks if there are duplicate place names or duplicate variations of place names. A list of duplicates is printed to `std.out` |
| Add PERSONS to PLACES                   | Takes a PERSONS file (containing names and place name variations) and a PLACES file. Counts the number of PERSONS in each PLACE and saves the result to a specified file. |
| Add PERSONS to GROUPS                   | Does the same as the above function but works with GROUPS. Several PLACES can be grouped and the counting is done for the group instead of the single PLACES |
| Get unreckognized PLACES                | Takes a PERSONS file, extracts all the place names which are not yet in the PLACE file. A list of unknown places is printed to `std.out` |
| Create HTML render of PLACESwithPERSONS | Takes the result of "Add PERSONS to PLACES" and creates a HTML render of it. |
| Add PERSONS to SHIPS                    | Basically the same as "Add PERSONS to PLACES" but with a SHIP file instead a PLACES file. These two file types have slightly different data columns and grouping is not possible.


The TSV files MUST have the following structure:

| File | Columns |
| ---- |------------- |
| PLACE file | [ID];Name;TGN;LAT;LON;Type;Category;Group;Maps-Link;Comment;UncertaintyFlag;Variation1;[Variation2-50] |
| PERSONS file | Last name;First name;Place name;IIIF-Link |
| SHIP file | Comment;Ship name;Computed Ship name;Ship nationality;Variation1;[Variation2-50] |


All the functions can remain in the provided conf file. If you don't want to execute a specific function, set the first option (`function.*`) to `FALSE`.

```python
###########################################################
## THIS IS A CONFIGURATION FILE FOR THE ASIA DIR PARSER  ##
## --> Below are the options (1) - (5) for this script.  ##
##     The functions are executed in this order.         ##
## --> The options can be changed accordingly.           ##
## --> Each option can be activated by setting           ##
##     function.* TRUE or FALSE                          ##
###########################################################

###########################################################
### (0) GENERAL CONFIGURATION
# (Log level can be INFO, WARN, ERROR)
log.level = WARN
# (File the log output gets saved to)
log.output = out.log
### END (0)
###########################################################

###########################################################
### (1) UPDATE A PLACES FILE
# Execute the function
function.updatePlaces = FALSE
# File to update
updatePlaces.file.in  = res/places_in.tsv
# Output file (do not specify an file ending, it will be generated)
updatePlaces.file.out = res/places_out
# File type for output (TSV, JSON, ALL)
updatePlaces.output.type = ALL
# Does the input file have a header line? (TRUE, FALSE)
updatePlaces.hasHeaders = TRUE
# Does the input file have a an id column (first column)? (TRUE, FALSE)
updatePlaces.hasIdColumn = FALSE
# Should the geo data be updated (through tgn id)? (TRUE, FALSE)
updatePlaces.updateGeoData = TRUE
# Should the geo data be updated even if it's already there? (TRUE, FALSE)
updatePlaces.forceReloadGeoData = FALSE
# Should the output file saved in alphabetical order? (TRUE, FALSE)
updatePlaces.sortByPlaceNames = TRUE
# Also save filtered lists (incomplete and complete entries)? (TRUE, FALSE)
updatePlaces.saveFilteredLists = TRUE
### END (1)
###########################################################

###########################################################
### (2) CHECK INTEGRITY OF PLACE FILE
# Execute the function
function.checkIntegrity = FALSE
# File to check
checkIntegrity.file.in  = res/places_in.tsv
# Does the input file have a header line? (TRUE, FALSE)
checkIntegrity.hasHeaders = TRUE
# Does the input file have a an id column (first column)? (TRUE, FALSE)
checkIntegrity.hasIdColumn = FALSE
### END (2)
###########################################################

###########################################################
### (3) ADD PERSONS TO PLACES 
# Execute the function
function.processPersonsFile = FALSE
# File to check
processPersonsFile.placesFile.in  = res/places_in.tsv
# Does the place input file have a header line? (TRUE, FALSE)
processPersonsFile.hasHeaders = TRUE
# Does the place input file have a an id column (first column)? (TRUE, FALSE)
processPersonsFile.hasIdColumn = FALSE
# The persons file to read
processPersonsFile.personsFile.in = res/persons_1896.tsv
# The places file to write
processPersonsFile.personsFile.out = res/placesWithPersons_1896.tsv
### END (3)
###########################################################

###########################################################
### (4) ADD PERSONS TO GROUPS 
# Execute the function
function.processPersonsFileToGroup = FALSE
# File to check
processPersonsFileToGroup.placesFile.in  = res/places_in.tsv
processPersonsFileToGroup.groupsFile.in  = res/groups.tsv
# Does the place input file have a header line? (TRUE, FALSE)
processPersonsFileToGroup.hasHeaders = TRUE
# Does the place input file have a an id column (first column)? (TRUE, FALSE)
processPersonsFileToGroup.hasIdColumn = FALSE
# The persons file to read
processPersonsFileToGroup.personsFile.in = res/persons_1896.tsv
# The places file to write
processPersonsFileToGroup.personsFile.out = res/placesInGroupsWithPersons_1896.tsv
### END (4)
###########################################################

###########################################################
### (5) GET UNRECKOGNIZED PLACES FROM PERSONS FILE 
# Execute the function
function.showUnreckognizedPlaces = FALSE
# File to check
showUnreckognizedPlaces.placesFile.in  = res/places_in.tsv
# Does the place input file have a header line? (TRUE, FALSE)
showUnreckognizedPlaces.hasHeaders = TRUE
# Does the place input file have a an id column (first column)? (TRUE, FALSE)
showUnreckognizedPlaces.hasIdColumn = FALSE
# The persons file with the unreckognized names
showUnreckognizedPlaces.personsFile.in  = res/persons_1896.tsv
### END (5)
###########################################################

###########################################################
### (6) CREATE HTML RENDER OF PLACES WITH PERSONS 
# Execute the function
function.createPlacesHtmlRender = TRUE
# File to check
createPlacesHtmlRender.placesFile.in  = res/places_in.tsv
# Does the place input file have a header line? (TRUE, FALSE)
createPlacesHtmlRender.hasHeaders = TRUE
# Does the place input file have a an id column (first column)? (TRUE, FALSE)
createPlacesHtmlRender.hasIdColumn = FALSE
# The persons file
createPlacesHtmlRender.personsFile.in  = res/persons_1896.tsv
# Base folder to store the htmlRender to
createPlacesHtmlRender.outFolder = res/htmlPersons1896
createPlacesHtmlRender.html.title = Persons in 1899
### END (6)
###########################################################

###########################################################
### (7) ADD PERSONS TO SHIPS
# Execute the function
function.processPersonsForShipsFile = FALSE
# File to check
processPersonsForShipsFile.shipsFile.in  = res/ships_in.tsv
# Does the place input file have a header line? (TRUE, FALSE)
processPersonsForShipsFile.hasHeaders = TRUE
# Does the place input file have a an id column (first column)? (TRUE, FALSE)
processPersonsForShipsFile.hasIdColumn = FALSE
# The persons file to read
processPersonsForShipsFile.personsFile.in = res/persons_1896.tsv
# The places file to write
processPersonsForShipsFile.personsFile.out = res/placesWithPersonsOnShips_1896.tsv
### END (7)
###########################################################
```


