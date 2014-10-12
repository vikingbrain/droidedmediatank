/*
 * Copyright 2011-2014 Rafael Iñigo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vikingbrain.dmt.service.util;

import java.util.LinkedList;

import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.service.DavidBoxService;
import com.vikingbrain.nmt.util.exceptions.TheDavidBoxClientException;

/**
 * It uses a thread to send to NMT all keyboard keys respecting the
 * time that is required for NMT to understand those keys.
 * 
 * @author Rafael Iñigo
 */
public class KeyboardTaskQueue {

	/** Tag for logging. */
	private static final String TAG = KeyboardTaskQueue.class.getSimpleName();

	private DavidBoxService davidBoxService;	

	private LinkedList<Keyboard2RemoteKey> keys;
	private Thread thread;
	private boolean running;
	private Runnable internalRunnable;

	//Last key sent.
	private static String lastRemoteKeySended = "";
	
	private class InternalRunnable implements Runnable {
		public void run() {
			internalRun();
		}
	}

	/**
	 * Constructor.
	 * @param davidBoxService the davidbox service
	 */
	public KeyboardTaskQueue(DavidBoxService davidBoxService) {
		keys = new LinkedList<Keyboard2RemoteKey>();
		internalRunnable = new InternalRunnable();
		this.davidBoxService = davidBoxService;
	}

	/**
	 * Starts the thread.
	 */
	public void start() {
		if (!running) {
			thread = new Thread(internalRunnable);
			thread.setDaemon(true);
			running = true;
			thread.start();
		}
	}

	/**
	 * Stops the thread.
	 */
	public void stop() {
		running = false;
	}

	/**
	 * Add a keyboard key to the queue.
	 * @param key the keyboard key
	 */
	private void addKey(Keyboard2RemoteKey key) {
		synchronized (keys) {
			keys.addFirst(key);
			keys.notify(); // notify any waiting threads
		}
	}

	/**
	 * Get next key in the queue.
	 * @return the next key in the queue
	 */
	private Keyboard2RemoteKey getNextKey() {
		synchronized (keys) {
			if (keys.isEmpty()) {
				try {
					keys.wait();
				} catch (InterruptedException e) {
					DmtLogger.e("androidx", "Task interrupted", e);
					stop();
				}
			}
			return keys.removeLast();
		}
	}

	/**
	 * Control the waiting time that is required so NMT understand
	 * property the symbol
	 */
	private void internalRun() {
		while (running) {
			Keyboard2RemoteKey key = getNextKey();
			try {
				
				//pause the key sending if the key pressed is the same as the key pressed before
				pauseIfKeyBelongsSameIRButton(key);
				
				executeKeySend(key);
				
			} catch (Throwable t) {
				DmtLogger.e("androidx", "Task threw an exception", t);
			}
		}
	}

	/**
	 * Executes the key sending to NMT.
	 * @param key the keyboard remote key to send
	 * @throws TheDavidBoxClientException exception in the operation
	 */
	private void executeKeySend(Keyboard2RemoteKey key) throws TheDavidBoxClientException{
		
		if (null != key){
			int pressTimesRemoteKey = key.getPressTimesRemoteKey();
			String remoteKey = key.getRemoteKey();

			for (int i = 0; i<pressTimesRemoteKey ; i++){				
				DmtLogger.d(TAG,"It will send to dadidbox the key: " + remoteKey);
				davidBoxService.executeKeyIR(remoteKey);
			}
		}
		
	}	

	/**
	 *	Pause the key sending if the key pressed is the same as the key pressed before.
	 *  It doesn't make wait if the key is backspace.
	 * @param newKey the new key
	 */
	private void pauseIfKeyBelongsSameIRButton(Keyboard2RemoteKey newKey){

		if (lastRemoteKeySended.equals(newKey.getRemoteKey())
				&& ! newKey.equals(Keyboard2RemoteKey.BACKSPACE)){
			try {
				//Make it wait a while			
			    Thread.sleep(Constants.WAITING_TIME_SAME_KEY_IR_REMOTE);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		lastRemoteKeySended = newKey.getRemoteKey();
	}
	
	/**
	 * Add a keyboard symbol to the queue.
	 * @param symbol the symbol
	 */
	public void addKeyboardSymbol(String symbol) {
		
		//Find the key corresponding to the android keyboard key pressed
		Keyboard2RemoteKey key = Keyboard2RemoteKey.findByKeyboardSymbol(symbol);

		//If there is a corresponding key then add it to queue
		if (null != key){
			//Add the key to the queue
			addKey(key);
		}
	}
}