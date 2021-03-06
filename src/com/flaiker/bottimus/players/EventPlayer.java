/******************************************************************************
 * Copyright (C) 2016 Fabian Lupa                                             *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.      *
 ******************************************************************************/

package com.flaiker.bottimus.players;

import net.dv8tion.jda.audio.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for a {@link Player} that sends events.
 */
public abstract class EventPlayer extends Player {
    private List<Runnable> stoppedListeners;
    private boolean started = false;
    private boolean playing = false;
    private boolean paused = false;
    private boolean stopped = true;

    public EventPlayer() {
        stoppedListeners = new ArrayList<>();
    }

    /**
     * Add a listener that gets run when the player stops.
     *
     * @param runnable The listener to be added
     */
    public void addStoppedListener(Runnable runnable) {
        stoppedListeners.add(runnable);
    }

    /**
     * Remove a registered listener
     *
     * @param runnable The listener to be removed
     */
    public void removeStoppedListener(Runnable runnable) throws IllegalArgumentException {
        if (!stoppedListeners.contains(runnable))
            throw new IllegalArgumentException("The listener to be removed was never added.");

        stoppedListeners.remove(runnable);
    }

    @Override
    public void play() {
        if (started && stopped) throw new IllegalStateException("Cannot restart player after it has been stopped.");
        started = true;
        playing = true;
        paused = false;
        stopped = false;
    }

    @Override
    public void pause() {
        playing = false;
        paused = true;
    }

    @Override
    public void stop() {
        playing = false;
        paused = false;
        stopped = true;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    @Override
    public byte[] provide20MsAudio() {
        byte[] bytes = super.provide20MsAudio();
        if (bytes == null) stoppedListeners.forEach(Runnable::run);

        return bytes;
    }

    protected void reset() {
        started = false;
        playing = false;
        paused = false;
        stopped = true;
    }
}
