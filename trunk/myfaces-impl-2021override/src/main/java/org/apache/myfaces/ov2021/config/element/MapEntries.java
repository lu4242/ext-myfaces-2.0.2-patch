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
package org.apache.myfaces.ov2021.config.element;

import java.io.Serializable;
import java.util.Iterator;

/**
 *
 * @author Manfred Geiler (latest modification by $Author: bommel $)
 * @version $Revision: 1187700 $ $Date: 2011-10-22 14:19:37 +0200 (Sa, 22 Okt 2011) $
 */
public abstract class MapEntries implements Serializable
{
    // <!ELEMENT map-entries (key-class?, value-class?, map-entry*)>

    public abstract String getKeyClass();

    public abstract String getValueClass();

    /**
     * @return Iterator over {@link MapEntry} entries
     */
    public abstract Iterator<? extends MapEntry> getMapEntries();

}
