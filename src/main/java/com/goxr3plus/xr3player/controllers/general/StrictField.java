/*
 * 
 */
package com.goxr3plus.xr3player.controllers.general;

import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 * This class represents a TextField which allows only some defined characters
 * and symbols.
 *
 * @author GOXR3PLUS
 */
public class StrictField extends TextField {

	/** The not allow. */
	String[] notAllow = new String[] { "/", "\\", ":", "*", "?", "\"", "<", ">", "|", "'" };

	/**
	 * Constructor.
	 */
	public StrictField() {

		setTooltip(new Tooltip("Not allowed:(<) (>) (:) (\") (/) (\\) (|) (?) (*) (')"));
		textProperty().addListener(l -> {

			if (getText() != null) {

				// Allow until 150 characters
				if (getText().length() > 150)
					setText(getText().substring(0, 150));

				// Strict Mode
				for (String s : notAllow)
					if (getText().contains(s))
						setText(getText().replace(s, ""));
			}

		});
	}

}
