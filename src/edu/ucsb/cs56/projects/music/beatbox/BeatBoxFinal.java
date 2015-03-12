package edu.ucsb.cs56.projects.music.beatbox;


import java.awt.*;
import javax.swing.*;
import java.io.*;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.event.*;

/** Class for Beat Box
 *@version UCSB, S13, 05/14/2013
 *@author Callum Steele
 *@author Miranda Aperghis
 *@author Richard Alvarez
 *@author Gabriel Romero
 *
 */

public class BeatBoxFinal {
    JFrame theFrame;
    JPanel mainPanel;
    JList incomingList;
    ArrayList<JCheckBox> checkboxList;
    int nextNum;
    Vector<String> listVector = new Vector<String>() ;
    String userName;
    ObjectOutputStream out;
    ObjectInputStream in;
    HashMap<String, boolean[] > otherSeqsMap = new HashMap<String, boolean[] >() ;
    Sequencer sequencer;
    Sequence sequence;
    Sequence mySequence = null;
    Track track;
    
    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Conga"} ;
    int[] instruments = { 35, 42, 46, 38, 49,39,50,60, 70, 72, 64, 56,58,47,67, 63} ;

    /**
       Main program
       @param args First string given as a command line argument is used as the username.
     */

    public static void main (String[] args) {
	if(args.length != 1)
	    new BeatBoxFinal().startUp("User"); // default to user
	else
	    new BeatBoxFinal().startUp(args[0] ) ;  // args[0] is your user ID/screen name
    }

    /**
       Attempts to connect to the server at csil.cs.ucsb.edu on port 4242 for messaging.
       Then calls methods to setup the Midi and GUI regardless of whether a connection was possible.
       @param name The username of the user.
     */

    public void startUp(String name) {
	userName = name;
	setUpMidi() ;
	buildGUI() ;
    } // close startUp

    /**
       Creates the GUI for the beatbox player.
     */

    public void buildGUI() {
	theFrame = new JFrame("Cyber BeatBox") ;
	theFrame.setMinimumSize(new Dimension(700,500));
	BorderLayout layout = new BorderLayout() ;
	JPanel background = new JPanel(layout) ;
	background.setBorder(BorderFactory.createEmptyBorder(10,10,10, 10) ) ;
	checkboxList = new ArrayList<JCheckBox>() ;
	Box buttonBox = new Box(BoxLayout.Y_AXIS) ;

	JButton start = new JButton("Start") ;
	start.addActionListener(new MyStartListener() ) ;
	buttonBox.add(start) ;
	JButton stop = new JButton("Pause") ;
	stop.addActionListener(new MyStopListener() ) ;
	stop.setBounds(300,300 , 10,10);
	buttonBox.add(stop) ;
	JButton upTempo = new JButton("Tempo Up") ;
	upTempo.addActionListener(new MyUpTempoListener() ) ;
	buttonBox.add(upTempo) ;
	JButton resetTempo = new JButton("Reset Tempo") ;
	resetTempo.addActionListener(new MyResetTempoListener() ) ;
	buttonBox.add(resetTempo) ;
	JButton downTempo = new JButton("Tempo Down") ;
	downTempo.addActionListener(new MyDownTempoListener() ) ;
	buttonBox.add(downTempo) ;
	JButton clear = new JButton("Clear") ;
	clear.addActionListener(new MyResetListener() ) ;
	
	buttonBox.add(clear) ;

	

        incomingList = new JList() ;
	incomingList.addListSelectionListener(new MyListSelectionListener() ) ;
	incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION) ;
	/*	JScrollPane theList = new JScrollPane(incomingList) ;
		buttonBox.add(theList) ; */        
	incomingList.setListData(listVector) ; // no data to start with
	background.add(BorderLayout.EAST, buttonBox) ;
	theFrame.getContentPane().add(background) ;         
	GridBagLayout grid = new GridBagLayout() ;
	GridBagConstraints con = new GridBagConstraints();
	con.gridx = 0; con.gridy = 0;
	con.weightx = 1.0; con.weighty = 1.0;
	con.fill = GridBagConstraints.BOTH;
	mainPanel = new JPanel(grid) ;
	background.add(BorderLayout.CENTER, mainPanel) ;
	for (int i = 0; i < 256; i++) {
	    if(i%16==0) {
		con.gridy++; con.gridx = 0;
		Label l = new Label(instrumentNames[i/16]);
		grid.setConstraints(l, con);
		mainPanel.add(l);
		con.gridx++;
	    }
	    JCheckBox c = new JCheckBox() ;
	    c.setSelected(false) ;
	    checkboxList.add(c) ;
	    grid.setConstraints(c, con);
	    mainPanel.add(c) ;
	    con.gridx++;
	} // end loop

