package com.muedsa.intellij.textReader.state;

import com.intellij.ui.JBColor;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JBColorConverter extends Converter<JBColor> {
    @Override
    public @Nullable JBColor fromString(@NotNull String value) {
        return new JBColor(Integer.parseInt(value), Integer.parseInt(value));
    }

    @Override
    public @Nullable String toString(@NotNull JBColor value) {
        return Integer.toString(value.getRGB());
    }
}
