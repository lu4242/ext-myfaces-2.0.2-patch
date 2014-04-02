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
package org.apache.myfaces.ext202patch.override;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.ProjectStage;
import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.PreRemoveFromViewEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;
import javax.faces.view.ViewDeclarationLanguage;
import javax.faces.view.ViewDeclarationLanguageFactory;
import javax.faces.view.ViewMetadata;
import org.apache.myfaces.buildtools.maven2.plugin.builder.annotation.JSFWebConfigParam;

import org.apache.myfaces.ext202patch.application.StateManagerImpl;
import org.apache.myfaces.shared_ext202patch.util.WebConfigParamUtils;
import org.apache.myfaces.shared_ext202patch.util.ClassUtils;
import org.apache.myfaces.view.facelets.AttachedFullStateWrapper;
import org.apache.myfaces.view.facelets.ComponentState;
import org.apache.myfaces.view.facelets.DefaultFaceletsStateManagementStrategy;
import org.apache.myfaces.view.facelets.FaceletViewDeclarationLanguage;
import org.apache.myfaces.view.facelets.compiler.CheckDuplicateIdFaceletUtils;

/**
 * This class implements partial state saving feature when facelets
 * is used to render pages. (Theorically it could be applied on jsp case too,
 * but all considerations below should be true before apply it).
 * 
 * The following considerations apply for this class:
 * 
 * 1. This StateManagementStrategy should only be active if javax.faces.PARTIAL_STATE_SAVING
 *    config param is active(true). See javadoc on StateManager for details.
 * 2. A map using component clientId as keys are used to hold the state.
 * 3. Each component has a valid id after ViewDeclarationLanguage.buildView().
 *    This implies that somewhere, every TagHandler that create an UIComponent 
 *    instance should call setId and assign it.
 * 4. Every TagHandler that create an UIComponent instance should call markInitialState
 *    after the component is populated. Otherwise, full state is always saved.
 * 5. A SystemEventListener is used to keep track for added and removed components, listen
 *    PostAddToViewEvent and PreRemoveFromViewEvent event triggered by UIComponent.setParent()
 *    method.
 * 6. It is not possible to use javax.faces.component.visit API to traverse the component
 *    tree during save/restore, because UIData.visitTree traverse all rows and we only need
 *    to restore state per component (not per row).
 * 7. It is necessary to preserve the order of the children added/removed between requests.
 * 8. Added and removed components could be seen as subtrees. This imply that we need to save
 *    the structure of the added components subtree and remove one component could be remove
 *    all its children and facets from view inclusive.
 * 9. It is necessary to save and restore the list of added/removed components between several
 *    requests.
 * 
 * @author Leonardo Uribe (latest modification by $Author: lu4242 $)
 * @version $Revision: 793245 $ $Date: 2009-07-11 18:50:53 -0500 (Sat, 11 Jul 2009) $
 * @since 2.0
 *
 */
public class StateFixDefaultFaceletsStateManagementStrategy extends DefaultFaceletsStateManagementStrategy
{
    public static final String CLIENTIDS_ADDED = "oam.CLIENTIDS_ADDED";
    
    public static final String CLIENTIDS_REMOVED = "oam.CLIENTIDS_REMOVED";
    
    /**
     * Key used on component attribute map to indicate if a component was added
     * after build view, so itself and all descendants should not use partial
     * state saving. There are two possible values:
     * 
     * Key not present: The component uses pss.
     * Boolean.TRUE: The component was added to the view after build view.
     * Boolean.FALSE: The component was removed/added to the view. Itself and all
     * descendants should be saved and restored, but we have to unregister/register
     * from CLIENTIDS_ADDED and CLIENTIDS_REMOVED lists.
     */
    public  static final String COMPONENT_ADDED_AFTER_BUILD_VIEW = "oam.COMPONENT_ADDED_AFTER_BUILD_VIEW"; 
    
    
    /**
     * Define how duplicate ids are checked when ProjectStage is Production, by default (auto) it only check ids of
     * components that does not encapsulate markup (like facelets UILeaf).
     *  
     * <ul>
     * <li>true: check all ids, including ids for components that are transient and encapsulate markup.</li>
     * <li>auto: (default) check ids of components that does not encapsulate markup (like facelets UILeaf). 
     * Note ids of UILeaf instances are generated by facelets vdl, start with "j_id", are never rendered 
     * into the response and UILeaf instances are never used as a target for listeners, so in practice 
     * there is no need to check such ids. This reduce the overhead associated with generate client ids.</li>
     * <li>false: do not do any check when ProjectStage is Production</li>
     * </ul>
     * <p> According to specification, identifiers must be unique within the scope of the nearest ancestor to 
     * the component that is a naming container.</p>
     */
    @JSFWebConfigParam(since="2.0.12, 2.1.6", defaultValue="auto", expectedValues="true, auto, false",
                       group="state", tags="performance")
    public static final String CHECK_ID_PRODUCTION_MODE
            = "org.apache.myfaces.CHECK_ID_PRODUCTION_MODE";
    
