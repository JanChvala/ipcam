## IPcam front-end module

### Prerequisites
These tools have to be available on classpath:

* bower
* npm

### How to install this module
Open directory containing this file in command line and type:

1. bower install
2. npm update

### Running only ipcam module
Open directory containing this file in command line and type:

3. gulp serve

This will start local server on port 3000 which will compile and serve the ipcam application. This will not enable backend services but it is good for fast testing due to automatic reload on file changes.


### Compiling this module to be served with backend
Open directory containing this file in command line and type:

3. gulp

This will compile the module and makes copy inside folder: ../../server/apps/ipcam

You can then compile the backend server to enable also the GCM messages for starting devices.
