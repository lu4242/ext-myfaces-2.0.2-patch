/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.ov2021.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.faces.context.ExternalContext;
import javax.faces.el.PropertyResolver;
import javax.faces.el.VariableResolver;

import org.apache.myfaces.ov2021.config.element.ManagedBean;
import org.apache.myfaces.ov2021.config.element.NavigationRule;
import org.apache.myfaces.ov2021.config.element.ResourceBundle;

/**
 * Holds all configuration information (from the faces-config xml files) that is needed later during runtime. The config
 * information in this class is only available to the MyFaces core implementation classes (i.e. the myfaces source
 * tree). See MyfacesConfig for config parameters that can be used for shared or component classes.
 * 
 * @author Manfred Geiler (latest modification by $Author: bommel $)
 * @version $Revision: 1187700 $ $Date: 2011-10-22 14:19:37 +0200 (Sa, 22 Okt 2011) $
 */
@SuppressWarnings("deprecation")
public class RuntimeConfig
{
    //private static final Log log = LogFactory.getLog(RuntimeConfig.class);
    private static final Logger log = Logger.getLogger(RuntimeConfig.class.getName());

    private static final String APPLICATION_MAP_PARAM_NAME = RuntimeConfig.class.getName();

    private final Collection<NavigationRule> _navigationRules = new ArrayList<NavigationRule>();
    private final Map<String, ManagedBean> _managedBeans = new HashMap<String, ManagedBean>();
    private boolean _navigationRulesChanged = false;
    private final Map<String, ResourceBundle> _resourceBundles = new HashMap<String, ResourceBundle>();
    private final Map<String, ManagedBean> _oldManagedBeans = new HashMap<String, ManagedBean>();
    
    private String _facesVersion;
    
    private List<ELResolver> facesConfigElResolvers;
    private List<ELResolver> applicationElResolvers;

    private VariableResolver _variableResolver;
    private PropertyResolver _propertyResolver;

    private ExpressionFactory _expressionFactory;

    private PropertyResolver _propertyResolverChainHead;

    private VariableResolver _variableResolverChainHead;
    
    private Comparator<ELResolver> _elResolverComparator;

    private final Map<String, org.apache.myfaces.ov2021.config.element.Converter> _converterClassNameToConfigurationMap =
        new ConcurrentHashMap<String, org.apache.myfaces.ov2021.config.element.Converter>();
    
    private NamedEventManager _namedEventManager;

    public static RuntimeConfig getCurrentInstance(ExternalContext externalContext)
    {
        RuntimeConfig runtimeConfig = (RuntimeConfig) externalContext.getApplicationMap().get(
                APPLICATION_MAP_PARAM_NAME);
        if (runtimeConfig == null)
        {
            runtimeConfig = new RuntimeConfig();
            externalContext.getApplicationMap().put(APPLICATION_MAP_PARAM_NAME, runtimeConfig);
        }
        return runtimeConfig;
    }

    public void purge()
    {
        _navigationRules.clear();
        _oldManagedBeans.clear();
        _oldManagedBeans.putAll(_managedBeans);
        _managedBeans.clear();
        _navigationRulesChanged = false;
        _converterClassNameToConfigurationMap.clear();
    }

    /**
     * Return the navigation rules that can be used by the NavigationHandler implementation.
     * 
     * @return a Collection of {@linkorg.apache.myfaces.ov2021.config.element.NavigationRule NavigationRule}s
     */
    public Collection<NavigationRule> getNavigationRules()
    {
        return Collections.unmodifiableCollection(_navigationRules);
    }

    public void addNavigationRule(NavigationRule navigationRule)
    {
        _navigationRules.add(navigationRule);

        _navigationRulesChanged = true;
    }

    public boolean isNavigationRulesChanged()
    {
        return _navigationRulesChanged;
    }

    public void setNavigationRulesChanged(boolean navigationRulesChanged)
    {
        _navigationRulesChanged = navigationRulesChanged;
    }

    /**
     * Return the managed bean info that can be used by the VariableResolver implementation.
     * 
     * @return a {@linkorg.apache.myfaces.ov2021.config.element.ManagedBean ManagedBean}
     */
    public ManagedBean getManagedBean(String name)
    {
        return _managedBeans.get(name);
    }

    public Map<String, ManagedBean> getManagedBeans()
    {
        return Collections.unmodifiableMap(_managedBeans);
    }

    public void addManagedBean(String name, ManagedBean managedBean)
    {
        _managedBeans.put(name, managedBean);
        if(_oldManagedBeans!=null)
            _oldManagedBeans.remove(name);
    }

    
    public final void addConverterConfiguration(final String converterClassName,
            final org.apache.myfaces.ov2021.config.element.Converter configuration)
    {
        checkNull(converterClassName, "converterClassName");
        checkEmpty(converterClassName, "converterClassName");
        checkNull(configuration, "configuration");

        _converterClassNameToConfigurationMap.put(converterClassName, configuration);
    }
    
