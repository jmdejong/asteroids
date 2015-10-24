How to start the .jar program (assuming a UNIX machine):
	-Open a terminal in the jar/ directory
	-Try executing the file by typing `./asteroids_vX.jar` (where X is the actual version number)
	-If this does not work, change the permissions of this file so that it is allowed to be executed (and then try again). 
		-This can be done by right-clicking the file and changing the permissions under `Properties`, 
		-or enter `chmod +x asteroids_vX.jar` in the terminal (where again, X is substituted for the version number)

How to build the program from source
	-It is possible to import the project (contained in the source/ directory) to Eclipse and build it from there. However,
	-There also exists a convenience Shell script (again, inside the source/ directory) called `make_and_run.sh` that can be executed from the terminal.
	-After calling this, it also is possible to call the Shell script `run.sh` to directly run the compiled file without re-building it.


How to read the documentation:
	-Open `source/doc/index.html` in any web browser.