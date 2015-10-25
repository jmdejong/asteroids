package aoop.asteroids.model;

import aoop.asteroids.Logging;
import aoop.asteroids.model.gameobjects.Explosion;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A class with functions for playing sounds.<br>
 * This class will also keep track of the background music, and assures only one background track is running at a time.
 */
public class Sound {
	
	
	/**
	 * stores whether the background music has started
	 */
	private boolean bgmHasStarted = false;
	
	
	/**
	 * Select and play an explosion sound based on the explosion object itself.<br>
	 * This way, all players will hear the same explosion sound.
	 * @param explosion the explosion object used to select which sound to play
	 */
	public void playExplosionSound(Explosion explosion){
		int index = new Random(explosion.hashCode()).nextInt(4);
		playSound("ExplosionNew"+index+".wav");
	}
	
	/** play a non background sound
	 * @param filename the file where the sound is stored
	 */
	public void playSound(String filename){
		playSound(filename, false);
	}
	
	/** 
	 * @return whether the background music has started
	 */
	public boolean hasBgmStarted(){
		return bgmHasStarted;
	}
	
	/**
	 * play a sound.<br>
	 * Background music will only be played if there is currently no background music
	 * @param filename the file where the sound is stored
	 * @param isBGM whether the sound is background music
	 */
	public void playSound(final String filename, final boolean isBGM){
		
		if(isBGM){
			bgmHasStarted=true;
		}
		
		new Thread(new Runnable() {
		
			@Override
			public void run() {
				
				class AudioListener implements LineListener {
					private boolean done = false;
					@Override
					public synchronized void update(LineEvent event) {
						LineEvent.Type eventType = event.getType();
						if (eventType == LineEvent.Type.STOP || eventType == LineEvent.Type.CLOSE) {
							done = true;
							notifyAll();
						}
					}
					public synchronized void waitUntilDone() throws InterruptedException {
						while (!done) {
							wait();
						}
					}
				}
				
				
				try {
					
					InputStream stream = new BufferedInputStream(new FileInputStream("sounds/"+filename));
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(stream);
					
					
					
					DataLine.Info info = new DataLine.Info(Clip.class, inputStream.getFormat());
					
					AudioListener listener = new AudioListener();
					
					try {
						Clip clip;	
						clip = (Clip) AudioSystem.getLine(info);
						
						
						clip.addLineListener(listener);
						clip.open(inputStream);
						
						try {
							clip.start();
							listener.waitUntilDone();
						} catch (InterruptedException e) {
							Logging.LOGGER.warning("Playing of sound '"+filename+"' was interrupted.");
						} finally {
							clip.drain();
							clip.close();
						}
					} finally {
						inputStream.close();
						if(isBGM){
							bgmHasStarted=false;
						}
					}
					
				} catch (LineUnavailableException | IOException | UnsupportedAudioFileException | IllegalStateException e) {
					//This happens when a file is unavailable or the sound device is busy.
					//Just don't play any sound when that happens.
					Logging.LOGGER.warning("Sound in '"+filename+"' could not be played");
				}
			}
			
		
		}).start();
		
	}
	
}