	theFrame.setBounds(50,50, 300, 300) ;
	theFrame.pack() ;
	theFrame.setVisible(true) ;
    } // close buildGUI

    /**
       Attempts to setup the Midi.
     */

    public void setUpMidi() {
	try {
	    sequencer = MidiSystem.getSequencer() ;
	    sequencer.open() ;
	    sequence = new Sequence(Sequence.PPQ, 4) ;
	    track = sequence.createTrack() ;
	    sequencer.setTempoInBPM(120) ;
	} catch(Exception e) {e.printStackTrace() ; }
    } // close setUpMidi

    /**
       Checks the status of the various checkboxes and using this creates a track which it plays.
     */

    public void buildTrackAndStart() {
        ArrayList<Integer> trackList = null; // this will hold the instruments for each 
        sequence.deleteTrack(track) ;
        track = sequence.createTrack() ;
        for (int i = 0; i < 16; i++) {
	    trackList = new ArrayList<Integer>() ;
	    for (int j = 0; j < 16; j++) {
		JCheckBox jc = (JCheckBox) checkboxList.get(j + (16*i) ) ;
		if (jc.isSelected() ) { 
		    int key = instruments[i] ;  
		    trackList.add(new Integer(key) ) ;
		} else {
		    trackList.add(null) ;  // because this slot should be empty in the track
		}
	    } // close inner loop
	    makeTracks(trackList) ;
        } // close outer loop
        track.add(makeEvent(192, 9,1,0, 15) ) ; // - so we always go to full 16 beats  
        try {
	    sequencer.setSequence(sequence) ; 
	    sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY) ;                   
	    sequencer.start() ;
	    sequencer.setTempoInBPM(120) ;
	} catch(Exception e) {e.printStackTrace() ;}
    } // close method

  


  /**
       Resets the various checkboxes.
     */

    public void resetTrack() {
	//  ArrayList<Integer> trackList = null; // this will hold the instruments for each 
        //sequence.deleteTrack(track) ;
        //track = sequence.createTrack() ;
        for (int i = 0; i < 16; i++) {
	    for (int j = 0; j < 16; j++) {
		JCheckBox jc = (JCheckBox) checkboxList.get(j + (16*i) ) ;
		jc.setSelected(false);
	    } // close inner loop
	}
    } // close method







    /**
       Listens for a click event on the start button.
     */

 

   public class MyStartListener implements ActionListener {

	/**
	   Creates the track and starts it when a click event occurs on the start button.
	   @param a ActionEvent containing details of the click event.
	 */

        public void actionPerformed(ActionEvent a) {
	    buildTrackAndStart() ;
        } // close actionPerformed
    } // close inner class

    /**
       Listens for a click event on the stop button.
     */

   public class MyResetListener implements ActionListener {

	/**
	   Creates the track and starts it when a click event occurs on the start button.
	   @param a ActionEvent containing details of the click event.
	 */

        public void actionPerformed(ActionEvent a) {
	    resetTrack() ;
        } // close actionPerformed
    } // close inner class



    public class MyStopListener implements ActionListener {

	/**
	   Stops the track when a click event occurs on the stop button.
	   @param a ActionEvent containing details of the click event.
	 */

        public void actionPerformed(ActionEvent a) {
	    sequencer.stop() ;
	} // close actionPerformed
    } // close inner class

    /**
       Listens for a click event on the UpTempo button.
     */

    public class MyUpTempoListener implements ActionListener {

	/**
	   Increases the tempo when a click event occurs on the UpTempo button.
	   @param a ActionEvent containing details of the click event.
	 */

        public void actionPerformed(ActionEvent a) {
	    float tempoFactor = sequencer.getTempoFactor() ;
	    sequencer.setTempoFactor((float) (tempoFactor * 1.03) ) ;
	} // close actionPerformed        
    } // close inner class

    /**
       Listens for a click event on the DownTempo button.
     */

    public class MyDownTempoListener implements ActionListener {

	/**
	   Decreases the tempo when a click event occurs on the DownTempo button.
	   @param a ActionEvent containing details of the click event.
	 */

	public void actionPerformed(ActionEvent a) {
	    float tempoFactor = sequencer.getTempoFactor() ;
	    sequencer.setTempoFactor((float) (tempoFactor *.97) ) ;
        }
    }
     public class MyResetTempoListener implements ActionListener {

	/**
	   
	   @param a ActionEvent containing details of the click event.
	 */

	public void actionPerformed(ActionEvent a) {
	    float tempoFactor =  sequencer.getTempoFactor();
	    sequencer.setTempoFactor((float) (tempoFactor*0.0 + 1.0) ) ;
	    
	     }
     }

   
    /**
       Listens for a click event on an item in the list.
     */

      public class MyListSelectionListener implements ListSelectionListener {

	/**
	   Changes a value in the sequence, stops the current track and rebuilds it, running the track after it is built.
	   @param le ListSelectionEvent that contains details of the click event.
	 */

        public void valueChanged(ListSelectionEvent le) {
	    if (! le.getValueIsAdjusting() ) {
		String selected = (String) incomingList.getSelectedValue() ;  
		if (selected != null) {    
		    // now go to the map, and change the sequence
		    boolean[] selectedState = (boolean[] ) otherSeqsMap.get(selected) ;
		    changeSequence(selectedState) ;                                     
		    sequencer.stop() ; 
		    buildTrackAndStart() ;
		}
	    }
	} // close valueChanged
} // close inner class


    /**
       Sets the checkboxes in the display according to the inputted boolean array.
       @param checkBoxState The boolean array to use to change the values of the displayed checkboxes.
     */
	
    public void changeSequence(boolean[] checkboxState) {
	for (int i = 0; i < 256; i++) {
	    JCheckBox check = (JCheckBox) checkboxList.get(i) ;
	    if (checkboxState[i] ) {
		check.setSelected(true) ;
	    } else {
                check.setSelected(false) ;
	    }
	} // close loop
    } // close changeSequence
    
    /**
       Makes a track from an ArrayList of Integer values.
       @param list The ArrayList of Integers from which to make the track.
     */

    public void makeTracks(ArrayList list) {
	Iterator it = list.iterator() ;        
	for (int i = 0; i < 16; i++) {
	    Integer num = (Integer) it.next() ;
	    if (num != null) {
		int numKey = num.intValue() ;                       
		track.add(makeEvent(144,9, numKey, 100, i) ) ;
		track.add(makeEvent(128,9, numKey, 100, i + 1) ) ;
	    }
        } // close loop
    } // close makeTracks()

    /**
       Creates a MidiEvent from the inputted parameters and returns it.
       @return The MidiEvent it created.
     */
    
    public  MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
	MidiEvent event = null;
	try {
            ShortMessage a = new ShortMessage() ;
            a.setMessage(comd, chan, one, two) ;
            event = new MidiEvent(a, tick) ;          
	}catch(Exception e) { }
	return event;
    } // close makeEvent

    
} // close class
