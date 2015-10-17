package aoop.asteroids.model;

import aoop.asteroids.Logging;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Random;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/** A class for playing sounds */
public class Sound {
	
	/*
	 * This probably does not belong in the model, but I leave it here because the ClientGame uses it
	 * I made this a singleton class. I don't know if this is the best idea.
	 * 
	 * TODO:
	 * - Fix sound error when loading multiple games on same computer
	 * - find something better than singleton
	 * 
	 */
	
	
	private boolean bgmHasStarted = false;
	
	
// 	private static Sound instance;
	
// 	public static Sound getInstance(){
// 		if(instance == null){
// 			instance = new Sound();
// 		}
// 		return instance;
// 	}
// 	
// 	private Sound(){}
	
	public void playExplosionSound(Explosion explosion){
		int index = new Random(explosion.hashCode()).nextInt(4);
		//playSound("Explosion"+index+".wav");
		playSound("ExplosionNew"+index+".wav");
	}
	
	public void playSound(String filename){
		playSound(filename, false);
	}
	
	
	public boolean hasBgmStarted(){
		return bgmHasStarted;
	}
	
	public void playSound(final String filename, /*final int startOffset,*/ final boolean isBGM){
		
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
						Clip clip;// = AudioSystem.getClip();
										
						clip = (Clip) AudioSystem.getLine(info);
						
						
						/*if(startOffset != 0){
							clip.setFramePosition(startOffset);
						}*/
						
						clip.addLineListener(listener);
						clip.open(inputStream);
						
						try {
							clip.start();
							listener.waitUntilDone();
						} catch (InterruptedException e) {
							Logging.LOGGER.info("Playing of sound '"+filename+"' was interrupted.");
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
					Logging.LOGGER.info("Sound in '"+filename+"' could not be played");
				}
			}
			
		
		}).start();
		
	}
	
}