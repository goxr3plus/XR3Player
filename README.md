### [![AlexKent](https://user-images.githubusercontent.com/20374208/75432997-f5422100-5957-11ea-87a2-164eb98d83ef.png)](https://www.minepi.com/AlexKent) Support me joining PI Network app with invitation code [AlexKent](https://www.minepi.com/AlexKent) [![AlexKent](https://user-images.githubusercontent.com/20374208/75432997-f5422100-5957-11ea-87a2-164eb98d83ef.png)](https://www.minepi.com/AlexKent)  
       
## I am in search for developers to keep on where i left XR3Player :)        
--- 
<h3 align="center" > XR3Player ( <a href="https://xr3player.netlify.com/" target="_blank">Download</a>  )</h3>
<p align="center">
<img src="https://cloud.githubusercontent.com/assets/20374208/26214265/6b605cae-3c04-11e7-9c14-2cd59e10dd03.png">
</p>      
<p align="center">                
<sup>        
<b>The most advanced Java Media Player/Organizer you will ever find out there </b>     
</sup>                     
</p>                                      
                           
---           
  
[![HitCount](http://hits.dwyl.io/goxr3plus/xr3player.svg)](http://hits.dwyl.io/goxr3plus/xr3player)  
[![Latest Version](https://img.shields.io/github/release/goxr3plus/XR3Player.svg?style=flat-square)](https://github.com/goxr3plus/XR3Player/releases)
[![Join the chat at https://gitter.im/XR3Player/Lobby](https://badges.gitter.im/XR3Player/Lobby.svg)](https://gitter.im/XR3Player/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
<a href="https://sourceforge.net/projects/xr3player/files/latest/download" rel="nofollow"><img alt="Download XR3Player" src="https://img.shields.io/sourceforge/dt/xr3player.svg"></a>
[![Total Downloads](https://img.shields.io/github/downloads/goxr3plus/XR3Player/total.svg)](https://github.com/goxr3plus/XR3Player/releases)
[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0)
[![GitHub issues](https://img.shields.io/github/issues/goxr3plus/XR3Player.svg)](https://github.com//goxr3plus/XR3Player/issues)
<a href="https://patreon.com/preview/8adae1b75d654b2899e04a9e1111f0eb" title="Donate to this project using Patreon"><img src="https://img.shields.io/badge/patreon-donate-yellow.svg" alt="Patreon donate button" /></a>
<a href="https://www.paypal.me/GOXR3PLUSCOMPANY" title="Donate to this project using Paypal"><img src="https://img.shields.io/badge/paypal-donate-yellow.svg" alt="PayPal donate button" /></a>


| Video|
|:-:|
| [![First](https://user-images.githubusercontent.com/20374208/48313813-34fdc180-e5ca-11e8-9da7-c6148dc0cbe5.png)](https://www.youtube.com/watch?v=7Hai7cavmUY)  |

# Attention for future contributors  
Check the section **HOW TO RUN PROJECT** because it needs just  20 seconds of adding some extra VM parameters .

## Platform Support ( 64 bit ) 

| Installer | Windows x64 | MacOS x64| Linux x64 | Android | IOS|
| ------- | :-----: | :-: | :-----: |  :-----: | :-----: |
| Download | [ link ](https://goxr3plus.github.io/xr3player.io/) | X (help wanted) | X (help wanted) |  X (planning) | X (planning) |

| DJ UI | Chromium Web Browser 
|:-:|:-:|
| ![First](https://user-images.githubusercontent.com/20374208/48313813-34fdc180-e5ca-11e8-9da7-c6148dc0cbe5.png) | ![Sec](https://goxr3plus.github.io/xr3player.io/img/xr3player/web_browser.jpg) |

| Multiple Users | Advanced UI 
|:-:|:-:|
| ![First](https://goxr3plus.github.io/xr3player.io/img/xr3player/login_mode.jpg) | ![Sec](https://goxr3plus.github.io/xr3player.io/img/xr3player/main_mode.jpg) |

-------------------------------------------------------------------------------------


# HOW TO RUN PROJECT 

To build XR3Player, you will need: 

* [JDK 12.0.2]
* [Maven](http://maven.apache.org/) - Version 3.6.0++

Open IntelliJ and fork the project (https://github.com/goxr3plus/XR3Player).

![2019-08-01_18-04-22](https://user-images.githubusercontent.com/20374208/62304551-d5f91900-b486-11e9-80e9-cf802d91ee6f.gif)

In order to run the project you should add the following **VM Options** ( easy using IntelliJ , Eclipse or Netbeans ) :

```
--add-exports javafx.controls/com.sun.javafx.scene.control.behavior=com.jfoenix
--add-exports javafx.controls/com.sun.javafx.scene.control=com.jfoenix
--add-exports javafx.base/com.sun.javafx.binding=com.jfoenix
--add-exports javafx.graphics/com.sun.javafx.stage=com.jfoenix
--add-exports javafx.base/com.sun.javafx.event=com.jfoenix
--add-exports javafx.graphics/com.sun.javafx.scene=org.controlsfx.controls,
--add-exports javafx.graphics/com.sun.javafx.scene.traversal=org.controlsfx.controls
```

Ready to go :) 

## Modular
 - **As XR3Player codebase keeps growing i decided to make it modular so it's main components are the below :**
   - [XR3Player Core](https://github.com/goxr3plus/XR3Player) ( The main code of XR3Player )
   - [XR3Capture](https://github.com/goxr3plus/XR3Capture) ( For capturing the computer screen )
   - [Stream Player](https://github.com/goxr3plus/java-stream-player) ( Audio Library 100% Java )
   -  //TODO JVisualizations ( Advanced Java Audio Visualizations Library )
   -  //TODO  [JAmplitudeVisuals](https://github.com/goxr3plus/Java-Audio-Wave-Spectrum-API) ( Advanced Java Library for representing Audio Amplitude Visualizations ) 
  
  
    
  

## Features
- **Done ✔️**
  - Support almost all audio formats through smart converting to .mp3
  - Amazing Audio Spectrum Visualizers
  - Audio Amplitudes Waveform
  - Chromium Web Browser
  - Full Dropbox access
  - Multiple User Accounts
  - Configurable via multiple settings
  - Advanced Tag Editor
  - File Organizer and Explorer
  - Multiple Libraries/Playlists support
  - System monitor ( CPU , RAM )
  - Audio Effects and Filters
- **TODO 🚧**
  - _XR3Player is actively developed. More features will come!_
  - Support all audio file formats by default
  - Support all video file formats by default
  - Speech Recongition 
  - Smart AI Assistant
  - Online Subscription website
  - Android and IOS applications

## Java Audio Tutorials and API's by GOXR3PLUS STUDIO
 - **Spectrum Analyzers**
   - [Java-Audio-Wave-Spectrum-API](https://github.com/goxr3plus/Java-Audio-Wave-Spectrum-API)
    ![image](https://github.com/goxr3plus/Java-Audio-Wave-Spectrum-API/raw/master/images/Screenshot_2.jpg?raw=true)
   - [Jave Spectrum Analyzers from Audio](https://github.com/goxr3plus/Java-Spectrum-Analyser-Tutorials)
   - [Capture Audio from Microphone and make complex spectrum analyzers](https://github.com/goxr3plus/Java-Microphone-Audio-Spectrum-Analyzers-Tutorial)
  
 - **Java multiple audio formats player**
   - [Java-stream-player](https://github.com/goxr3plus/java-stream-player)
  
 - **Speech Recognition/Translation/Synthenizers**
   - [Java Speech Recognition/Translation/Synthesizer based on Google Cloud Services](https://github.com/goxr3plus/java-google-speech-api)
   - [Java-Speech-Recognizer-Tutorial--Calculator](https://github.com/goxr3plus/Java-Speech-Recognizer-Tutorial--Calculator)
   - [Java+MaryTTS=Java Text To Speech](https://github.com/goxr3plus/Java-Text-To-Speech-Tutorial)
   - [Java Speech Recognition Program based on Google Cloud Services ](https://github.com/goxr3plus/Java-Google-Speech-Recognizer)
   - [Java Google Text To Speech](https://github.com/goxr3plus/Java-Google-Text-To-Speech)
   - [Full Google Translate Support using Java](https://github.com/goxr3plus/java-google-translator)
   - [Professional Java Google Desktop Translator](https://github.com/goxr3plus/Java-Google-Desktop-Translator)

## QUESTIONS
<details>
  <summary>System Requirements</summary>
  <p>
   
    1) At least 4 Cores CPU > 2.0 GHZ CPU Intel or AMD 
     
    2) A good GPU (Graphics Processing Unit) [ It requires graphic power for visualizers ]

    3) At least 4GB DDR3|DDR4 Ram [ Java Programs are known to consume a little bit more RAM ;) ]
  </p>
</details>

<details>
  <summary>License</summary>
  <p>
    https://www.google.com/search?q=GNU+LGPL+3.0&oq=GNU+LGPL+3.0&aqs=chrome..69i57j0.6247j0j4&sourceid=chrome&ie=UTF-8
  </p>
</details>

## About JxBrowser

>XR3Player uses JxBrowser http://www.teamdev.com/jxbrowser, which is a proprietary software, owned by TeamDev Ltd. The use of JxBrowser >is governed by JxBrowser Product Licence Agreement http://www.teamdev.com/jxbrowser-licence-agreement. 
>You may not use JxBrowser separately from XR3Player project without explicit permission of TeamDev Ltd.


## Specs / Open-source libraries:


- [**Ikonli Font Icons packs**](https://aalmiray.github.io/ikonli) Ikonli provides icon packs that can be used in Java applications. Currently Swing and JavaFX UI toolkits are supported.
- [**ControlsFX**](http://fxexperience.com/controlsfx/features/) UI controls and useful API for JavaFX 8.0 and beyond .
- [**JFoenix**](https://github.com/jfoenixadmin/JFoenix)  JavaFX Material Design Library .
- [**RichTextFX**](https://github.com/FXMisc/RichTextFX) RichTextFX provides a memory-efficient text area for JavaFX that allows the developer to style ranges of text, display custom objects in-line (no more HTMLEditor), and override the default behavior only where necessary without overriding any other part of the behavior.
- [**FX-BorderlessScene**](https://github.com/goxr3plus/FX-BorderlessScene) Undecorated JavaFX Scene with implemented move, resize, minimise, maximise, close and Windows Aero Snap controls.
- [**JavaSysmon2**](https://github.com/goxr3plus/javasysmon2) Manage OS processes and get cpu and memory stats cross-platform in Java. 
- [**Jnativehook**](https://github.com/kwhat/jnativehook) Global keyboard and mouse listeners for Java .
- [**Sqlite-jdbc**](https://github.com/xerial/sqlite-jdbc) SQLite JDBC Driver .
- [**Commons-Validator**](https://commons.apache.org/proper/commons-validator/) A common issue when receiving data either electronically or from user input is verifying the integrity of the data. This work is repetitive and becomes even more complicated when different sets of validation rules need to be applied to the same set of data based on locale. Error messages may also vary by locale. This package addresses some of these issues to speed development and maintenance of validation rules.
- [**EasyBind**](https://github.com/TomasMikula/EasyBind) EasyBind leverages lambdas to reduce boilerplate when creating custom bindings, provides a type-safe alternative to Bindings.select* methods (inspired by Anton Nashatyrev's feature request, planned for JavaFX 9) and adds monadic operations to ObservableValue.
- [**JSoup**](https://jsoup.org/) Java library for working with real-world HTML. It provides a very convenient API for extracting and manipulating data, using the best of DOM, CSS, and jquery-like methods.
- [**JSON-Simple**](https://github.com/cliftonlabs/json-simple) Java 7+ toolkit to quickly develop RFC 4627 JSON compatible applications
- [**Java-Google-Speech-API**](https://github.com/goxr3plus/java-google-speech-api) J.A.R.V.I.S. Java Speech API: Just A Reliable Vocal Interpreter & Synthesizer. This is a project for the Java Speech API. The program interprets vocal inputs into text and synthesizes voices from text input. The program supports dozens of languages and even has the ability to auto-detect languages!
- [**JAudioTagger**](http://www.jthink.net/jaudiotagger/) Jaudiotagger is the Audio Tagging library used by Jaikoz for tagging data in Audio files.
- [**Java-Stream-Player**](https://github.com/goxr3plus/java-stream-player) Java Advanced Audio Controller Library (WAV, AU, AIFF, MP3, OGG VORBIS, FLAC, MONKEY's AUDIO and SPEEX audio formats ).
- [**JavaFX-Web-Browser**](https://github.com/goxr3plus/JavaFX-Web-Browser) Embeddable or Standalone JavaFX Web Browser.
- [**Mp3agic**](https://github.com/mpatric/mp3agic) A java library for reading mp3 files and reading / manipulating the ID3 tags (ID3v1 and ID3v2.2 through ID3v2.4).
- [**JAVE2**](https://github.com/a-schild/jave2) The JAVE (Java Audio Video Encoder) library is Java wrapper on the ffmpeg project
 

[![Build Status](https://travis-ci.org/goxr3plus/XR3Player.svg?branch=master)](https://travis-ci.org/goxr3plus/XR3Player)

