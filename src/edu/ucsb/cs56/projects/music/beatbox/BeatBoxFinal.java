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
*
*/

public class BeatBoxFinal {
	JFrame theFrame;
	JPanel mainPanel;
	JTextArea DisplayTempo;
	ArrayList<JCheckBox> checkboxList;
	int nextNum;
	Vector<String> listVector = new Vector<String>();
	String userName;
	HashMap<String, boolean[] > otherSeqsMap = new HashMap<String, boolean[] >();
	Sequencer sequencer;
	Sequence sequence;
	Sequence mySequence = null;
	Track track;
	String x = "abc";
	ActionListener more = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
		ArrayList<Integer> trackList = null; // this will hold the instruments for each
		sequence.deleteTrack(track);
		track = sequence.createTrack();
		for (int i = 0; i < 16; i++) {
			trackList = new ArrayList<Integer>();
			for (int j = 0; j < 16; j++) {
				JCheckBox jc = (JCheckBox)checkboxList.get(j + (16 * i));
				if (jc.isSelected()) {
					int key = instruments[i];
					trackList.add(new Integer(key));
				}
				else {
					trackList.add(null);  // because this slot should be empty in the track
				}
			} // close inner loop
			makeTracks(trackList);
			track.add(makeEvent(176,1,127,0,16));   // self-made code
		} // close outer loop
		track.add(makeEvent(192, 9,1,0, 15)); // - so we always go to full 16 beats
		try {
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
			sequencer.start();
			sequencer.setTempoInBPM(120);
		}
		catch (Exception ex) { ex.printStackTrace(); }
	}
	};  // Make an actionListener variable more to dynamically update sound when clicking on JcheckBox.



	String[] instrumentNames = { "Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Conga" };
	int[] instruments = { 35, 42, 46, 38, 49,39,50,60, 70, 72, 64, 56,58,47,67, 63 };

	/**
	Main program
	@param args First string given as a command line argument is used as the username.
	*/

	public static void main(String[] args) {
		if (args.length != 1)
			new BeatBoxFinal().startUp("User"); // default to user
		else
			new BeatBoxFinal().startUp(args[0]);  // args[0] is your user ID/screen name
	}

	/**
	Attempts to connect to the server at csil.cs.ucsb.edu on port 4242 for messaging.
	Then calls methods to setup the Midi and GUI regardless of whether a connection was possible.
	@param name The username of the user.
	*/

	public void startUp(String name) {
		userName = name;
		setUpMidi();
		buildGUI();
	} // close startUp

	  /**
	  Creates the GUI for the beatbox player.
	  */

	public void buildGUI() {
		theFrame = new JFrame("Cyber BeatBox");
		theFrame.setMinimumSize(new Dimension(700, 500));
		BorderLayout layout = new BorderLayout();
		JPanel background = new JPanel(layout);
		background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		checkboxList = new ArrayList<JCheckBox>();
		Box buttonBox = new Box(BoxLayout.Y_AXIS);
		JButton start = new JButton("       Start       ");
		start.addActionListener(new MyStartListener());
		buttonBox.add(start);
		float tempoFactor = sequencer.getTempoInBPM();
		String tempo = "Default Tempo in BPM: " + Float.toString(sequencer.getTempoInBPM());
		DisplayTempo = new JTextArea(tempo);


		JButton stop = new JButton("       Stop        ");
		stop.addActionListener(new MyStopListener());
		buttonBox.add(stop);
		JButton upTempo = new JButton("  Tempo Up    ");
		upTempo.addActionListener(new MyUpTempoListener());
		buttonBox.add(upTempo);
		JButton downTempo = new JButton("Tempo Down ");
		downTempo.addActionListener(new MyDownTempoListener());
		buttonBox.add(downTempo);
		JButton ResetBox = new JButton("   Reset Box   ");
		ResetBox.addActionListener(new MyResetBoxListener());
		buttonBox.add(ResetBox);
		JButton ResetTemp = new JButton("  Reset Temp ");
		ResetTemp.addActionListener(new MyResetTempListener());
		buttonBox.add(ResetTemp);
		JButton CopyBox = new JButton("    CopyBox    ");
		CopyBox.addActionListener(new MyCopyBoxListener());
		buttonBox.add(CopyBox);
		buttonBox.add(DisplayTempo);



		background.add(BorderLayout.EAST, buttonBox);
		theFrame.getContentPane().add(background);
		GridBagLayout grid = new GridBagLayout();
		GridBagConstraints con = new GridBagConstraints();
		con.gridx = 0; con.gridy = 0;
		con.weightx = 1.0; con.weighty = 1.0;
		con.fill = GridBagConstraints.BOTH;
		mainPanel = new JPanel(grid);
		background.add(BorderLayout.CENTER, mainPanel);
		for (int i = 0; i < 256; i++) {
			if (i % 16 == 0) {
				con.gridy++; con.gridx = 0;
				Label l = new Label(instrumentNames[i / 16]);
				grid.setConstraints(l, con);
				mainPanel.add(l);
				con.gridx++;
			}
			JCheckBox c = new JCheckBox();
			c.setSelected(false);
			checkboxList.add(c);
			grid.setConstraints(c, con);
			mainPanel.add(c);
			con.gridx++;
		} // end loop

		theFrame.setBounds(50, 50, 300, 300);
		theFrame.pack();
		theFrame.setVisible(true);
	} // close buildGUI

	  /**
	  Attempts to setup the Midi.
	  */

	public void setUpMidi() {
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequence = new Sequence(Sequence.PPQ, 4);
			track = sequence.createTrack();
			sequencer.setTempoInBPM(120);
		}
		catch (Exception e) { e.printStackTrace(); }
	} // close setUpMidi

	  /**
	  Checks the status of the various checkboxes and using this creates a track which it plays.
	  */

	public void buildTrackAndStart() {
		ArrayList<Integer> trackList = null; // this will hold the instruments for each 
		sequence.deleteTrack(track);
		track = sequence.createTrack();
		for (int i = 0; i < 16; i++) {
			trackList = new ArrayList<Integer>();
			for (int j = 0; j < 16; j++) {
				JCheckBox jc = (JCheckBox)checkboxList.get(j + (16 * i));
				if (jc.isSelected()) {
					int key = instruments[i];
					trackList.add(new Integer(key));
				}
				else {
					trackList.add(null);  // because this slot should be empty in the track
				}
			} // close inner loop
			makeTracks(trackList);
			track.add(makeEvent(176, 1, 127, 0, 16));   // self-made code
		} // close outer loop
		track.add(makeEvent(192, 9, 1, 0, 15)); // - so we always go to full 16 beats  
		try {
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
			sequencer.start();
			sequencer.setTempoInBPM(120);
		}
		catch (Exception e) { e.printStackTrace(); }
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				JCheckBox box = (JCheckBox)checkboxList.get(j + (i * 16));
				box.addActionListener(more);
			}
		}   // add a way to update sound
	} // close method

	  /**
	  Listens for a click event on the start button.
	  */


	public class MyResetBoxListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			for (int i = 0; i < checkboxList.size(); i++) {
				(checkboxList.get(i)).setSelected(false);
			}
			sequencer.stop();
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					JCheckBox box = (JCheckBox)checkboxList.get(j + (i * 16));
					box.removeActionListener(more);
				}
			}
		}
	}

	public class MyResetTempListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor / tempoFactor));
			float temp = (float)(tempoFactor / tempoFactor);
			//float tempoFactor1 = sequencer.getTempoInBPM();
			String tempo = Float.toString(temp * sequencer.getTempoInBPM());
			DisplayTempo.setText("\nUpdated Tempo in BPM: " + tempo);
		}

	}

	public class MyStartListener implements ActionListener {

		/**
		Creates the track and starts it when a click event occurs on the start button.
		@param a ActionEvent containing details of the click event.
		*/

		public void actionPerformed(ActionEvent a) {
			buildTrackAndStart();
		} // close actionPerformed
	} // close inner class

	  /**
	  Listens for a click event on the stop button.
	  */

	public class MyStopListener implements ActionListener {

		/**
		Stops the track when a click event occurs on the stop button.
		@param a ActionEvent containing details of the click event.
		*/

		public void actionPerformed(ActionEvent a) {
			sequencer.stop();
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					JCheckBox box = (JCheckBox)checkboxList.get(j + (i * 16));
					box.removeActionListener(more);
				}
			}
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
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor * 1.03));
			float temp = (float)(tempoFactor * 1.03);
			//float tempoFactor1 = sequencer.getTempoInBPM();
			String tempo = Float.toString(temp * sequencer.getTempoInBPM());
			DisplayTempo.setText("\nUpdated Tempo in BPM: " + tempo);
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
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float)(tempoFactor *.97));
			float temp = (float)(tempoFactor * 0.97);
			//float tempoFactor1 = sequencer.getTempoInBPM();
			String tempo = Float.toString(temp * sequencer.getTempoInBPM());
			DisplayTempo.setText("\nUpdated Tempo in BPM: " + tempo);
		}
	}


	public class MyCopyBoxListener implements ActionListener {
		/**
		Copy the first 4 boxes of each instruments to the last 12 boxes of each of them.
		*/
		public void actionPerformed(ActionEvent a) {
			for (int i = 0; i < checkboxList.size(); i += 16) {
				for (int j = i; j < i + 4; j++) {
					if (checkboxList.get(j).isSelected()) {
						(checkboxList.get(j + 4)).setSelected(true);
						(checkboxList.get(j + 8)).setSelected(true);
						(checkboxList.get(j + 12)).setSelected(true);
					}
				}
			}
			buildTrackAndStart();
		}
	}// make a copy action


	public void makeTracks(ArrayList list) {
		Iterator it = list.iterator();
		for (int i = 0; i < 16; i++) {
			Integer num = (Integer)it.next();
			//Integer num = (Integer) list.get(i);
			if (num != null) {
				int numKey = num.intValue();
				track.add(makeEvent(144, 9, numKey, 100, i));
				track.add(makeEvent(128, 9, numKey, 100, i + 1));
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
			ShortMessage a = new ShortMessage();
			a.setMessage(comd, chan, one, two);
			event = new MidiEvent(a, tick);
		}
		catch (Exception e) {}
		return event;
	} // close makeEvent
} // close class
