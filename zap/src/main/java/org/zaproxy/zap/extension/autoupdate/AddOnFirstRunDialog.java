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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.parosproxy.paros.Constant;
import org.zaproxy.zap.control.AddOn;

/**
 * The dialog shown after an add-on is installed or updated, with its changes and getting-started
 * tip.
 *
 * @since 2.18.0
 */
public class AddOnFirstRunDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final JCheckBox doNotShowAgainCheckBox;
    private boolean doNotShowAgain;

    /**
     * Constructs the dialog.
     *
     * @param parent the parent window.
     * @param addOn the add-on just installed or updated.
     * @param tip the getting-started tip, or {@code null} if the add-on provides none.
     */
    public AddOnFirstRunDialog(Window parent, AddOn addOn, String tip) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle(AddOnFirstRun.getTitle(addOn));

        JEditorPane content = new JEditorPane();
        content.setEditable(false);
        content.setContentType("text/html; charset=UTF-8");
        content.setText(buildContent(addOn, tip));

        JButton okButton = new JButton(Constant.messages.getString("all.button.ok"));
        okButton.addActionListener(e -> dispose());

        doNotShowAgainCheckBox = new JCheckBox(Constant.messages.getString("all.prompt.dontshow"));
        doNotShowAgainCheckBox.addActionListener(
                e -> doNotShowAgain = doNotShowAgainCheckBox.isSelected());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        buttons.add(doNotShowAgainCheckBox);
        buttons.add(okButton);

        add(new JScrollPane(content), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(500, 350));
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Tells whether the "do not show again" option was selected.
     *
     * @return {@code true} if selected, {@code false} otherwise.
     */
    public boolean isDoNotShowAgain() {
        return doNotShowAgain;
    }

    private static String buildContent(AddOn addOn, String tip) {
        StringBuilder content = new StringBuilder("<html><body>");
        content.append("<h2>")
                .append(Constant.messages.getString("cfu.firstrun.changes"))
                .append("</h2>");
        content.append(addOn.getChanges() != null ? addOn.getChanges() : "");
        if (tip != null && !tip.isEmpty()) {
            content.append("<h2>")
                    .append(Constant.messages.getString("cfu.firstrun.tip"))
                    .append("</h2>");
            content.append(tip);
        }
        content.append("</body></html>");
        return content.toString();
    }
}
