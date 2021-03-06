/*
 * This file is part of the OpenNMS(R) Application.
 *
 * OpenNMS(R) is Copyright (C) 2008 The OpenNMS Group, Inc.  All rights reserved.
 * OpenNMS(R) is a derivative work, containing both original code, included code and modified
 * code that was published under the GNU General Public License. Copyrights for modified
 * and included code are below.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * For more information contact:
 * OpenNMS Licensing       <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 */
package org.opennms.opennmsd;

import org.opennms.nnm.SnmpObjId;


/**
 * EventIdentity
 *
 * @author brozow
 */
public class EventIdentity {
    
    private String m_enterpriseId;
    private int m_generic;
    private int m_specific;
    private SnmpObjId m_eventObjectId;
    
    public EventIdentity(String enterpriseId, int generic, int specific) {
        m_enterpriseId = enterpriseId;
        m_generic = generic;
        m_specific = specific;
        if (m_generic == 6) {
            m_eventObjectId = SnmpObjId.get(m_enterpriseId+".0."+m_specific);
        } else {
            m_eventObjectId = SnmpObjId.get(m_enterpriseId+"."+(m_generic+1));
        }
    }
    
    public String getEnterpriseId() {
        return m_enterpriseId;
    }

    public int getGeneric() {
        return m_generic;
    }

    public int getSpecific() {
        return m_specific;
    }
    
    public SnmpObjId getEventObjectId() {
        return m_eventObjectId;
    }
    
    
    public boolean equals(Object obj) {
        if (obj instanceof EventIdentity) {
            EventIdentity other = (EventIdentity)obj;
            return getEventObjectId().equals(other.getEventObjectId());
        }
        return false;
    }

    public int hashCode() {
        return getEventObjectId().hashCode();
    }

    public String toString() {
        return getEventObjectId().toString();
    }

}
