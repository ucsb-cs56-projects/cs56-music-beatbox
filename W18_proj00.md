
Project 1. Cristian Diaz:ElBenSwolo and 
a. This program is about a beatbox which mixes sounds together. It has a gui interface with with you can choose certain beats and press play to make your music mix. Also there is a chat functionality where you can talk to other players who are running the program.
------

b. As a user, I can make music using the Beatbox gui.
   As a user, I can run the command "ant client" so that the music Beatbox is launched
   As a user, I can select instruments to make a beat at each of the 16 beats.
   As a user, I can increase or deccrease the tempo of the music by pressing a button.
   As a user, once I have selected the instruments to play at each beat, I can press play to start playing my Beats.
------
   
c. The software runs. The user is presented with a gui which has an array of 16 instruments such as Maracas, Whistle and Hand Clap among others. There are a total of 16 beats and for each beat there is a checkbox for which the user can select wheter the instrument will play in that particular beat. There is a start and stop button to play the mix and a tempo up and tempo down buttons which increase of decrease the speed of the mix.
------
d. As a user, I would like to add more instruments.
   As a user, I would like to save the configuration of the music beatbox.
   As a user, I would like a cleaner interface.
   As a user, I would like to see a picture of the instruments.
   As a user, I would like to be able to increase the tempo on the fly.
   As a user, I would like to be able to not have to press the stop and start buttons when adding a new instrument beat.
   As a user, I would like to see some sample beats.
------
e. The readme file is in a poor state. There is a brief explanation of what the program does but nothing else. We could add the overall structure of the program. We could talk about the javax.sound.midi package. We could explain the purpose of the server and the structure of it wnd how it commnuticates with the main program.
------
f. The current state of the build.xml file is understandable. The targets are clear and do not need any descriptions. There is no old legacy JWS stuff that needs to be removed. 
------
g. There are about 1700 points for this project in issues. We can definitely get 100 easily. Most of them are pretty clear. There is one which is kind of confusing which says "make it easier to see each of the 4 beats".
------
h. * As a user, I would like a cleaner interface.
   * As a user, I would like to see a picture of the instruments.
   * As a user, I would like to be able to increase the tempo on the fly.
   * As a user, I would like to be able to not have to press the stop and start buttons when adding a new instrument beat.
   * As a user, I would like to see some sample beats.
* https://github.com/ucsb-cs56-projects/cs56-music-beatbox/issues/30
* https://github.com/ucsb-cs56-projects/cs56-music-beatbox/issues/29
* https://github.com/ucsb-cs56-projects/cs56-music-beatbox/issues/28
* https://github.com/ucsb-cs56-projects/cs56-music-beatbox/issues/27
* https://github.com/ucsb-cs56-projects/cs56-music-beatbox/issues/26
* https://github.com/ucsb-cs56-projects/cs56-music-beatbox/issues/25
------
i. The project only consists of two java files, BeatBoxFianl.java and MusicServer.java. Basically the BeatBoxFinal file makes the whole project, and the MusicServer file is set up just in case someone wants to play online. The code is easy to understand with its variables names, method names and class names being declared clearly and logically. Also, there are comments on the bottom of every methods and some confusing lines that help the programmer to understand what the code is actuall doing, but some numbers are not given a comment, making them as ""magic numbers" to other programmers. The classes seem to be clear in their naming, but most of them are only for implementing the listener interface. The code has very good indentation and is easy to read. However the server class seems a bit confusing in the naming of the variables. The information for one screenfull of text would be to start refactoring the code. I think the code is good for the most part but it really is lacking in features and presentation. Thats where the work needs to happen.
------
j. There are no tests in the program. One of the problems with the program is that is is one big file and it doesnt have many classes. And the unit testing work better with classes. We could create more classes and do better testing. The classes could be the instruments themselves and also each beatbox configuration could be a class too.
