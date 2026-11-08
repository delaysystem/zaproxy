/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2026 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.extension.autoupdate;

import java.awt.EventQueue;
import java.awt.Window;
import java.util.MissingResourceException;
import org.apache.commons.configuration.Configuration;
import org.parosproxy.paros.Constant;
import org.zaproxy.zap.control.AddOn;

/**
 * Shows the "first run" information of an add-on after it is installed or updated.
 *
 * <p>The dialog shows the add-on's changes and, when the add-on provides one, a getting-started
 * tip. It is shown at most once per add-on version and never on the command line or in daemon mode.
 *
 * @since 2.18.0
 */
public final class AddOnFirstRun {

    private static final String PARAM_SHOWN_VERSION = "addons.firstrun.shown.";
    private static final String PARAM_DISABLED = "addons.firstrun.disabled";

    private static final String TIP_KEY_PREFIX = "addon.";
    private static final String TIP_KEY_SUFFIX = ".firstrun.tip";

    private AddOnFirstRun() {}

    /**
     * Shows the first-run dialog for the given add-on if it was not shown yet for its version.
     *
     * <p>Must be called with a UI available, the dialog is shown on the event dispatch thread.
     *
     * @param parent the parent window.
     * @param config the configuration used to track shown versions.
     * @param addOn the add-on just installed or updated.
     */
    public static void showIfNeeded(Window parent, Configuration config, AddOn addOn) {
        if (isDisabled(config) || !shouldShow(config, addOn)) {
            return;
        }
        if (EventQueue.isDispatchThread()) {
            show(parent, config, addOn);
        } else {
            EventQueue.invokeLater(() -> show(parent, config, addOn));
        }
    }

    private static void show(Window parent, Configuration config, AddOn addOn) {
        AddOnFirstRunDialog dialog = new AddOnFirstRunDialog(parent, addOn, getTip(addOn));
        dialog.setVisible(true);
        if (dialog.isDoNotShowAgain()) {
            setDisabled(config, true);
        }
        markShown(config, addOn);
    }

    static boolean shouldShow(Configuration config, AddOn addOn) {
        String shownVersion = config.getString(PARAM_SHOWN_VERSION + addOn.getId());
        return !addOn.getVersion().toString().equals(shownVersion);
    }

    static void markShown(Configuration config, AddOn addOn) {
        config.setProperty(PARAM_SHOWN_VERSION + addOn.getId(), addOn.getVersion().toString());
    }

    static boolean isDisabled(Configuration config) {
        return config.getBoolean(PARAM_DISABLED, false);
    }

    static void setDisabled(Configuration config, boolean disabled) {
        config.setProperty(PARAM_DISABLED, disabled);
    }

    /**
     * Gets the getting-started tip of the given add-on, if it provides one.
     *
     * <p>The tip is read from the add-on's resource bundle with the key {@code
     * addon.<id>.firstrun.tip}, so it can be translated like any other add-on message.
     *
     * @param addOn the add-on.
     * @return the tip, or {@code null} if the add-on provides none.
     */
    public static String getTip(AddOn addOn) {
        if (addOn.getResourceBundle() == null) {
            return null;
        }
        try {
            return addOn.getResourceBundle()
                    .getString(TIP_KEY_PREFIX + addOn.getId() + TIP_KEY_SUFFIX);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    static String getTitle(AddOn addOn) {
        return Constant.messages.getString(
                "cfu.firstrun.title", addOn.getName(), addOn.getVersion());
    }
}
