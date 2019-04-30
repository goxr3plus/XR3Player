package com.goxr3plus.xr3player.controllers.custom;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * User: hansolo Date: 16.04.14 Time: 22:07
 */
public class FlipEvent extends Event {
	public static final EventType<FlipEvent> FLIP_TO_FRONT_FINISHED = new EventType<>(ANY, "flipToFrontFinished");
	public static final EventType<FlipEvent> FLIP_TO_BACK_FINISHED = new EventType<>(ANY, "flipToBackFinished");

	public FlipEvent(final Object SOURCE, final EventTarget TARGET, final EventType<FlipEvent> EVENT_TYPE) {
		super(SOURCE, TARGET, EVENT_TYPE);
	}
}