This app has been developed by: Josué Herrero.


Hi, I just wanted to make a few remarks about my work.

	The architectural approach that I have chosen for this app has been to have only one presenter, 
the activity one. This is because for this feature the fragments just act like a pure view, they have 
not intrinsic logic attached. So all the control logic is in the activity presenter. 

	Outside of the presenter the activity is in chargeof the permissions management and the location 
settings requests. So we can change the location library with no impact on the presenter.

The presenter is a pure Java class so it is easy to test. From that on, if the app is going to be big 
it is good to have a persistence layer to encapsulate all the database and network logic 
(inside different places), so the presenter doesn´t actually know from where is the data coming. 

It is decoupled. In this case I use a repository for retrieving/saving the paths file from disk, 
so the presenter doesn´t have the context.

Features:

	-	All the mandatory features plus the next ones: 
	
	-	Allow the user to see each journey’s path plotted on a map, when selected from the list.
	-	Secure the data that’s stored on the device.(The path list file is encrypted)
	-	Retain the user’s data if the app is deleted and re-installed.
	-	I have written unit tests for the presenter.


Behaviour:

	-	The app has two location modes: tracking(prints a blue line on the map), 
		and recording(prints a red line on the map).

	-	At the beggining the tracking and recording are deactivated.

	-	There is a menu in the toolbar, 
			The left option is for activating the tracking. It will be deactivated 
			automatically if the app goes to the background.
			The right option is for activating the recording, it will remain activated 
			in the background.

	-	Permissions and location settings will be asked when activating the tracking/recording, 
		it will be necesary to select the option again when all the permission/setting are ready.

	-	If the user starts a recording and he stops it, it is saved and the list is updated.

	-	If the user is recording and he selects "tracking" the next thing will happen: 
		the recording path will be stored in disk and the path list will be updated, then
		the current tracking path(blue) will be updated with the recorded positions. 

		The recording path(red) will be deleted from the screen and the standard tracking will 
		continue normally.

			
Possible improvements (apart from ui):

	-	For saving/loading the paths file it would be good to do it in another thread. This would
		 be needed for big files.
	-	Hardcoding the encryption key is no a good idea. The best approx would be build it in 
		runtime doing string/byte "operations", for a proper ofuscation.
	-	On location tracking(better outdoors): It would be needed a location filtering for avoiding the false jumps in the tracking.
		I started this project using the SmartLocation Library: https://github.com/mrmans0n/smart-location-lib. And
		the tracking was far better.

This project was built in a Windows environment.
If you have any question don´t hesitate to ask me!

