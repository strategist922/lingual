/*
 * Copyright (c) 2007-2013 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cascading.lingual.catalog.target;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import cascading.lingual.catalog.CatalogOptions;
import cascading.lingual.catalog.FormatProperties;
import cascading.lingual.catalog.Protocol;
import cascading.lingual.catalog.SchemaCatalog;
import cascading.lingual.catalog.SchemaDef;
import cascading.lingual.catalog.builder.ProtocolBuilder;
import cascading.lingual.common.Printer;
import cascading.lingual.platform.PlatformBroker;

import static java.util.Arrays.asList;

/**
 *
 */
public class ProtocolTarget extends CRUDTarget
  {
  public ProtocolTarget( Printer printer, CatalogOptions options )
    {
    super( printer, options );
    }

  @Override
  protected boolean performRename( PlatformBroker platformBroker )
    {
    SchemaCatalog catalog = platformBroker.getCatalog();
    String schemaName = getOptions().getSchemaName();
    Protocol oldProtocol = Protocol.getProtocol( getOptions().getProtocolName() );
    Protocol newProtocol = Protocol.getProtocol( getOptions().getRenameName() );
    return catalog.renameProtocol( schemaName, oldProtocol, newProtocol );
    }

  @Override
  protected boolean performRemove( PlatformBroker platformBroker )
    {
    SchemaCatalog catalog = platformBroker.getCatalog();
    String schemaName = getOptions().getSchemaName();
    Protocol protocol = Protocol.getProtocol( getOptions().getProtocolName() );
    return catalog.removeProtocol( schemaName, protocol );
    }

  @Override
  protected Object getSource( PlatformBroker platformBroker )
    {
    SchemaCatalog catalog = platformBroker.getCatalog();
    String sourceProtocol = getOptions().getProtocolName();
    String schemaName = getOptions().getSchemaName();
    Collection<Protocol> protocols = catalog.getSchemaDef( schemaName ).getAllProtocols();

    for( Protocol protocol : protocols )
      {
      if( protocol.getName().equals( sourceProtocol ) )
        return sourceProtocol;
      }

    return null;
    }

  @Override
  protected List<String> performUpdate( PlatformBroker platformBroker )
    {
    Protocol protocol = Protocol.getProtocol( getOptions().getProtocolName() );

    if( protocol == null )
      throw new IllegalArgumentException( "update action must have a protocol name value" );

    SchemaCatalog catalog = platformBroker.getCatalog();
    String schemaName = getOptions().getSchemaName();
    String providerName = getOptions().getProviderName();

    if( providerName == null )
      providerName = joinOrNull( catalog.getProtocolProperty( schemaName, protocol, FormatProperties.PROVIDER ) );

    if( providerName == null )
      throw new IllegalArgumentException( "provider is required" );

    return performAdd( platformBroker );
    }

  @Override
  protected void validateAdd( PlatformBroker platformBroker )
    {
    Protocol protocol = Protocol.getProtocol( getOptions().getProtocolName() );

    if( protocol == null )
      throw new IllegalArgumentException( "add action must have a protocol name value" );

    String providerName = getOptions().getProviderName();

    if( providerName == null )
      throw new IllegalArgumentException( "provider is required" );

    SchemaCatalog catalog = platformBroker.getCatalog();
    String schemaName = getOptions().getSchemaName();

    validateProviderName( catalog, schemaName, providerName );
    }

  @Override
  protected List<String> performAdd( PlatformBroker platformBroker )
    {
    SchemaCatalog catalog = platformBroker.getCatalog();
    String protocolName = getOptions().getProtocolName();
    Protocol protocol = Protocol.getProtocol( protocolName );
    String schemaName = getOptions().getSchemaName();
    Map<String, String> properties = getOptions().getProperties();
    List<String> schemes = getOptions().getSchemes();
    String providerName = getOptions().getProviderName();

    catalog.addUpdateProtocol( schemaName, protocol, schemes, properties, providerName );

    return asList( protocol.getName() );
    }

  @Override
  protected Collection<String> performGetNames( PlatformBroker platformBroker )
    {
    SchemaCatalog catalog = platformBroker.getCatalog();
    String schemaName = getOptions().getSchemaName();

    return catalog.getProtocolNames( schemaName );
    }

  @Override
  protected Map performShow( PlatformBroker platformBroker )
    {
    Protocol protocol = Protocol.getProtocol( getOptions().getProtocolName() );
    SchemaCatalog catalog = platformBroker.getCatalog();
    String schemaName = getOptions().getSchemaName();
    SchemaDef schemaDef = catalog.getSchemaDefChecked( schemaName );

    return new ProtocolBuilder( schemaDef, getOptions().getProviderName() ).format( protocol );
    }
  }
