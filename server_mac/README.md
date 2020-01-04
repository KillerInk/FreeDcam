# SERVER (MAC)

This is a ready-to-use server which connects to the cellstorm-module of the Freedcam. It acts as a data-sink for images which gets packed as an **UDP-stream**. In the GUI of the FreedCAM you need to choose the correct **#PORT** and **IP-Address** of the server where the **cserver** file is getting executed from.
The MAC/Unix-version of the cserver is a derivate initially developed by @Killerink. 

## run the server
The prebuild server listens on port **1111** and lays in the folder: **FreeDcam/server_mac/build/exe/cserver**. Steps to run it on a MAC (tested 10.12.6): 

```
brew install libtiff
brew install libjpeg
brew install libz 

git clone FREEDCAM-URL
cd FreeDcam/server_mac/build/exe/cserver
chown 777 cserver 
./cserver 
```

The output looks as follows: 
```
Benedicts-MBP:cserver bene$ ./cserver 
get dng profile 
get matrix 
Init dng writer
DOne Init dng writer
setsockopt: Undefined error: 0
Benedicts-MBP:cserver bene$ ./cserver 
get dng profile 
get matrix 
Init dng writer
DOne Init dng writer
Bytes received: 1024
Bytes received: 1024
Bytes received: 1024
Bytes received: 1024
```

The server expects a pixelnumber of 500*500 pixels.

## Build from Source 

Configure Android Studio (3.X) in order to build C++ projects (install clang compiler from XCode). Also install the following libraries through brew:
```
brew install libtiff
brew install libjpeg
brew install libz
```

Then go to the right-hand-side panel which says **Gradle**. Besides the **APP** compilation tasks, you'll find the **server-mac** task. Doubleclick on Assembly and the compiler will build the project in ``exe/cserver``. 


## Contributions
It's a work-in-progress project. If you find it usefull or have any suggestions, please contribute by forking this repo. 
