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

/**
 * Enumeration that holds the matching between keyboard keys and
 * the control remote keys including the number of times the control remote
 * key must be pressed to obtain the corresponding keyboard key. 
 * 
 * @author Rafael Iñigo
 */
public enum Keyboard2RemoteKey {
	
	//TODO MISSING KEYS	
	//libra
	//&
	//*
	//+
	//comi " lla

    ONE  ("1", "one", 1), 
    PERIOD (".", "one", 2),
    FORWARD_SLASH("/", "one", 3),
    COMA(",","one",4),
    COLON (":", "one", 5),
    MINUS("-","one",6),    
    UNDERSCORE("_","one",7),
    QUESTION_MARK("?","one",8),
    EXCLAMATION("!","one",9),
    APOSTROPHE("'","one",10),
    AT ("@", "one", 11),
    SEMI_COLON(";","one",12),                      
    PARENTHESIS_LEFT("(","one",13),
    PARENTHESIS_RIGHT(")","one",14),
    LEFT_SQUARE_BRACKET("[","one",15),
    RIGHT_SQUARE_BRACKET("]","one",16),        
    DOLLAR("$","one",17),        
    
//TODO TEST   DONT-KNOW("","one",18); is a big - symbol
    EQUAL("=","one",8),
            
    PERCENT("%","one",19),
//TODO TEST   20 ayen o hash?
    HASH("#","one",20),
    
    A ("a", "two", 1),
    B ("b", "two", 2),
    C ("c", "two", 3),
    TWO  ("2", "two", 4),

    D("d", "three", 1),
    E("e", "three", 2),
    F("f", "three", 3),
    THREE("3", "three", 4),

    G("g", "four", 1),
    H("h", "four", 2),
    I("i", "four", 3),
    FOUR("4", "four", 4),

    J("j", "five", 1),
    K("k", "five", 2),
    L("l", "five", 3),
    FIVE("5", "five", 4),
	
    M("m", "six", 1),
    N("n", "six", 2),
    O("o", "six", 3),
    SIX("6", "six", 4),
	
    P("p", "seven", 1),
    Q("q", "seven", 2),
    R("r", "seven", 3),
    S("s", "seven", 4),
    SEVEN("7", "seven", 5),
	
    T("t", "eight", 1),
    U("u", "eight", 2),
    V("v", "eight", 3),
    EIGHT("8", "eight", 4),
	
    W("w", "nine", 1),
    X("x", "nine", 2),
    Y("y", "nine", 3),
    Z("z", "nine", 4),
    NINE("9", "nine", 5),
    		
    ZERO("0","zero",1),
    SPACE(" ","zero",2),        
    
    
    BACKSPACE("BACKSPACE","delete",1),
//    EXCLAMATION("!","exclamation",1),
//    AT("@","at",1),
//    HASH("#","hash",1),
//    DOLLAR("$","dollar",1),
//    PERCENT("%","percent",1),
    CARET("^","caret",1),
    AMPERSAND("&","ampersand",1),
    ASTERISK("*","asterisk",1),
//    PARENTHESIS_LEFT("(","parenthesis_left",1),
//    PARENTHESIS_RIGHT(")","parenthesis_right",1),
//    MINUS("-","minus",1),
//????        EQUAL("=","equal",1),
//    LEFT_SQUARE_BRACKET("[","left_square_bracket",1),
//    RIGHT_SQUARE_BRACKET("]","right_square_bracket",1),
//    SEMI_COLON(";","semi_colon",1),              
//    APOSTROPHE("'","apostrophe",1),
//    COMA(",","comma",1),
//    PERIOD(".","period",1),
//    FORWARD_SLASH("/","forward_slash",1),
//    UNDERSCORE("_","underscore",1),
    PLUS("+","plus",1),
    PIPE("|","pipe",1),
    LEFT_CURLY_BRACE("{","left_curly_brace",1),
    RIGHT_CURLY_BRACE("}","right_curly_brace",1),
//    COLON(".","colon",1),        
    LESS_THAN("<","less_than",1),
    GREATER_THAN(">","greater_than",1);
//    QUESTION_MARK("?","question_mark",1);
    

    private String keyboardSymbol = null;
    private String remoteKey = null;
    private int pressTimesRemoteKey = 0;
    
    /**
     * Constructor. 
     * @param keyboardSymbol the keyboard key
     * @param remoteKey the remote control key
     * @param pressTimesRemoteKey number of times that remote control key needs
     * to be pressed to obtain the corresponding keyboard key
     */
    private Keyboard2RemoteKey(String keyboardSymbol, String remoteKey, int pressTimesRemoteKey) {
        this.keyboardSymbol = keyboardSymbol;
        this.remoteKey = remoteKey;
        this.pressTimesRemoteKey = pressTimesRemoteKey;
    }

    /**
     * Get keyboard key.
     * @return the keyboard key
     */
    public String getKeyboardSymbol() {
        return keyboardSymbol;
    }

    /**
     * Get the remote control key.
     * @return the remote control key
     */
	public String getRemoteKey() {
		return remoteKey;
	}

	/**
	 * Get the number of times the remote control key
	 * needs to be pressed.
	 * @return the number of times the remote control key
	 * needs to be pressed
	 */
	public int getPressTimesRemoteKey() {
		return pressTimesRemoteKey;
	}

	/**
	 * Find the corresponding keyboard to remote control translation for
	 * a keyboard symbol.
	 * @param keyboardSymbol the keyboard symbol 
	 * @return the object with the corresponding translation
	 */
	public static Keyboard2RemoteKey findByKeyboardSymbol(String keyboardSymbol) {
		Keyboard2RemoteKey type = null;
	    for (Keyboard2RemoteKey actual : Keyboard2RemoteKey.values()) {
        	if(actual.getKeyboardSymbol().equals(keyboardSymbol)){
        		type = actual;
        	}
	    }
		return type;
	}	

}
