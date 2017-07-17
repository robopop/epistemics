/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.rest.controller;

import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import selemca.epistemics.data.entity.Setting;
import selemca.epistemics.mentalworld.beliefsystem.repository.SettingRepository;

import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping(MentalWorldRestController.URL_PREFIX)
public class MentalWorldRestController {
    protected static final String URL_PREFIX = "/epistemics";
    protected static final String SERVLET_SETTING = "/setting";
    protected static final String PARAM_SETTING_ID = "settingId";

    @Autowired
    private SettingRepository settingRepository;

    /*
     * CONCEPT
     */
    @RequestMapping(value = SERVLET_SETTING + "/{" + PARAM_SETTING_ID + "}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public List<Setting> listSettings(@ApiParam(value = PARAM_SETTING_ID, required = false) @PathParam(PARAM_SETTING_ID) String settingId) {
        List<Setting> settings = new ArrayList<>();
        if (settingId != null) {
            Optional<Setting> settingOptional = settingRepository.findOne(settingId);
            if (settingOptional.isPresent()) {
                settings.add(settingOptional.get());
            }
        } else {
            settings.addAll(settingRepository.findAll());
        }
        Logger.getLogger(getClass().getSimpleName()).info(String.format("Requesting settings -> %s", settings));
        return settings;
    }

    @RequestMapping(value = SERVLET_SETTING, method = RequestMethod.PUT)
    @Consumes(MediaType.APPLICATION_JSON)
    public void editSetting(@RequestBody Setting setting) {
        Logger.getLogger(getClass().getSimpleName()).info("Storing setting: " + setting);
        settingRepository.save(setting);
    }

    @RequestMapping(value = SERVLET_SETTING, method = RequestMethod.DELETE)
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeSetting(@RequestBody Setting setting) {
        Logger.getLogger(getClass().getSimpleName()).info("Deleting setting: " + setting);
        settingRepository.delete(setting);
    }

}
