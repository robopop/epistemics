/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.mentalworldadmin.ui;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

public class SpringContextHelper {

    private ApplicationContext context;
    public SpringContextHelper(ServletContext servletContext) {
        /*ServletContext servletContext = 
                ((WebApplicationContext) application.getContext())
                .getHttpSession().getServletContext();*/
        context = WebApplicationContextUtils.
                getRequiredWebApplicationContext(servletContext);
    }

    public Object getBean(final String beanRef) {
        return context.getBean(beanRef);
    }    
}