package core.web;

import com.jeefw.service.sys.InformationService;
import core.service.Service;
import core.support.ExtJSBaseParameter;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.*;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * @author 杨添
 * @框架唯一的升级和技术支持地址：http://shop111863449.taobao.com
 */
public class SystemInitListener implements ServletContextListener {

    private static ApplicationContext applicationContext;

    public static Object getDynamicProperty(String entityName, Serializable key, String propName) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object cache = request.getAttribute(entityName + "_" + key);
        if (cache != null) {
            try {
                return PropertyUtils.getProperty(cache, propName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String s = entityName.substring(0, 1).toLowerCase() + entityName.substring(1) + "ServiceImpl";
        Service service = (Service) applicationContext.getBean(s);
        Object entity = (ExtJSBaseParameter) service.get(key);
        request.setAttribute(entityName + "_" + key, entity);
        try {
            return PropertyUtils.getProperty(entity, propName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub

    }

    private void initIndexingInformation() {

    }

    private void initConvertor() {
        ConvertUtils.register(new JavaUtilDateConverter(), java.util.Date.class);
        ConvertUtils.register(new ByteConverter(null), Byte.class);
        ConvertUtils.register(new ShortConverter(null), Short.class);
        ConvertUtils.register(new IntegerConverter(null), Integer.class);
        ConvertUtils.register(new LongConverter(null), Long.class);
        ConvertUtils.register(new FloatConverter(null), Float.class);
        ConvertUtils.register(new DoubleConverter(null), Double.class);
    }

    public void contextInitialized(ServletContextEvent event) {
        initConvertor();
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
        // applicationContext.xml已定时生成索引，此代码仅供测试，可删除
        InformationService informationService = (InformationService) applicationContext.getBean("informationService");
        informationService.indexingInformation();
    }

}