    public org.apache.myfaces.ov2021.config.element.Converter getConverterConfiguration(String converterClassName)
    {
        return (org.apache.myfaces.ov2021.config.element.Converter)_converterClassNameToConfigurationMap.get(converterClassName);
    }
    
    private void checkNull(final Object param, final String paramName)
    {
        if (param == null)
        {
            throw new NullPointerException(paramName + " can not be null.");
        }
    }

    private void checkEmpty(final String param, final String paramName)
    {
        if (param.length() == 0)
        {
            throw new NullPointerException("String " + paramName + " can not be empty.");
        }
    }

    /**
     * Return the resourcebundle which was configured in faces config by var name
     * 
     * @param name
     *            the name of the resource bundle (content of var)
     * @return the resource bundle or null if not found
     */
    public ResourceBundle getResourceBundle(String name)
    {
        return _resourceBundles.get(name);
    }

    /**
     * @return the resourceBundles
     */
    public Map<String, ResourceBundle> getResourceBundles()
    {
        return _resourceBundles;
    }

    public void addResourceBundle(ResourceBundle bundle)
    {
        if (bundle == null)
        {
            throw new IllegalArgumentException("bundle must not be null");
        }
        String var = bundle.getVar();
        if (_resourceBundles.containsKey(var) && log.isLoggable(Level.WARNING))
        {
            log.warning("Another resource bundle for var '" + var + "' with base name '"
                    + _resourceBundles.get(var).getBaseName() + "' is already registered. '"
                    + _resourceBundles.get(var).getBaseName() + "' will be replaced with '" + bundle.getBaseName()
                    + "'.");
        }
        _resourceBundles.put(var, bundle);
    }

    public void addFacesConfigElResolver(ELResolver resolver)
    {
        if (facesConfigElResolvers == null)
        {
            facesConfigElResolvers = new ArrayList<ELResolver>();
        }
        facesConfigElResolvers.add(resolver);
    }

    public List<ELResolver> getFacesConfigElResolvers()
    {
        return facesConfigElResolvers;
    }

    public void addApplicationElResolver(ELResolver resolver)
    {
        if (applicationElResolvers == null)
        {
            applicationElResolvers = new ArrayList<ELResolver>();
        }
        applicationElResolvers.add(resolver);
    }

    public List<ELResolver> getApplicationElResolvers()
    {
        return applicationElResolvers;
    }

    public void setVariableResolver(VariableResolver variableResolver)
    {
        _variableResolver = variableResolver;
    }

    public VariableResolver getVariableResolver()
    {
        return _variableResolver;
    }

    public void setPropertyResolver(PropertyResolver propertyResolver)
    {
        _propertyResolver = propertyResolver;
    }

    public PropertyResolver getPropertyResolver()
    {
        return _propertyResolver;
    }

    public ExpressionFactory getExpressionFactory()
    {
        return _expressionFactory;
    }

    public void setExpressionFactory(ExpressionFactory expressionFactory)
    {
        _expressionFactory = expressionFactory;
    }

    public void setPropertyResolverChainHead(PropertyResolver resolver)
    {
        _propertyResolverChainHead = resolver;
    }

    public PropertyResolver getPropertyResolverChainHead()
    {
        return _propertyResolverChainHead;
    }

    public void setVariableResolverChainHead(VariableResolver resolver)
    {
        _variableResolverChainHead = resolver;
    }

    public VariableResolver getVariableResolverChainHead()
    {
        return _variableResolverChainHead;
    }

    public Map<String, ManagedBean> getManagedBeansNotReaddedAfterPurge()
    {
        return _oldManagedBeans;
    }

    public void resetManagedBeansNotReaddedAfterPurge()
    {
        _oldManagedBeans.clear();
    }
    
    public String getFacesVersion ()
    {
        return _facesVersion;
    }
    
    void setFacesVersion (String facesVersion)
    {
        _facesVersion = facesVersion;
    }

    public NamedEventManager getNamedEventManager()
    {
        return _namedEventManager;
    }

    public void setNamedEventManager(NamedEventManager namedEventManager)
    {
        this._namedEventManager = namedEventManager;
    }

    public Comparator<ELResolver> getELResolverComparator()
    {
        return _elResolverComparator;
    }
    
    public void setELResolverComparator(Comparator<ELResolver> elResolverComparator)
    {
        _elResolverComparator = elResolverComparator;
    }
}
