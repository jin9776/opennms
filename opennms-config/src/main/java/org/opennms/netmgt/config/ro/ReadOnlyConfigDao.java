/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2019 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2019 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.config.ro;

import java.util.Date;

import org.opennms.netmgt.config.ReadOnlyConfig;

/**
 * Read the Efffective Configuration from the realtional datastore.
 */
public interface ReadOnlyConfigDao<T extends ReadOnlyConfig> {

    /**
     * Returns the matching configuration for the provided key and type. 
     * By convention, the key value is the filename of the preexisting configuration.
     * If the config has been read from the DB less than <i>cacheLengthInMillis</i> ago, a cached version of the
     * configuration will be returned. The default cache length is 5 minutes and 
     * can be overwritten for each ReadOnly Config Dao. 
     */
    public T getByKey(Class<T> type, String key, long cacheLengthInMillis);

    /**
     * Returns the last update date for the object with the given key.
     * Returns null if there is no matching configuration in the db.
     */
    public Date getLastUpdated(String key);

    /**
     * Returns the current configuration unmarshalled from the datastore.
     * Each ReadOnlyConfigDao will implement a type approriate version of this method.
     */
    public T getConfig();

}
