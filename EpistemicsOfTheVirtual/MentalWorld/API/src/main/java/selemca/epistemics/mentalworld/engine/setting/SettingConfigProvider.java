/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.setting;

import java.util.List;

/**
 * Created by henrizwols on 22-11-15.
 */
public interface SettingConfigProvider {
    String getGroupLabel();

    List<SettingConfig> getSettingConfigs();

}
