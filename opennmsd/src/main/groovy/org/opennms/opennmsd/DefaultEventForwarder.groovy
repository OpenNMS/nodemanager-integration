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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
 
import org.apache.log4j.Logger
import org.opennms.opennmsd.AbstractEventForwarder
import groovy.xml.MarkupBuilder

class DefaultEventForwarder extends AbstractEventForwarder {

    private static Logger m_log = Logger.getLogger(DefaultEventForwarder.class);
    
    private String m_host;
    private String m_openNmsHost;
    private int m_port;
    private DateFormat m_dateFormat;
    private Resolver m_resolver;
    
    public DefaultEventForwarder() {
        m_log.debug("DefaultEventForwarder created")
        m_host = InetAddress.getLocalHost().hostName;
        
        m_dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        m_dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

    }
    
    public String toString() {
        return "DefaultEventForwarder"
    }
    
    public String getHost() {
        return m_host;
    }
    
    public String getOpenNmsHost() {
        return m_openNmsHost;
    }
    
    public void setOpenNmsHost(String host) {
        m_openNmsHost = host;
    }
    
    public int getPort() {
        return m_port;
    }
    
    public void setPort(int port) {
        m_port = port;
    }
    
    public void setResolver(Resolver resolver) {
        m_resolver = resolver;
    }
    
    public void start() {
        assert m_openNmsHost;
        assert m_port;
        assert m_resolver;

        preserve(StatusEvent.createStartEvent());

        super.start();
        
    }
    
    public void stop() {

        preserve(StatusEvent.createStopEvent());
        
        super.stop();
        
    }
    
    public withEventXml(Closure c) throws IOException {
        Socket socket = null;
        try {
            socket = new Socket(m_openNmsHost, m_port);
            socket.outputStream.withWriter { out ->
                def xml = new MarkupBuilder(out);
                c.call(xml);
            }
        } finally {
            try { if (socket != null) socket.close(); } catch (IOException e) { m_log.info("Failed to close the socket to opennms.") }
        }
    }

    protected void forwardEvents(List eventsToForward) {
        int count = eventsToForward.size();
        m_log.debug("openNmsHost is ${m_openNmsHost} forwarding ${eventsToForward.size()} events.")

        try {
            withEventXml { xml ->
                if (eventsToForward) {
                    use(EventMarshalMethods) {
                        xml.log {
                            events {
                                for(Event e in eventsToForward) {
                                    e.resolveNodeLabel(m_resolver)
                                    e.marshal(xml)
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            m_log.debug("finished sending eventList ${eventsToForward}")
        }
    }
}
