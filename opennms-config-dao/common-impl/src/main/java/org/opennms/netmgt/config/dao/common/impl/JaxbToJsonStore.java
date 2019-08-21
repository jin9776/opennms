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

package org.opennms.netmgt.config.dao.common.impl;

import java.io.IOException;
import java.util.function.Consumer;

import org.codehaus.jackson.map.ObjectMapper;
import org.opennms.core.xml.JacksonUtils;
import org.opennms.features.distributed.kvstore.api.JsonStore;

public interface JaxbToJsonStore<T> {
    ObjectMapper objectMapper = JacksonUtils.createDefaultObjectMapper();

    default Consumer<T> getJsonWriterCallbackFunction(JsonStore jsonStore, String key, String context) {
        return config -> {
            try {
                // Synchronize to ensure mapping the pojo and writing it to the store is atomic
                synchronized (this) {
                    jsonStore.put(key, objectMapper.writeValueAsString(config), context);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
