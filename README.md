# Elysium 

[Demo](https://youtu.be/IwAC8IK5skc)

Elysium is a convenient music service with additional features.
The project is an Android application on the front end and a server with
a database on the back end, the code of which is written in Kotlin and Java.
A well-thought-out project structure makes the code intuitive, 
and fast algorithms ensure high performance and speed of the application. 
In our project we tried to pay attention on details, in particular its quality 
in multi-threaded mode with a high load.<br/><br/>

[Features](#Features)<br/>
[Installation](#Installation)<br/>
[License](#License)<br/>

<p align="middle">
  <img src="https://github.com/ElysiumHSE/Elysium/blob/release/Screenshots/mainPage.jpg" height="400" hspace="7" />
  <img src="https://github.com/ElysiumHSE/Elysium/blob/release/Screenshots/fullTrack.jpg" height="400" hspace="7" />
  <img src="https://github.com/ElysiumHSE/Elysium/blob/release/Screenshots/comments.jpg" height="400" hspace="7" /> 
  <img src="https://github.com/ElysiumHSE/Elysium/blob/release/Screenshots/search.jpg" height="400" hspace="7" />
</p>

## Features
* Android application
   * Pleasant and intuitive interface
   * Smooth eye-safe dark theme
* Authentication
   * Provides one's own access to app and keeps your account safe!
* A music player itself
   * Neat player panel inside app & same on quick access bar
   * Nice dynamic bar to rewind and switch tracks
   * Good view on track covers at full screen mode
* Search bar
   * Pretty fast search algorithm
   * Finds tracks even despite typos!
* Comments
   * Well-organized ability to read one's comments on tracks and leave your own!
* Open-source, expandable and absolutely free

## Installation
* Clone this repo
* Open Elysium folder via IntelliJ IDEA 
   * Insert your database parameters into 
[persistence.xml](https://github.com/ElysiumHSE/Elysium/blob/release/Elysium/src/main/resources/META-INF/persistence.xml)
   * Create gradle.properties file on path Elysium/Elysium
   * Insert this text: JWT_SECRET_KEY="{your secret key}". Key can be generated via online resources(for example [here](https://generate-random.org/encryption-key-generator?count=1&bytes=32&cipher=aes-256-cbc&string=&password=))
   * Run server via IntelliJ IDEA

* Open ElysiumApp folder in Android Studio
  * Setup Firebase Storage
    * Create new project at Firebase Console, setup Storage with sources of tracks and covers and paste URLs of them to your database
    * Link your project with Android Studio
  * Run app
* Enjoy!

## License
[MIT License](https://github.com/ElysiumHSE/Elysium/blob/master/LICENSE)
