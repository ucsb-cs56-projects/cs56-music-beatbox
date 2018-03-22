cs56-music-beatbox
==================

This program is about a beatbox which mixes sounds together. It has a gui interface with with you can choose certain beats and press play to make your music mix. Also there is a chat functionality where you can talk to other players who are running the program. The software runs. The user is presented with a gui which has an array of 16 instruments such as Maracas, Whistle and Hand Clap among others. There are a total of 16 beats and for each beat there is a checkbox for which the user can select wheter the instrument will play in that particular beat. There is a start and stop button to play the mix and a tempo up and tempo down buttons which increase of decrease the speed of the mix.

Note to the student trying to figure out if they want this repo
---------------------------------------------------------------
1. To run this program. Type the command "ant client"
2. There is a row of Checkboxes on the top of the gui, which indicate the beat position while the music is being played.
3. There are also tempo up, tempo down and reset tempo buttons to control the tempo of the music. Also, there is a text message that display the current tempoBPM in the gui.
4. When the music is being played, you can click on the checkboxes then the sound will automatically update.
5. The copy button copys the first 4 beats of the music to the last 12 beats.
6. The code contains some comments that will help you have a better understanding of the code.



project history
===============
```
YES | mastergberry | A gui that has the ability to mix together all sorts of instruments just like a beatbox
```
w18 remarks
==================

The code creates a very interesting music beatbox. There are two methods that might be difficult to understand which are makeEvent() and makeTrack(). You don't have to understand these two methods to do the project. The thing you have to notice is that there are actually 276 Jcheckboxes in this gui. For the first 16 Jcheckboxes they are used to display the beat. Notice that there is a bug in the project. Specification on the bug has been posted to this link: https://github.com/ucsb-cs56-projects/cs56-music-beatbox/issues/10. When I tried running the program on different computers, the bug came up on some computers. For the other computers the bug is gone for no reason. We have not figured that out but I hope the experience can help you. 