    private static final String CHECK_ID_PRODUCTION_MODE_DEFAULT = "auto";
    private static final String CHECK_ID_PRODUCTION_MODE_TRUE = "true";
    private static final String CHECK_ID_PRODUCTION_MODE_FALSE = "false";
    private static final String CHECK_ID_PRODUCTION_MODE_AUTO = "auto";
    
    private static final String SERIALIZED_VIEW_REQUEST_ATTR = 
        StateManagerImpl.class.getName() + ".SERIALIZED_VIEW";

    private ViewDeclarationLanguageFactory _vdlFactory;
    
    private RenderKitFactory _renderKitFactory = null;
    
    private String _checkIdsProductionMode;
    
    public StateFixDefaultFaceletsStateManagementStrategy ()
    {
        _vdlFactory = (ViewDeclarationLanguageFactory)FactoryFinder.getFactory(FactoryFinder.VIEW_DECLARATION_LANGUAGE_FACTORY);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public UIViewRoot restoreView (FacesContext context, String viewId, String renderKitId)
    {
        ResponseStateManager manager;
        Object state[];
        Map<String, Object> states;
        UIViewRoot view = null;
 
        // The value returned here is expected to be false (set by RestoreViewExecutor), but
        //we don't know if some ViewHandler wrapper could change it, so it is better to save the value.
        final boolean oldContextEventState = context.isProcessingEvents();
        // Get previous state from ResponseStateManager.
        manager = getRenderKitFactory().getRenderKit(context, renderKitId).getResponseStateManager();
        
        state = (Object[]) manager.getState(context, viewId);
        
        if (state == null)
        {
            //No state could be restored, return null causing ViewExpiredException
            return null;
        }
        
        if (state[1] instanceof Object[])
        {
            Object[] fullState = (Object[]) state[1]; 
            view = (UIViewRoot) internalRestoreTreeStructure((TreeStructComponent)fullState[0]);

            if (view != null)
            {
                context.setViewRoot (view);
                view.processRestoreState(context, fullState[1]);
            }
        }
        else
        {
            // Per the spec: build the view.
            ViewDeclarationLanguage vdl = _vdlFactory.getViewDeclarationLanguage(viewId);
            try {
                ViewMetadata metadata = vdl.getViewMetadata (context, viewId);
                
                Collection<UIViewParameter> viewParameters = null;
                
                if (metadata != null)
                {
                    view = metadata.createMetadataView(context);
                    
                    if (view != null)
                    {
                        viewParameters = metadata.getViewParameters(view);
                    }
                }
                if (view == null)
                {
                    view = context.getApplication().getViewHandler().createView(context, viewId);
                }
                
                context.setViewRoot (view); 
                
                // TODO: Why is necessary enable event processing?
                // ANS: On RestoreViewExecutor, setProcessingEvents is called first to false
                // and then to true when postback. Since we need listeners registered to PostAddToViewEvent
                // event to be handled, we should enable it again. We are waiting a response from EG about
                // the behavior of those listeners, because for partial state saving we need this listeners
                // be called from here and relocate components properly, but for now we have to let this code as is.
                try 
                {
                    context.setProcessingEvents (true);
                    vdl.buildView (context, view);
                    // In the latest code related to PostAddToView, it is
                    // triggered no matter if it is applied on postback. It seems that MYFACES-2389, 
                    // TRINIDAD-1670 and TRINIDAD-1671 are related.
                    // This code is no longer necessary, but better let it here.
                    //_publishPostBuildComponentTreeOnRestoreViewEvent(context, view);
                    suscribeListeners(view);
                }
                finally
                {
                    context.setProcessingEvents (oldContextEventState);
                }
            }
            catch (Throwable e)
            {
                throw new FacesException ("unable to create view \"" + viewId + "\"", e);
            }

            if (state != null && state[1] != null)
            {
                states = (Map<String, Object>) state[1];
                
                // Visit the children and restore their state.
                
                //view.visitTree (VisitContext.createVisitContext (context), new RestoreStateVisitor (states));
                
                //Restore state of current components
                restoreStateFromMap(context, states, view);
                
                // TODO: handle dynamic add/removes as mandated by the spec.  Not sure how to do handle this yet.
                List<String> clientIdsRemoved = getClientIdsRemoved(view);
                
                if (clientIdsRemoved != null)
                {
                    for (String clientId : clientIdsRemoved)
                    {
                        view.invokeOnComponent(context, clientId, new ContextCallback()
                            {
                                public void invokeContextCallback(FacesContext context,
                                        UIComponent target)
                                {
                                    if (target.getParent() != null)
                                    {
                                        if (!target.getParent().getChildren().remove(target))
                                        {
                                            String key = null;
                                            for (Map.Entry<String, UIComponent> entry : target.getParent().getFacets().entrySet())
                                            {
                                                if (entry.getValue()==target)
                                                {
                                                    key = entry.getKey();
                                                    break;
                                                }
                                            }
                                            if (key != null)
                                            {
                                                target.getParent().getFacets().remove(key);
                                            }
                                        }
                                    }
                                }
                            });
                    }
                }
                
                List<String> clientIdsAdded = getClientIdsAdded(view);
                if (clientIdsAdded != null)
                {
                    for (String clientId : clientIdsAdded)
                    {
                        final AttachedFullStateWrapper wrapper = (AttachedFullStateWrapper) states.get(clientId);
                        if (wrapper != null)
                        {
                            final Object[] addedState = (Object[]) wrapper.getWrappedStateObject(); 
                            if (addedState != null)
                            {
                                if (addedState.length == 2)
                                {
                                    view = (UIViewRoot) internalRestoreTreeStructure((TreeStructComponent) addedState[0]);
                                    view.processRestoreState(context, addedState[1]);
                                    break;
                                }
                                else
                                {
                                    final String parentClientId = (String) addedState[0];
                                    view.invokeOnComponent(context, parentClientId, new ContextCallback()
                                    {
                                        public void invokeContextCallback(FacesContext context,
                                                UIComponent target)
                                        {
                                            if (addedState[1] != null)
                                            {
                                                String facetName = (String) addedState[1];
                                                UIComponent child = internalRestoreTreeStructure((TreeStructComponent) addedState[3]);
                                                child.processRestoreState(context, addedState[4]);
                                                target.getFacets().put(facetName,child);
                                            }
                                            else
                                            {
                                                Integer childIndex = (Integer) addedState[2];
                                                UIComponent child = internalRestoreTreeStructure((TreeStructComponent) addedState[3]);
                                                child.processRestoreState(context, addedState[4]);
                                                try
                                                {
                                                    target.getChildren().add(childIndex, child);
                                                }
                                                catch (IndexOutOfBoundsException e)
                                                {
                                                    // We can't be sure about where should be this 
                                                    // item, so just add it. 
                                                    target.getChildren().add(child);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Restore binding, because UIViewRoot.processRestoreState() is never called
        //the event processing has to be enabled because of the restore view event triggers
        //TODO ask the EG the this is a spec violation if we do it that way
        //see Section 2.2.1
        // TODO: Why is necessary enable event processing?
        // ANS: On RestoreViewExecutor, setProcessingEvents is called first to false
        // and then to true when postback. Since we need listeners registered to PostAddToViewEvent
        // event to be handled, we should enable it again. We are waiting a response from EG about
        // the behavior of those listeners (see comment on vdl.buildView). 
        // -= Leonardo Uribe =- I think enable event processing in this point does not have any
        // side effect. Enable it allows programatically add components when binding is set with 
        // pss enabled. That feature works without pss, so we should preserve backward behavior.
        // Tomahawk t:aliasBean example creating components on binding requires this to work.
        //context.setProcessingEvents(true);
        //try {
        //    view.visitTree(VisitContext.createVisitContext(context), new RestoreStateCallback());
        //} finally {
        //    context.setProcessingEvents(oldContextEventState);
        //}
        return view;
    }
    
    /*
    private static void _publishPostBuildComponentTreeOnRestoreViewEvent(FacesContext context, UIComponent component)
    {
        context.getApplication().publishEvent(context, PostBuildComponentTreeOnRestoreViewEvent.class, UIComponent.class, component);
        
        if (component.getChildCount() > 0)
        {
            // PostAddToViewEvent could cause component relocation
            // (h:outputScript, h:outputStylesheet, composite:insertChildren, composite:insertFacet)
            // so we need to check if the component was relocated or not
            List<UIComponent> children = component.getChildren();
            UIComponent child = null;
            UIComponent currentChild = null;
            int i = 0;
            while (i < children.size())
            {
                child = children.get(i);
                // Iterate over the same index if the component was removed
                // This prevents skip components when processing
                do 
                {
                    _publishPostBuildComponentTreeOnRestoreViewEvent(context, child);
                    currentChild = child;
                }
                while ((i < children.size()) &&
                       ((child = children.get(i)) != currentChild) );
                i++;
            }
        }
        if (component.getFacetCount() > 0)
        {
            for (UIComponent child : component.getFacets().values())
            {
                _publishPostBuildComponentTreeOnRestoreViewEvent(context, child);
            }
        }        
    }*/

    @Override
    public Object saveView (FacesContext context)
    {
        UIViewRoot view = context.getViewRoot();
        Object states;
        
        if (view == null)
        {
            // Not much that can be done.
            
            return null;
        }
        
        if (view.isTransient())
        {
            // Must return null immediately per spec.
            
            return null;
        }
        
        ExternalContext externalContext = context.getExternalContext();
        
        Object serializedView = context.getAttributes()
            .get(SERIALIZED_VIEW_REQUEST_ATTR);
        
        //Note on ajax case the method saveState could be called twice: once before start
        //document rendering and the other one when it is called StateManager.getViewState method.
        if (serializedView == null)
        {
                    
            // Make sure the client IDs are unique per the spec.
            
            if (context.isProjectStage(ProjectStage.Production))
            {
                if (CHECK_ID_PRODUCTION_MODE_AUTO.equals(getCheckIdProductionMode(context)))
                {
                    CheckDuplicateIdFaceletUtils.checkIdsStatefulComponents(context, view);
                }
                else if (CHECK_ID_PRODUCTION_MODE_TRUE.equals(getCheckIdProductionMode(context)))
                {
                    CheckDuplicateIdFaceletUtils.checkIds(context, view);
                }
            }
            else
            {
                CheckDuplicateIdFaceletUtils.checkIds(context, view);
            }
            
            // Create save state objects for every component.
            
            //view.visitTree (VisitContext.createVisitContext (context), new SaveStateVisitor (states));
            
            if (view.getAttributes().containsKey(COMPONENT_ADDED_AFTER_BUILD_VIEW))
            {
                ensureClearInitialState(view);
                states = new Object[]{
                            internalBuildTreeStructureToSave(view),
                            view.processSaveState(context)};
            }
            else
            {
                states = new HashMap<String, Object>();

                saveStateOnMap(context,(Map<String,Object>) states, view);
                
                if ( ((Map<String,Object>)states).isEmpty())
                {
                    states = null;
                }
            }
            
            // TODO: not sure the best way to handle dynamic adds/removes as mandated by the spec.
            
            // As required by ResponseStateManager, the return value is an Object array.  First
            // element is the structure object, second is the state map.
            
            serializedView = new Object[] { null, states };
            
            //externalContext.getRequestMap().put(DefaultFaceletsStateManagementHelper.SERIALIZED_VIEW_REQUEST_ATTR,
            //        serializedView);
            context.getAttributes().put(SERIALIZED_VIEW_REQUEST_ATTR, serializedView);
        }
        
        //if (!context.getApplication().getStateManager().isSavingStateInClient(context))
        //{
        //    helper.saveSerializedViewInServletSession(context, serializedView);
        //}
        
        return serializedView;
    }
    
    private void restoreStateFromMap(final FacesContext context, final Map<String,Object> states,
            final UIComponent component)
    {
        if (states == null)
        {
            return;
        }
        
        try
        {
            //Restore view
            component.pushComponentToEL(context, component);
            Object state = states.get(component.getClientId(context));
            if (state != null)
            {
                if (state instanceof AttachedFullStateWrapper)
                {
                    //Don't restore this one! It will be restored when the algorithm remove and add it.
                    return;
                }
                try
                {
                    component.restoreState(context, state);
                }
                catch(Exception e)
                {
                    throw new IllegalStateException("Error restoring component: "+component.getClientId(context), e);
                }
            }
    
            //Scan children
            if (component.getChildCount() > 0)
            {
                //String currentClientId = component.getClientId();
                
                List<UIComponent> children  = component.getChildren();
                for (int i = 0; i < children.size(); i++)
                {
                    UIComponent child = children.get(i);
                    if (child != null && !child.isTransient())
                    {
                        restoreStateFromMap( context, states, child);
                    }
                }
            }
    
            //Scan facets
            if (component.getFacetCount() > 0)
            {
                Map<String, UIComponent> facetMap = component.getFacets();
                
                for (Map.Entry<String, UIComponent> entry : facetMap.entrySet())
                {
                    UIComponent child = entry.getValue();
                    if (child != null && !child.isTransient())
                    {
                        //String facetName = entry.getKey();
                        restoreStateFromMap( context, states, child);
                    }
                }
            }
        }
        finally
        {
            component.popComponentFromEL(context);
        }
    }

    static List<String> getClientIdsAdded(UIViewRoot root)
    {
        return (List<String>) root.getAttributes().get(CLIENTIDS_ADDED);
    }
    
    static void setClientsIdsAdded(UIViewRoot root, List<String> clientIdsList)
    {
        root.getAttributes().put(CLIENTIDS_ADDED, clientIdsList);
    }
    
    static List<String> getClientIdsRemoved(UIViewRoot root)
    {
        return (List<String>) root.getAttributes().get(CLIENTIDS_REMOVED);
    }
    
    static void setClientsIdsRemoved(UIViewRoot root, List<String> clientIdsList)
    {
        root.getAttributes().put(CLIENTIDS_REMOVED, clientIdsList);
    }
    
    @SuppressWarnings("unchecked")
    private void registerOnAddRemoveList(FacesContext facesContext, String clientId)
    {
        UIViewRoot uiViewRoot = facesContext.getViewRoot();

        List<String> clientIdsAdded = (List<String>) getClientIdsAdded(uiViewRoot);
        if (clientIdsAdded == null)
        {
            //Create a set that preserve insertion order
            clientIdsAdded = new ArrayList<String>();
        }
        clientIdsAdded.add(clientId);

        setClientsIdsAdded(uiViewRoot, clientIdsAdded);

        List<String> clientIdsRemoved = (List<String>) getClientIdsRemoved(uiViewRoot);
        if (clientIdsRemoved == null)
        {
            //Create a set that preserve insertion order
            clientIdsRemoved = new ArrayList<String>();
        }

        clientIdsRemoved.add(clientId);

        setClientsIdsRemoved(uiViewRoot, clientIdsRemoved);
    }
    
    @SuppressWarnings("unchecked")
    private void registerOnAddList(FacesContext facesContext, String clientId)
    {
        UIViewRoot uiViewRoot = facesContext.getViewRoot();

        List<String> clientIdsAdded = (List<String>) getClientIdsAdded(uiViewRoot);
        if (clientIdsAdded == null)
        {
            //Create a set that preserve insertion order
            clientIdsAdded = new ArrayList<String>();
        }
        clientIdsAdded.add(clientId);

        setClientsIdsAdded(uiViewRoot, clientIdsAdded);
    }
            
    private void saveStateOnMap(final FacesContext context, final Map<String,Object> states,
            final UIComponent component)
    {
        ComponentState componentAddedAfterBuildView = null;
        try
        {
            component.pushComponentToEL(context, component);
            
            //Scan children
            if (component.getChildCount() > 0)
            {
                List<UIComponent> children  = component.getChildren();
                for (int i = 0; i < children.size(); i++)
                {
                    UIComponent child = children.get(i);
                    if (child != null && !child.isTransient())
                    {
                        componentAddedAfterBuildView
                                = (ComponentState) child.getAttributes().get(COMPONENT_ADDED_AFTER_BUILD_VIEW);
                        if (componentAddedAfterBuildView != null)
                        {
                            if (ComponentState.REMOVE_ADD.equals(componentAddedAfterBuildView))
                            {
                                registerOnAddRemoveList(context, child.getClientId(context));
                                child.getAttributes().put(COMPONENT_ADDED_AFTER_BUILD_VIEW, ComponentState.ADDED);
                            }
                            else if (ComponentState.ADD.equals(componentAddedAfterBuildView))
                            {
                                registerOnAddList(context, child.getClientId(context));
                                child.getAttributes().put(COMPONENT_ADDED_AFTER_BUILD_VIEW, ComponentState.ADDED);
                            }
                            ensureClearInitialState(child);
                            //Save all required info to restore the subtree.
                            //This includes position, structure and state of subtree
                            states.put(child.getClientId(context), new AttachedFullStateWrapper( 
                                    new Object[]{
                                        component.getClientId(context),
                                        null,
                                        i,
                                        internalBuildTreeStructureToSave(child),
                                        child.processSaveState(context)}));
                        }
                        else
                        {
                            saveStateOnMap( context, states, child);
                        }
                    }
                }
            }
    
            //Scan facets
            
            if (component.getFacetCount() > 0)
            {
                Map<String, UIComponent> facetMap = component.getFacets();
                
                for (Map.Entry<String, UIComponent> entry : facetMap.entrySet())
                {
                    UIComponent child = entry.getValue();
                    if (child != null && !child.isTransient())
                    {
                        String facetName = entry.getKey();
                        componentAddedAfterBuildView = (ComponentState) child.getAttributes().get(COMPONENT_ADDED_AFTER_BUILD_VIEW);
                        if (componentAddedAfterBuildView != null)
                        {
                            if (ComponentState.REMOVE_ADD.equals(componentAddedAfterBuildView))
                            {
                                registerOnAddRemoveList(context, child.getClientId(context));
                                child.getAttributes().put(COMPONENT_ADDED_AFTER_BUILD_VIEW, ComponentState.ADDED);
                            }
                            else if (ComponentState.ADD.equals(componentAddedAfterBuildView))
                            {
                                registerOnAddList(context, child.getClientId(context));
                                child.getAttributes().put(COMPONENT_ADDED_AFTER_BUILD_VIEW, ComponentState.ADDED);
                            }
                            //Save all required info to restore the subtree.
                            //This includes position, structure and state of subtree
                            ensureClearInitialState(child);
                            states.put(child.getClientId(context),new AttachedFullStateWrapper(new Object[]{
                                component.getClientId(context),
                                facetName,
                                null,
                                internalBuildTreeStructureToSave(child),
                                child.processSaveState(context)}));
                        }
                        else
                        {
                            saveStateOnMap( context, states, child);
                        }
                    }
                }
            }
            
            //Save state        
            Object savedState = component.saveState(context);
            
            //Only save if the value returned is null
            if (savedState != null)
            {
                states.put(component.getClientId(context), savedState);            
            }
        }
        finally
        {
            component.popComponentFromEL(context);
        }
    }
    
    protected void ensureClearInitialState(UIComponent c)
    {
        c.clearInitialState();
        if (c.getChildCount() > 0)
        {
            for (int i = 0, childCount = c.getChildCount(); i < childCount; i++)
            {
                UIComponent child = c.getChildren().get(i);
                ensureClearInitialState(child);
            }
        }
        if (c.getFacetCount() > 0)
        {
            for (UIComponent child : c.getFacets().values())
            {
                ensureClearInitialState(child);
            }
        }
    }
    
    public void suscribeListeners(UIViewRoot uiViewRoot)
    {
        PostAddPreRemoveFromViewListener componentListener = new PostAddPreRemoveFromViewListener();
        uiViewRoot.subscribeToViewEvent(PostAddToViewEvent.class, componentListener);
        uiViewRoot.subscribeToViewEvent(PreRemoveFromViewEvent.class, componentListener);
    }
    
    private void checkIds (FacesContext context, UIComponent component, Set<String> existingIds)
    {
        String id;
        Iterator<UIComponent> children;
        
        if (component == null)
        {
            return;
        }
        
        // Need to use this form of the client ID method so we generate the client-side ID.
        
        id = component.getClientId (context);
        
        if (existingIds.contains (id))
        {
            throw new IllegalStateException ("component with duplicate id \"" + id + "\" found");
        }
        
        existingIds.add (id);
        
        int facetCount = component.getFacetCount();
        if (facetCount > 0)
        {
            for (UIComponent facet : component.getFacets().values())
            {
                checkIds (context, facet, existingIds);
            }
        }
        for (int i = 0, childCount = component.getChildCount(); i < childCount; i++)
        {
            UIComponent child = component.getChildren().get(i);
            checkIds (context, child, existingIds);
        }
    }
    
    protected RenderKitFactory getRenderKitFactory()
    {
        if (_renderKitFactory == null)
        {
            _renderKitFactory = (RenderKitFactory)FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        }
        return _renderKitFactory;
    }
    
    /*
    private static class RestoreStateCallback implements VisitCallback
    {
        private PostRestoreStateEvent event;

        public VisitResult visit(VisitContext context, UIComponent target)
        {
            if (event == null)
            {
                event = new PostRestoreStateEvent(target);
            }
            else
            {
                event.setComponent(target);
            }

            // call the processEvent method of the current component.
            // The argument event must be an instance of AfterRestoreStateEvent whose component
            // property is the current component in the traversal.
            target.processEvent(event);
            
            return VisitResult.ACCEPT;
        }
    }*/
    
    /*
    private class RestoreStateVisitor implements VisitCallback {
        private Map<String, Object> states;
        
        private RestoreStateVisitor (Map<String, Object> states)
        {
            this.states = states;
        }
        
        @Override
        public VisitResult visit (VisitContext context, UIComponent target)
        {
            FacesContext facesContext = context.getFacesContext();
            Object state = states.get (target.getClientId (facesContext));
            
            if (state != null) {
                target.restoreState (facesContext, state);
            }
            
            return VisitResult.ACCEPT;
        }
    }
    
    private class SaveStateVisitor implements VisitCallback {
        private Map<String, Object> states;
        
        private SaveStateVisitor (Map<String, Object> states)
        {
            this.states = states;
        }
        
        @Override
        public VisitResult visit (VisitContext context, UIComponent target)
        {
            FacesContext facesContext = context.getFacesContext();
            Object state;
            
            if ((target == null) || target.isTransient()) {
                // No need to bother with these components or their children.
                
                return VisitResult.REJECT;
            }
            
            state = target.saveState (facesContext);
            
            if (state != null) {
                // Save by client ID into our map.
                
                states.put (target.getClientId (facesContext), state);
            }
            
            return VisitResult.ACCEPT;
        }
    }
    */


    protected String getCheckIdProductionMode(FacesContext facesContext)
    {
        if (_checkIdsProductionMode == null)
        {
            _checkIdsProductionMode
                    = WebConfigParamUtils.getStringInitParameter(facesContext.getExternalContext(),
                    CHECK_ID_PRODUCTION_MODE, CHECK_ID_PRODUCTION_MODE_DEFAULT); //default (auto)
        }
        return _checkIdsProductionMode;
    }

    
    public static class PostAddPreRemoveFromViewListener implements SystemEventListener
    {
        private transient FacesContext _facesContext;

        public boolean isListenerForSource(Object source)
        {
            // PostAddToViewEvent and PreRemoveFromViewEvent are
            // called from UIComponentBase.setParent
            return (source instanceof UIComponent);
        }

        public void processEvent(SystemEvent event)
        {
            UIComponent component = (UIComponent) event.getSource();
            
            if (component.isTransient())
            {
                return;
            }
            
            // This is a view listener. It is not saved on the state and this listener
            // is suscribed each time the view is restored, so we can cache facesContext
            // here
            if (_facesContext == null)
            {
                _facesContext = FacesContext.getCurrentInstance();
            }
            //FacesContext facesContext = FacesContext.getCurrentInstance();
            if (FaceletViewDeclarationLanguage.isRefreshingTransientBuild(_facesContext))
            {
                return;
            }
            
            if (event instanceof PostAddToViewEvent)
            {
                //PostAddToViewEvent
                component.getAttributes().put(COMPONENT_ADDED_AFTER_BUILD_VIEW, ComponentState.ADD);
            }
            else
            {
                //PreRemoveFromViewEvent
                UIViewRoot uiViewRoot = _facesContext.getViewRoot();
                
                List<String> clientIdsRemoved = getClientIdsRemoved(uiViewRoot);
                if (clientIdsRemoved == null)
                {
                    //Create a set that preserve insertion order
                    clientIdsRemoved = new ArrayList<String>();
                }
                clientIdsRemoved.add(component.getClientId(_facesContext));
                setClientsIdsRemoved(uiViewRoot, clientIdsRemoved);
            }
        }
    }
    
    private static TreeStructComponent internalBuildTreeStructureToSave(UIComponent component)
    {
        TreeStructComponent structComp = new TreeStructComponent(component.getClass().getName(),
                                                                 component.getId());

        //children
        if (component.getChildCount() > 0)
        {
            List<TreeStructComponent> structChildList = new ArrayList<TreeStructComponent>();
            for (int i = 0, childCount = component.getChildCount(); i < childCount; i++)
            {
                UIComponent child = component.getChildren().get(i);     
                if (!child.isTransient())
                {
                    TreeStructComponent structChild = internalBuildTreeStructureToSave(child);
                    structChildList.add(structChild);
                }
            }
            
            TreeStructComponent[] childArray = structChildList.toArray(new TreeStructComponent[structChildList.size()]);
            structComp.setChildren(childArray);
        }

        //facets
        
        if (component.getFacetCount() > 0)
        {
            Map<String, UIComponent> facetMap = component.getFacets();
            List<Object[]> structFacetList = new ArrayList<Object[]>();
            for (Map.Entry<String, UIComponent> entry : facetMap.entrySet())
            {
                UIComponent child = entry.getValue();
                if (!child.isTransient())
                {
                    String facetName = entry.getKey();
                    TreeStructComponent structChild = internalBuildTreeStructureToSave(child);
                    structFacetList.add(new Object[] {facetName, structChild});
                }
            }
            
            Object[] facetArray = structFacetList.toArray(new Object[structFacetList.size()]);
            structComp.setFacets(facetArray);
        }

        return structComp;
    }
    
    private static UIComponent internalRestoreTreeStructure(TreeStructComponent treeStructComp)
    {
        String compClass = treeStructComp.getComponentClass();
        String compId = treeStructComp.getComponentId();
        UIComponent component = (UIComponent)ClassUtils.newInstance(compClass);
        component.setId(compId);

        //children
        TreeStructComponent[] childArray = treeStructComp.getChildren();
        if (childArray != null)
        {
            List<UIComponent> childList = component.getChildren();
            for (int i = 0, len = childArray.length; i < len; i++)
            {
                UIComponent child = internalRestoreTreeStructure(childArray[i]);
                childList.add(child);
            }
        }

        //facets
        Object[] facetArray = treeStructComp.getFacets();
        if (facetArray != null)
        {
            Map<String, UIComponent> facetMap = component.getFacets();
            for (int i = 0, len = facetArray.length; i < len; i++)
            {
                Object[] tuple = (Object[])facetArray[i];
                String facetName = (String)tuple[0];
                TreeStructComponent structChild = (TreeStructComponent)tuple[1];
                UIComponent child = internalRestoreTreeStructure(structChild);
                facetMap.put(facetName, child);
            }
        }

        return component;
    }

    public static class TreeStructComponent implements Serializable
    {
        private static final long serialVersionUID = 5069109074684737231L;
        private String _componentClass;
        private String _componentId;
        private TreeStructComponent[] _children = null; // Array of children
        private Object[] _facets = null; // Array of Array-tuples with Facetname and TreeStructComponent

        TreeStructComponent(String componentClass, String componentId)
        {
            _componentClass = componentClass;
            _componentId = componentId;
        }

        public String getComponentClass()
        {
            return _componentClass;
        }

        public String getComponentId()
        {
            return _componentId;
        }

        void setChildren(TreeStructComponent[] children)
        {
            _children = children;
        }

        TreeStructComponent[] getChildren()
        {
            return _children;
        }

        Object[] getFacets()
        {
            return _facets;
        }

        void setFacets(Object[] facets)
        {
            _facets = facets;
        }
    }
    
}
