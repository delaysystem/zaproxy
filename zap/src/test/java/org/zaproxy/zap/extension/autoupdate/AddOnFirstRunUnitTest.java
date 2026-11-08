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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zaproxy.zap.Version;
import org.zaproxy.zap.control.AddOn;
import org.zaproxy.zap.utils.ZapXmlConfiguration;

/** Unit test for {@link AddOnFirstRun}. */
class AddOnFirstRunUnitTest {

    private ZapXmlConfiguration config;
    private AddOn addOn;

    @BeforeEach
    void setUp() {
        config = new ZapXmlConfiguration();
        addOn = mock(AddOn.class);
        given(addOn.getId()).willReturn("test");
        given(addOn.getVersion()).willReturn(new Version("1.0.0"));
    }

    @Test
    void shouldShowWhenNeverShown() {
        assertThat(AddOnFirstRun.shouldShow(config, addOn), is(true));
    }

    @Test
    void shouldNotShowWhenAlreadyShownForVersion() {
        // Given
        AddOnFirstRun.markShown(config, addOn);
        // When / Then
        assertThat(AddOnFirstRun.shouldShow(config, addOn), is(false));
    }

    @Test
    void shouldShowAgainWhenVersionChanged() {
        // Given
        AddOnFirstRun.markShown(config, addOn);
        given(addOn.getVersion()).willReturn(new Version("2.0.0"));
        // When / Then
        assertThat(AddOnFirstRun.shouldShow(config, addOn), is(true));
    }

    @Test
    void shouldReturnNullTipWhenAddOnHasNoBundle() {
        // Given
        given(addOn.getResourceBundle()).willReturn(null);
        // When / Then
        assertThat(AddOnFirstRun.getTip(addOn), is(nullValue()));
    }

    @Test
    void shouldReturnNullTipWhenAddOnHasNoTipKey() {
        // Given
        ResourceBundle bundle = mock(ResourceBundle.class);
        given(bundle.getString(anyString()))
                .willThrow(new MissingResourceException("missing", "bundle", "key"));
        given(addOn.getResourceBundle()).willReturn(bundle);
        // When / Then
        assertThat(AddOnFirstRun.getTip(addOn), is(nullValue()));
    }

    @Test
    void shouldReturnTipFromAddOnBundle() {
        // Given
        ResourceBundle bundle = mock(ResourceBundle.class);
        given(bundle.getString("addon.test.firstrun.tip")).willReturn("A tip.");
        given(addOn.getResourceBundle()).willReturn(bundle);
        // When / Then
        assertThat(AddOnFirstRun.getTip(addOn), is("A tip."));
    }
}
