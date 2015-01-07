/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.qcarver.wumpus;

import org.apache.commons.cli.Options;

/**
 
 *
 * @author qcarver
 */
public interface Playable {
    /**
     * this is the main play loop 1) any preamble text 2) do{show output, get
     * input}(while game is still in play) 3) any wrap up, score showing.. etc.
     */
    public void play(Configuration configuration);
}
