# IPcam application server
This is simple Node.js server which is one part of the resultant system of my master thesis.

It is responsible for serving static pages and alo for exposing simple API for registering Android device and starting it using Google Cloud Messaging.
 
## Demo
This server is for demonstration purposes only and it is accessible at:

http://ipcam.janchvala.cz/

The Android client application is accessible:

* http://ipcam.janchvala.cz/assets/cz.janchvala.android.ipcamera.apk
* or in this repository: apps/ipcam/public/assets/cz.janchvala.android.ipcamera.apk


### Instructions to try the project
#### Android client (streaming):

1. install Android application client
2. wait until it gets registered
3. insert password
4. change stream setting (menu)
5. enable streaming (by Switch in Toolbar)

(Android prior 5.0 - Lollipop) after the stream is requested it is opened in web browser instead of Service. You need to confirm access to camera.


#### Playback:

1. go to: http://ipcam.janchvala.cz/
2. click on green camera FAB in right bottom corner
3. insert username from Android application
4. wait for stream to start
5. insert password when prompted
6. enjoy the stream


## Compilation and running
You need to do couple of things if you want to install this server on your own machine:

1. build the **ipcam** front-end module (see *apps/ipcam/README*)
2. build and run the **ipcam** back-end module (see *server/README*)

