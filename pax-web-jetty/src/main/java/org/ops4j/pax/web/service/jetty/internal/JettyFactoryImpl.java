/* Copyright 2007 Alin Dreghiciu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.web.service.jetty.internal;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.web.service.spi.model.ServerModel;

class JettyFactoryImpl
    implements JettyFactory
{

    /**
     * Associated server model.
     */
    private final ServerModel m_serverModel;

    /**
     * Constrcutor.
     *
     * @param serverModel asscociated server model
     */
    JettyFactoryImpl( final ServerModel serverModel )
    {
        NullArgumentException.validateNotNull( serverModel, "Service model" );
        m_serverModel = serverModel;
    }

    /**
     * {@inheritDoc}
     */
    public JettyServer createServer()
    {
        return new JettyServerImpl( m_serverModel );
    }

    /**
     * {@inheritDoc}
     */
    public Connector createConnector( final int port,
                                      final String host,
                                      final boolean useNIO )
    {
        if( useNIO )
        {
            final SelectChannelConnector nioConnector = new NIOSocketConnectorWrapper();
            nioConnector.setHost( host );
            nioConnector.setPort( port );
            nioConnector.setUseDirectBuffers( true );
            return nioConnector;
        }
        else
        {
            final Connector connector = new SocketConnectorWrapper();
            connector.setPort( port );
            connector.setHost( host );
            return connector;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Connector createSecureConnector( final int port,
                                            final String sslKeystore,
                                            final String sslPassword,
                                            final String sslKeyPassword,
                                            final String host,
                                            final String sslKeystoreType,
                                            final boolean isClientAuthNeeded,
                                            final boolean isClientAuthWanted )
    {
        final SslSocketConnector connector = new SslSocketConnector();
        connector.setPort( port );
        connector.setKeystore( sslKeystore );
        connector.setPassword( sslPassword );
        connector.setKeyPassword( sslKeyPassword );
        connector.setHost( host );

        // Fix for POODLE bug
        connector.getSslContextFactory().addExcludeProtocols("SSLv3");

        connector.setNeedClientAuth( isClientAuthNeeded );
        connector.setWantClientAuth( isClientAuthWanted );

        if( sslKeystoreType != null )
        {
            connector.setKeystoreType( sslKeystoreType );
        }
        return connector;
    }

}
