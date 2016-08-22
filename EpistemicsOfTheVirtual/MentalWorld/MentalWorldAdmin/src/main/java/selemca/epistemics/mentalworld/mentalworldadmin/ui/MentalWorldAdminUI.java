/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.mentalworldadmin.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import org.apache.commons.configuration.Configuration;
import selemca.epistemics.mentalworld.engine.setting.SettingConfigProvider;
import selemca.epistemics.mentalworld.mentalworldadmin.setting.SettingConfigProviderRegistry;

import javax.servlet.annotation.WebServlet;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("serial")
@Title("Epistemics of the Virtual administration")
@Theme("valo")
public class MentalWorldAdminUI extends UI {
    private SettingConfigProviderRegistry settingConfigProviderRegistry;
    private Configuration applicationSettings;
    private final TabSheet tabsheet = new TabSheet();

    @Override
    protected void init(VaadinRequest request) {
        SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
        settingConfigProviderRegistry = (SettingConfigProviderRegistry) helper.getBean("settingConfigProviderRegistry");
        applicationSettings = (Configuration) helper.getBean("applicationSettings");
        setLocale(Locale.ENGLISH);
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {

    }

    private void buildLayout() {
        List<SettingConfigProvider> settingConfigProviders = settingConfigProviderRegistry.getSettingConfigProviders();
        Collections.sort(settingConfigProviders, (p1, p2) -> p1.getGroupLabel().compareTo(p2.getGroupLabel()));

        VerticalLayout settingSelectorLayout = new VerticalLayout();
        TabSheet hiddenTabSheet = new TabSheet();
        hiddenTabSheet.setTabsVisible(false);
        Panel settingTabPanel = new Panel(hiddenTabSheet);
        settingTabPanel.setSizeFull();


        for (SettingConfigProvider settingConfigProvider : settingConfigProviders) {
            String label = settingConfigProvider.getGroupLabel();
            Button selectorButton = new Button(label);
            SettingsFormComponent settingPanel = new SettingsFormComponent(applicationSettings, settingConfigProvider);
            settingSelectorLayout.addComponent(selectorButton);
            hiddenTabSheet.addTab(settingPanel, label);
            selectorButton.addClickListener(e -> {
                hiddenTabSheet.setSelectedTab(settingPanel);
                settingPanel.reset();
            });
        }

        settingSelectorLayout.setSpacing(true);
        hiddenTabSheet.setSizeFull();
        HorizontalLayout mainLayout = new HorizontalLayout(settingSelectorLayout, settingTabPanel);
        mainLayout.setSpacing(true);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(settingSelectorLayout, 1);
        mainLayout.setExpandRatio(settingTabPanel, 5);
        setContent(mainLayout);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MentalWorldAdminUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
