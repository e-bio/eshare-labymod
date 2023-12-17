package com.rappytv.eshare;

import net.labymod.api.addon.LabyAddon;

public class EShareAddon extends LabyAddon<EShareConfig> {

    @Override
    protected void enable() {
        registerSettingCategory();
    }

    @Override
    protected Class<? extends EShareConfig> configurationClass() {
        return EShareConfig.class;
    }
}
