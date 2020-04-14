Cyberpunk2106
===============
This project was created in order to control the machine through the Arduino board.

Introduction
-------------
The main language is Kotlin '1.3.61' version for this moment. MVVM pattern architecture with [Android Jetpack official cite](https://developer.android.com/jetpack) with LiveData, Navigation, Data Binding and etc. First of all i draw some fragments.xml and get something like this. To begin with, the most important thing was to figure out how to work through the TCP protocol. Thanks to my practice in epam, I figured out the client applications with the server there and it was easy to remember how to develop the client part. I created TCPClientImpl with three interfaces: write, read, connection and just listen output messages from server with ViewModel and Repository. 

Tasks
--------
1) Create connection to the server with tcp protocol 
2) Create settings fragment with switchers(high beams, dipped beams, turn signals, stove)
3) Create info fragment with car characteristics 
4) Create main fragment with car lock, start the car button and secure switcher

Implementation
----------------
There are four fragments to work on Single Activity pattern. We have Main Activity for init server connection and observe network connection via Broadcast Receiver also init bottom navigation view. At second fragment we have CyberPunk 2106 splash screen for two seconds. In the main menu we can switch between fragments. But i have a problem that i create a new one after each click on the bottom navigation. For all three fragments i create one ViewModel that store local data with LiveData on the v****** values. I tried to write clean code, but my small knowledge about callbacks and Deferred types can not give me that and i store the data in the fragments with SharedPrefs. For settings i analize each line like vol, tvn, tok etc.

Screen Shoots
-----------------
<img src="https://github.com/mishokU/Cyberpunk2106/blob/master/screenshoots/main_screen.jpg?raw=true" height="400" width=auto><img src="https://github.com/mishokU/Cyberpunk2106/blob/master/screenshoots/settings.jpg?raw=true" width=auto><img src="https://github.com/mishokU/Cyberpunk2106/blob/master/screenshoots/switchers.jpg?raw=true" height="400" width=auto>
